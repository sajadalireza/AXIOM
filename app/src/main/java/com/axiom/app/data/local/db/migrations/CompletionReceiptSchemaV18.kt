package com.axiom.app.data.local.db.migrations

/**
 * WP-205 (PROMPT 41B) — single source of truth for the additive v17 -> v18 DDL that
 * enriches the existing `completion_receipt` table into the canonical rich First-Win
 * completion receipt (design spec §7).
 *
 * Both the runtime [MIGRATION_17_18] object and the JVM contract test
 * (`MigrationV17V18SqliteTest`) consume THIS list, so the test proves the exact
 * statements shipped in production — not a hand-copied paraphrase.
 *
 * Strictly additive and non-destructive: only `ALTER TABLE ... ADD COLUMN`. No DROP,
 * no DELETE, no table rebuild, no row rewrite, no DataStore read, no backfill. Every
 * added column is NULLABLE with no default, so pre-existing v17 rows keep their exact
 * values and receive NULL for the rich fields — historical XP / streak are never
 * fabricated (DECISION 3). No second receipt table is created (DECISION 1).
 *
 * The column list, order, affinity, and nullability are kept identical to Room's
 * exported 18.json for `completion_receipt`, so Room's runtime schema validation
 * accepts a database migrated by these statements.
 */
object CompletionReceiptSchemaV18 {
    val STATEMENTS: List<String> = listOf(
        "ALTER TABLE `completion_receipt` ADD COLUMN `sessionId` TEXT",
        "ALTER TABLE `completion_receipt` ADD COLUMN `missionId` TEXT",
        "ALTER TABLE `completion_receipt` ADD COLUMN `hunterXpAwarded` INTEGER",
        "ALTER TABLE `completion_receipt` ADD COLUMN `skillXpAwarded` INTEGER",
        "ALTER TABLE `completion_receipt` ADD COLUMN `resultingStreak` INTEGER",
        "ALTER TABLE `completion_receipt` ADD COLUMN `completedAt` INTEGER"
    )
}
