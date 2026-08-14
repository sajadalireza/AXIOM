package com.axiom.app.data.analytics

import com.axiom.app.data.local.AxiomDatabase
import com.axiom.app.data.local.entity.EventQueueEntity
import com.axiom.app.domain.analytics.AnalyticsEventStore
import com.axiom.app.domain.analytics.AnalyticsPayloadPolicy
import com.axiom.app.domain.analytics.QueuedAnalyticsEvent
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * WP-206 — Room-backed [AnalyticsEventStore] over the existing v18 `event_queue` table. Reads /
 * deletes / purges ONLY analytics event types (see [AnalyticsPayloadPolicy.ANALYTICS_EVENT_TYPES],
 * which includes the WP-205 First-Win causal event); functional / non-analytics rows are never
 * touched (§26 purge-predicate scope). No schema change.
 */
@Singleton
class RoomAnalyticsEventStore @Inject constructor(
    private val database: AxiomDatabase
) : AnalyticsEventStore {

    private val analyticsTypes: List<String> = AnalyticsPayloadPolicy.ANALYTICS_EVENT_TYPES.toList()

    override suspend fun pendingAnalytics(): List<QueuedAnalyticsEvent> =
        database.eventQueueDao().getPendingByTypes(analyticsTypes).map { it.toQueued() }

    override suspend fun delete(eventId: String) {
        database.eventQueueDao().deleteById(eventId)
    }

    override suspend fun purgeAnalytics(): Int =
        database.eventQueueDao().purgeByTypes(analyticsTypes)

    private fun EventQueueEntity.toQueued(): QueuedAnalyticsEvent {
        val props = runCatching {
            val obj = JSONObject(payload.ifBlank { "{}" })
            obj.keys().asSequence().associateWith { key -> obj.opt(key)?.toString() ?: "" }
        }.getOrDefault(emptyMap())
        return QueuedAnalyticsEvent(
            eventId = eventId,
            idempotencyKey = idempotencyKey,
            eventType = eventType,
            properties = props
        )
    }
}
