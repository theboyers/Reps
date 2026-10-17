package net.theboyers.reps

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests for [buildAudioCueSchedule] — the function that decides when the
 * foreground audio service should tick and tone during a workout.
 */
class AudioCueScheduleTest {

    private val now = 1_000_000L

    private fun work(durationSec: Int) =
        WorkoutStep.Work("Ex", "", 1, 1, "$durationSec sec", durationSec)

    private fun repWork() =
        WorkoutStep.Work("Ex", "", 1, 1, "10", 0)

    private fun rest(durationSec: Int) = WorkoutStep.Rest(durationSec, "")

    @Test
    fun repBasedCurrentStep_producesNoCues() {
        assertTrue(buildAudioCueSchedule(listOf(repWork()), 0, 0L, now).isEmpty())
    }

    @Test
    fun fromIndexOutOfRange_producesNoCues() {
        assertTrue(buildAudioCueSchedule(listOf(rest(10)), 5, now + 10_000, now).isEmpty())
    }

    @Test
    fun restStep_producesFiveTicksAndAnEndTone() {
        val end = now + 10_000
        val cues = buildAudioCueSchedule(listOf(rest(10)), 0, end, now)

        assertEquals(6, cues.size)
        assertEquals(5, cues.count { it.type == AudioCueType.TICK })
        assertEquals(AudioCueType.REST_END, cues.last().type)
        assertEquals(end, cues.last().atMillis)
    }

    @Test
    fun ticksFallInTheFinalFiveSeconds() {
        val end = now + 10_000
        val ticks = buildAudioCueSchedule(listOf(rest(10)), 0, end, now)
            .filter { it.type == AudioCueType.TICK }

        assertEquals(
            listOf(end - 5_000, end - 4_000, end - 3_000, end - 2_000, end - 1_000),
            ticks.map { it.atMillis }
        )
    }

    @Test
    fun ticksAlreadyInThePastAreDropped() {
        val end = now + 4_000 // 4 seconds left: the 5s and 4s ticks are gone
        val cues = buildAudioCueSchedule(listOf(rest(10)), 0, end, now)

        assertEquals(4, cues.size)
        assertEquals(3, cues.count { it.type == AudioCueType.TICK })
    }

    @Test
    fun restEndingImminently_hasOnlyTheEndTone() {
        val end = now + 500
        val cues = buildAudioCueSchedule(listOf(rest(10)), 0, end, now)

        assertEquals(listOf(AudioCueType.REST_END), cues.map { it.type })
    }

    @Test
    fun timedWorkStep_producesOnlyTheTimerEndTone() {
        val end = now + 30_000
        val cues = buildAudioCueSchedule(listOf(work(30)), 0, end, now)

        assertEquals(listOf(AudioCueType.TIMER_END), cues.map { it.type })
        assertEquals(end, cues.single().atMillis)
    }

    @Test
    fun scheduleChainsThroughTimedStepsUntilRepBased() {
        // A 10s rest, then a 20s timed work step, then a rep-based step.
        val steps = listOf(rest(10), work(20), repWork())
        val restEnd = now + 10_000
        val cues = buildAudioCueSchedule(steps, 0, restEnd, now)

        // rest: 5 ticks + REST_END (6), work: TIMER_END (1) = 7
        assertEquals(7, cues.size)
        assertEquals(AudioCueType.REST_END, cues[5].type)
        assertEquals(AudioCueType.TIMER_END, cues.last().type)
        assertEquals(restEnd + 20_000, cues.last().atMillis)
    }

    @Test
    fun scheduleStopsAtARepBasedStep() {
        val steps = listOf(work(20), repWork())
        val cues = buildAudioCueSchedule(steps, 0, now + 20_000, now)

        // Only the timed work step's end; the rep-based step produces nothing.
        assertEquals(listOf(AudioCueType.TIMER_END), cues.map { it.type })
    }

    @Test
    fun cuesAreSortedChronologically() {
        val steps = listOf(rest(10), rest(10))
        val cues = buildAudioCueSchedule(steps, 0, now + 10_000, now)

        assertEquals(cues.sortedBy { it.atMillis }, cues)
    }
}
