package net.theboyers.reps

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Guards persistence compatibility: routines saved before the weight/warmup
 * fields existed must still deserialize, and new fields must survive a round trip.
 */
class RoutineEntrySerializationTest {

    private val gson = Gson()

    @Test
    fun legacyJsonWithoutNewFields_deserializesWithDefaults() {
        // Shape written by v1.2.0 and earlier.
        val legacy = """
            [{"exerciseName":"Squats","details":"","sets":"3","repsOrDuration":"10",
              "restBetweenSetsSeconds":60,"type":"STRENGTH","isPerSide":false}]
        """.trimIndent()

        val type = object : TypeToken<List<RoutineEntry>>() {}.type
        val entries: List<RoutineEntry> = gson.fromJson(legacy, type)

        val entry = entries.single()
        assertEquals("Squats", entry.exerciseName)
        assertNull(entry.weight)
        assertEquals(0, entry.warmupSets)
        assertNull(entry.warmupReps)
        assertNull(entry.warmupWeight)
    }

    @Test
    fun roundTrip_preservesWeightAndWarmup() {
        val original = RoutineEntry(
            exerciseName = "Bench Press",
            details = "",
            sets = "3",
            repsOrDuration = "8",
            restBetweenSetsSeconds = 60,
            type = "STRENGTH",
            isPerSide = false,
            weight = "135 lb",
            warmupSets = 2,
            warmupReps = "10",
            warmupWeight = "45 lb"
        )

        val json = gson.toJson(listOf(original))
        val type = object : TypeToken<List<RoutineEntry>>() {}.type
        val restored: List<RoutineEntry> = gson.fromJson(json, type)

        assertEquals(original, restored.single())
    }
}
