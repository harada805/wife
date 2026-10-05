package com.example.wife.agent

import com.example.wife.BuildConfig

/**
 * Satu-satunya tempat BuildConfig.GEMINI_API_KEY dibaca, supaya unit test JVM bisa
 * menimpanya tanpa Android runtime.
 */
object BuildConfigSafe {
    private var override: String? = null

    fun apiKey(): String = override ?: BuildConfig.GEMINI_API_KEY

    /** Hanya untuk unit test. */
    fun setForTesting(key: String?) {
        override = key
    }
}
