package com.pxworld.server

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.UUID

class Database(url: String, user: String?, password: String?) : AutoCloseable {

    private val source = HikariDataSource(HikariConfig().apply {
        jdbcUrl = url
        user?.let { username = it }
        password?.let { this.password = it }
        maximumPoolSize = POOL_SIZE
        isAutoCommit = true
    })

    fun migrate() {
        transaction { connection ->
            connection.createStatement().use { it.execute("CREATE TABLE IF NOT EXISTS schema_version (version INT PRIMARY KEY, applied_at BIGINT NOT NULL)") }
            val applied = connection.createStatement().use { statement ->
                statement.executeQuery("SELECT version FROM schema_version").use { rows -> generateSequence { if (rows.next()) rows.getInt(1) else null }.toSet() }
            }
            MIGRATIONS.filter { (version, _) -> version !in applied }.forEach { (version, resource) ->
                val sql = Database::class.java.getResource(resource)?.readText() ?: error("missing migration $resource")
                sql.split(';').map { it.trim() }.filter { it.isNotEmpty() }.forEach { statementText ->
                    connection.createStatement().use { it.execute(statementText) }
                }
                connection.prepareStatement("INSERT INTO schema_version (version, applied_at) VALUES (?, ?)").use {
                    it.setInt(1, version)
                    it.setLong(2, System.currentTimeMillis())
                    it.executeUpdate()
                }
            }
        }
    }

    fun <T> transaction(block: (Connection) -> T): T = source.connection.use { connection ->
        connection.autoCommit = false
        try {
            block(connection).also { connection.commit() }
        } catch (failure: Throwable) {
            connection.rollback()
            throw failure
        }
    }

    fun update(sql: String, vararg parameters: Any?): Int = transaction { connection -> connection.prepared(sql, parameters).use { it.executeUpdate() } }

    fun <T> query(sql: String, vararg parameters: Any?, map: (ResultSet) -> T): List<T> = transaction { connection ->
        connection.prepared(sql, parameters).use { statement ->
            statement.executeQuery().use { rows -> generateSequence { if (rows.next()) map(rows) else null }.toList() }
        }
    }

    fun <T> single(sql: String, vararg parameters: Any?, map: (ResultSet) -> T): T? = query(sql, *parameters, map = map).firstOrNull()

    override fun close() = source.close()

    companion object {
        const val POOL_SIZE: Int = 8
        val MIGRATIONS: List<Pair<Int, String>> = listOf(
            1 to "/migrations/V1__init.sql",
            2 to "/migrations/V2__telemetry_time_indexes.sql",
        )

        fun newId(): String = UUID.randomUUID().toString()
    }
}

fun Connection.prepared(sql: String, parameters: Array<out Any?>): PreparedStatement =
    prepareStatement(sql).apply {
        parameters.forEachIndexed { index, value ->
            when (value) {
                null -> setObject(index + 1, null)
                is String -> setString(index + 1, value)
                is Int -> setInt(index + 1, value)
                is Long -> setLong(index + 1, value)
                is Boolean -> setBoolean(index + 1, value)
                is Double -> setDouble(index + 1, value)
                else -> setString(index + 1, value.toString())
            }
        }
    }
