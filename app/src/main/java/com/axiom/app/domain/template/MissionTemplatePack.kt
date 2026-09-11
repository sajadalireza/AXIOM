package com.axiom.app.domain.template

/**
 * Human-authored Mission Template Pack for the Software / Solopreneur Beachhead (§10).
 *
 * Invariant: Every template provides a clear physical Done Condition, an implementation
 * context trigger, and actionable leverage advice. No generic motivational fluff.
 */
object MissionTemplatePack {

    val TEMPLATES: List<MissionTemplate> = listOf(
        MissionTemplate(
            id = "solopreneur_customer_interview",
            beachhead = Beachhead.SOFTWARE_SOLOPRENEUR,
            category = TemplateCategory.CUSTOMER_DISCOVERY,
            titleEn = "Customer Problem Interview (1-on-1)",
            titleFa = "مصاحبه رو در رو با کاربر برای کشف مشکل",
            descriptionEn = "Conduct a focused 20-30 min discovery conversation to map real user bottlenecks and past workarounds.",
            descriptionFa = "انجام یک مصاحبه ۲۰ تا ۳۰ دقیقه‌ای متمرکز برای شناسایی دقیق گلوگاه‌ها و راه‌حل‌های قبلی کاربر.",
            doneConditionEn = "Completed 1-on-1 interview; documented 3 verbatim pain-point quotes and the user's current manual workaround.",
            doneConditionFa = "مصاحبه انجام شد؛ ۳ نقل قول مستقیم از چالش‌های اصلی و راه‌حل دستی فعلی کاربر ثبت گردید.",
            contextTriggerEn = "When scheduled calendar call alert triggers, with quiet notepad open.",
            contextTriggerFa = "هنگام به صدا درآمدن هشدار تقویم برای جلسه، با صفحه یادداشت آماده.",
            defaultDurationMinutes = 45,
            recommendedTrack = "Intelligence",
            recommendedSkillName = "Problem Discovery",
            leverageTipEn = "Never pitch your solution during a discovery interview. Ask about what they actually did last week.",
            leverageTipFa = "هرگز در حین مصاحبه کشف نیاز، محصول خود را پروموت نکنید. درباره اقدامات واقعی هفته گذشته بپرسید."
        ),
        MissionTemplate(
            id = "solopreneur_minimal_auth_slice",
            beachhead = Beachhead.SOFTWARE_SOLOPRENEUR,
            category = TemplateCategory.PRODUCT_ENGINEERING,
            titleEn = "Vertical Slice: Core Action E2E",
            titleFa = "پیاده‌سازی برش عمودی عملکرد اصلی سرتاسری",
            descriptionEn = "Implement a thin vertical spike connecting UI, domain logic, and persistence for a single user journey.",
            descriptionFa = "پیاده‌سازی یک مسیر باریک عمودی که رابط کاربری، منطق و دیتابیس را برای یک سناریو به هم متصل می‌کند.",
            doneConditionEn = "Core action executes end-to-end with unit test passing and verified in local emulator.",
            doneConditionFa = "عملکرد اصلی سرتاسر اجرا شد، تست واحد پاس شد و در ایمولاتور صحت آن تایید گردید.",
            contextTriggerEn = "At desk with IDE open, phone on Do Not Disturb.",
            contextTriggerFa = "پشت میز کار با محیط توسعه باز و تلفن همراه در حالت سکوت کامل.",
            defaultDurationMinutes = 60,
            recommendedTrack = "Build",
            recommendedSkillName = "Deep Work",
            leverageTipEn = "Avoid building wide architecture before the thin spike proves the integration contract.",
            leverageTipFa = "قبل از اثبات درستی مسیر باریک یکپارچه‌سازی، از ساخت معماری‌های گسترده خودداری کنید."
        ),
        MissionTemplate(
            id = "solopreneur_cold_outreach_sequence",
            beachhead = Beachhead.SOFTWARE_SOLOPRENEUR,
            category = TemplateCategory.DISTRIBUTION_SALES,
            titleEn = "Targeted Outreach to 5 ICP Prospects",
            titleFa = "ارسال پیام متمرکز به ۵ مخاطب ایده‌آل",
            descriptionEn = "Send 5 hyper-personalized outreach messages addressing each prospect's observed public bottleneck.",
            descriptionFa = "ارسال ۵ پیام عمیقاً شخصی‌سازی‌شده با اشاره به چالش‌های واقعی و مشاهده‌شده مخاطب.",
            doneConditionEn = "5 personalized messages sent; logged recipient profiles and customized angles in pipeline tracker.",
            doneConditionFa = "۵ پیام شخصی‌سازی‌شده ارسال شد؛ مشخصات افراد و زاویه اختصاصی پیام در ردیاب ثبت گردید.",
            contextTriggerEn = "Morning focus block with verified prospect CRM list open.",
            contextTriggerFa = "بازه تمرکز صبحگاهی با فهرست بازبینی‌شده مخاطبان هدف.",
            defaultDurationMinutes = 45,
            recommendedTrack = "Commercial",
            recommendedSkillName = "Outreach",
            leverageTipEn = "If a message could be sent to 100 people without changing words, delete it. Specificity drives replies.",
            leverageTipFa = "اگر پیامی می‌تواند بدون تغییر برای ۱۰۰ نفر ارسال شود، آن را حذف کنید. شخصی‌سازی باعث پاسخگویی است."
        ),
        MissionTemplate(
            id = "solopreneur_funnel_dropoff_audit",
            beachhead = Beachhead.SOFTWARE_SOLOPRENEUR,
            category = TemplateCategory.OPERATIONS_METRICS,
            titleEn = "Core Conversion Funnel Leak Audit",
            titleFa = "تحلیل نقاط ریزش در قیف تبدیل اصلی",
            descriptionEn = "Audit step-by-step conversion data from landing to first core action to locate the largest leak.",
            descriptionFa = "بررسی داده‌های تبدیل گام‌به‌گام از ورود به صفحه تا انجام اولین اقدام برای یافتن بیشترین ریزش.",
            doneConditionEn = "Calculated conversion drop-off percentages per step; identified the single highest drop-off screen.",
            doneConditionFa = "درصد ریزش هر مرحله محاسبه شد و صفحه‌ای که بیشترین افت کاربر را دارد مشخص گردید.",
            contextTriggerEn = "With telemetry analytics query dashboard and analysis sheet open.",
            contextTriggerFa = "با داشبورد تحلیلی داده‌ها و جدول ثبت محاسبات باز.",
            defaultDurationMinutes = 30,
            recommendedTrack = "Intelligence",
            recommendedSkillName = "Analytics",
            leverageTipEn = "Fixing a 50% leak in the middle of your funnel doubles your output without buying more traffic.",
            leverageTipFa = "رفع یک ریزش ۵۰ درصدی در میانه قیف، بدون هزینه تبلیغات اضافه خروجی شما را دوبرابر می‌کند."
        ),
        MissionTemplate(
            id = "solopreneur_smoke_test_deployment",
            beachhead = Beachhead.SOFTWARE_SOLOPRENEUR,
            category = TemplateCategory.PRODUCT_ENGINEERING,
            titleEn = "Automated Smoke Test & Deploy Sanity",
            titleFa = "اجرای تست‌های اعتبارسنجی و بررسی صحت انتشار",
            descriptionEn = "Run the full automated test suite, verify database migrations, and perform a live production sanity check.",
            descriptionFa = "اجرای تمام تست‌های خودکار، بررسی سازگاری پایگاه داده و انجام تست صحت روی نسخه نهایی.",
            doneConditionEn = "Local test suite passes 100%, remote CI green, and production smoke check verified.",
            doneConditionFa = "تمام تست‌های محلی پاس شدند، تست‌های مخزن سبز شدند و استقرار با موفقیت تایید شد.",
            contextTriggerEn = "Before tagging release commit, git working directory clean.",
            contextTriggerFa = "قبل از برچسب‌گذاری نسخه جدید، با وضعیت پاک گیت.",
            defaultDurationMinutes = 30,
            recommendedTrack = "Build",
            recommendedSkillName = "Code Quality",
            leverageTipEn = "Never ship manually what an automated contract test can verify in seconds.",
            leverageTipFa = "کاری را که یک تست خودکار در چند ثانیه بررسی می‌کند هرگز دستی انجام ندهید."
        ),
        MissionTemplate(
            id = "solopreneur_competitive_teardown",
            beachhead = Beachhead.SOFTWARE_SOLOPRENEUR,
            category = TemplateCategory.CUSTOMER_DISCOVERY,
            titleEn = "Competitor Workflow Teardown",
            titleFa = "تحلیل عمیق جریان کاری محصول رقیب",
            descriptionEn = "Deconstruct the primary competitor's onboarding flow, pricing structure, and user pain points.",
            descriptionFa = "کالبدشکافی کامل ورود کاربر، نحوه قیمت‌گذاری و نقاط ضعف اصلی محصول رقیب.",
            doneConditionEn = "Documented 3 competitor weaknesses and 1 unique advantage for your product thesis.",
            doneConditionFa = "ثبت ۳ نقطه‌ضعف رقیب و ۱ مزیت اختصاصی برای تثبیت فرضیه محصول شما.",
            contextTriggerEn = "Research block with competitor free trial active and recording tool ready.",
            contextTriggerFa = "بازه تحقیقاتی با حساب آزمایشی فعال رقیب و ابزار یادداشت‌برداری.",
            defaultDurationMinutes = 45,
            recommendedTrack = "Intelligence",
            recommendedSkillName = "Market Intelligence",
            leverageTipEn = "Search 1-star and 2-star reviews of competitors to find what customers are desperate for.",
            leverageTipFa = "نظرات ۱ و ۲ ستاره رقبا را بخوانید تا بفهمید کاربران واقعاً از چه چیزی کلافه هستند."
        ),
        MissionTemplate(
            id = "solopreneur_changelog_distribution",
            beachhead = Beachhead.SOFTWARE_SOLOPRENEUR,
            category = TemplateCategory.DISTRIBUTION_SALES,
            titleEn = "Publish Outcome-Focused Changelog",
            titleFa = "نگارش و انتشار تغییرات محصول با تمرکز بر نتیجه",
            descriptionEn = "Write and publish a customer-facing release note explaining what problem the update solves.",
            descriptionFa = "نگارش و انتشار خلاصه تغییرات محصول با توضیح شفاف اینکه چه مشکلی برای کاربر حل شده است.",
            doneConditionEn = "Changelog published to public channel or emailed to active users with 1 clear call-to-action.",
            doneConditionFa = "گزارش تغییرات در کانال عمومی یا ایمیل کاربران با یک اقدام شفاف بعدی منتشر شد.",
            contextTriggerEn = "Immediately following production release tag.",
            contextTriggerFa = "بلافاصله پس از انتشار موفقیت‌آمیز نسخه در محیط واقعی.",
            defaultDurationMinutes = 30,
            recommendedTrack = "Commercial",
            recommendedSkillName = "Product Marketing",
            leverageTipEn = "Users do not care about internal refactors. Translate every commit into a concrete benefit.",
            leverageTipFa = "کاربران به ریفکتورهای داخلی اهمیتی نمی‌دهند. هر تغییر فنی را به یک فایده ملموس ترجمه کنید."
        ),
        MissionTemplate(
            id = "solopreneur_weekly_metric_checkpoint",
            beachhead = Beachhead.SOFTWARE_SOLOPRENEUR,
            category = TemplateCategory.OPERATIONS_METRICS,
            titleEn = "Weekly Unit Economics & Velocity Check",
            titleFa = "بررسی هفتگی شاخص‌های اقتصادی و سرعت اجرا",
            descriptionEn = "Log active pipeline metrics, revenue, server costs, and total deep work hours.",
            descriptionFa = "ثبت شاخص‌های هفتگی، درآمد، هزینه‌های سرور و کل ساعات کار عمیق ثبت‌شده.",
            doneConditionEn = "Updated weekly finance/metrics dashboard; identified whether burn rate matches runway.",
            doneConditionFa = "داشبورد هفتگی مالی به‌روزرسانی شد و تطابق هزینه با بازه زمانی بقا مشخص گردید.",
            contextTriggerEn = "End-of-week review ritual with accounting & telemetry dashboards open.",
            contextTriggerFa = "در پایان هفته کاری با داشبوردهای مالی و داده‌های عملکردی باز.",
            defaultDurationMinutes = 30,
            recommendedTrack = "Intelligence",
            recommendedSkillName = "Financial Reality",
            leverageTipEn = "Facing financial numbers weekly prevents sudden runway shocks.",
            leverageTipFa = "بررسی هفتگی ارقام مالی مانع از مواجهه ناگهانی با اتمام بودجه می‌شود."
        )
    )

    fun getById(id: String): MissionTemplate? {
        return TEMPLATES.firstOrNull { it.id == id }
    }

    fun getByCategory(category: TemplateCategory): List<MissionTemplate> {
        return TEMPLATES.filter { it.category == category }
    }
}
