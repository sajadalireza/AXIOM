// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.kotlin.android) apply false
  alias(libs.plugins.kotlin.compose) apply false
  alias(libs.plugins.google.devtools.ksp) apply false
  alias(libs.plugins.hilt) apply false
  alias(libs.plugins.roborazzi) apply false
  // alias(libs.plugins.secrets) apply false
}

tasks.register("diagnoseSupabase") {
    notCompatibleWithConfigurationCache("Only for manual diagnostics")
    doLast {
        println("====================================================")
        println("           SUPABASE CONNECTION DIAGNOSTIC           ")
        println("====================================================")
        
        var url = System.getenv("SUPABASE_URL") ?: ""
        var key = System.getenv("SUPABASE_KEY") ?: ""
        
        val envFile = file(".env")
        if (envFile.exists()) {
            envFile.readLines().forEach { line ->
                val trimmed = line.trim()
                if (trimmed.isNotEmpty() && !trimmed.startsWith("#")) {
                    val parts = trimmed.split("=", limit = 2)
                    if (parts.size == 2) {
                        val k = parts[0].trim()
                        val v = parts[1].trim().removeSurrounding("\"").removeSurrounding("'")
                        if (k == "SUPABASE_URL") url = v
                        if (k == "SUPABASE_KEY") key = v
                    }
                }
            }
        }
        
        println("Raw SUPABASE_URL: '$url'")
        // WP-104 SEC-104-002/004: never print the key (even partially). Show only a
        // non-reversible SHA-256 fingerprint prefix + length so diagnostics stay safe.
        val visibleKey = if (key.isNotEmpty()) {
            val fp = java.security.MessageDigest.getInstance("SHA-256")
                .digest(key.toByteArray()).joinToString("") { "%02x".format(it) }.take(12)
            "set (len=${key.length}, sha256=$fp…)"
        } else {
            "not set"
        }
        println("SUPABASE_KEY: $visibleKey")
        
        if (url.isEmpty()) {
            println("❌ ERROR: SUPABASE_URL is not set!")
            println("Please define it in the Secrets panel in AI Studio or in your .env file.")
            return@doLast
        }
        if (key.isEmpty()) {
            println("❌ ERROR: SUPABASE_KEY is not set!")
            println("Please define it in the Secrets panel in AI Studio or in your .env file.")
            return@doLast
        }
        
        // Sanitize URL
        var sanitizedUrl = url.trim().removeSurrounding("\"").removeSurrounding("'").trim()
        if (!sanitizedUrl.contains(".") && !sanitizedUrl.contains("/")) {
            sanitizedUrl = "https://$sanitizedUrl.supabase.co"
        } else if (!sanitizedUrl.startsWith("http://") && !sanitizedUrl.startsWith("https://")) {
            sanitizedUrl = "https://$sanitizedUrl"
        }
        
        val sanitizedKey = key.trim().removeSurrounding("\"").removeSurrounding("'").trim()
        
        println("Sanitized Target URL: $sanitizedUrl")
        
        // 1. Test basic endpoint ping to Supabase Rest API (just the base REST endpoint)
        testEndpoint(sanitizedUrl, sanitizedKey, "")
        
        // 2. Test the specific table 'activation_codes'
        testEndpoint(sanitizedUrl, sanitizedKey, "rest/v1/activation_codes")
        
        // 3. Test querying for a code
        testEndpoint(sanitizedUrl, sanitizedKey, "rest/v1/activation_codes?select=*&limit=1")
    }
}

fun testEndpoint(baseUrl: String, apiKey: String, path: String) {
    val fullPath = if (path.isEmpty()) baseUrl else "$baseUrl/$path"
    println("\n----------------------------------------------------")
    println("Testing Endpoint: $fullPath")
    try {
        val url = java.net.URL(fullPath)
        val conn = url.openConnection() as java.net.HttpURLConnection
        conn.requestMethod = "GET"
        conn.setRequestProperty("apikey", apiKey)
        conn.setRequestProperty("Authorization", "Bearer $apiKey")
        conn.connectTimeout = 8000
        conn.readTimeout = 8000
        
        val code = conn.responseCode
        println("HTTP Status Code: $code")
        
        val headers = conn.headerFields
        println("Response Headers:")
        headers.forEach { k, v ->
            if (k != null) {
                println("  $k: $v")
            }
        }
        
        val stream = if (code in 200..299) conn.inputStream else conn.errorStream
        if (stream != null) {
            val content = stream.bufferedReader().use { it.readText() }
            println("Response Body: $content")
        } else {
            println("No response body.")
        }
        
        if (code == 401) {
            println("👉 DIAGNOSIS (HTTP 401): Your API key is invalid or lacks access to this API gateway.")
            println("   Verify that your key is the 'anon' (or modern sb_publishable_) PUBLIC client key. Never ship an 'sb_secret_' or 'service_role' key in an Android build. Also verify it contains exactly the JWT characters from Supabase Dashboard (Settings -> API).")
        } else if (code == 403) {
            println("👉 DIAGNOSIS (HTTP 403): Unauthorized / Row Level Security (RLS) block.")
            println("   Ensure your 'activation_codes' table has policies enabled allowing SELECT/UPDATE for Anon users, or disable RLS for testing.")
        } else if (code == 404) {
            println("👉 DIAGNOSIS (HTTP 404): Resource not found.")
            println("   Check if the table 'activation_codes' exists in public database and has been correctly created.")
        } else if (code in 200..299) {
            println("🟩 SUCCESS: The request succeeded perfectly!")
        }
    } catch (e: Exception) {
        println("❌ CONNECTION ERROR: ${e.message}")
        e.printStackTrace()
    }
}

