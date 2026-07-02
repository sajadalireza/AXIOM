package com.axiom.app.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

object LocalizationUtils {
    private fun cleanKey(input: String): String {
        return input.lowercase().trim()
            .replace("&", "and")
            .replace("-", "_")
            .replace("/", "_")
            .replace("(", "")
            .replace(")", "")
            .replace("  ", " ")
            .replace(" ", "_")
            .replace("__", "_")
    }

    fun getLocalizedSkillName(name: String, context: Context): String {
        val cleanName = cleanKey(name)
        val resId = context.resources.getIdentifier("skill_$cleanName", "string", context.packageName)
        return if (resId != 0) context.getString(resId) else name
    }

    fun getLocalizedCategoryName(category: String, context: Context): String {
        val cleanCat = cleanKey(category)
        val resId = context.resources.getIdentifier("category_$cleanCat", "string", context.packageName)
        return if (resId != 0) context.getString(resId) else category
    }
}

@Composable
fun getLocalizedSkillName(name: String): String {
    return LocalizationUtils.getLocalizedSkillName(name, LocalContext.current)
}

@Composable
fun getLocalizedCategoryName(category: String): String {
    return LocalizationUtils.getLocalizedCategoryName(category, LocalContext.current)
}
