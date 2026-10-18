package net.theboyers.reps

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import kotlin.math.max

/**
 * Foreground service that plays the workout's timed audio cues — countdown ticks
 * in the final seconds of a rest and the "next up" tone when a rest ends — while
 * the app is backgrounded or the screen is off.
 *
 * The UI publishes a schedule of absolute wall-clock cue times whenever the
 * workout state changes. The service holds a partial wakelock only while cues
 * are pending and releases it as soon as the schedule is exhausted (for example
 * while waiting on a rep-based step), so idle battery use stays minimal.
 */
class WorkoutAudioService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private var toneGenerator: ToneGenerator? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private val scheduled = mutableListOf<Runnable>()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        toneGenerator = try {
            ToneGenerator(AudioManager.STREAM_MUSIC, 90)
        } catch (_: RuntimeException) {
            null
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_SCHEDULE -> {
                startInForeground()
                val times = intent.getLongArrayExtra(EXTRA_TIMES) ?: longArrayOf()
                val types = intent.getIntArrayExtra(EXTRA_TYPES) ?: intArrayOf()
                scheduleCues(times, types)
            }
            ACTION_STOP -> {
                stopEverything()
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        stopEverything()
        toneGenerator?.release()
        toneGenerator = null
        super.onDestroy()
    }

    private fun startInForeground() {
        val notification = buildNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun scheduleCues(times: LongArray, types: IntArray) {
        cancelScheduled()
        if (times.isEmpty()) {
            releaseWakeLock()
            return
        }

        val now = System.currentTimeMillis()
        val lastAt = times.maxOrNull() ?: now
        // Keep the CPU awake only across the span that actually has cues (bounded
        // by a safety cap), then let it sleep.
        acquireWakeLock((lastAt - now).coerceAtLeast(0L) + CUE_WINDOW_MARGIN_MS)

        times.forEachIndexed { index, at ->
            val type = types.getOrElse(index) { CUE_TICK }
            val runnable = Runnable { play(type) }
            scheduled.add(runnable)
            handler.postDelayed(runnable, (at - System.currentTimeMillis()).coerceAtLeast(0L))
        }

        // Once the cues run out, shut the service down so it never lingers in
        // the background (a new schedule cancels this via cancelScheduled()).
        val finish = Runnable {
            releaseWakeLock()
            stopEverything()
            stopSelf()
        }
        scheduled.add(finish)
        handler.postDelayed(finish, (lastAt - System.currentTimeMillis()).coerceAtLeast(0L) + CUE_WINDOW_MARGIN_MS)
    }

    private fun play(cueType: Int) {
        val tone = when (cueType) {
            CUE_TICK -> TONE_TICK
            CUE_REST_END -> TONE_REST_END
            else -> TONE_TIMER_END
        }
        val duration = when (cueType) {
            CUE_TICK -> 90
            CUE_REST_END -> 500
            else -> 600
        }
        try {
            toneGenerator?.startTone(tone, duration)
        } catch (_: RuntimeException) {
            // Tone unavailable on this device — ignore.
        }
    }

    private fun cancelScheduled() {
        scheduled.forEach { handler.removeCallbacks(it) }
        scheduled.clear()
    }

    private fun stopEverything() {
        cancelScheduled()
        releaseWakeLock()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
    }

    @Suppress("WakelockTimeout")
    private fun acquireWakeLock(timeoutMs: Long) {
        releaseWakeLock()
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "$packageName:workout-audio"
        ).apply {
            setReferenceCounted(false)
            acquire(max(timeoutMs, MIN_WAKE_LOCK_MS).coerceAtMost(MAX_WAKE_LOCK_MS))
        }
    }

    private fun releaseWakeLock() {
        wakeLock?.let { if (it.isHeld) it.release() }
        wakeLock = null
    }

    private fun createNotificationChannel() {
        val manager = getSystemService(NotificationManager::class.java) ?: return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Workout sound cues",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Plays countdown ticks and tones while a workout is running"
            setShowBadge(false)
        }
        manager.createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        val openApp = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val stopSounds = PendingIntent.getService(
            this,
            1,
            Intent(this, WorkoutAudioService::class.java).setAction(ACTION_STOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Workout in progress")
            .setContentText("Sound cues play even with the screen off")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(openApp)
            .setOngoing(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(0, "Stop sounds", stopSounds)
            .build()
    }

    companion object {
        const val ACTION_SCHEDULE = "net.theboyers.reps.action.SCHEDULE"
        const val ACTION_STOP = "net.theboyers.reps.action.STOP"
        const val EXTRA_TIMES = "cue_times"
        const val EXTRA_TYPES = "cue_types"

        const val CUE_TICK = 0
        const val CUE_REST_END = 1
        const val CUE_TIMER_END = 2

        // ToneGenerator built-in tone types (distinct cues for tick vs. next-up).
        private const val TONE_TICK = ToneGenerator.TONE_PROP_BEEP
        private const val TONE_REST_END = ToneGenerator.TONE_PROP_ACK
        private const val TONE_TIMER_END = ToneGenerator.TONE_PROP_BEEP2

        private const val CHANNEL_ID = "workout_audio"
        private const val NOTIFICATION_ID = 42
        private const val CUE_WINDOW_MARGIN_MS = 2_000L
        private const val MIN_WAKE_LOCK_MS = 3_000L
        private const val MAX_WAKE_LOCK_MS = 90 * 60 * 1000L

        /** Builds a schedule intent for [times] and [types] (parallel arrays). */
        fun scheduleIntent(context: Context, times: LongArray, types: IntArray): Intent =
            Intent(context, WorkoutAudioService::class.java)
                .setAction(ACTION_SCHEDULE)
                .putExtra(EXTRA_TIMES, times)
                .putExtra(EXTRA_TYPES, types)

        fun stopIntent(context: Context): Intent =
            Intent(context, WorkoutAudioService::class.java).setAction(ACTION_STOP)
    }
}
