package com.Lia.assistant.action

import org.json.JSONArray

object LiaToolCatalog {
    fun declarations(): JSONArray {
        val array = JSONArray()

        // Common core tools
        array.put(
            LiaToolHelper.functionDecl(
                name = "open_app",
                description = "Launches an installed Android app by name or label on the device.",
                properties = mapOf("app_name" to "Name of the app to launch (e.g., YouTube, WhatsApp, Camera)"),
                required = listOf("app_name")
            )
        )

        array.put(
            LiaToolHelper.functionDecl(
                name = "call_contact",
                description = "Places a phone call to a saved contact name or direct phone number.",
                properties = mapOf("contact_name_or_number" to "Contact display name or phone digits to call"),
                required = listOf("contact_name_or_number")
            )
        )

        array.put(
            LiaToolHelper.functionDecl(
                name = "message_contact",
                description = "Sends or prepares a message for SMS or WhatsApp with automated sending support.",
                properties = mapOf(
                    "contact_name_or_number" to "Contact name or phone number",
                    "message" to "Text content of the message",
                    "platform" to "Messaging platform: 'sms' or 'whatsapp' (default 'whatsapp')"
                ),
                required = listOf("contact_name_or_number", "message")
            )
        )

        array.put(
            LiaToolHelper.functionDecl(
                name = "build_website",
                description = "Invokes the 3D Website Forge reactor to design, stream, and render a complete animated website based on a description.",
                properties = mapOf("prompt" to "Detailed description of the website to build"),
                required = listOf("prompt")
            )
        )

        // Screen accessibility control tools
        array.put(
            LiaToolHelper.functionDecl(
                name = "read_screen",
                description = "Reads visible textual and interactive elements currently shown on the phone screen.",
                properties = emptyMap()
            )
        )

        array.put(
            LiaToolHelper.functionDecl(
                name = "tap_text",
                description = "Taps on a screen button, text label, or icon matching target_text.",
                properties = mapOf("target_text" to "Text label or description of the UI element to tap"),
                required = listOf("target_text")
            )
        )

        array.put(
            LiaToolHelper.functionDecl(
                name = "type_text",
                description = "Types text into the currently focused input field.",
                properties = mapOf("text" to "The string to type into the field"),
                required = listOf("text")
            )
        )

        array.put(
            LiaToolHelper.functionDecl(
                name = "scroll_screen",
                description = "Scrolls the currently active screen.",
                properties = mapOf("direction" to "Direction to scroll: 'down', 'up', 'forward', or 'backward'"),
                required = listOf("direction")
            )
        )

        array.put(
            LiaToolHelper.functionDecl(
                name = "go_back",
                description = "Performs Android system Back action.",
                properties = emptyMap()
            )
        )

        array.put(
            LiaToolHelper.functionDecl(
                name = "go_home",
                description = "Performs Android system Home action.",
                properties = emptyMap()
            )
        )

        // Social media agent tools
        array.put(
            LiaToolHelper.functionDecl(
                name = "execute_social_media_task",
                description = "Runs an automated UI action on Instagram or Facebook (e.g., preparing a post, reel, or story). Always confirm before publishing.",
                properties = mapOf(
                    "platform" to "Target platform: 'instagram' or 'facebook'",
                    "action" to "Action: 'create_post', 'create_reel', 'create_story', 'text_post'",
                    "media_uri" to "Optional shared media content URI",
                    "caption" to "Caption or text to post",
                    "mode" to "'draft' or 'publish' (defaults to 'draft')",
                    "target_account" to "Optional target account handle"
                ),
                required = listOf("platform", "action")
            )
        )

        array.put(
            LiaToolHelper.functionDecl(
                name = "social_media_task_control",
                description = "Controls an ongoing social media task after user confirmation or rejection.",
                properties = mapOf(
                    "command" to "Control command: 'confirm', 'reject', 'resume', 'cancel', or 'status'",
                    "task_id" to "ID of the task to control"
                ),
                required = listOf("command", "task_id")
            )
        )

        // WhatsApp agent tools
        array.put(
            LiaToolHelper.functionDecl(
                name = "execute_whatsapp_task",
                description = "Performs a WhatsApp action: read unread, search chat, or send message.",
                properties = mapOf(
                    "action" to "'read_unread', 'send_message', 'read_chat', 'search_chat', 'mute_chat'",
                    "contact_query" to "Contact name or phone number",
                    "message_text" to "Message body"
                ),
                required = listOf("action")
            )
        )

        array.put(
            LiaToolHelper.functionDecl(
                name = "whatsapp_task_control",
                description = "Controls or confirms an ongoing WhatsApp task.",
                properties = mapOf(
                    "command" to "'confirm', 'reject', 'cancel', 'status'",
                    "task_id" to "Task ID"
                ),
                required = listOf("command", "task_id")
            )
        )

        return array
    }
}
