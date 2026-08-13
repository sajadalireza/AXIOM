package com.axiom.app.data

import com.axiom.app.domain.model.KeyRelationship
import com.axiom.app.domain.model.MajorMilestone

/**
 * WP-202 (PO Decision 2): the identifiably-personal blueprint payload that USED to be
 * seeded automatically for every fresh install by the old `seedDefaultProfileIfNeeded`
 * bootstrap (now neutralized to [SeedDataHelper.seedReferenceCatalogsIfNeeded]).
 *
 * It is relocated here into the `src/test` source set so that:
 *  - it NEVER ships in any APK (test source is excluded from all app variants), and
 *  - it can no longer execute automatically on a fresh install.
 *
 * It survives only as an explicitly isolated development fixture / opt-in template for
 * exercising the personal-profile code paths in tests. Production bootstrap must never
 * reference this object.
 */
object PersonalBlueprintTestFixture {

    /** Personal, identity-bearing milestone (career/research decision). Dev-only. */
    val MAJOR_MILESTONE = MajorMilestone(
        id = "milestone_vehicle_decision",
        label = "Vehicle Decision — Path A vs Path B",
        targetDate = 0L, // fixed in fixture; real bootstrap must not seed this
        description = "Make the vital decision between Path A (Academia/Munich research) or Path B (direct commercial venture)."
    )

    /** Personal, identity-bearing named relationships. Dev-only. */
    val KEY_RELATIONSHIPS = listOf(
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
}
