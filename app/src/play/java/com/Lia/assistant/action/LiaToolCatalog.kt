package com.Lia.assistant.action

import org.json.JSONArray

object LiaToolCatalog {
    fun declarations(): JSONArray {
        val array = JSONArray()

        // open_app
        array.put(
            LiaToolHelper.functionDecl(
                name = "open_app",
                description = "Launches an installed Android app by name or label on the device.",
                properties = mapOf("app_name" to "Name of the app to launch (e.g., YouTube, WhatsApp, Camera)"),
                required = listOf("app_name")
            )
        )

        // call_contact
        array.put(
            LiaToolHelper.functionDecl(
                name = "call_contact",
                description = "Places a phone call to a saved contact name or direct phone number.",
                properties = mapOf("contact_name_or_number" to "Contact display name or phone digits to call"),
                required = listOf("contact_name_or_number")
            )
        )

        // message_contact
        array.put(
            LiaToolHelper.functionDecl(
                name = "message_contact",
                description = "Prepares and opens a message composer for SMS or WhatsApp. The user reviews and sends.",
                properties = mapOf(
                    "contact_name_or_number" to "Contact name or phone number",
                    "message" to "Text content of the message",
                    "platform" to "Messaging platform: 'sms' or 'whatsapp' (default 'whatsapp')"
                ),
                required = listOf("contact_name_or_number", "message")
            )
        )

        // build_website
        array.put(
            LiaToolHelper.functionDecl(
                name = "build_website",
                description = "Invokes the 3D Website Forge reactor to design, stream, and render a complete animated website based on a description.",
                properties = mapOf("prompt" to "Detailed description of the website to build (e.g. 'a modern cozy cafe website in Hindi and English with menu and booking')"),
                required = listOf("prompt")
            )
        )

        return array
    }
}
