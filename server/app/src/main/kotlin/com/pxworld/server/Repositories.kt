package com.pxworld.server

import kotlinx.serialization.Serializable

data class AccountRow(
    val id: String,
    val email: String?,
    val passwordHash: String?,
    val displayName: String,
    val kind: String,
    val roles: Set<String>,
    val bannedUntil: Long?,
    val createdAt: Long,
)

@Serializable
data class SaveRow(val slot: String, val revision: Long, val updatedAt: Long, val body: String)

@Serializable
data class ContentReleaseRow(val version: String, val sha256: String, val bytes: Long, val records: Int, val publishedBy: String, val createdAt: Long)

@Serializable
data class AuditRow(val id: String, val actorId: String, val action: String, val target: String, val reason: String, val payload: String, val createdAt: Long)

@Serializable
data class MailRow(val id: String, val subject: String, val grants: String, val claimed: Boolean, val createdAt: Long)

@Serializable
data class AgentRunRow(val id: String, val mode: String, val status: String, val visited: Int, val registered: Int, val launchPercent: Double, val createdAt: Long)

data class TelemetryFilter(val from: Long, val to: Long, val name: String? = null, val clientVersion: String? = null, val accountId: String? = null) {
    fun clause(): Pair<String, Array<Any?>> {
        val conditions = mutableListOf("created_at >= ?", "created_at < ?")
        val parameters = mutableListOf<Any?>(from, to)
        name?.let { conditions += "name = ?"; parameters += it }
        clientVersion?.let { conditions += "client_version = ?"; parameters += it }
        accountId?.let { conditions += "account_id = ?"; parameters += it }
        return conditions.joinToString(" AND ") to parameters.toTypedArray()
    }
}

data class TelemetryBucketCount(val name: String, val bucketIndex: Int, val count: Int)

data class TelemetryBreakdownRow(val name: String, val clientVersion: String?, val count: Int)

data class TelemetryAccountSpread(val distinctAccounts: Int, val anonymousEvents: Int)

data class TelemetryPosition(val createdAt: Long, val id: String)

data class TelemetryEventRow(val id: String, val accountId: String?, val name: String, val payload: String, val clientVersion: String?, val createdAt: Long)

class Repositories(private val db: Database, private val clock: () -> Long) {

    fun createAccount(email: String?, passwordHash: String?, displayName: String, kind: String, roles: Set<String>): AccountRow {
        val row = AccountRow(Database.newId(), email?.lowercase(), passwordHash, displayName, kind, roles, null, clock())
        db.update(
            "INSERT INTO accounts (id, email, password_hash, display_name, kind, roles, banned_until, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
            row.id, row.email, row.passwordHash, row.displayName, row.kind, row.roles.sorted().joinToString(","), null, row.createdAt,
        )
        return row
    }

    fun accountByEmail(email: String): AccountRow? = db.single("SELECT * FROM accounts WHERE email = ?", email.lowercase(), map = ::account)
    fun account(id: String): AccountRow? = db.single("SELECT * FROM accounts WHERE id = ?", id, map = ::account)
    fun staffCount(): Int = db.single("SELECT COUNT(*) FROM accounts WHERE kind = 'staff'") { it.getInt(1) } ?: 0

    fun searchAccounts(query: String, limit: Int): List<AccountRow> =
        db.query(
            "SELECT * FROM accounts WHERE LOWER(COALESCE(email, '')) LIKE ? OR LOWER(display_name) LIKE ? OR id = ? ORDER BY created_at DESC LIMIT ?",
            "%${query.lowercase()}%", "%${query.lowercase()}%", query, limit, map = ::account,
        )

    fun setBan(accountId: String, until: Long?) = db.update("UPDATE accounts SET banned_until = ? WHERE id = ?", until, accountId)

    fun saves(accountId: String): List<SaveRow> =
        db.query("SELECT slot, revision, updated_at, body FROM saves WHERE account_id = ? ORDER BY slot", accountId) { SaveRow(it.getString(1), it.getLong(2), it.getLong(3), it.getString(4)) }

    fun save(accountId: String, slot: String): SaveRow? =
        db.single("SELECT slot, revision, updated_at, body FROM saves WHERE account_id = ? AND slot = ?", accountId, slot) { SaveRow(it.getString(1), it.getLong(2), it.getLong(3), it.getString(4)) }

    fun putSave(accountId: String, slot: String, expectedRevision: Long, body: String): SaveRow? = db.transaction { connection ->
        val current = connection.prepared("SELECT revision FROM saves WHERE account_id = ? AND slot = ?", arrayOf<Any?>(accountId, slot)).use { statement ->
            statement.executeQuery().use { rows -> if (rows.next()) rows.getLong(1) else 0L }
        }
        if (current != expectedRevision) return@transaction null
        val now = clock()
        val next = current + 1
        if (current == 0L) {
            connection.prepared("INSERT INTO saves (account_id, slot, revision, body, updated_at) VALUES (?, ?, ?, ?, ?)", arrayOf<Any?>(accountId, slot, next, body, now)).use { it.executeUpdate() }
        } else {
            connection.prepared("UPDATE saves SET revision = ?, body = ?, updated_at = ? WHERE account_id = ? AND slot = ?", arrayOf<Any?>(next, body, now, accountId, slot)).use { it.executeUpdate() }
        }
        connection.prepared("INSERT INTO save_history (id, account_id, slot, revision, body, created_at) VALUES (?, ?, ?, ?, ?, ?)", arrayOf<Any?>(Database.newId(), accountId, slot, next, body, now)).use { it.executeUpdate() }
        connection.prepared(
            "DELETE FROM save_history WHERE account_id = ? AND slot = ? AND revision <= ?",
            arrayOf<Any?>(accountId, slot, next - HISTORY_DEPTH),
        ).use { it.executeUpdate() }
        SaveRow(slot, next, now, body)
    }

    fun saveHistory(accountId: String, slot: String): List<SaveRow> =
        db.query("SELECT slot, revision, created_at, body FROM save_history WHERE account_id = ? AND slot = ? ORDER BY revision DESC", accountId, slot) {
            SaveRow(it.getString(1), it.getLong(2), it.getLong(3), it.getString(4))
        }

    fun addRelease(version: String, sha256: String, body: String, records: Int, publishedBy: String) {
        if (db.single("SELECT version FROM content_releases WHERE version = ?", version) { it.getString(1) } != null) return
        db.update(
            "INSERT INTO content_releases (version, sha256, body, bytes, records, published_by, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)",
            version, sha256, body, body.toByteArray().size.toLong(), records, publishedBy, clock(),
        )
    }

    fun releases(): List<ContentReleaseRow> =
        db.query("SELECT version, sha256, bytes, records, published_by, created_at FROM content_releases ORDER BY created_at DESC") {
            ContentReleaseRow(it.getString(1), it.getString(2), it.getLong(3), it.getInt(4), it.getString(5), it.getLong(6))
        }

    fun releaseBody(version: String): String? = db.single("SELECT body FROM content_releases WHERE version = ?", version) { it.getString(1) }

    fun promote(env: String, version: String, actorId: String) = db.transaction { connection ->
        connection.prepared("DELETE FROM content_channels WHERE env = ?", arrayOf<Any?>(env)).use { it.executeUpdate() }
        connection.prepared("INSERT INTO content_channels (env, version, promoted_by, promoted_at) VALUES (?, ?, ?, ?)", arrayOf<Any?>(env, version, actorId, clock())).use { it.executeUpdate() }
    }

    fun channels(): Map<String, String> = db.query("SELECT env, version FROM content_channels") { it.getString(1) to it.getString(2) }.toMap()

    fun addTelemetry(accountId: String?, name: String, payload: String, clientVersion: String?) =
        db.update("INSERT INTO telemetry_events (id, account_id, name, payload, client_version, created_at) VALUES (?, ?, ?, ?, ?, ?)", Database.newId(), accountId, name, payload, clientVersion, clock())

    fun telemetryByName(since: Long): Map<String, Int> =
        db.query("SELECT name, COUNT(*) FROM telemetry_events WHERE created_at >= ? GROUP BY name", since) { it.getString(1) to it.getInt(2) }.toMap()

    fun telemetryBucketCounts(filter: TelemetryFilter, start: Long, width: Long): List<TelemetryBucketCount> {
        val (where, parameters) = filter.clause()
        return db.query(
            "SELECT name, bucket_index, COUNT(*) FROM (SELECT name, (created_at - CAST(? AS BIGINT)) / CAST(? AS BIGINT) AS bucket_index FROM telemetry_events WHERE $where) bucketed GROUP BY name, bucket_index",
            start, width, *parameters,
        ) { TelemetryBucketCount(it.getString(1), it.getLong(2).toInt(), it.getInt(3)) }
    }

    fun telemetryBreakdown(from: Long, to: Long): List<TelemetryBreakdownRow> =
        db.query("SELECT name, client_version, COUNT(*) FROM telemetry_events WHERE created_at >= ? AND created_at < ? GROUP BY name, client_version", from, to) {
            TelemetryBreakdownRow(it.getString(1), it.getString(2), it.getInt(3))
        }

    fun telemetryAccountSpread(filter: TelemetryFilter): TelemetryAccountSpread {
        val (where, parameters) = filter.clause()
        return db.single("SELECT COUNT(DISTINCT account_id), COUNT(*) - COUNT(account_id) FROM telemetry_events WHERE $where", *parameters) {
            TelemetryAccountSpread(it.getInt(1), it.getInt(2))
        } ?: TelemetryAccountSpread(0, 0)
    }

    fun telemetryEvents(filter: TelemetryFilter, after: TelemetryPosition?, limit: Int): List<TelemetryEventRow> {
        val (where, parameters) = filter.clause()
        val keyset = after?.let { " AND (created_at < ? OR (created_at = ? AND id < ?))" }.orEmpty()
        val keysetParameters = after?.let { arrayOf<Any?>(it.createdAt, it.createdAt, it.id) } ?: emptyArray()
        return db.query(
            "SELECT id, account_id, name, payload, client_version, created_at FROM telemetry_events WHERE $where$keyset ORDER BY created_at DESC, id DESC LIMIT ?",
            *parameters, *keysetParameters, limit,
        ) { TelemetryEventRow(it.getString(1), it.getString(2), it.getString(3), it.getString(4), it.getString(5), it.getLong(6)) }
    }

    fun count(table: String, where: String = "1 = 1", vararg parameters: Any?): Int {
        require(table in COUNTABLE) { "table $table is not countable" }
        return db.single("SELECT COUNT(*) FROM $table WHERE $where", *parameters) { it.getInt(1) } ?: 0
    }

    fun audit(actorId: String, action: String, target: String, reason: String, payload: String) =
        db.update("INSERT INTO audit_log (id, actor_id, action, target, reason, payload, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)", Database.newId(), actorId, action, target, reason, payload, clock())

    fun auditLog(target: String?, limit: Int): List<AuditRow> =
        if (target == null) {
            db.query("SELECT * FROM audit_log ORDER BY seq DESC LIMIT ?", limit, map = ::auditRow)
        } else {
            db.query("SELECT * FROM audit_log WHERE target = ? ORDER BY seq DESC LIMIT ?", target, limit, map = ::auditRow)
        }

    fun addMail(accountId: String, subject: String, grants: String): String {
        val id = Database.newId()
        db.update("INSERT INTO mail (id, account_id, subject, grants, claimed, created_at) VALUES (?, ?, ?, ?, ?, ?)", id, accountId, subject, grants, false, clock())
        return id
    }

    fun mail(accountId: String): List<MailRow> =
        db.query("SELECT id, subject, grants, claimed, created_at FROM mail WHERE account_id = ? ORDER BY created_at DESC", accountId) {
            MailRow(it.getString(1), it.getString(2), it.getString(3), it.getBoolean(4), it.getLong(5))
        }

    fun claimMail(accountId: String, mailId: String): MailRow? = db.transaction { connection ->
        val row = connection.prepared("SELECT id, subject, grants, claimed, created_at FROM mail WHERE id = ? AND account_id = ?", arrayOf<Any?>(mailId, accountId)).use { statement ->
            statement.executeQuery().use { rows -> if (rows.next()) MailRow(rows.getString(1), rows.getString(2), rows.getString(3), rows.getBoolean(4), rows.getLong(5)) else null }
        } ?: return@transaction null
        if (row.claimed) return@transaction null
        connection.prepared("UPDATE mail SET claimed = TRUE WHERE id = ?", arrayOf<Any?>(mailId)).use { it.executeUpdate() }
        row
    }

    fun addBattleValidation(accountId: String, encounterId: String, claimed: String, replayed: String, valid: Boolean) =
        db.update(
            "INSERT INTO battle_validations (id, account_id, encounter_id, claimed_outcome, replayed_outcome, valid, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)",
            Database.newId(), accountId, encounterId, claimed, replayed, valid, clock(),
        )

    fun addAgentRun(mode: String, status: String, visited: Int, registered: Int, launchPercent: Double, report: String, uploadedBy: String): String {
        val id = Database.newId()
        db.update(
            "INSERT INTO agent_runs (id, mode, status, visited, registered, launch_percent, report, uploaded_by, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
            id, mode, status, visited, registered, launchPercent, report, uploadedBy, clock(),
        )
        return id
    }

    fun agentRuns(limit: Int): List<AgentRunRow> =
        db.query("SELECT id, mode, status, visited, registered, launch_percent, created_at FROM agent_runs ORDER BY created_at DESC LIMIT ?", limit) {
            AgentRunRow(it.getString(1), it.getString(2), it.getString(3), it.getInt(4), it.getInt(5), it.getDouble(6), it.getLong(7))
        }

    fun agentRunReport(id: String): String? = db.single("SELECT report FROM agent_runs WHERE id = ?", id) { it.getString(1) }

    private fun account(rows: java.sql.ResultSet) = AccountRow(
        id = rows.getString("id"),
        email = rows.getString("email"),
        passwordHash = rows.getString("password_hash"),
        displayName = rows.getString("display_name"),
        kind = rows.getString("kind"),
        roles = rows.getString("roles").split(',').filter { it.isNotBlank() }.toSet(),
        bannedUntil = rows.getLong("banned_until").takeIf { !rows.wasNull() },
        createdAt = rows.getLong("created_at"),
    )

    private fun auditRow(rows: java.sql.ResultSet) = AuditRow(
        rows.getString("id"), rows.getString("actor_id"), rows.getString("action"), rows.getString("target"),
        rows.getString("reason"), rows.getString("payload"), rows.getLong("created_at"),
    )

    companion object {
        const val HISTORY_DEPTH: Long = 30
        val COUNTABLE: Set<String> = setOf("accounts", "saves", "telemetry_events", "battle_validations", "mail", "agent_runs", "content_releases", "audit_log")
    }
}
