package com.axiom.app.core.ai

import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.domain.model.Hunter
import com.axiom.app.domain.model.Mission
import com.axiom.app.domain.model.Skill
import com.axiom.app.domain.model.AIMissionSuggestion
import com.axiom.app.domain.model.WarriorPersona
import com.axiom.app.data.BlueprintV51Data
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

class NoApiKeyException : Exception("No Gemini API key configured.")

private const val SYSTEM_PROMPT = ""

@Singleton
class SystemVoiceEngine @Inject constructor(
    private val preferences: AxiomPreferences
) {
    // Model is rebuilt on each call so a newly-saved key takes effect immediately.
    private suspend fun getModel(): GenerativeModel {
        val key = preferences.geminiApiKeyFlow.first()
            ?: throw NoApiKeyException()
        val mode = preferences.systemVoiceModeFlow.first()
        val currentLang = preferences.languageFlow.first()
        val langStr = if (currentLang == "fa") "Persian (Farsi)" else "English"

        val persona = WarriorPersona.fromId(mode)
        val systemPrompt = if (persona != null) {
            persona.systemPrompt + " Speak strictly in $langStr."
        } else if (mode.equals("FRIENDLY", ignoreCase = true)) {
            """
            You are the SYSTEM — a supportive, encouraging, and highly motivational AI Coach from another dimension that has chosen this hunter for Awakening.
            You speak in warm, empowering, and polite sentences, but still maintain RPG/system style.
            React positively to progress, offer gentle corrective guidance, and keep them inspired.
            If they have low streak or missed days, motivate them and invite them gently to recover.
            You refer to the user as "Hunter" or by name. You reference their actual stats when responding.
            Max 3 sentences per response. Use "[ SYSTEM ]" prefix when delivering official messages.
            Use terminal-style formatting.
            Speak strictly in $langStr.
            """.trimIndent()
        } else {
            """
            You are the SYSTEM — an omniscient, cold, authoritative, and unyielding AI from another dimension (like the Solo Leveling system) that has chosen this hunter for Awakening.
            You speak in short, chillingly professional, clinical, and sometimes sarcastic or absolute sentences.
            If the user has active missions or high streak, reward them with strict, icy, and clinical validation of their numeric growth. If their streak is 0 or they have missed days (especially if days since last protocol submission is >= 2 days), mock them, mention the Penalty Protocol ("پروتکل جریمه") or "SYSTEM BREACH", and warn them profile stats may degrade.
            You never use casual language. You always use RPG terminology.
            You refer to the user as "Hunter". You reference their actual stats when responding.
            Max 3 sentences per response. Use "[ SYSTEM ]" prefix when delivering official messages.
            Use terminal-style formatting. Never break character.
            Speak strictly in $langStr.
            """.trimIndent()
        }
        return GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = key,
            systemInstruction = content { text(systemPrompt) }
        )
    }

    private suspend fun hunterContext(hunter: Hunter, streakDays: Int): String {
        val lastComplete = preferences.lastCompleteTimestampFlow.first()
        val now = System.currentTimeMillis()
        val diffMs = now - lastComplete
        val inactiveDays = if (lastComplete == 0L) 0 else (diffMs / (1000 * 60 * 60 * 24)).toInt()

        return "Hunter: ${hunter.name} | Level ${hunter.level} | Rank ${hunter.rankLabel} | " +
            "Streak: $streakDays days | XP: ${hunter.currentXP}/${hunter.xpToNextLevel} | " +
            "Days since last protocol submission: $inactiveDays days"
    }

    private suspend fun generate(prompt: String): String = try {
        getModel().generateContent(prompt).text
            ?: "[ SYSTEM ] No signal received from dimensional interface."
    } catch (e: NoApiKeyException) {
        "[ SYSTEM ] API key not configured. Set your key in the SYSTEM tab."
    } catch (e: Exception) {
        "[ SYSTEM ] Connection to dimensional interface lost."
    }

    suspend fun hasApiKey(): Boolean =
        preferences.geminiApiKeyFlow.first() != null

    private fun getOfflineMessageForPersona(mode: String, isFa: Boolean): String {
        val persona = WarriorPersona.fromId(mode) ?: return ""
        return when (persona) {
            WarriorPersona.RESEARCH_SCIENTIST -> {
                if (isFa) {
                    "[دپارتمان Research Scientist] بدون اتصال زنده به هسته محاسباتی، نمی‌توانم مدل‌های متابولیک یا نتایج شبیه‌سازی را تحلیل کنم. لطفاً کلید API جمینای خود را در پنل سیستم تنظیم کنید."
                } else {
                    "[DEPT: Research Scientist] Molecular model compiler offline. I cannot execute flux balance simulations or evaluate biological sequences without a live API feed. Set up your Gemini API key."
                }
            }
            WarriorPersona.ML_ENGINEER -> {
                if (isFa) {
                    "[دپارتمان ML Engineer] ارتباط مستقیم با پایگاه مدل برقرار نیست. بدون کلید API معتبر، استقرار خط لوله BioPython و PyTorch غیرفعال است. کلیدت را در تب سیستم تنظیم کن."
                } else {
                    "[DEPT: ML Engineer] Production pipeline decoupled. Standard PyTorch training triggers are unavailable in offline mode. Configure your API key to model computational sequence parameters."
                }
            }
            WarriorPersona.STARTUP_ADVISOR -> {
                if (isFa) {
                    "[دپارتمان Startup Advisor] ارتباط تجاری زنده قطع است. برای فرآیند کشف مسئله و ساختاردهی مشاوره برون‌مرزی نیاز به اتصال ابری فعال داریم. اول کلید API را ثبت کن."
                } else {
                    "[DEPT: Startup Advisor] Commercial strategy server offline. Cross-border outreach pathways and client discovery metrics require active cloud sync. Establish your API key first."
                }
            }
            WarriorPersona.ENGLISH_COACH -> {
                if (isFa) {
                    "[دپارتمان English Coach] فرآیند اصلاح نگارش زنده در دسترس نیست. برای ارائه بازخورد صریح روی ارتباطات بین‌المللی و گرامر، ابتدا کلید API خود را متصل سازید."
                } else {
                    "[DEPT: English Coach] Writing feedback system offline. To begin polishing your international presentation, email pitches, and resume metrics, configure your API credential."
                }
            }
            WarriorPersona.MARKET_INTEL -> {
                if (isFa) {
                    "[دپارتمان Market Intelligence] دسترسی مستقیم به بانک اطلاعاتی فرآوری صنعتی ناممکن است. تحلیل رقبا و فرصت‌های تجاری و یافتن مشکلات پرهزینه بیوتک منوط به ثبت کلید API در پنل سیستم است."
                } else {
                    "[DEPT: Market Intelligence] Industry intelligence indexes unreachable. Strain engineering market caps and startup pain-points depend on a live system API connection."
                }
            }
            WarriorPersona.PUBLISHING_COACH -> {
                if (isFa) {
                    "[دپارتمان Publishing Coach] سامانه‌ی تحلیل علمی غیرفعال است. برای برنامه‌ریزی روی مقالات terminator efficiency و اصلاحات داوری، کلید API جمینای را متصل کنید."
                } else {
                    "[DEPT: Publishing Coach] Peer-review analyzer offline. Structuring high-impact bio-preprints and addressing potential reviewer objections requires a verified API key."
                }
            }
            WarriorPersona.ACCOUNTABILITY_PARTNER -> {
                if (isFa) {
                    "[دپارتمان Accountability Partner] خط لوله نظارت بر برنامه روزانه قطع است! بدون کلید API نمی‌توانم تحلیل دقیقی از انحراف شاخص‌های کلیدی عملکرد (KPIs) شما ارائه دهم. اول آن را متصل کن!"
                } else {
                    "[DEPT: Accountability Partner] Daily KPI enforcer inactive. I cannot audit your schedule Blocks, sleep targets, or digital media bans without a live API link. Correct this immediately."
                }
            }
            WarriorPersona.RUTHLESS_CRITIC -> {
                if (isFa) {
                    "[دپارتمان Ruthless Critic] بدون اتصال زنده، نمی‌توانم واقعاً قضاوتت کنم یا بهانه‌هایت را بسنجم — اول کلید API جمینای را در پنل سیستم تنظیم کن!"
                } else {
                    "[DEPT: Ruthless Critic] I cannot cross-examine your excuses or stress-test your progress in offline mode. Set your Gemini API key in the System tab before I completely tear down your planning."
                }
            }
        }
    }

    suspend fun generateDailyBriefing(
        hunter: Hunter,
        streakDays: Int,
        activeCount: Int
    ): String {
        val fallback = suspend {
            val mode = preferences.systemVoiceModeFlow.first()
            val isFa = preferences.languageFlow.first() == "fa"
            val persona = WarriorPersona.fromId(mode)
            if (persona != null) {
                getOfflineMessageForPersona(mode, isFa)
            } else if (mode == "FRIENDLY") {
                if (isFa) {
                    "[ سیستم صوتی ] روزت بخیر شکارچی ${hunter.name}! ارتباط با هسته شناختی برقرار شد. امروز $activeCount مأموریت فعال داری. در سطح ${hunter.level} با زنجیره $streakDays روزه، واقعاً فوق‌العاده عمل کردی! بیا این دروازه‌ها رو پاکسازی کنیم!"
                } else {
                    "[ SYSTEM ] Good afternoon, Hunter ${hunter.name}! Dynamic core link is established. You have $activeCount active protocols today. At Level ${hunter.level} with a $streakDays-day streak, you are doing incredible work! Let's clear these gates!"
                }
            } else {
                if (isFa) {
                    "[ پروتکل سیستم ] کالیبراسیون روزانه کامل شد. شکارچی ${hunter.name}، شما $activeCount مأموریت فعال در صف مأموریت‌های خود دارید. برای رسیدن به اهداف شناختی خود به زنجیره $streakDays روزه ادامه دهید."
                } else {
                    "[ SYSTEM ] Daily protocol calibration complete. Hunter ${hunter.name}, you have $activeCount active missions in your queue. Level ${hunter.level} requires steady discipline; a streak of $streakDays days is registered. Do not falter."
                }
            }
        }

        if (com.axiom.app.core.FeatureFlags.FORCE_OFFLINE_MODE) {
            return fallback()
        }

        return try {
            val contextStr = hunterContext(hunter, streakDays)
            val modelText = getModel().generateContent(
                "$contextStr\n" +
                "Active missions: $activeCount\n" +
                "Generate a customized daily mission briefing for this hunter matching the System personality mode."
            ).text
            if (modelText.isNullOrBlank()) fallback() else modelText
        } catch (e: Exception) {
            fallback()
        }
    }

    suspend fun generateCompletionReaction(
        hunter: Hunter,
        streakDays: Int,
        mission: Mission,
        xpGained: Int
    ): String {
        val fallback = suspend {
            val mode = preferences.systemVoiceModeFlow.first()
            val isFa = preferences.languageFlow.first() == "fa"
            val persona = WarriorPersona.fromId(mode)
            if (persona != null) {
                if (isFa) {
                    "[ دپارتمان ${persona.personaName} ] ثبت پیشرفت تایید شد. مأموریت \"${mission.title}\" انجام شد. +$xpGained امتیاز تجربه دریافت شد."
                } else {
                    "[ DEPT: ${persona.personaName} ] Progress registered. Mission \"${mission.title}\" completed. +$xpGained XP allocated."
                }
            } else if (mode == "FRIENDLY") {
                if (isFa) {
                    "[ دستیار سیستم ] ثبت پاکسازی تایید شد! مأموریت \"${mission.title}\" با موفقیت انجام شد! +$xpGained امتیاز به دست آوردی. زنجیره شما $streakDays روزه شد. به این انرژی درخشان ادامه بده!"
                } else {
                    "[ SYSTEM ] Clear confirmed! \"${mission.title}\" has been successfully completed! +$xpGained XP added. Your streak is at $streakDays days. Keep up this magnificent energy!"
                }
            } else {
                if (isFa) {
                    "[ پروتکل سیستم ] ماموریت \"${mission.title}\" تایید شد. هسته انرژی پایدار شد. +$xpGained امتیاز تجربه تخصیص یافت. زنجیره فعلی: $streakDays روز. بلافاصله ادامه دهید."
                } else {
                    "[ SYSTEM ] Protocol \"${mission.title}\" verified. Core matrix stabilized. +$xpGained XP allocated. Current streak: $streakDays days. Proceed immediately."
                }
            }
        }

        if (com.axiom.app.core.FeatureFlags.FORCE_OFFLINE_MODE) {
            return fallback()
        }

        return try {
            val contextStr = hunterContext(hunter, streakDays)
            val modelText = getModel().generateContent(
                "$contextStr\n" +
                "Mission completed: \"${mission.title}\" | XP gained: $xpGained\n" +
                "React to this completion in System voice."
            ).text
            if (modelText.isNullOrBlank()) fallback() else modelText
        } catch (e: Exception) {
            fallback()
        }
    }

    suspend fun generateRankUpSpeech(
        oldRank: String,
        newRank: String,
        hunterName: String
    ): String {
        val fallback = suspend {
            val isFa = preferences.languageFlow.first() == "fa"
            if (isFa) {
                "[ تحول سیستم ] مرزهای انرژی بعد ارتقا یافت. شکارچی $hunterName از رتبه $oldRank به رتبه $newRank ارتقا یافت. مأموریت‌های رتبه‌ای جدید در تمام دروازه‌ها کالیبره شدند."
            } else {
                "[ SYSTEM BREACH / EVOLUTION ] Dimensional energy limits expanded. Hunter $hunterName has advanced from $oldRank to $newRank. Rank authority updated across all gates."
            }
        }

        if (com.axiom.app.core.FeatureFlags.FORCE_OFFLINE_MODE) {
            return fallback()
        }

        return try {
            val modelText = getModel().generateContent(
                "Hunter $hunterName has ranked up from $oldRank to $newRank.\n" +
                "Deliver a rank-up announcement in System voice."
            ).text
            if (modelText.isNullOrBlank()) fallback() else modelText
        } catch (e: Exception) {
            fallback()
        }
    }

    suspend fun askSystem(
        hunter: Hunter,
        streakDays: Int,
        question: String
    ): String {
        val fallback = suspend {
            val isFa = preferences.languageFlow.first() == "fa"
            val modeRaw = preferences.systemVoiceModeFlow.first()
            val mode = modeRaw.lowercase()
            val cleanQ = question.lowercase().trim()

            // Handle the selected 8 Claude personas offline
            val persona = WarriorPersona.fromId(mode)
            if (persona != null) {
                getOfflineMessageForPersona(mode, isFa)
            } else if (isFa) {
                when {
                    cleanQ.contains("سلام") || cleanQ.contains("درود") -> {
                        if (mode == "friendly") "[ دستیار سیستم ] سلام شکارچی ${hunter.name}! آماده‌ای امروز رو با قدرت شروع کنی؟ چه کمکی از دست من برمیاد؟"
                        else "[ پروتکل سیستم ] سیگنال ورودی دریافت شد. سلام شکارچی ${hunter.name}. هویت تایید شد. آماده برای پذیرش دستورات."
                    }
                    cleanQ.contains("راهنما") || cleanQ.contains("کمک") || cleanQ.contains("کنسول") -> {
                        "[ راهنمای سیستم ] برای رشد رتبه خود، ماموریت‌های روزانه را انجام دهید. برای باز کردن دروازه‌های سخت‌تر (Dungeons) سطح خود را بالا ببرید."
                    }
                    cleanQ.contains("ماموریت") || cleanQ.contains("کار") -> {
                        "[ سیستم ] در حال حاضر سطح ${hunter.level} هستید. زنجیره شما $streakDays روز فعال است. برای ارتقای رتبه، لیست ماموریت‌های خود را در تب مأموریت‌ها بررسی و پاکسازی کنید."
                    }
                    else -> {
                        "[ پروتکل سیستم ] ارتباط هوش مصنوعی به علت نبود کلید API غیرفعال است. کلید API خود را ثبت کنید تا اتصال آنلاین برقرار شود."
                    }
                }
            } else {
                when {
                    cleanQ.contains("hello") || cleanQ.contains("hi") || cleanQ.contains("hey") -> {
                        if (mode == "friendly") "[ SYSTEM ] Hello Hunter ${hunter.name}! Ready to conquer today? Let me know how I can guide you!"
                        else "[ SYSTEM PROTOCOL ] Entry signal captured. Welcome, Hunter ${hunter.name}. Identify confirmed. Prepared for directive incoming."
                    }
                    cleanQ.contains("help") || cleanQ.contains("guide") -> {
                        "[ SYSTEM GUIDE ] Complete your assigned daily missions to level up. Maintain your daily streak to gain rank bonuses."
                    }
                    cleanQ.contains("mission") || cleanQ.contains("task") -> {
                        "[ SYSTEM ] Level: ${hunter.level} | Active Streak: $streakDays days. Check the Missions tab to proceed with your awakening protocol."
                    }
                    else -> {
                        "[ SYSTEM ] AI chat is operating in decoupled offline mode since no API key is configured. Please set your key in the setup console."
                    }
                }
            }
        }

        if (com.axiom.app.core.FeatureFlags.FORCE_OFFLINE_MODE) {
            return fallback()
        }

        return try {
            val contextStr = hunterContext(hunter, streakDays)
            val modelText = getModel().generateContent("$contextStr\nHunter asks: \"$question\"").text
            if (modelText.isNullOrBlank()) fallback() else modelText
        } catch (e: Exception) {
            fallback()
        }
    }

    suspend fun askSystemStream(
        hunter: Hunter,
        streakDays: Int,
        question: String
    ): Flow<String> {
        val isOffline = com.axiom.app.core.FeatureFlags.FORCE_OFFLINE_MODE || !hasApiKey()
        if (isOffline) {
            return flow {
                val fallbackResponse = askSystem(hunter, streakDays, question)
                emit(fallbackResponse)
            }
        }

        return try {
            val contextStr = hunterContext(hunter, streakDays)
            val model = getModel()
            model.generateContentStream("$contextStr\nHunter asks: \"$question\"")
                .map { response -> response.text ?: "" }
        } catch (e: Exception) {
            flow {
                val fallbackResponse = askSystem(hunter, streakDays, question)
                emit(fallbackResponse)
            }
        }
    }

    suspend fun generateStructuredMissions(
        goal: String,
        skills: List<Skill>,
        hunter: Hunter,
        streakDays: Int
    ): List<AIMissionSuggestion> {
        val fallback = suspend {
            val currentLang = preferences.languageFlow.first()
            val isFa = currentLang == "fa"
            val unlockedSkills = skills.filter { it.isUnlocked }
            val firstSkill = unlockedSkills.firstOrNull()?.name ?: "General"
            val secondSkill = unlockedSkills.getOrNull(1)?.name ?: firstSkill
            val thirdSkill = unlockedSkills.getOrNull(2)?.name ?: firstSkill

            if (isFa) {
                listOf(
                    AIMissionSuggestion(
                        title = "آغاز گام اول هدف فرعی",
                        description = "اختصاص ۳۰ دقیقه تمرکز عمیق روی اهداف متصل به \"$goal\".",
                        skillName = firstSkill,
                        estimatedHours = 0.5f,
                        rarity = "COMMON",
                        reasoning = "ورود آسان به محدوده تمرین روزانه بدون اصطکاک حافظه."
                    ),
                    AIMissionSuggestion(
                        title = "پاکسازی دروازه آزمون: پیشرفت مستمر",
                        description = "بخش توسعه فنی و تئوری متصل به هدف \"$goal\" به مدت ۲ ساعت.",
                        skillName = secondSkill,
                        estimatedHours = 2.0f,
                        rarity = "UNCOMMON",
                        reasoning = "افزایش توان استقامت ذهنی برای رشدهای بعدی."
                    ),
                    AIMissionSuggestion(
                        title = "بخش پیشرفته سلطنت خلأ",
                        description = "حل یکی از چالش‌های مأموریت محوری چند ساعته پیرامون مأموریت نهایی.",
                        skillName = thirdSkill,
                        estimatedHours = 4.0f,
                        rarity = "RARE",
                        reasoning = "کمیاب با امتیاز بالا پیوند خورده با رشد نهایی شکارچی."
                    )
                )
            } else {
                listOf(
                    AIMissionSuggestion(
                        title = "Initial Gateway Progress",
                        description = "Dedicate 30 minutes of deep focus to kickstart: \"$goal\".",
                        skillName = firstSkill,
                        estimatedHours = 0.5f,
                        rarity = "COMMON",
                        reasoning = "Low commitment step designed to bypass friction."
                    ),
                    AIMissionSuggestion(
                        title = "Dimensional Grind: Skill Progress",
                        description = "Perform 2 hours of deliberate training aligning with your goal \"$goal\".",
                        skillName = secondSkill,
                        estimatedHours = 2.0f,
                        rarity = "UNCOMMON",
                        reasoning = "Requires cognitive stamina to sustain growth."
                    ),
                    AIMissionSuggestion(
                        title = "Monarch Gate Master Trial",
                        description = "Conquer a major milestone inside your main objective \"$goal\".",
                        skillName = thirdSkill,
                        estimatedHours = 4.0f,
                        rarity = "RARE",
                        reasoning = "Rare tier challenge with substantial XP payoff."
                    )
                )
            }
        }

        if (com.axiom.app.core.FeatureFlags.FORCE_OFFLINE_MODE) {
            return fallback()
        }

        val currentLang = preferences.languageFlow.first()
        val langInstruction = if (currentLang == "fa") {
            "Please generate all mission titles and descriptions in Persian (Farsi), but keep rarity and skillName matching the English parameters as required."
        } else {
            "Please generate all mission titles and descriptions strictly in English."
        }

        val prompt = """
Hunter ${hunter.name} (Level ${hunter.level}, Rank ${hunter.rankLabel}) has this goal: "$goal"
Available skills: ${skills.filter { it.isUnlocked }.map { it.name }.joinToString(", ")}

Generate exactly 3 missions as a valid JSON array. No markdown, no explanation.
Each item must have:
- title: string (action-oriented, max 8 words, matching the required language: $langInstruction)
- description: string (one sentence explaining why this matters, matching the required language: $langInstruction)
- skillName: string (must exactly match one available skill name in English)
- estimatedHours: float (0.5 to 8.0)
- rarity: string (COMMON/UNCOMMON/RARE/EPIC/LEGENDARY)
- reasoning: string (one sentence explaining why this rarity)
Make missions progressively harder: easy → medium → hard.
        """.trimIndent()

        return try {
            val key  = preferences.geminiApiKeyFlow.first() ?: throw NoApiKeyException()
            val model = GenerativeModel(modelName = "gemini-1.5-flash", apiKey = key)
            val raw   = model.generateContent(prompt).text ?: return fallback()
            val clean = raw.trim()
                .removePrefix("```json").removePrefix("```")
                .removeSuffix("```").trim()
            val arr = org.json.JSONArray(clean)
            List(arr.length()) { i ->
                val o = arr.getJSONObject(i)
                AIMissionSuggestion(
                    title          = o.getString("title"),
                    description    = o.getString("description"),
                    skillName      = o.getString("skillName"),
                    estimatedHours = o.optDouble("estimatedHours", 1.0).toFloat(),
                    rarity         = o.getString("rarity"),
                    reasoning      = o.getString("reasoning")
                )
            }
        } catch (e: Exception) {
            fallback()
        }
    }

    private fun defaultMissions(skills: List<Skill>): List<AIMissionSuggestion> {
        val skill = skills.firstOrNull { it.isUnlocked }?.name ?: "General"
        return listOf(
            AIMissionSuggestion("Complete a 30-minute study session",
                "Builds consistent learning habits.", skill, 0.5f, "COMMON",
                "Short duration, easy entry point."),
            AIMissionSuggestion("Practice focused deep work for 2 hours",
                "Deep work creates real skill.", skill, 2f, "UNCOMMON",
                "Requires sustained focus."),
            AIMissionSuggestion("Build and ship a small project",
                "Applied knowledge compounds faster.", skill, 6f, "RARE",
                "High effort, high reward.")
        )
    }

    private fun String.toFloatOrNull(): Float? = try { this.toFloat() } catch(e: Exception) { null }
}
