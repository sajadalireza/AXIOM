import java.security.MessageDigest
import java.util.Base64

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.devtools.ksp)
}

apply(plugin = "com.google.dagger.hilt.android")

fun Project.getEnvValue(key: String): String {
    val envValue = providers.environmentVariable(key).orNull
    if (!envValue.isNullOrBlank()) {
        println("getEnvValue: Found $key in environment variables.")
        return envValue
    }
    
    val envFile = rootProject.file(".env")
    if (envFile.exists()) {
        val lines = envFile.readLines()
        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isNotEmpty() && !trimmed.startsWith("#")) {
                val parts = trimmed.split("=", limit = 2)
                if (parts.size == 2 && parts[0].trim() == key) {
                    val value = parts[1].trim().removeSurrounding("\"").removeSurrounding("'")
                    println("getEnvValue: Found $key in .env file.")
                    return value
                }
            }
        }
    }

    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        val lines = localPropertiesFile.readLines()
        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isNotEmpty() && !trimmed.startsWith("#")) {
                val parts = trimmed.split("=", limit = 2)
                if (parts.size == 2 && parts[0].trim() == key) {
                    val value = parts[1].trim().removeSurrounding("\"").removeSurrounding("'")
                    println("getEnvValue: Found $key in local.properties file.")
                    return value
                }
            }
        }
    }

    println("getEnvValue: Key $key could not be found anywhere!")
    return ""
}

/**
 * WP-104 SEC-104-002 — fail-closed classifier for the Supabase client credential.
 * Mirrors com.axiom.app.core.security.SupabaseKeyPolicy (build scripts can't import app
 * classes). Only PUBLIC keys may be baked into an APK. Never prints the key value.
 */
fun classifySupabaseClientKey(raw: String): String {
    val key = raw.trim()
    if (key.isEmpty()) return "EMPTY"
    if (key.startsWith("sb_publishable_")) return "PUBLISHABLE"
    if (key.startsWith("sb_secret_")) return "SECRET"
    val parts = key.split(".")
    if (parts.size == 3) {
        val role = try {
            val seg = parts[1]
            val pad = when (seg.length % 4) { 2 -> "$seg=="; 3 -> "$seg="; else -> seg }
            val json = String(Base64.getUrlDecoder().decode(pad))
            Regex("\"role\"\\s*:\\s*\"([^\"]+)\"").find(json)?.groupValues?.get(1)
        } catch (e: Exception) { null }
        return when (role) {
            "anon" -> "LEGACY_ANON"
            "service_role" -> "SERVICE_ROLE"
            else -> "UNKNOWN"
        }
    }
    return "MALFORMED"
}

fun Project.assertSupabaseClientKeySafe(): String {
    val cls = classifySupabaseClientKey(getEnvValue("SUPABASE_KEY"))
    val safe = cls == "EMPTY" || cls == "PUBLISHABLE" || cls == "LEGACY_ANON"
    if (!safe) {
        throw GradleException(
            "AXIOM WP-104: unsafe Supabase client key rejected (class=$cls). " +
                "Only an 'anon' JWT or 'sb_publishable_' key may ship in an Android build; " +
                "'sb_secret_'/'service_role'/unknown keys are forbidden. Key value not shown."
        )
    }
    return cls
}


android {
    namespace = "com.axiom.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.axiom.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 3
        versionName = "1.2.0"

        // WP-104 SEC-104-002: fail the build if an elevated/unsafe Supabase key is supplied.
        val supabaseKeyClass = project.assertSupabaseClientKeySafe()
        println("SUPABASE_KEY client-safety class: $supabaseKeyClass")
        buildConfigField("String", "SUPABASE_URL", "\"${project.getEnvValue("SUPABASE_URL")}\"")
        buildConfigField("String", "SUPABASE_KEY", "\"${project.getEnvValue("SUPABASE_KEY")}\"")
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"${project.getEnvValue("GOOGLE_WEB_CLIENT_ID")}\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    val hasReleaseKeystore = getEnvValue("KEYSTORE_PATH").let { it.isNotBlank() && file(it).exists() }

    signingConfigs {
        create("release") {
            if (hasReleaseKeystore) {
                storeFile = file(getEnvValue("KEYSTORE_PATH"))
                storePassword = getEnvValue("KEYSTORE_PASSWORD")
                keyAlias = getEnvValue("KEY_ALIAS")
                keyPassword = getEnvValue("KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            // Never fall back to debug signing: a release build without a valid
            // production keystore must fail (enforced by the taskGraph check
            // below) instead of silently shipping a debug-signed APK.
            signingConfig = if (hasReleaseKeystore) signingConfigs.getByName("release") else null

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    gradle.taskGraph.whenReady {
        val wantsRelease = allTasks.any {
            (it.name.startsWith("assemble") || it.name.startsWith("bundle")) && it.name.contains("Release")
        }
        if (wantsRelease && !hasReleaseKeystore) {
            throw GradleException(
                "AXIOM: Release build requested but no valid KEYSTORE_PATH found — " +
                    "refusing to fall back to debug signing. " +
                    "Set KEYSTORE_PATH/KEYSTORE_PASSWORD/KEY_ALIAS/KEY_PASSWORD."
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.coil.compose)

    // Vico Charting
    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m3)
    implementation(libs.vico.core)

    implementation(libs.androidx.datastore.preferences)
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0") // ← update to latest when available for gemini-3.5-flash

    // Networking & Supabase
    implementation(libs.retrofit)
    implementation(libs.converter.moshi)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.moshi.kotlin)
    implementation(libs.play.services.auth)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.hilt:hilt-work:1.1.0")
    ksp("androidx.hilt:hilt-compiler:1.1.0")

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.sqlite.jdbc)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.room.testing)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

val vendoredFontChecksums = mapOf(
    "src/main/res/font/fira_code_medium.ttf" to "97091f90623661fb4f7979c10d188f30f4806d8ce326b0bc8d1acc79dcc20d8f",
    "src/main/res/font/fira_code_regular.ttf" to "5992ab9640e2df491b2f609467b1de60e8bc39b2c28db184342a0592d98f6117",
    "src/main/res/font/fraunces_italic_variable.ttf" to "b24448c43702fac4ee856781d461a0dfba8d8e594b6e8e190234b75fed2c0e01",
    "src/main/res/font/fraunces_variable.ttf" to "177ff6c0f14e5550a3c624247cd1189611d4eb65d000b14944c63d967958abbb",
    "src/main/res/font/outfit_variable.ttf" to "fc7287273e66929776e2ba54f144fe699080bec29f61bf649d70d871468aeade"
)

tasks.register("verifyVendoredFonts") {
    group = "verification"
    description = "Fails when a required vendored font is missing or differs from its pinned SHA-256."
    inputs.files(vendoredFontChecksums.keys.map { file(it) })

    doLast {
        vendoredFontChecksums.forEach { (path, expectedChecksum) ->
            val fontFile = file(path)
            check(fontFile.isFile) { "Missing vendored font: $path" }

            val digest = MessageDigest.getInstance("SHA-256")
            fontFile.inputStream().use { input ->
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                while (true) {
                    val bytesRead = input.read(buffer)
                    if (bytesRead < 0) break
                    digest.update(buffer, 0, bytesRead)
                }
            }
            val actualChecksum = digest.digest().joinToString("") { "%02x".format(it) }
            check(actualChecksum == expectedChecksum) {
                "Vendored font checksum mismatch for $path: expected $expectedChecksum, got $actualChecksum"
            }
        }
    }
}

tasks.matching { it.name == "preBuild" }.configureEach {
    dependsOn("verifyVendoredFonts")
}

tasks.register("renamePackages") {
    doLast {
        val rootDir = file("src/main/java")
        var count = 0
        rootDir.walkTopDown().forEach { file ->
            if (file.isFile && file.extension == "kt") {
                val content = file.readText()
                var newContent = content
                var updated = false
                if (newContent.contains("package com.example")) {
                    newContent = newContent.replace("package com.example", "package com.axiom.app")
                    updated = true
                }
                if (newContent.contains("com.example.")) {
                    newContent = newContent.replace("com.example.", "com.axiom.app.")
                    updated = true
                }
                if (updated) {
                    file.writeText(newContent)
                    println("Renamed in: ${file.path}")
                    count++
                }
            }
        }
        println("SUCCESS: Renamed packages/imports in $count files!")
    }
}

tasks.register("rebrandStrings") {
    doLast {
        val engFile = file("src/main/res/values/strings.xml")
        val faFile = file("src/main/res/values-fa/strings.xml")
        
        fun rebrandEng(value: String): String {
            var v = value
            v = v.replace("AXIOM", "WARRIOR")
            v = v.replace("Axiom", "Warrior")
            v = v.replace("axiom", "warrior")
            v = v.replace("Hunters", "Warriors")
            v = v.replace("Hunter", "Warrior")
            v = v.replace("hunters", "warriors")
            v = v.replace("hunter", "warrior")
            v = v.replace("[ SYSTEM ]", "[ COMMAND ]")
            v = v.replace("SYSTEM", "COMMAND")
            v = v.replace("the System", "the Command")
            v = v.replace("the system", "the command")
            v = v.replace("System", "Command")
            v = v.replace("system", "command")
            v = v.replace("Dungeons", "Campaigns")
            v = v.replace("Dungeon", "Campaign")
            v = v.replace("dungeons", "campaigns")
            v = v.replace("dungeon", "campaign")
            v = v.replace("Shadow Army", "Reserve Corps")
            v = v.replace("shadow army", "reserve corps")
            v = v.replace("Shadows", "Reserves")
            v = v.replace("Shadow", "Reserve")
            v = v.replace("shadows", "reserves")
            v = v.replace("shadow", "reserve")
            v = v.replace("Bosses", "Checkpoints")
            v = v.replace("Boss", "Checkpoint")
            v = v.replace("bosses", "checkpoints")
            v = v.replace("boss", "checkpoint")
            v = v.replace("E-Class Recruit", "E-Class Recruit")
            v = v.replace("E-Rank", "E-Class Recruit")
            v = v.replace("D-Rank", "D-Class Recruit")
            v = v.replace("C-Rank", "C-Class Specialist")
            v = v.replace("B-Rank", "B-Class Specialist")
            v = v.replace("A-Rank", "A-Class Operative")
            v = v.replace("S-Rank", "S-Class Operative")
            return v
        }
        
        fun rebrandFa(value: String): String {
            var v = value
            v = v.replace("اکسیوم", "WARRIOR")
            v = v.replace("شکارچیان", "وریرها")
            v = v.replace("شکارچیانی", "وریرهایی")
            v = v.replace("شکارچی‌گری", "وریر‌گری")
            v = v.replace("شکارچی", "وریر")
            v = v.replace("هانترها", "وریرها")
            v = v.replace("هانتر", "وریر")
            v = v.replace("[ SYSTEM ]", "[ COMMAND ]")
            v = v.replace("سیستم", "فرماندهی")
            v = v.replace("دانجن‌ها", "کمپین‌ها")
            v = v.replace("دانجن", "کمپین")
            v = v.replace("دروازه‌ها", "کمپین‌ها")
            v = v.replace("دروازه", "کمپین")
            v = v.replace("درگاه‌ها", "کمپین‌ها")
            v = v.replace("درگاه", "کمپین")
            v = v.replace("نبرد غول‌آخر", "نبرد چک‌پوینت")
            v = v.replace("نبرد بأس", "نبرد چک‌پوینت")
            v = v.replace("ارتش سایه‌ها", "سپاه ذخیره")
            v = v.replace("ارتش سایه‌", "سپاه ذخیره")
            v = v.replace("سپاه سایه‌ها", "سپاه ذخیره")
            v = v.replace("سایه‌های", "نیروهای ذخیره")
            v = v.replace("سایه‌ها", "رزروها")
            v = v.replace("سایه‌ی", "رزرو")
            v = v.replace("سایه", "رزرو")
            v = v.replace("باس‌ها", "چک‌پوینت‌ها")
            v = v.replace("باس", "چک‌پوینت")
            v = v.replace("رئیس‌ها", "چک‌پوینت‌ها")
            v = v.replace("رئیس", "چک‌پوینت")
            v = v.replace("غول آخر", "چک‌پوینت")
            v = v.replace("غول‌آخر", "چک‌پوینت")
            v = v.replace("رتبه E", "مأمور درجه E")
            v = v.replace("رتبه D", "مأمور درجه D")
            v = v.replace("رتبه C", "کمک‌عملیاتگر درجه C")
            v = v.replace("رتبه B", "کمک‌عملیاتگر درجه B")
            v = v.replace("رتبه A", "عملیاتگر درجه A")
            v = v.replace("رتبه S", "عملیاتگر درجه S")
            return v
        }
        
        fun processFile(file: File, isFa: Boolean): Int {
            val text = file.readText()
            val regex = "(<string name=\"[^\"]+\">)([^<]*)(</string>)".toRegex()
            var count = 0
            val newText = regex.replace(text) { match ->
                val prefix = match.groupValues[1]
                val value = match.groupValues[2]
                val suffix = match.groupValues[3]
                val newValue = if (isFa) rebrandFa(value) else rebrandEng(value)
                if (newValue != value) {
                    count++
                    prefix + newValue + suffix
                } else {
                    match.value
                }
            }
            file.writeText(newText)
            return count
        }
        
        val engCount = processFile(engFile, false)
        val faCount = processFile(faFile, true)
        println("REBRAND COMPLETED: Eng changed $engCount, Fa changed $faCount")
    }
}
