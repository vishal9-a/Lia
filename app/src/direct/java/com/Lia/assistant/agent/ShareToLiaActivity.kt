package com.Lia.assistant.agent

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast

class ShareToLiaActivity : Activity() {
    companion object {
        @Volatile
        var lastSharedUri: Uri? = null
            private set
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent?.action == Intent.ACTION_SEND) {
            val uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
            if (uri != null) {
                lastSharedUri = uri
                Toast.makeText(this, "Media shared with Lia AI", Toast.LENGTH_SHORT).show()
            }
        }
        finish()
    }
}
