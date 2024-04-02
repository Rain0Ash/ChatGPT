package com.rain0ash.chatgpt

import android.content.Intent
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class ChatGPTWebViewClient(private val activity: AppCompatActivity) : WebViewClient() {
    private var iscss = false

    override fun shouldOverrideUrlLoading(webview: WebView?, request: WebResourceRequest?): Boolean {
        val url = request?.url.toString()

        return if (url.startsWith("https://chat.openai.com/") || url.startsWith("https://auth.openai.com/") || url.startsWith("https://auth0.openai.com/")) {
            false
        } else {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            activity.startActivity(intent)
            true
        }
    }

    override fun onPageCommitVisible(webview: WebView?, url: String?) {
        super.onPageCommitVisible(webview, url)

        if (webview == null) {
            return
        }

        if (url != null && url.startsWith("https://chat.openai.com/")) {
            val css = asset("css.js").getOrNull() ?: return

            webview.evaluateJavascript(css) { result ->
                iscss = result != null
            }
        }
    }

    override fun onPageFinished(webview: WebView?, url: String?) {
        super.onPageFinished(webview, url)

        if (!iscss && webview != null && url != null && url.startsWith("https://chat.openai.com/")) {
            val css = asset("css.js").getOrNull() ?: return

            webview.evaluateJavascript(css, null)
        }

        iscss = false
    }

    private fun asset(filename: String): Result<String> {
        return try {
            val reader = BufferedReader(InputStreamReader(activity.assets.open(filename)))
            Result.success(reader.readText())
        } catch (exception: IOException) {
            Result.failure(exception)
        }
    }
}