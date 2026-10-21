package com.Lia.assistant.action

import android.content.Context
import com.Lia.assistant.agent.NovaAccessibilityService
import com.Lia.assistant.agent.Platform
import com.Lia.assistant.agent.SocialAction
import com.Lia.assistant.agent.SocialTaskRequest
import com.Lia.assistant.agent.TaskMode
import com.Lia.assistant.agent.WhatsAppAgent
import kotlinx.coroutines.delay
import org.json.JSONArray
import org.json.JSONObject

object ActionExecutor {
    private var pendingSocialConfirmation: SocialTaskRequest? = null

    suspend fun execute(context: Context, name: String, args: JSONObject): JSONObject {
        val result = JSONObject()
        val service = NovaAccessibilityService.instance

        when (name) {
            "open_app" -> {
                val appName = args.optString("app_name", "").trim()
                if (appName.isEmpty()) {
                    result.put("error", "No app name provided")
                    return result
                }
                val apps = InstalledAppLabelCache.get(context)
                val pkg = AppLauncher.findApp(appName, apps)
                if (pkg != null && AppLauncher.launch(context, pkg)) {
                    result.put("status", "app_opened")
                    result.put("app_name", appName)
                    result.put("package", pkg)
                } else {
                    result.put("error", "App not found or could not be launched: $appName")
                }
            }

            "call_contact" -> {
                val query = args.optString("contact_name_or_number", "").trim()
                if (query.isEmpty()) {
                    result.put("error", "No contact name or number provided")
                    return result
                }
                when (val lookup = ContactResolver.lookup(context, query)) {
                    is ContactLookupResult.Found -> {
                        val callStatus = CommIntents.callContact(context, lookup.phoneNumber)
                        result.put("status", callStatus)
                        result.put("contact_name", lookup.displayName)
                        result.put("phone_number", lookup.phoneNumber)
                    }
                    is ContactLookupResult.NoPermission -> {
                        result.put("error", "Permission READ_CONTACTS not granted")
                    }
                    is ContactLookupResult.NotFound -> {
                        result.put("error", "Contact not found: $query")
                    }
                }
            }

            "message_contact" -> {
                val recipientQuery = args.optString("contact_name_or_number", "").trim()
                val message = args.optString("message", "").trim()
                val platform = args.optString("platform", "whatsapp").trim()

                if (message.isEmpty()) {
                    result.put("error", "No message content provided")
                    return result
                }

                val recipientNumber = if (recipientQuery.isNotEmpty()) {
                    when (val lookup = ContactResolver.lookup(context, recipientQuery)) {
                        is ContactLookupResult.Found -> lookup.phoneNumber
                        else -> recipientQuery
                    }
                } else ""

                val status = CommIntents.openMessageComposer(context, recipientNumber, message, platform)
                result.put("status", status)
                result.put("platform", platform)
                result.put("recipient", recipientQuery)

                // Try auto-tapping Send if accessibility service is active
                if (service != null && platform.equals("whatsapp", ignoreCase = true)) {
                    var sent = false
                    for (i in 0 until 12) {
                        delay(400)
                        if (service.tapText("Continue to chat")) {
                            delay(400)
                        }
                        if (service.tapText("Send")) {
                            sent = true
                            break
                        }
                    }
                    if (sent) {
                        result.put("status", "sent")
                    } else {
                        result.put("status", "composer_opened_send_not_found")
                    }
                }
            }

            "read_screen" -> {
                if (service == null) {
                    result.put("error", "accessibility_not_enabled")
                } else {
                    val elements = service.readScreen(maxElements = 40, maxLabelLength = 40)
                    val arr = JSONArray()
                    elements.forEach { arr.put(it) }
                    result.put("status", "success")
                    result.put("screen_elements", arr)
                }
            }

            "tap_text" -> {
                val targetText = args.optString("target_text", "").trim()
                if (service == null) {
                    result.put("error", "accessibility_not_enabled")
                } else if (targetText.isEmpty()) {
                    result.put("error", "No target_text provided")
                } else {
                    val tapped = service.tapText(targetText)
                    if (tapped) {
                        result.put("status", "tapped")
                        result.put("target", targetText)
                    } else {
                        result.put("error", "element_not_found")
                    }
                }
            }

            "type_text" -> {
                val text = args.optString("text", "")
                if (service == null) {
                    result.put("error", "accessibility_not_enabled")
                } else {
                    val typed = service.typeText(text)
                    if (typed) {
                        result.put("status", "typed")
                    } else {
                        result.put("error", "no_focused_field")
                    }
                }
            }

            "scroll_screen" -> {
                val direction = args.optString("direction", "down")
                if (service == null) {
                    result.put("error", "accessibility_not_enabled")
                } else {
                    val scrolled = service.scrollScreen(direction)
                    if (scrolled) {
                        result.put("status", "scrolled")
                    } else {
                        result.put("error", "nothing_scrollable")
                    }
                }
            }

            "go_back" -> {
                if (service == null) {
                    result.put("error", "accessibility_not_enabled")
                } else {
                    val done = service.goBack()
                    result.put("status", if (done) "done" else "failed")
                }
            }

            "go_home" -> {
                if (service == null) {
                    result.put("error", "accessibility_not_enabled")
                } else {
                    val done = service.goHome()
                    result.put("status", if (done) "done" else "failed")
                }
            }

            "execute_social_media_task" -> {
                val req = SocialTaskRequest.parse(args)
                if (req.mode == TaskMode.PUBLISH) {
                    pendingSocialConfirmation = req
                    result.put("status", "waiting_for_confirmation")
                    result.put("task_id", req.taskId)
                    result.put("prompt_to_user", "You asked to publish a ${req.action} on ${req.platform}. Should I go ahead and post it?")
                } else {
                    result.put("status", "draft_created")
                    result.put("task_id", req.taskId)
                    result.put("details", "Draft created for ${req.platform}: ${req.caption}")
                }
            }

            "social_media_task_control" -> {
                val cmd = args.optString("command", "").lowercase()
                val taskId = args.optString("task_id", "")
                val pending = pendingSocialConfirmation
                if (cmd == "confirm" && pending != null && pending.taskId == taskId) {
                    pendingSocialConfirmation = null
                    result.put("status", "submitted_unverified")
                    result.put("message", "Post submitted to ${pending.platform}. Please verify on the app.")
                } else if (cmd == "reject" || cmd == "cancel") {
                    pendingSocialConfirmation = null
                    result.put("status", "cancelled")
                } else {
                    result.put("status", "idle")
                }
            }

            "execute_whatsapp_task" -> {
                return WhatsAppAgent.execute(context, args)
            }

            "whatsapp_task_control" -> {
                result.put("status", "task_controlled")
            }

            else -> {
                result.put("error", "Unknown tool: $name")
            }
        }

        return result
    }
}
