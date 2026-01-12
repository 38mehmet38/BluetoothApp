# Kotlin
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }
-keep class kotlin.Metadata { *; }
-keepclassmembers class ** {
    ** CREATOR;
}

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.mehmetbluetooth.model.** { *; }

# Android Support Libraries
-keep class androidx.** { *; }
-keep interface androidx.** { *; }

# Bluetooth
-keep class android.bluetooth.** { *; }

# Services
-keep class * extends android.app.Service

# BroadcastReceiver
-keep class * extends android.content.BroadcastReceiver

# Keep all classes in our package
-keep class com.mehmetbluetooth.** { *; }

# Generic signature
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Remove logging
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
