# ProGuard rules for LastChat

# Keep Room entities
-keep class com.lastchat.app.data.local.entity.** { *; }
-keep class com.lastchat.app.data.model.** { *; }
-keepclassmembers class * {
    @androidx.room.* <fields>;
}
-keep class * extends androidx.room.RoomDatabase

# Keep DTOs for serialization
-keep class com.lastchat.app.data.remote.dto.** { *; }
-keepclassmembers class com.lastchat.app.data.remote.dto.** { <init>(...); }

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** { kotlinx.serialization.KSerializer serializer(...); }

# Koin
-keep class * extends org.koin.core.module.Module
-keep class com.lastchat.app.di.** { *; }

# Compose
-keep class androidx.compose.** { *; }
-keepclassmembers class androidx.compose.** { *; }
