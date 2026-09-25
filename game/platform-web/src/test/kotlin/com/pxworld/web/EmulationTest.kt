package com.pxworld.web

import emulate.java.lang.IntegerUnsignedEmulation
import emulate.java.lang.LongUnsignedEmulation
import java.security.MessageDigest
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import emu.java.security.MessageDigest as BrowserMessageDigest
import emu.java.util.concurrent.ConcurrentLinkedQueue as BrowserQueue

class EmulationTest {

    @Test
    fun `sha-256 matches the jvm for every padding boundary`() {
        val random = Random(SEED)
        (0..300).forEach { length ->
            val message = random.nextBytes(length)
            assertContentEquals(MessageDigest.getInstance("SHA-256").digest(message), BrowserMessageDigest.getInstance("SHA-256").digest(message), "length $length")
        }
    }

    @Test
    fun `sha-256 accepts incremental updates and resets after digest`() {
        val digest = BrowserMessageDigest.getInstance("SHA-256")
        digest.update("pxworld ".toByteArray())
        digest.update("save".toByteArray(), 0, 4)
        assertContentEquals(MessageDigest.getInstance("SHA-256").digest("pxworld save".toByteArray()), digest.digest())
        assertContentEquals(MessageDigest.getInstance("SHA-256").digest(ByteArray(0)), digest.digest())
    }

    @Test
    fun `unsupported digests are rejected like the jvm`() {
        assertFailsWith<java.security.NoSuchAlgorithmException> { BrowserMessageDigest.getInstance("MD5") }
    }

    @Test
    fun `unsigned strings match the jvm`() {
        val random = Random(SEED)
        val longs = listOf(0L, 1L, -1L, Long.MIN_VALUE, Long.MAX_VALUE, -10L, 9L) + List(1000) { random.nextLong() }
        longs.forEach { assertEquals(it.toULong().toString(), LongUnsignedEmulation.toUnsignedString(it)) }
        val ints = listOf(0, 1, -1, Int.MIN_VALUE, Int.MAX_VALUE) + List(1000) { random.nextInt() }
        ints.forEach { assertEquals(it.toUInt().toString(), IntegerUnsignedEmulation.toUnsignedString(it)) }
    }

    @Test
    fun `queue keeps insertion order`() {
        val queue = BrowserQueue<Int>()
        queue += listOf(3, 1, 2)
        assertEquals(3, queue.peek())
        assertEquals(listOf(3, 1, 2), generateSequence { queue.poll() }.toList())
        assertEquals(0, queue.size)
    }

    companion object {
        const val SEED: Int = 20260926
    }
}
