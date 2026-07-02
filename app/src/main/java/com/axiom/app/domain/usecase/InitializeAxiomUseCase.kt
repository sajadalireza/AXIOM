package com.axiom.app.domain.usecase

import com.axiom.app.domain.model.Dungeon
import com.axiom.app.domain.model.Hunter
import com.axiom.app.domain.model.Mission
import com.axiom.app.domain.model.Skill
import com.axiom.app.domain.repository.HunterRepository
import com.axiom.app.domain.repository.SkillRepository
import com.axiom.app.domain.repository.DungeonRepository
import com.axiom.app.domain.repository.MissionRepository
import com.axiom.app.data.BlueprintV51Data
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
            rankGlyph = "E",
            // Generic placeholder so AwakeningCompleteScreen has something to reveal —
            // BlueprintWizardViewModel.completeOnboarding() overwrites this with the
            // user's chosen domain-specific thesis once they reach that step.
            personalThesis = BlueprintV51Data.DRIVING_THESIS
        )
        hunterRepository.updateHunterProfile(defaultHunter)

        // Grant a starter Streak Shield so the highest-churn window (day 1-6, before
        // the first 7-day milestone would otherwise award one) isn't the one week with
        // zero forgiveness for a missed day.
        preferences.awardStreakFreeze()

        // 2. Create Starter Skills — generic, domain-neutral so XP progression isn't
        // tied to any one person's life goals. BlueprintWizardViewModel's track
        // selection (career/finance/health/relationships) layers on top of these
        // once the user reaches that step; these are just the always-present base set.
        val skills = listOf(
            Skill(
                id = "skill_deep_work",
                name = "Deep Work",
                category = "Deep Work",
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
                id = "skill_creative_output",
                name = "Creative Output",
                category = "Creative Output",
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
                id = "skill_communication",
                name = "Communication",
                category = "Communication",
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
                id = "skill_personal_growth",
                name = "Personal Growth",
                category = "Personal Growth",
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
                title = "Complete a High-Leverage Focus Block (2 Hours)",
                track = "Deep Work",
                rarity = "Rare",
                skillId = "skill_deep_work",
                skillName = "Deep Work",
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
