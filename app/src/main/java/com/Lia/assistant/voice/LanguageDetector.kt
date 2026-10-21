package com.Lia.assistant.voice

enum class DetectedLanguage {
    HINDI,
    HINGLISH,
    ENGLISH,
    UNKNOWN
}

object LanguageDetector {
    private val HINGLISH_MARKERS = setOf(
        "hai", "hain", "ho", "kya", "kyun", "kaise", "nahi", "haan",
        "mujhe", "tumhe", "aap", "mera", "yaar", "bhai", "accha", "theek",
        "matlab", "karna", "karo", "raha", "chahiye", "batao", "suno", "dekho",
        "abhi", "phir", "lekin", "bahut", "thoda", "kuch", "sab", "wala",
        "meri", "mere", "hum", "tum", "apna", "apne", "apni", "hoga", "hogi",
        "dena", "karo", "de", "lo", "bolo", "pe", "ka", "ki", "ke", "ko", "se"
    )

    fun detect(text: String?): DetectedLanguage {
        if (text.isNullOrBlank()) return DetectedLanguage.UNKNOWN

        for (ch in text) {
            if (ch in '\u0900'..'\u097F') {
                return DetectedLanguage.HINDI
            }
        }

        val words = text.lowercase()
            .split(Regex("[^a-zA-Z']+"))
            .filter { it.isNotBlank() }

        if (words.isEmpty()) return DetectedLanguage.UNKNOWN

        val hits = words.count { it in HINGLISH_MARKERS }

        return if (words.size <= 4) {
            if (hits >= 1) DetectedLanguage.HINGLISH else DetectedLanguage.ENGLISH
        } else {
            val ratio = hits.toDouble() / words.size.toDouble()
            if (hits >= 2 || ratio >= 0.15) {
                DetectedLanguage.HINGLISH
            } else {
                DetectedLanguage.ENGLISH
            }
        }
    }
}
