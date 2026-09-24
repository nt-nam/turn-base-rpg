package com.pxworld.server

import java.io.File

data class ServerConfig(
    val port: Int,
    val env: String,
    val databaseUrl: String,
    val databaseUser: String?,
    val databasePassword: String?,
    val jwtSecret: String,
    val jwtIssuer: String,
    val contentDir: File,
    val legacyAssetsDir: File,
    val contentWritable: Boolean,
    val adminEmail: String?,
    val adminPassword: String?,
    val corsHosts: List<String>,
) {
    companion object {
        const val DEV_SECRET: String = "pxworld-dev-secret-change-me"

        fun fromEnvironment(read: (String) -> String? = System::getenv): ServerConfig {
            val env = read("PXWORLD_ENV") ?: "dev"
            val secret = read("PXWORLD_JWT_SECRET") ?: DEV_SECRET
            require(env == "dev" || secret != DEV_SECRET) { "PXWORLD_JWT_SECRET must be set outside dev" }
            return ServerConfig(
                port = read("PXWORLD_PORT")?.toInt() ?: 8080,
                env = env,
                databaseUrl = read("PXWORLD_DB_URL") ?: "jdbc:h2:file:./build/pxworld-dev;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
                databaseUser = read("PXWORLD_DB_USER"),
                databasePassword = read("PXWORLD_DB_PASSWORD"),
                jwtSecret = secret,
                jwtIssuer = "pxworld-$env",
                contentDir = File(read("PXWORLD_CONTENT_DIR") ?: "content"),
                legacyAssetsDir = File(read("PXWORLD_LEGACY_ASSETS_DIR") ?: "assets"),
                contentWritable = (read("PXWORLD_CONTENT_WRITABLE") ?: if (env == "dev") "true" else "false").toBoolean(),
                adminEmail = read("PXWORLD_ADMIN_EMAIL") ?: if (env == "dev") "admin@pxworld.local" else null,
                adminPassword = read("PXWORLD_ADMIN_PASSWORD") ?: if (env == "dev") "admin-dev-password" else null,
                corsHosts = (read("PXWORLD_CORS_HOSTS") ?: "localhost:5173").split(',').map { it.trim() }.filter { it.isNotEmpty() },
            )
        }
    }
}
