package com.axiom.app.domain.model

/**
 * Observable evidence requirement for completing a Mission.
 */
enum class EvidenceLevel {
    NONE,
    BINARY_CHECKLIST,
    METRIC_OR_NOTE
}

/**
 * Interactive scheduling slot for execution planning (resolving G2-S3-02).
 */
sealed interface ScheduleSlot {
    val key: String
    val defaultTimeLabel: String

    data object Immediate : ScheduleSlot {
        override val key: String = "IMMEDIATE"
        override val defaultTimeLabel: String = "Now"
    }

    data object Morning : ScheduleSlot {
        override val key: String = "MORNING"
        override val defaultTimeLabel: String = "09:00"
    }

    data object Afternoon : ScheduleSlot {
        override val key: String = "AFTERNOON"
        override val defaultTimeLabel: String = "14:00"
    }

    data object Evening : ScheduleSlot {
        override val key: String = "EVENING"
        override val defaultTimeLabel: String = "19:00"
    }

    data class Custom(val timeLabel: String) : ScheduleSlot {
        override val key: String = "CUSTOM"
        override val defaultTimeLabel: String = timeLabel
    }

    companion object {
        fun fromKey(key: String?, customLabel: String? = null): ScheduleSlot {
            return when (key?.uppercase()) {
                "MORNING" -> Morning
                "AFTERNOON" -> Afternoon
                "EVENING" -> Evening
                "CUSTOM" -> Custom(customLabel ?: "Custom")
                else -> Immediate
            }
        }
    }
}

/**
 * Encapsulates the structured metadata of an authored Mission.
 */
data class MissionAuthoringMetadata(
    val doneCondition: String,
    val contextTrigger: String = "",
    val notes: String = "",
    val evidenceLevel: EvidenceLevel = EvidenceLevel.NONE,
    val scheduleSlotKey: String = ScheduleSlot.Immediate.key,
    val durationMinutes: Int = 45
)

/**
 * Payload submitted from the Mission Authoring surface.
 */
data class MissionAuthoringPayload(
    val title: String,
    val doneCondition: String,
    val contextTrigger: String = "",
    val durationMinutes: Int = 45,
    val evidenceLevel: EvidenceLevel = EvidenceLevel.NONE,
    val scheduleSlot: ScheduleSlot = ScheduleSlot.Immediate,
    val skillId: String,
    val track: String = "Capability",
    val dungeonId: String? = null,
    val isTimedMission: Boolean = false,
    val notes: String = "",
    // Advanced ROI parameters (with sensible defaults)
    val marketDemand: Float = 5f,
    val leverage: Float = 5f,
    val complexity: Float = 5f,
    val customRarity: String? = null,
    // Deliberate Practice Log-As-Completed parameters
    val logAsCompleted: Boolean = false,
    val sessionGoalSet: Boolean = true,
    val sessionGotFeedback: Boolean = true,
    val sessionPushedComfortZone: Boolean = true
) {
    val isValid: Boolean
        get() = title.isNotBlank() && doneCondition.isNotBlank() && skillId.isNotBlank() && durationMinutes > 0

    val estimatedHours: Float
        get() = (durationMinutes / 60f).coerceAtLeast(0.25f)
}

/**
 * Canonical serializer & parser helper ensuring zero database schema changes
 * while retaining full structured authoring truth.
 */
object MissionAuthoringSerializer {
    private const val DONE_TAG = "[DONE_CONDITION]:"
    private const val CONTEXT_TAG = "[CONTEXT]:"
    private const val EVIDENCE_TAG = "[EVIDENCE]:"
    private const val SCHEDULE_TAG = "[SCHEDULE]:"
    private const val DURATION_TAG = "[DURATION_MIN]:"
    private const val NOTES_TAG = "[NOTES]:"

    fun formatDescription(
        doneCondition: String,
        contextTrigger: String = "",
        notes: String = "",
        evidenceLevel: EvidenceLevel = EvidenceLevel.NONE,
        scheduleSlot: ScheduleSlot = ScheduleSlot.Immediate,
        durationMinutes: Int = 45
    ): String {
        val sb = StringBuilder()
        sb.append(DONE_TAG).append(" ").append(doneCondition.trim()).append("\n")
        if (contextTrigger.isNotBlank()) {
            sb.append(CONTEXT_TAG).append(" ").append(contextTrigger.trim()).append("\n")
        }
        sb.append(EVIDENCE_TAG).append(" ").append(evidenceLevel.name).append("\n")
        sb.append(SCHEDULE_TAG).append(" ").append(scheduleSlot.key).append("\n")
        sb.append(DURATION_TAG).append(" ").append(durationMinutes).append("\n")
        if (notes.isNotBlank()) {
            sb.append(NOTES_TAG).append(" ").append(notes.trim())
        }
        return sb.toString().trim()
    }

    fun parseDescription(raw: String?): MissionAuthoringMetadata {
        if (raw.isNullOrBlank()) {
            return MissionAuthoringMetadata(doneCondition = "")
        }

        var doneCondition = ""
        var contextTrigger = ""
        var notes = ""
        var evidenceLevel = EvidenceLevel.NONE
        var scheduleKey = ScheduleSlot.Immediate.key
        var duration = 45

        val lines = raw.lines()
        var parsingNotes = false
        val notesBuilder = StringBuilder()

        for (line in lines) {
            val trimmed = line.trim()
            if (parsingNotes) {
                notesBuilder.append(line).append("\n")
                continue
            }

            when {
                trimmed.startsWith(DONE_TAG) -> {
                    doneCondition = trimmed.removePrefix(DONE_TAG).trim()
                }
                trimmed.startsWith(CONTEXT_TAG) -> {
                    contextTrigger = trimmed.removePrefix(CONTEXT_TAG).trim()
                }
                trimmed.startsWith(EVIDENCE_TAG) -> {
                    val evStr = trimmed.removePrefix(EVIDENCE_TAG).trim()
                    evidenceLevel = try {
                        EvidenceLevel.valueOf(evStr)
                    } catch (_: Exception) {
                        EvidenceLevel.NONE
                    }
                }
                trimmed.startsWith(SCHEDULE_TAG) -> {
                    scheduleKey = trimmed.removePrefix(SCHEDULE_TAG).trim()
                }
                trimmed.startsWith(DURATION_TAG) -> {
                    duration = trimmed.removePrefix(DURATION_TAG).trim().toIntOrNull() ?: 45
                }
                trimmed.startsWith(NOTES_TAG) -> {
                    parsingNotes = true
                    notesBuilder.append(trimmed.removePrefix(NOTES_TAG).trim()).append("\n")
                }
                else -> {
                    // Fallback for legacy plain text description: treat as doneCondition or notes
                    if (doneCondition.isBlank()) {
                        doneCondition = trimmed
                    } else {
                        notesBuilder.append(line).append("\n")
                    }
                }
            }
        }

        notes = notesBuilder.toString().trim()

        return MissionAuthoringMetadata(
            doneCondition = doneCondition,
            contextTrigger = contextTrigger,
            notes = notes,
            evidenceLevel = evidenceLevel,
            scheduleSlotKey = scheduleKey,
            durationMinutes = duration
        )
    }
}
