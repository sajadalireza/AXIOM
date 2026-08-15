package com.axiom.app.domain.analytics

/**
 * WP-206 — three-state analytics consent. This is the ONLY consent vocabulary for the
 * analytics/telemetry pipeline (Decision A). Default is [UNKNOWN] (never assume consent).
 *
 * - [UNKNOWN]  local retain is allowed, but network upload MUST be 0.
 * - [GRANTED]  events are eligible for dispatch (at-least-once while granted).
 * - [DECLINED] already-queued analytics are deleted, future analytics are not collected/enqueued,
 *              and future upload MUST be 0.
 */
enum class AnalyticsConsentState { UNKNOWN, GRANTED, DECLINED }

/**
 * Pure per-event routing verdict produced by [ConsentDecisionEngine]. No network, no I/O — this
 * is a total function of [AnalyticsConsentState] only.
 *
 * - [HOLD]          retain locally, do NOT upload (UNKNOWN).
 * - [SEND_ELIGIBLE] enqueue and dispatch (GRANTED).
 * - [DELETE]        purge queued analytics and do not collect (DECLINED).
 */
enum class QueueDecision { HOLD, SEND_ELIGIBLE, DELETE }
