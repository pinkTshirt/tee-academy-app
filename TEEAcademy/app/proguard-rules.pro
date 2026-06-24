# Room, Hilt, Media3 generally need no extra rules with current AGP/R8.
# Keep entity/model classes that are deserialized from seed JSON via kotlinx.serialization.
-keepclassmembers class com.teeacademy.app.data.seed.** { *; }
-keepclassmembers class com.teeacademy.app.data.local.entity.** { *; }
