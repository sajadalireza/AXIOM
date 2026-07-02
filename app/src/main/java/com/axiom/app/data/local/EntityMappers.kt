package com.axiom.app.data.local

import com.axiom.app.data.local.entity.DungeonEntity
import com.axiom.app.data.local.entity.HunterEntity
import com.axiom.app.data.local.entity.MissionEntity
import com.axiom.app.data.local.entity.ShadowEntity
import com.axiom.app.data.local.entity.SkillEntity
import com.axiom.app.domain.model.Dungeon
import com.axiom.app.domain.model.Hunter
import com.axiom.app.domain.model.Mission
import com.axiom.app.domain.model.Shadow
import com.axiom.app.domain.model.Skill

// ═══════════════════════════════════════════════════════════════
// ENTITY MAPPERS
//
// Bidirectional mapping between Room entities and domain models.
//
//   Entity  →  Domain :  XxxEntity.toDomain()
//   Domain  →  Entity :  DomainModel.toEntity()
//
// All 5 entity pairs are covered:
//   Hunter · Mission · Dungeon · Skill · Shadow
// ═══════════════════════════════════════════════════════════════

// ───────────────────────────────────────────────────────────────
// HUNTER
// ───────────────────────────────────────────────────────────────

fun HunterEntity.toDomain(): Hunter = Hunter(
    id             = id,
    name           = name,
    level          = level,
    rankLabel      = "WARRIOR",
    totalXP        = totalXP,
    currentXP      = currentXP,
    xpToNextLevel  = xpToNextLevel,
    progressPercent = progressPercent,
    rankColor      = 0xFF1D9E75L,
    rankGlyph      = "◈",
    personalThesis = personalThesis
)

fun Hunter.toEntity(): HunterEntity = HunterEntity(
    id             = id,
    name           = name,
    level          = level,
    rankLabel      = rankLabel,
    totalXP        = totalXP,
    currentXP      = currentXP,
    xpToNextLevel  = xpToNextLevel,
    progressPercent = progressPercent,
    rankColor      = rankColor,
    rankGlyph      = rankGlyph,
    personalThesis = personalThesis
)

// ───────────────────────────────────────────────────────────────
// MISSION
// ───────────────────────────────────────────────────────────────

fun MissionEntity.toDomain(): Mission = Mission(
    id             = id,
    title          = title,
    track          = track,
    rarity         = rarity,
    skillId        = skillId,
    skillName      = skillName,
    xpReward       = xpReward,
    powerScore     = powerScore,
    status         = status,
    dungeonId      = dungeonId,
    estimatedHours = estimatedHours,
    actualHours    = actualHours,
    createdAt      = createdAt,
    completedAt    = completedAt,
    rarityColor    = rarityColor,
    isInstantGate  = isInstantGate,
    description    = description,
    trackId        = trackId,
    scheduleBlockId = scheduleBlockId,
    qualityScore   = qualityScore,
    effectiveHours = effectiveHours
)

fun Mission.toEntity(): MissionEntity = MissionEntity(
    id             = id,
    title          = title,
    track          = track,
    rarity         = rarity,
    skillId        = skillId,
    skillName      = skillName,
    xpReward       = xpReward,
    powerScore     = powerScore,
    status         = status,
    dungeonId      = dungeonId,
    estimatedHours = estimatedHours,
    actualHours    = actualHours,
    createdAt      = createdAt,
    completedAt    = completedAt,
    rarityColor    = rarityColor,
    isInstantGate  = isInstantGate,
    description    = description,
    trackId        = trackId,
    scheduleBlockId = scheduleBlockId,
    qualityScore   = qualityScore,
    effectiveHours = effectiveHours
)

// ───────────────────────────────────────────────────────────────
// DUNGEON
// ───────────────────────────────────────────────────────────────

fun DungeonEntity.toDomain(): Dungeon = Dungeon(
    id               = id,
    name             = name,
    description      = description,
    rarity           = rarity,
    totalStages      = totalStages,
    completedStages  = completedStages,
    isBossDefeated   = isBossDefeated,
    createdAt        = createdAt,
    completedAt      = completedAt,
    stageDescriptions = stageDescriptions
)

fun Dungeon.toEntity(): DungeonEntity = DungeonEntity(
    id               = id,
    name             = name,
    description      = description,
    rarity           = rarity,
    totalStages      = totalStages,
    completedStages  = completedStages,
    isBossDefeated   = isBossDefeated,
    createdAt        = createdAt,
    completedAt      = completedAt,
    stageDescriptions = stageDescriptions
)

// ───────────────────────────────────────────────────────────────
// SKILL
// ───────────────────────────────────────────────────────────────

fun SkillEntity.toDomain(): Skill = Skill(
    id                  = id,
    name                = name,
    category            = category,
    currentXP           = currentXP,
    level               = level,
    rankLabel           = "MASTERY",
    parentId            = parentId,
    isUnlocked          = isUnlocked,
    xpToNextRank        = xpToNextRank,
    rankProgressPercent = rankProgressPercent,
    isShadowCandidate   = false,
    rankColor           = 0xFF1D9E75L,
    trackId             = trackId,
    totalRawHours       = totalRawHours,
    totalEffectiveHours = totalEffectiveHours
)

fun Skill.toEntity(): SkillEntity = SkillEntity(
    id                  = id,
    name                = name,
    category            = category,
    currentXP           = currentXP,
    level               = level,
    rankLabel           = rankLabel,
    parentId            = parentId,
    isUnlocked          = isUnlocked,
    xpToNextRank        = xpToNextRank,
    rankProgressPercent = rankProgressPercent,
    isShadowCandidate   = isShadowCandidate,
    rankColor           = rankColor,
    trackId             = trackId,
    totalRawHours       = totalRawHours,
    totalEffectiveHours = totalEffectiveHours
)

// ───────────────────────────────────────────────────────────────
// SHADOW
// ───────────────────────────────────────────────────────────────

fun ShadowEntity.toDomain(): Shadow = Shadow(
    id         = id,
    name       = name,
    skillId    = skillId,
    rankLabel  = rankLabel,
    acquiredAt = acquiredAt,
    skillCategory = skillCategory
)

fun Shadow.toEntity(): ShadowEntity = ShadowEntity(
    id         = id,
    name       = name,
    skillId    = skillId,
    rankLabel  = rankLabel,
    acquiredAt = acquiredAt,
    skillCategory = skillCategory
)
