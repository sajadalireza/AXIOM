package com.axiom.app.domain.usecase

import com.axiom.app.domain.model.Dungeon
import com.axiom.app.domain.model.Hunter
import com.axiom.app.domain.model.Mission
import com.axiom.app.domain.model.Skill
import com.axiom.app.domain.repository.HunterRepository
import com.axiom.app.domain.repository.SkillRepository
import com.axiom.app.domain.repository.DungeonRepository
import com.axiom.app.domain.repository.MissionRepository
import com.axiom.app.data.local.AxiomPreferences
import java.util.UUID
import javax.inject.Inject

class InitializeAxiomUseCase @Inject constructor(
    private val hunterRepository: HunterRepository,
    private val skillRepository: SkillRepository,
    private val dungeonRepository: DungeonRepository,
    private val missionRepository: MissionRepository,
    private val preferences: AxiomPreferences
) {
    suspend operator fun invoke(customName: String = "Sung Jin-Woo") {
        // PRIORITY 3: stamp first-launch time so progressive disclosure can gate tabs
        preferences.recordFirstLaunchIfNeeded()

        val existingProfile = hunterRepository.getDirectHunterProfile()
        if (existingProfile != null) return

        // 1. Create Starter Hunter Profile (E-Rank)
        val hunterId = UUID.randomUUID().toString()
        val defaultHunter = Hunter(
            id = hunterId,
            name = customName,
            level = 1,
            rankLabel = "E-Rank",
            totalXP = 0,
            currentXP = 0,
            xpToNextLevel = 100,
            progressPercent = 0.0f,
            rankColor = 0xFF9E9E9E, // Grey
            rankGlyph = "E"
        )
        hunterRepository.updateHunterProfile(defaultHunter)

        // 2. Create Starter Skills (aligned with SeedDataHelper parent skills)
        val skills = listOf(
            Skill(
                id = "skill_ml_and_computational_biology",
                name = "ML & Computational Biology",
                category = "ML & Computational Biology",
                currentXP = 0L,
                level = 1,
                rankLabel = "E-Rank",
                parentId = null,
                isUnlocked = true,
                xpToNextRank = 100L,
                rankProgressPercent = 0.0f,
                isShadowCandidate = false,
                rankColor = 0xFF9E9E9E,
                trackId = "capability"
            ),
            Skill(
                id = "skill_public_research_project",
                name = "Public Research Project",
                category = "Public Research Project",
                currentXP = 0L,
                level = 1,
                rankLabel = "E-Rank",
                parentId = null,
                isUnlocked = true,
                xpToNextRank = 100L,
                rankProgressPercent = 0.0f,
                isShadowCandidate = false,
                rankColor = 0xFF9E9E9E,
                trackId = "capability"
            ),
            Skill(
                id = "skill_english_and_outreach",
                name = "English & Outreach",
                category = "English & Outreach",
                currentXP = 0L,
                level = 1,
                rankLabel = "E-Rank",
                parentId = null,
                isUnlocked = true,
                xpToNextRank = 100L,
                rankProgressPercent = 0.0f,
                isShadowCandidate = false,
                rankColor = 0xFF9E9E9E,
                trackId = "capability"
            ),
            Skill(
                id = "skill_income_and_commercial_intelligence",
                name = "Income & Commercial Intelligence",
                category = "Income & Commercial Intelligence",
                currentXP = 0L,
                level = 1,
                rankLabel = "E-Rank",
                parentId = null,
                isUnlocked = true,
                xpToNextRank = 100L,
                rankProgressPercent = 0.0f,
                isShadowCandidate = false,
                rankColor = 0xFF9E9E9E,
                trackId = "commercial_intelligence"
            ),
            Skill(
                id = "skill_relocation_progress",
                name = "Relocation Progress",
                category = "Relocation Progress",
                currentXP = 0L,
                level = 1,
                rankLabel = "E-Rank",
                parentId = null,
                isUnlocked = true,
                xpToNextRank = 100L,
                rankProgressPercent = 0.0f,
                isShadowCandidate = false,
                rankColor = 0xFF9E9E9E,
                trackId = "capability"
            ),
            Skill(
                id = "skill_physical_mastery",
                name = "Physical Mastery",
                category = "Physical Mastery",
                currentXP = 0L,
                level = 1,
                rankLabel = "E-Rank",
                parentId = null,
                isUnlocked = true,
                xpToNextRank = 100L,
                rankProgressPercent = 0.0f,
                isShadowCandidate = false,
                rankColor = 0xFF9E9E9E,
                trackId = "capability"
            )
        )

        for (skill in skills) {
            skillRepository.insertSkill(skill)
        }

        // 3. Create Starter Dungeon (C-Rank Instant Dungeon)
        val dungeonId = UUID.randomUUID().toString()
        val starterDungeon = Dungeon(
            id = dungeonId,
            name = "Double Dungeon Discovery",
            description = "A multi-stage gauntlet culminating in a boss fight against the God Statue.",
            rarity = "C-Rank",
            totalStages = 3,
            completedStages = 0,
            isBossDefeated = false,
            createdAt = System.currentTimeMillis(),
            completedAt = null
        )
        dungeonRepository.insertDungeon(starterDungeon)

        // 4. Create Starter Missions (linked directly to the system-aligned root skills)
        val starterMissions = listOf(
            Mission(
                id = UUID.randomUUID().toString(),
                title = "Daily Strength Protocol (100 Push-ups, 100 Sit-ups, 10km Run)",
                track = "Daily Protocol",
                rarity = "Normal",
                skillId = "skill_physical_mastery",
                skillName = "Physical Mastery",
                xpReward = 30,
                powerScore = 2.5f,
                status = "ACTIVE",
                dungeonId = null,
                estimatedHours = 1.5f,
                actualHours = null,
                createdAt = System.currentTimeMillis(),
                completedAt = null,
                rarityColor = 0xFF4CAF50
            ),
            Mission(
                id = UUID.randomUUID().toString(),
                title = "Implement MVVM Architecture Patterns",
                track = "Engineering",
                rarity = "Rare",
                skillId = "skill_ml_and_computational_biology",
                skillName = "ML & Computational Biology",
                xpReward = 50,
                powerScore = 4.0f,
                status = "ACTIVE",
                dungeonId = dungeonId, // Linked to Dungeon stage 1
                estimatedHours = 3.0f,
                actualHours = null,
                createdAt = System.currentTimeMillis(),
                completedAt = null,
                rarityColor = 0xFF2196F3
            )
        )

        for (mission in starterMissions) {
            missionRepository.insertMission(mission)
        }
    }
}
