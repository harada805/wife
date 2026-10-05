package com.example.wife.agent

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class GeminiRepositoryTest {

    private val repository = GeminiRepository(GeminiService())

    @Test
    fun `maps status codes to sealed failures`() {
        assertTrue(repository.mapFailure(RuntimeException("HTTP 503 malformed {")).let { it is GeminiFailure.Overloaded })
        assertTrue(repository.mapFailure(RuntimeException("status=429 quota exceeded")).let { it is GeminiFailure.RateLimited })
        assertTrue(repository.mapFailure(RuntimeException("404 NOT_FOUND")).let { it is GeminiFailure.NotFound })
        assertTrue(repository.mapFailure(RuntimeException("400 invalid argument")).let { it is GeminiFailure.InvalidRequest })
        assertTrue(repository.mapFailure(IOException("timeout")).let { it is GeminiFailure.Network })
        assertTrue(repository.mapFailure(RuntimeException("something surprising")).let { it is GeminiFailure.Unknown })
    }

    @Test
    fun `retries overloaded failures three times with exponential backoff`() = runTest {
        var attempts = 0
        val delays = mutableListOf<Long>()

        val result = repository.executeWithRetry(
            jitterProvider = { 0L },
            sleeper = { delays += it },
            logger = { _, _ -> }
        ) {
            attempts++
            throw RuntimeException("503 service unavailable")
        }

        assertEquals(4, attempts)
        assertEquals(listOf(1000L, 2000L, 4000L), delays)
        assertTrue(result is GeminiCallResult.Failure)
        assertTrue((result as GeminiCallResult.Failure).error is GeminiFailure.Overloaded)
    }

    @Test
    fun `does not retry invalid requests`() = runTest {
        var attempts = 0
        val delays = mutableListOf<Long>()

        val result = repository.executeWithRetry(
            jitterProvider = { 0L },
            sleeper = { delays += it },
            logger = { _, _ -> }
        ) {
            attempts++
            throw RuntimeException("400 bad request")
        }

        assertEquals(1, attempts)
        assertTrue(delays.isEmpty())
        assertTrue(result is GeminiCallResult.Failure)
        assertTrue((result as GeminiCallResult.Failure).error is GeminiFailure.InvalidRequest)
    }

    @Test(expected = CancellationException::class)
    fun `retry is cancellable`() = runTest {
        repository.executeWithRetry(
            logger = { _, _ -> }
        ) {
            throw CancellationException("kill switch")
        }
    }
}
