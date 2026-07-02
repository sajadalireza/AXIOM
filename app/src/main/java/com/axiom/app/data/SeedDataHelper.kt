package com.axiom.app.data

import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.domain.model.*
import com.axiom.app.domain.repository.*
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeedDataHelper @Inject constructor(
    private val skillRepository: SkillRepository,
    private val muscleGroupRepository: MuscleGroupRepository,
    private val warriorProfileRepository: WarriorProfileRepository,
    private val hunterRepository: HunterRepository,
    private val preferences: AxiomPreferences
) {
    private fun cleanId(name: String): String {
        return name.lowercase().trim()
            .replace("&", "and")
            .replace("-", "_")
            .replace("/", "_")
            .replace("(", "")
            .replace(")", "")
            .replace("  ", " ")
            .replace(" ", "_")
            .replace("__", "_")
    }

    suspend fun seedSkillsIfNeeded() {
        val seeded = preferences.skillTreeSeededFlow.first()
        if (seeded) return

        // 1. Insert 6 parent skills (isUnlocked=true)
        val mlBiologyParent = createParentSkill("ML & Computational Biology", "ML & Computational Biology", "capability")
        val researchProjectParent = createParentSkill("Public Research Project", "Public Research Project", "capability")
        val englishOutreachParent = createParentSkill("English & Outreach", "English & Outreach", "capability")
        val commercialParent = createParentSkill("Income & Commercial Intelligence", "Income & Commercial Intelligence", "commercial_intelligence")
        val relocationParent = createParentSkill("Relocation Progress", "Relocation Progress", "capability")
        val physicalParent = createParentSkill("Physical Mastery", "Physical Mastery", "capability")

        val parents = listOf(mlBiologyParent, researchProjectParent, englishOutreachParent, commercialParent, relocationParent, physicalParent)
        for (parent in parents) {
            skillRepository.insertSkill(parent)
        }

        // 2. Insert child skills (isUnlocked=false)
        val children = listOf(
            // ML & Computational Biology children
            createChildSkill("ESM2 & Protein Modeling", "ML & Computational Biology", mlBiologyParent.id, "capability"),
            createChildSkill("COBRApy & Metabolic Modeling", "ML & Computational Biology", mlBiologyParent.id, "capability"),
            createChildSkill("PyTorch Deployment", "ML & Computational Biology", mlBiologyParent.id, "capability"),
            createChildSkill("FastAPI / Streamlit", "ML & Computational Biology", mlBiologyParent.id, "capability"),

            // Public Research Project children
            createChildSkill("Terminator Efficiency Research", "Public Research Project", researchProjectParent.id, "capability"),
            createChildSkill("GitHub Portfolio", "Public Research Project", researchProjectParent.id, "capability"),
            createChildSkill("Preprint Writing", "Public Research Project", researchProjectParent.id, "capability"),

            // English & Outreach children
            createChildSkill("LinkedIn Direct Outreach", "English & Outreach", englishOutreachParent.id, "capability"),
            createChildSkill("Technical English Writing", "English & Outreach", englishOutreachParent.id, "capability"),
            createChildSkill("Client Communication", "English & Outreach", englishOutreachParent.id, "capability"),

            // Income & Commercial Intelligence children
            createChildSkill("Problem Discovery Conversations", "Income & Commercial Intelligence", commercialParent.id, "commercial_intelligence"),
            createChildSkill("Consulting Delivery", "Income & Commercial Intelligence", commercialParent.id, "commercial_intelligence"),
            createChildSkill("Monthly Income Diagnostic", "Income & Commercial Intelligence", commercialParent.id, "commercial_intelligence"),

            // Relocation Progress children
            createChildSkill("PhD Applications (Path A)", "Relocation Progress", relocationParent.id, "capability"),
            createChildSkill("Highly Skilled Migrant Visa (Path B)", "Relocation Progress", relocationParent.id, "capability"),
            createChildSkill("Dubai Bridge Prep (backup)", "Relocation Progress", relocationParent.id, "capability"),

            // Physical Mastery children
            createChildSkill("Strength Training", "Physical Mastery", physicalParent.id, "capability"),
            createChildSkill("Sleep Quality", "Physical Mastery", physicalParent.id, "capability"),
            createChildSkill("Daily Hygiene", "Physical Mastery", physicalParent.id, "capability")
        )

        for (child in children) {
            skillRepository.insertSkill(child)
        }

        // Mark as seeded
        preferences.setSkillTreeSeeded(true)
    }

    private fun createParentSkill(name: String, category: String, trackId: String? = null): Skill {
        val skillId = "skill_${cleanId(name)}"
        return Skill(
            id = skillId,
            name = name,
            category = category,
            currentXP = 0L,
            level = 1,
            rankLabel = "E-Rank",
            parentId = null,
            isUnlocked = true,
            xpToNextRank = 100L,
            rankProgressPercent = 0.0f,
            isShadowCandidate = false,
            rankColor = 0xFF9E9E9E,
            trackId = trackId
        )
    }

    private fun createChildSkill(name: String, category: String, parentId: String, trackId: String? = null): Skill {
        val skillId = "skill_${cleanId(name)}"
        return Skill(
            id = skillId,
            name = name,
            category = category,
            currentXP = 0L,
            level = 1,
            rankLabel = "E-Rank",
            parentId = parentId,
            isUnlocked = true,
            xpToNextRank = 100L,
            rankProgressPercent = 0.0f,
            isShadowCandidate = false,
            rankColor = 0xFF9E9E9E,
            trackId = trackId
        )
    }

    suspend fun seedMuscleGroupsIfNeeded() {
        val seeded = preferences.muscleGroupsSeededFlow.first()
        if (seeded) return

        val muscleGroups = listOf(
            MuscleGroup("chest", "Chest", 0, null, 100),
            MuscleGroup("back", "Back", 0, null, 100),
            MuscleGroup("shoulders", "Shoulders", 0, null, 100),
            MuscleGroup("biceps", "Biceps", 0, null, 100),
            MuscleGroup("triceps", "Triceps", 0, null, 100),
            MuscleGroup("legs", "Legs", 0, null, 100),
            MuscleGroup("core", "Core", 0, null, 100),
            MuscleGroup("forearms", "Forearms", 0, null, 100)
        )

        muscleGroupRepository.insertMuscleGroups(muscleGroups)
        preferences.setMuscleGroupsSeeded(true)
    }

    suspend fun seedDefaultProfileIfNeeded() {
        val seeded = preferences.alirezaProfileSeededFlow.first()
        if (seeded) return

        // 1. Ensure basic parent skills exist in DB
        seedSkillsIfNeeded()
        seedMuscleGroupsIfNeeded()

        // 2. Create Default Hunter Profile (starts at Rank D or E, let's say D-Rank)
        val hunter = Hunter(
            id = "default_hunter_id",
            name = "Warrior",
            level = 1,
            rankLabel = "D-Rank",
            totalXP = 0L,
            currentXP = 0,
            xpToNextLevel = 100,
            progressPercent = 0.0f,
            rankColor = 0xFFD4A843, // Golden/Yellow
            rankGlyph = "D",
            personalThesis = BlueprintV51Data.DRIVING_THESIS
        )
        hunterRepository.updateHunterProfile(hunter)

        // 3. Create Default Warrior Blueprint Profile
        val warriorProfile = WarriorProfile(
            id = "default",
            codename = "Warrior",
            oneLineThesis = BlueprintV51Data.DRIVING_THESIS,
            rareProfileDescription = BlueprintV51Data.RARE_PROFILE_DESCRIPTION
        )
        warriorProfileRepository.saveProfile(warriorProfile)

        // 4. Create Cores and Pillars Tracks
        BlueprintV51Data.CORE_TRACKS.forEach { warriorProfileRepository.saveTrack(it) }
        BlueprintV51Data.PILLARS.forEach { warriorProfileRepository.saveTrack(it) }

        // 5. Create Schedule blocks
        BlueprintV51Data.SCHEDULE_BLOCKS.forEach { warriorProfileRepository.saveScheduleBlock(it) }

        // 6. Create Custom KPIs
        BlueprintV51Data.CUSTOM_KPIS.forEach { warriorProfileRepository.saveCustomKPI(it) }

        // 7. Create Iron Rules
        BlueprintV51Data.IRON_RULES.forEach { warriorProfileRepository.saveIronRule(it) }

        // 8. Create Hard Truths and Affirmations
        BlueprintV51Data.HARD_TRUTHS.forEach { warriorProfileRepository.saveHardTruthOrAffirmation(it) }
        BlueprintV51Data.AFFIRMATIONS.forEach { warriorProfileRepository.saveHardTruthOrAffirmation(it) }

        // 9. Major Milestones
        val milestone = MajorMilestone(
            id = "milestone_vehicle_decision",
            label = "Vehicle Decision — Path A vs Path B",
            targetDate = System.currentTimeMillis() + (10L * 30L * 24L * 60L * 60L * 1000L), // 10 months
            description = "Make the vital decision between Path A (Academia/Munich research) or Path B (direct commercial venture)."
        )
        warriorProfileRepository.saveMajorMilestone(milestone)

        // 10. Key Relationships
        val relationships = listOf(
            KeyRelationship(
                id = "rel_mentor",
                label = "Academic Guidance",
                category = "Mentor",
                lastInteractionAt = null,
                preparedTalkingPoint = "German laboratory opportunities, yeast cofactor regeneration paper feedback, or Munich co-author requests."
            ),
            KeyRelationship(
                id = "rel_connector",
                label = "German Biotech Peer",
                category = "Connector",
                lastInteractionAt = null,
                preparedTalkingPoint = "How researchers in Germany deploy COBRApy to Streamlit for biologists, or Munich networking."
            ),
            KeyRelationship(
                id = "rel_peer",
                label = "Iran ML Peer",
                category = "Peer",
                lastInteractionAt = null,
                preparedTalkingPoint = "BioPython sequence parsers, PyTorch optimization tips, and Tehran ML meetups."
            ),
            KeyRelationship(
                id = "rel_buyer",
                label = "Biotech Client",
                category = "Buyer",
                lastInteractionAt = null,
                preparedTalkingPoint = "Under-utilized carbon sources in yeast fermentation, optimization, or computational model validation."
            )
        )
        for (rel in relationships) {
            warriorProfileRepository.saveKeyRelationship(rel)
        }

        // 11. Enable Financial Checkpoints & Mark Blueprint Wizard as Complete
        preferences.setFinancialModuleEnabled(true)
        preferences.setBlueprintSetupComplete(false)
        preferences.setFirstMissionDone(false) // Let the wizard onboard the user
        preferences.setSetupComplete()

        // 12. Set Seeding Flag to avoid future duplicates
        preferences.setAlirezaProfileSeeded(true)
    }
}
