package com.Lia.assistant.voice

import com.Lia.assistant.data.LanguagePreference

object ChatPromptBuilder {
    fun build(
        personality: Personality,
        language: LanguagePreference,
        assistantName: String
    ): String {
        val parts = mutableListOf<String>()

        parts.add(
            "You are $assistantName, a personal AI companion living on the user's phone. " +
            "This is typed text chat, so never mention tapping, listening, or speaking. " +
            "Format your answers with clean Markdown paragraphs when helpful, but stay concise and natural."
        )

        parts.add(personality.systemInstruction)

        val langPolicy = when (language) {
            LanguagePreference.AUTO -> {
                "Language policy: Match the user's language. If they write in Hindi, reply in Hindi. " +
                "If they write in Hinglish, reply in modern natural Hinglish. If they write in English, reply in English. " +
                "Never translate their question back to them."
            }
            LanguagePreference.HINDI -> "Language policy: Always write in Hindi (हिंदी)."
            LanguagePreference.HINGLISH -> "Language policy: Always write in conversational Hinglish (Hindi + English blend)."
            LanguagePreference.ENGLISH -> "Language policy: Always write in English."
        }
        parts.add(langPolicy)

        parts.add(
            "Safety and demeanor: Never claim to be human or to have a biological body. " +
            "No sexually explicit content. Immediately respect requests to change tone or topic."
        )

        return parts.joinToString("\n\n")
    }
}
