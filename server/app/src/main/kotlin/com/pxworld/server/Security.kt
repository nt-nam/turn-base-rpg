package com.pxworld.server

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import java.security.SecureRandom
import java.util.Base64
import java.util.Date
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object Roles {
    const val PLAYER = "player"
    const val ADMIN = "staff_admin"
    const val SUPPORT = "staff_support"
    const val LIVEOPS = "staff_liveops"
    const val CREATOR = "staff_creator"
    const val QA = "staff_qa"
    const val DEV = "staff_dev"
    val STAFF: Set<String> = setOf(ADMIN, SUPPORT, LIVEOPS, CREATOR, QA, DEV)
}

object Passwords {
    private const val ITERATIONS = 120_000
    private const val KEY_BITS = 256
    private val random = SecureRandom()

    fun hash(password: String): String {
        val salt = ByteArray(16).also(random::nextBytes)
        return "pbkdf2$$ITERATIONS$${encode(salt)}$${encode(derive(password, salt, ITERATIONS))}"
    }

    fun verify(password: String, stored: String): Boolean {
        val parts = stored.split('$')
        if (parts.size != 4 || parts[0] != "pbkdf2") return false
        val expected = decode(parts[3])
        val actual = derive(password, decode(parts[2]), parts[1].toInt())
        return java.security.MessageDigest.isEqual(expected, actual)
    }

    private fun derive(password: String, salt: ByteArray, iterations: Int): ByteArray =
        SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(PBEKeySpec(password.toCharArray(), salt, iterations, KEY_BITS)).encoded

    private fun encode(bytes: ByteArray): String = Base64.getEncoder().withoutPadding().encodeToString(bytes)
    private fun decode(text: String): ByteArray = Base64.getDecoder().decode(text)
}

class Tokens(secret: String, private val issuer: String, private val lifetimeMillis: Long = DEFAULT_LIFETIME) {

    val algorithm: Algorithm = Algorithm.HMAC256(secret)
    val verifier = JWT.require(algorithm).withIssuer(issuer).build()

    fun issue(account: AccountRow, now: Long): String = JWT.create()
        .withIssuer(issuer)
        .withSubject(account.id)
        .withClaim(ROLES, account.roles.toList())
        .withClaim(NAME, account.displayName)
        .withIssuedAt(Date(now))
        .withExpiresAt(Date(now + lifetimeMillis))
        .sign(algorithm)

    companion object {
        const val ROLES = "roles"
        const val NAME = "name"
        const val DEFAULT_LIFETIME: Long = 12 * 60 * 60 * 1000L
    }
}

class Forbidden(message: String) : RuntimeException(message)

class Unauthenticated(message: String) : RuntimeException(message)

data class Caller(val accountId: String, val roles: Set<String>) {
    fun has(role: String): Boolean = role in roles || Roles.ADMIN in roles
    val isStaff: Boolean get() = roles.any { it in Roles.STAFF }
}

fun ApplicationCall.caller(): Caller {
    val principal = principal<JWTPrincipal>() ?: throw Forbidden("authentication required")
    return Caller(principal.subject ?: throw Forbidden("token has no subject"), principal.payload.getClaim(Tokens.ROLES).asList(String::class.java).orEmpty().toSet())
}

fun ApplicationCall.requireRole(vararg roles: String): Caller {
    val caller = caller()
    if (roles.none(caller::has)) throw Forbidden("requires one of ${roles.joinToString()}")
    return caller
}
