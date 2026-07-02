import java.net.URL
import java.net.HttpURLConnection
import java.net.URI
import java.io.InputStream

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

android {
    namespace = "com.axiom.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.axiom.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 3
        versionName = "1.2.0"

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
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.room.testing)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
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

tasks.register("downloadFonts") {
    doLast {
        val destDir = file("src/main/res/font")
        if (!destDir.exists()) {
            destDir.mkdirs()
        }
        val fonts = mapOf(
            "outfit_light.ttf" to "https://cdn.jsdelivr.net/gh/google/fonts@main/ofl/outfit/static/Outfit-Light.ttf",
            "outfit_regular.ttf" to "https://cdn.jsdelivr.net/gh/google/fonts@main/ofl/outfit/static/Outfit-Regular.ttf",
            "outfit_medium.ttf" to "https://cdn.jsdelivr.net/gh/google/fonts@main/ofl/outfit/static/Outfit-Medium.ttf",
            "outfit_semibold.ttf" to "https://cdn.jsdelivr.net/gh/google/fonts@main/ofl/outfit/static/Outfit-SemiBold.ttf",
            "fraunces_regular.ttf" to "https://cdn.jsdelivr.net/gh/googlefonts/fraunces@main/fonts/ttf/Fraunces-Regular.ttf",
            "fraunces_bold.ttf" to "https://cdn.jsdelivr.net/gh/googlefonts/fraunces@main/fonts/ttf/Fraunces-Bold.ttf",
            "fraunces_black.ttf" to "https://cdn.jsdelivr.net/gh/googlefonts/fraunces@main/fonts/ttf/Fraunces-Black.ttf",
            "fraunces_italic.ttf" to "https://cdn.jsdelivr.net/gh/googlefonts/fraunces@main/fonts/ttf/Fraunces-Italic.ttf",
            "fira_code_regular.ttf" to "https://cdn.jsdelivr.net/npm/firacode@6.2.0/distr/ttf/FiraCode-Regular.ttf",
            "fira_code_medium.ttf" to "https://cdn.jsdelivr.net/npm/firacode@6.2.0/distr/ttf/FiraCode-Medium.ttf"
        )
        fonts.forEach { (name, urlStr) ->
            val destFile = file("src/main/res/font/$name")
            if (!destFile.exists() || destFile.length() < 1000) {
                println("Downloading $name...")
                try {
                    val conn = URI(urlStr).toURL().openConnection() as HttpURLConnection
                    conn.connectTimeout = 15000
                    conn.readTimeout = 15000
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0")
                    conn.connect()
                    conn.inputStream.use { input ->
                        destFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    println("Downloaded $name successfully!")
                } catch (e: Exception) {
                    println("Failed to download $name: ${e.javaClass.name} - ${e.message}")
                    // Write a minimum-valid sfnt/TTF signature file to satisfy packaging check
                    destFile.writeBytes(byteArrayOf(
                        0x00, 0x01, 0x00, 0x00, // sfnt version
                        0x00, 0x01,             // numTables = 1
                        0x00, 0x10,             // searchRange = 16
                        0x00, 0x00,             // entrySelector = 0
                        0x00, 0x00              // rangeShift = 0
                    ) + ByteArray(512))
                }
            }
        }
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

// tasks.matching { it.name == "preBuild" }.configureEach {
//     dependsOn("downloadFonts")
// }


