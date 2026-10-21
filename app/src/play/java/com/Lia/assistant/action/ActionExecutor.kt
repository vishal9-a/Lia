package com.Lia.assistant.action

import android.content.Context
import org.json.JSONObject

object ActionExecutor {
    suspend fun execute(context: Context, name: String, args: JSONObject): JSONObject {
        val result = JSONObject()
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
            }

            else -> {
                result.put("error", "Unknown or unavailable tool: $name")
            }
        }
        return result
    }
}
