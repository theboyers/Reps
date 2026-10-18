# Add project specific ProGuard rules here.

# Keep data classes used by Gson for serialization/deserialization.
-keep class net.theboyers.reps.RoutineEntry { *; }
-keep class net.theboyers.reps.SavedRoutine { *; }
-keep class net.theboyers.reps.WorkoutSession { *; }
-keep class net.theboyers.reps.WorkoutDraft { *; }
-keep class net.theboyers.reps.WorkoutStep$Work { *; }
-keepattributes Signature
-keepattributes *Annotation*

# Preserve line numbers in crash stack traces.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
