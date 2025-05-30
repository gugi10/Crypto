# Proguard rules for CryptoTracker app

# Kotlin / Coroutines
-dontwarn kotlinx.coroutines.**
-keepclasseswithmembernames class kotlinx.coroutines.internal.MainDispatcherFactory {
    public static final kotlinx.coroutines.MainCoroutineDispatcher INSTANCE;
}
-keepclassmembernames class kotlinx.coroutines.flow.internal.AbstractSharedFlowKt {
    static final kotlinx.coroutines.flow.SharedFlow Slot;
}
-keepclassmembernames class kotlinx.coroutines.internal.MainDispatchersKt {
    public static final kotlinx.coroutines.MainCoroutineDispatcher Main;
}
-keepclassmembernames class kotlinx.coroutines.BuildersKt { # Preserves "launch", "async", etc.
    public static <T> kotlinx.coroutines.BuildersKt ... launch(...);
    public static <T> kotlinx.coroutines.BuildersKt ... async(...);
}
-keepnames class kotlinx.coroutines.Job # Preserves Job's name.
-keepnames class kotlinx.coroutines.CoroutineScope # Preserves CoroutineScope's name.
-keepnames class kotlin.coroutines.Continuation # Preserves Continuation's name.

# Jetpack Compose (R8 usually handles this well, but some common rules if needed)
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <fields>;
}
-keepclassmembers @androidx.compose.ui.tooling.preview.Preview class * {
    <fields>;
    <init>(...);
}
-keepclassmembers class * extends androidx.compose.ui.tooling.preview.PreviewParameterProvider {
    public <init>(...);
    public static <fields>; # Keep static fields if any are used by previews
}
-keepclass class androidx.compose.runtime. tapahtumaluokka.RecomposerKt { *; } # Specific for Compose runtime in some cases


# Hilt
-dontwarn dagger.hilt.**
-keepnames class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ActivityComponentManager.ActivityComponentBuilder { *; }
-keep class * extends dagger.hilt.android.internal.managers.BroadcastReceiverComponentManager.BroadcastReceiverComponentBuilder { *; }
-keep class * extends dagger.hilt.android.internal.managers.FragmentComponentManager.FragmentComponentBuilder { *; }
-keep class * extends dagger.hilt.android.internal.managers.ServiceComponentManager.ServiceComponentBuilder { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager.ViewComponentBuilder { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewWithFragmentComponentManager.ViewWithFragmentComponentBuilder { *; }
-keep class * implements dagger.hilt.InstallIn { *; }
-keep @dagger.hilt.InstallIn class * { *; }
-keep class * { @dagger.hilt.android.AndroidEntryPoint *; }
-keep class * { @dagger.hilt.android.HiltAndroidApp *; }
-keep class * { @dagger.Module *; }
-keep class * { @dagger.Provides *; }
-keep class * { @javax.inject.Inject *; }
-keep class * { @javax.inject.Singleton *; }


# Retrofit / OkHttp / Moshi
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn com.squareup.moshi.**

# Keep model classes used by Retrofit/Moshi
-keep class com.example.cryptotracker.data.model.** { *; }
# Keep custom Moshi adapters
-keep class com.example.cryptotracker.di.NetworkModule$PricePointListAdapter { *; }
-keep class com.example.cryptotracker.di.NetworkModule$PricePointListJsonAdapterFactory { *; }
# Ensure Moshi's reflective adapter factory can find constructors if reflection is used
-keepclassmembers class * {
    @com.squareup.moshi.Json <fields>;
    <init>(...); # Keep constructors for Moshi's reflection
}


# Room
-keep class androidx.room.RoomDatabase { *; }
-keep class androidx.room.RoomOpenHelper { *; }
-keep class * extends androidx.room.RoomDatabase
-keepclassmembers class * extends androidx.room.RoomDatabase {
    java.util.Map getRequiredTypeConverters();
}
-keep @androidx.room.Entity class * { *; } # Keep entities
-keep @androidx.room.Dao class * { *; } # Keep DAOs
-keep @androidx.room.TypeConverter class * { *; } # Keep TypeConverters
-keep @androidx.room.Database class * { *; } # Keep Database classes


# Coil
-dontwarn coil.**
# Coil uses OkHttp; rules for OkHttp should cover its networking.
# If using GIFs or SVGs, specific rules might be needed if those decoders are not kept.
# Keep R8 rules from Coil documentation if issues arise:
# https://coil-kt.github.io/coil/r8/


# YCharts (co.yml:ycharts)
# No specific Proguard rules mentioned in its documentation.
# Keep data classes used by charts if they are not already covered by model rules.
-keep class co.yml.charts.common.model.** { *; }
-keep class co.yml.charts.ui.linechart.model.** { *; } # Keep line chart models explicitly


# General Android / Java
-keepattributes Signature # Keep generic signatures
-keepattributes *Annotation* # Keep annotations
-keepattributes InnerClasses # Keep inner classes information

# Keep parcelable creators
-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# Keep enums (if any are serialized/deserialized by name, or used by reflection)
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# For libraries that use reflection on names (like some JSON libs if not using code gen)
-keepnames class com.example.cryptotracker.data.model.**

# If you use @SerializedName with Retrofit/Gson (not Moshi's @Json), you might need:
# -keepclassmembers class * {
# @com.google.gson.annotations.SerializedName <fields>;
# }
# -keep public class * extends com.google.gson.TypeAdapter


# AndroidX Core specific rules (usually covered by AGP, but can be explicit)
-keep class androidx.core.os.HandlerCompat { *; }

# Fix for java.lang.NoSuchMethodError: java.lang.Class.getNestHost
# https://github.com/square/okhttp/issues/7467
-dontwarn java.lang.ClassValue
