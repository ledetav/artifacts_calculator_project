# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Preserve line numbers for debugging
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Optimization settings
-optimizationpasses 7
-dontusemixedcaseclassnames
-verbose
-allowaccessmodification
-mergeinterfacesaggressively

# Kotlin
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }
-keep interface kotlin.** { *; }
-keep interface kotlinx.** { *; }
-dontwarn kotlin.**
-dontwarn kotlinx.**

# Jetpack Compose
-keep class androidx.compose.** { *; }
-keep interface androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Room
-keep class androidx.room.** { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keep @androidx.room.Database class * { *; }
-dontwarn androidx.room.**

# Hilt
-keep class dagger.hilt.** { *; }
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }
-keep @dagger.hilt.android.qualifiers.** class * { *; }
-dontwarn dagger.hilt.**

# Retrofit & OkHttp
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep interface retrofit2.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn javax.annotation.**

# Gson
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keep class * implements com.google.gson.TypeAdapter
-dontwarn com.google.gson.**

# Data classes
-keep class com.nokaori.genshinaibuilder.data.** { *; }
-keep class com.nokaori.genshinaibuilder.domain.** { *; }
-keep class com.nokaori.genshinaibuilder.presentation.** { *; }

# Coil
-keep class coil.** { *; }
-keep interface coil.** { *; }
-dontwarn coil.**

# PaddleOCR
-keep class com.baidu.paddle.** { *; }
-keep class com.baidu.fastdeploy.** { *; }
-dontwarn com.baidu.paddle.**
-dontwarn com.baidu.fastdeploy.**

# Lifecycle
-keep class androidx.lifecycle.** { *; }
-keep interface androidx.lifecycle.** { *; }

# Navigation
-keep class androidx.navigation.** { *; }
-keep interface androidx.navigation.** { *; }

# DataStore
-keep class androidx.datastore.** { *; }
-keep interface androidx.datastore.** { *; }

# Paging
-keep class androidx.paging.** { *; }
-keep interface androidx.paging.** { *; }

# Biometric
-keep class androidx.biometric.** { *; }
-keep interface androidx.biometric.** { *; }

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep Serializable implementations
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
