package com.pxworld.server

import io.ktor.http.Parameters
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import java.util.Base64

@Serializable data class TelemetryNameCountView(val name: String, val count: Int)
@Serializable data class TelemetryVersionCountView(val clientVersion: String?, val count: Int)
@Serializable data class TelemetrySeriesView(val name: String, val counts: List<Int>)
@Serializable data class TelemetrySummaryView(
    val from: Long,
    val to: Long,
    val bucket: String,
    val bucketMillis: Long,
    val bucketStarts: List<Long>,
    val total: Int,
    val distinctAccounts: Int,
    val anonymousEvents: Int,
    val totals: List<TelemetryNameCountView>,
    val series: List<TelemetrySeriesView>,
    val clientVersions: List<TelemetryVersionCountView>,
    val availableNames: List<TelemetryNameCountView>,
    val availableClientVersions: List<TelemetryVersionCountView>,
)
@Serializable data class TelemetryEventView(val id: String, val accountId: String?, val name: String, val clientVersion: String?, val payload: JsonElement, val createdAt: Long)
@Serializable data class TelemetryEventPageView(val events: List<TelemetryEventView>, val nextCursor: String?)

enum class TelemetryBucket(val id: String, val millis: Long) {
    MINUTE("minute", 60_000L),
    HOUR("hour", 60 * 60_000L),
    DAY("day", 24 * 60 * 60_000L),
    ;

    companion object {
        fun of(id: String): TelemetryBucket = entries.firstOrNull { it.id == id } ?: throw IllegalArgumentException("bucket must be one of ${entries.map { it.id }}")

        fun forRange(millis: Long): TelemetryBucket = when {
            millis <= MINUTE_BUCKETS_UP_TO_HOURS * HOUR.millis -> MINUTE
            millis <= HOUR_BUCKETS_UP_TO_DAYS * DAY.millis -> HOUR
            else -> DAY
        }

        private const val MINUTE_BUCKETS_UP_TO_HOURS = 6
        private const val HOUR_BUCKETS_UP_TO_DAYS = 7
    }
}

fun Route.telemetryExplorer(repositories: Repositories) {
    route("/telemetry") {
        get("/summary") {
            call.requireRole(*TELEMETRY_READERS)
            val filter = call.request.queryParameters.telemetryFilter().copy(accountId = null)
            val bucket = call.request.queryParameters.optional("bucket")?.let(TelemetryBucket::of) ?: TelemetryBucket.forRange(filter.to - filter.from)
            call.respond(repositories.telemetrySummary(filter, bucket))
        }
        get("/events") {
            call.requireRole(*TELEMETRY_READERS)
            val parameters = call.request.queryParameters
            val filter = parameters.telemetryFilter()
            val requested = parameters.optional("limit")?.let { it.toIntOrNull() ?: throw IllegalArgumentException("limit must be a number") } ?: DEFAULT_EVENT_PAGE
            require(requested >= 1) { "limit must be at least 1" }
            val limit = requested.coerceAtMost(MAX_EVENT_PAGE)
            val rows = repositories.telemetryEvents(filter, parameters.optional("cursor")?.let(::decodeCursor), limit + 1)
            val page = rows.take(limit)
            val nextCursor = if (rows.size > limit) page.last().let { encodeCursor(TelemetryPosition(it.createdAt, it.id)) } else null
            call.respond(TelemetryEventPageView(page.map { it.view() }, nextCursor))
        }
    }
}

private fun Repositories.telemetrySummary(filter: TelemetryFilter, bucket: TelemetryBucket): TelemetrySummaryView {
    val start = Math.floorDiv(filter.from, bucket.millis) * bucket.millis
    val bucketCount = (filter.to - start + bucket.millis - 1) / bucket.millis
    require(bucketCount <= MAX_BUCKETS) { "range needs $bucketCount ${bucket.id} buckets; at most $MAX_BUCKETS are allowed, choose a larger bucket" }
    val countsByName = sortedMapOf<String, IntArray>()
    telemetryBucketCounts(filter, start, bucket.millis).forEach { row ->
        countsByName.getOrPut(row.name) { IntArray(bucketCount.toInt()) }[row.bucketIndex] += row.count
    }
    val series = countsByName.map { (name, counts) -> TelemetrySeriesView(name, counts.toList()) }.sortedByDescending { it.counts.sum() }
    val breakdown = telemetryBreakdown(filter.from, filter.to)
    val selected = breakdown.filter { row -> (filter.name == null || row.name == filter.name) && (filter.clientVersion == null || row.clientVersion == filter.clientVersion) }
    val spread = telemetryAccountSpread(filter)
    return TelemetrySummaryView(
        from = filter.from,
        to = filter.to,
        bucket = bucket.id,
        bucketMillis = bucket.millis,
        bucketStarts = List(bucketCount.toInt()) { index -> start + index * bucket.millis },
        total = series.sumOf { it.counts.sum() },
        distinctAccounts = spread.distinctAccounts,
        anonymousEvents = spread.anonymousEvents,
        totals = series.map { TelemetryNameCountView(it.name, it.counts.sum()) },
        series = series,
        clientVersions = versionCounts(selected),
        availableNames = breakdown.groupBy { it.name }.map { (name, rows) -> TelemetryNameCountView(name, rows.sumOf { it.count }) }.sortedWith(compareByDescending<TelemetryNameCountView> { it.count }.thenBy { it.name }),
        availableClientVersions = versionCounts(breakdown),
    )
}

private fun versionCounts(rows: List<TelemetryBreakdownRow>): List<TelemetryVersionCountView> =
    rows.groupBy { it.clientVersion }
        .map { (version, group) -> TelemetryVersionCountView(version, group.sumOf { it.count }) }
        .sortedWith(compareByDescending<TelemetryVersionCountView> { it.count }.thenBy(nullsLast()) { it.clientVersion })

private fun TelemetryEventRow.view() = TelemetryEventView(id, accountId, name, clientVersion, ApiJson.parseToJsonElement(payload), createdAt)

private fun Parameters.telemetryFilter(): TelemetryFilter {
    val from = epochMillis("from")
    val to = epochMillis("to")
    require(from < to) { "from must be before to" }
    require(to - from <= MAX_RANGE_DAYS * TelemetryBucket.DAY.millis) { "range must span at most $MAX_RANGE_DAYS days" }
    return TelemetryFilter(from, to, optional("name"), optional("clientVersion"), optional("accountId"))
}

private fun Parameters.epochMillis(key: String): Long =
    optional(key)?.toLongOrNull()?.takeIf { it >= 0 } ?: throw IllegalArgumentException("$key must be epoch milliseconds")

private fun Parameters.optional(key: String): String? = this[key]?.trim()?.takeIf { it.isNotEmpty() }

private fun encodeCursor(position: TelemetryPosition): String =
    Base64.getUrlEncoder().withoutPadding().encodeToString("${position.createdAt}:${position.id}".toByteArray())

private fun decodeCursor(cursor: String): TelemetryPosition {
    val text = runCatching { String(Base64.getUrlDecoder().decode(cursor)) }.getOrNull()
    val createdAt = text?.substringBefore(':')?.toLongOrNull()
    val id = text?.substringAfter(':', "")
    require(createdAt != null && !id.isNullOrEmpty()) { "invalid cursor" }
    return TelemetryPosition(createdAt, id)
}

private val TELEMETRY_READERS = arrayOf(Roles.LIVEOPS, Roles.DEV, Roles.QA)
private const val MAX_RANGE_DAYS = 90
private const val MAX_BUCKETS = 1_500
private const val DEFAULT_EVENT_PAGE = 50
private const val MAX_EVENT_PAGE = 200
