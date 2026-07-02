package com.axiom.app.domain.usecase

import com.axiom.app.data.local.dao.MissionDao
import com.axiom.app.domain.model.Mission
import com.axiom.app.domain.repository.MissionRepository
import com.axiom.app.domain.repository.SkillRepository
import com.axiom.app.domain.repository.WarriorProfileRepository
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

class GenerateDailyMissionsFromScheduleUseCase @Inject constructor(
    private val warriorRepository: WarriorProfileRepository,
    private val skillRepository: SkillRepository,
    private val missionRepository: MissionRepository,
    private val missionDao: MissionDao
) {
    suspend operator fun invoke() {
        val blocks = warriorRepository.getAllScheduleBlocks()
        val tracks = warriorRepository.getAllTracks()

        // Safely resolve active skills from DB flow
        val allSkills = try {
            skillRepository.getAllSkills().first()
        } catch (e: Exception) {
            emptyList()
        }
        val fallbackSkillId = allSkills.firstOrNull()?.id ?: "general_skill"
        val fallbackSkillName = allSkills.firstOrNull()?.name ?: "General Focus"

        val now = System.currentTimeMillis()

        for (block in blocks) {
            // Check if there is already a mission for today linked to this scheduleBlockId
            val existingMissions = try {
                missionDao.getMissionsByScheduleBlockId(block.id)
            } catch (e: Exception) {
                emptyList()
            }
            val existsToday = existingMissions.any { isSameDay(it.createdAt, now) }

            if (!existsToday) {
                // Find associated track
                val associatedTrack = tracks.find { it.id == block.trackId }
                val trackName = associatedTrack?.name ?: block.tag

                // Create a new Mission
                val missionId = UUID.randomUUID().toString()

                val isCritical = block.isNonNegotiable
                val rarity = if (isCritical) "EPIC" else "COMMON"
                val rarityColor = if (isCritical) 0xFF8A5CD4 else 0xFF6E6658 // EpicPurple or CommonGray
                val xpReward = if (isCritical) 50 else 15
                val powerScore = if (isCritical) 5.0f else 2.0f

                val newMission = Mission(
                    id = missionId,
                    title = block.title,
                    track = trackName,
                    rarity = rarity,
                    skillId = fallbackSkillId,
                    skillName = fallbackSkillName,
                    xpReward = xpReward,
                    powerScore = powerScore,
                    status = "ACTIVE",
                    dungeonId = null,
                    estimatedHours = 1.0f,
                    actualHours = null,
                    createdAt = now,
                    completedAt = null,
                    rarityColor = rarityColor,
                    isInstantGate = false,
                    description = block.actionDescription,
                    trackId = block.trackId,
                    scheduleBlockId = block.id
                )

                missionRepository.insertMission(newMission)
            }
        }
    }

    private fun isSameDay(t1: Long, t2: Long): Boolean {
        if (t1 == 0L || t2 == 0L) return false
        val cal1 = Calendar.getInstance().apply { timeInMillis = t1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = t2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}
