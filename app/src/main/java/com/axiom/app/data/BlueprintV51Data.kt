package com.axiom.app.data

import com.axiom.app.domain.model.*

object BlueprintV51Data {
    const val DRIVING_THESIS = "I will build a high-impact life focused on my chosen domain, achieving mastery, securing financial independent milestones, and establishing a daily discipline that supports growth."

    const val RARE_PROFILE_DESCRIPTION = "A balanced profile combining high-leverage professional skills, deep physical capability, and relentless daily execution toward long-term targets."

    val PILLARS = listOf(
        Track(
            id = "career",
            name = "Career",
            color = 0xFF3182CE,
            icon = "work",
            description = "Professional growth, building assets, mastering skills, and high-impact work."
        ),
        Track(
            id = "finance",
            name = "Finance",
            color = 0xFFD4A843,
            icon = "payments",
            description = "Financial independence, cash flow generation, and smart capital allocation."
        ),
        Track(
            id = "health",
            name = "Health",
            color = 0xFF48BB78,
            icon = "favorite",
            description = "Physical optimization, clean nutrition, strength training, and robust sleep hygiene."
        ),
        Track(
            id = "relationships",
            name = "Relationships",
            color = 0xFFE53E3E,
            icon = "groups",
            description = "Fostering deep familial foundations, elite peer networks, and supportive alliances."
        )
    )

    val CORE_TRACKS = listOf(
        Track(
            id = "capability",
            name = "Capability",
            color = 0xFF3182CE,
            icon = "psychology",
            description = "Daily skill acquisition and engineering excellence."
        ),
        Track(
            id = "commercial_intelligence",
            name = "Commercial Intelligence",
            color = 0xFFD4A843,
            icon = "trending_up",
            description = "Value creation, marketing, consulting, and building scalable revenue models."
        )
    )

    val SCHEDULE_BLOCKS = listOf(
        ScheduleBlock(
            id = "sb_01",
            trackId = null,
            startTime = "06:30",
            title = "Wake + Reset",
            actionDescription = "Cold water · 10 min stretch · No phone · Write today's 3 outcomes on paper",
            tag = "Foundation",
            recurrence = "DAILY",
            isNonNegotiable = false
        ),
        ScheduleBlock(
            id = "sb_02",
            trackId = "capability",
            startTime = "07:30",
            title = "★ Deep Focus Block",
            actionDescription = "Deep focused work on main project. High concentration, zero notifications.",
            tag = "Critical",
            recurrence = "DAILY",
            isNonNegotiable = true
        ),
        ScheduleBlock(
            id = "sb_03",
            trackId = "health",
            startTime = "17:00",
            title = "Exercise — Non-Negotiable",
            actionDescription = "45 min minimum · Gym / Running / Bodyweight · Keep physical peak.",
            tag = "Health",
            recurrence = "DAILY",
            isNonNegotiable = true
        ),
        ScheduleBlock(
            id = "sb_04",
            trackId = "commercial_intelligence",
            startTime = "18:00",
            title = "★ High-Leverage Output",
            actionDescription = "Write documentation, push code commits, complete outbound communications.",
            tag = "Wealth Engine",
            recurrence = "DAILY",
            isNonNegotiable = false
        ),
        ScheduleBlock(
            id = "sb_05",
            trackId = null,
            startTime = "22:00",
            title = "Wind Down → Sleep 23:00",
            actionDescription = "No work · Write tomorrow's outcomes · 7.5 hrs minimum sleep.",
            tag = "Protected",
            recurrence = "DAILY",
            isNonNegotiable = false
        )
    )

    val CUSTOM_KPIS = listOf(
        CustomKPI(
            id = "kpi_focus_hours",
            trackId = "capability",
            name = "Hours of Deep Focus",
            targetValue = 4.0f,
            targetUnit = "hours/day",
            measurementHint = "Track via study session or stopwatch",
            redFlagAction = "Secure morning slot. Eliminate all distraction vectors."
        ),
        CustomKPI(
            id = "kpi_exercise",
            trackId = "health",
            name = "Exercise sessions",
            targetValue = 5.0f,
            targetUnit = "sessions/week",
            measurementHint = "Gym checklist",
            redFlagAction = "Body health dictates mind performance. Get moving."
        ),
        CustomKPI(
            id = "kpi_sleep",
            trackId = "health",
            name = "Sleep Target",
            targetValue = 7.5f,
            targetUnit = "hrs/night",
            measurementHint = "Sleep cycle metric",
            redFlagAction = "Sleep is non-negotiable. Turn off screens at 22:00."
        ),
        CustomKPI(
            id = "kpi_output_commits",
            trackId = "commercial_intelligence",
            name = "High-leverage commits/deliverables",
            targetValue = 3.0f,
            targetUnit = "per week",
            measurementHint = "Finished public artifacts",
            redFlagAction = "Fewer talks, more ships. Push changes regularly."
        )
    )

    val IRON_RULES = listOf(
        IronRule("rule_01", 1, "Project gets first 2 hours every day. Before email. Before messages.", true, LinkedSignalType.FIRST_TWO_HOURS),
        IronRule("rule_02", 2, "7.5 hours sleep. Always. Focus drops dramatically with fatigue.", true, LinkedSignalType.SLEEP_TARGET),
        IronRule("rule_03", 3, "Ship high-impact work weekly. Outlines, code commits, or content publishing.", true, LinkedSignalType.CUSTOM_KPI, "kpi_output_commits"),
        IronRule("rule_04", 4, "Define clear output before every work session. Never work aimlessly.", false, LinkedSignalType.NONE),
        IronRule("rule_05", 5, "Friday review. No exceptions. 60 minutes. Audit outputs and diagnostics.", true, LinkedSignalType.NONE)
    )

    val HARD_TRUTHS = listOf(
        HardTruthOrAffirmation("truth_01", CalibrationType.TRUTH, "Your current trajectory is exactly equal to your system design.", 1),
        HardTruthOrAffirmation("truth_02", CalibrationType.TRUTH, "Months 1–3 feel like nothing is working. 80% quit here. Execute with absolute discipline.", 2),
        HardTruthOrAffirmation("truth_03", CalibrationType.TRUTH, "Excuses are lies told to cushion structural failures.", 3)
    )

    val AFFIRMATIONS = listOf(
        HardTruthOrAffirmation("affirmation_01", CalibrationType.AFFIRMATION, "I control my actions, reactions, and focus entirely.", 1),
        HardTruthOrAffirmation("affirmation_02", CalibrationType.AFFIRMATION, "The silence in months 1–3 is not evidence the plan is failing. I check the diagnostic on Friday.", 2),
        HardTruthOrAffirmation("affirmation_03", CalibrationType.AFFIRMATION, "The probability of high success is near-zero for those who never start. I execute daily.", 3)
    )

    val FINANCIAL_CHECKPOINTS = listOf(
        FinancialCheckpoint("fc_m3", 3, 100f, "$"),
        FinancialCheckpoint("fc_m6", 6, 1000f, "$"),
        FinancialCheckpoint("fc_m9", 9, 2500f, "$"),
        FinancialCheckpoint("fc_m12", 12, 5000f, "$")
    )

    val WEALTH_TIMELINE = listOf(
        MonthlyIncomeEntry("wi_y1", 12, 1000f),
        MonthlyIncomeEntry("wi_y2", 24, 3000f),
        MonthlyIncomeEntry("wi_y3", 36, 6000f)
    )

    // Thesis Templates by Domain
    val THESIS_TEMPLATES = mapOf(
        "career" to listOf(
            "I will become an industry-leading software architect, shipping high-impact code, mentoring high-performing teams, and building robust cloud systems.",
            "I will establish a high-leverage independent consulting business, securing international clients, and solving high-value corporate bottlenecks.",
            "I will excel in scientific research and academia, publishing breakthrough papers, securing funding, and collaborating with international labs."
        ),
        "finance" to listOf(
            "I will achieve complete financial independence, building multiple streams of passive cash flow, and master local asset preservation.",
            "I will scale my startup venture to sustainable profitability, securing angel investment, and achieving a successful exit within 5 years.",
            "I will master wealth-building through disciplined investment strategies, high-yielding portfolios, and strict expense management."
        ),
        "health" to listOf(
            "I will optimize my physical performance, mastering clean nutrition, high-intensity functional training, and perfect sleep hygiene.",
            "I will run a marathon, building ultimate metabolic endurance, cardiovascular strength, and absolute daily focus.",
            "I will achieve full mind-body calibration, combining daily mobility routines, weight management, and robust physical resilience."
        ),
        "relationships" to listOf(
            "I will build a deep, supportive family foundation, protecting quality quality time, and fostering growth in all loved ones.",
            "I will cultivate an elite professional network, surrounding myself with world-class mentors, ambitious peers, and collaborative allies.",
            "I will serve my local and global community, organizing high-impact initiatives, and mentoring the next generation of builders."
        )
    )
}
