package com.axiom.app.ui

object SystemMessages {
    fun missionComplete(rarity: String, isFa: Boolean = java.util.Locale.getDefault().language == "fa"): String {
        return when (rarity.uppercase()) {
            "LEGENDARY" -> if (isFa) LEGENDARY_COMPLETE_FA else LEGENDARY_COMPLETE
            "EPIC"      -> if (isFa) EPIC_COMPLETE_FA else EPIC_COMPLETE
            "RARE"      -> if (isFa) RARE_COMPLETE_FA else RARE_COMPLETE
            "UNCOMMON"  -> if (isFa) UNCOMMON_COMPLETE_FA else UNCOMMON_COMPLETE
            else        -> if (isFa) COMMON_COMPLETE_FA else COMMON_COMPLETE
        }.random()
    }

    private val COMMON_COMPLETE = listOf(
        "Protocol resolved. Efficiency logged.",
        "Objective cleared. Data archived.",
        "Task execution confirmed. Move to next.",
        "Directive fulfilled. System updated.",
        "Output recorded. Hunter efficiency: nominal.",
        "Compliance registered. Continue."
    )
    private val COMMON_COMPLETE_FA = listOf(
        "پروتکل برطرف شد. بهره‌وری ثبت شد.",
        "هدف پاکسازی شد. داده‌ها بایگانی شدند.",
        "اجرای وظیفه تایید شد. حرکت به مرحله بعد.",
        "دستورالعمل انجام شد. سیستم بروزرسانی شد.",
        "خروجی ثبت شد. کارایی هانتر: اسمی.",
        "فرمانبری ثبت شد. ادامه دهید."
    )

    private val UNCOMMON_COMPLETE = listOf(
        "Uncommon protocol completed. Adaptation noted.",
        "Above-average output detected. Metrics updated.",
        "Proficiency confirmed. Skill trajectory: positive.",
        "Non-standard objective resolved. Performance logged.",
        "Efficiency above baseline. Keep the cadence.",
        "Protocol clear. Rare behavioral pattern observed."
    )
    private val UNCOMMON_COMPLETE_FA = listOf(
        "پروتکل غیرمعمول تکمیل شد. هماهنگی ثبت شد.",
        "خروجی بالاتر از حد متوسط شناسایی شد. متریک‌ها بروز شدند.",
        "مهارت تأیید شد. مسیر مهارت: مثبت.",
        "هدف غیر استاندارد حل شد. عملکرد ثبت شد.",
        "کارایی بالاتر از حد پایه. ریتم را حفظ کنید.",
        "پروتکل پاکسازی شد. الگوی رفتاری کمیاب مشاهده شد."
    )

    private val RARE_COMPLETE = listOf(
        "Rare protocol resolved. Hunter capability expanding.",
        "High-difficulty objective cleared. Impressive execution.",
        "Rare mission archived. This is not forgotten.",
        "Exceptional effort logged. Trajectory: ascending.",
        "Rare protocol complete. Shadow Army takes note.",
        "Difficulty tier exceeded. System recalibrating."
    )
    private val RARE_COMPLETE_FA = listOf(
        "پروتکل کمیاب حل شد. قابلیت‌های هانتر در حال گسترش است.",
        "هدف با سختی بالا پاکسازی شد. اجرای چشمگیر.",
        "مأموریت کمیاب بایگانی شد. این فراموش نخواهد شد.",
        "تلاش استثنایی ثبت شد. مسیر تکاملی: صعودی.",
        "پروتکل کمیاب تکمیل شد. سپاه سایه توجه می‌کند.",
        "پله سختی رد شد. سیستم در حال کالیبره‌سازی مجدد."
    )

    private val EPIC_COMPLETE = listOf(
        "Epic protocol resolved. You are becoming something else.",
        "High-tier objective cleared. The system acknowledges.",
        "Epic directive complete. Power ceiling rising.",
        "This level of output is registered as extraordinary.",
        "Epic mission archived. Hunter classification: evolving.",
        "Rare human behavior detected: follow-through at scale."
    )
    private val EPIC_COMPLETE_FA = listOf(
        "پروتکل حماسی حل شد. شما در حال تبدیل شدن به چیز دیگری هستید.",
        "هدف سطح بالا پاکسازی شد. سیستم تایید می‌کند.",
        "دستور حماسی کامل شد. سقف قدرت در حال افزایش است.",
        "این سطح از خروجی به عنوان فوق‌العاده ثبت شده است.",
        "مأموریت حماسی بایگانی شد. طبقه‌بندی هانتر: در حال تکامل.",
        "رفتار انسانی کمیاب شناسایی شد: پیگیری در مقیاس بالا."
    )

    private val LEGENDARY_COMPLETE = listOf(
        "LEGENDARY PROTOCOL RESOLVED. Hunter, you are the system now.",
        "Mythic objective archived. This will not be forgotten.",
        "LEGENDARY directive complete. Power tier: undefined.",
        "Extraordinary output recorded. Your streak is your most valuable asset.",
        "LEGENDARY classification confirmed. Reality updated.",
        "This mission was not supposed to be possible. It was."
    )
    private val LEGENDARY_COMPLETE_FA = listOf(
        "پروتکل افسانه‌ای حل شد. هانتر، اکنون شما خود سیستم هستید.",
        "هدف اسطوره‌ای بایگانی شد. این فراموش نخواهد شد.",
        "دستور کار افسانه‌ای تکمیل شد. رتبه قدرت: نامشخص.",
        "خروجی فوق‌العاده ضبط شد. رشته متوالی باارزش‌ترین دارایی شماست.",
        "طبقه‌بندی افسانه‌ای تایید شد. واقعیت بروزرسانی شد.",
        "این مأموریت غیرممکن انگاشته می‌شد، اما انجام شد."
    )
}
