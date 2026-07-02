# ── Kotlin ────────────────────────────────────────────────
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod

# ── Coroutines ────────────────────────────────────────────
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# ── Hilt / Dagger ─────────────────────────────────────────
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends androidx.lifecycle.ViewModel { <init>(...); }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }
-dontwarn dagger.hilt.internal.aggregatedroot.**

# ── Room ──────────────────────────────────────────────────
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-dontwarn androidx.room.paging.**

# ── Jetpack Compose ───────────────────────────────────────
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ── Gemini AI SDK ─────────────────────────────────────────
-keep class com.google.ai.client.generativeai.** { *; }
-keep class com.google.ai.** { *; }
-dontwarn com.google.ai.**
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# ── JSON (used for AI mission parsing) ────────────────────
-keep class org.json.** { *; }

# ── DataStore ─────────────────────────────────────────────
-keep class androidx.datastore.** { *; }
-dontwarn androidx.datastore.**
# ── Retrofit ──────────────────────────────────────────────────────────────
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepattributes Exceptions
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-dontwarn retrofit2.**

# ── Moshi ─────────────────────────────────────────────────────────────────
-keep @com.squareup.moshi.JsonClass class * { *; }
-keep class com.axiom.app.data.remote.** { *; }
-keepclassmembers class * {
    @com.squareup.moshi.FromJson <methods>;
    @com.squareup.moshi.ToJson <methods>;
}
-keep class com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
-dontwarn com.squareup.moshi.**

# ── OkHttp ────────────────────────────────────────────────────────────────
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
# ── CrashReporter ─────────────────────────────────────────
-keep class com.axiom.app.core.CrashReporter { *; }
