package com.rain0ash.chatgpt

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView

class ChatGPTWebView : WebView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var lasturl: String? = null
    suspend fun update() {
        val result = IPAddressValidator().country()
        val country = result.getOrNull()?.country2

        if (country == null || country == "RU" || country == "UA") {
            val url = url
            if (url != null && url.startsWith("https://chat.openai.com/", true)) {
                lasturl = url
            }

            loadUrl("http://erblock.crimea.com/")
        } else {
            lasturl?.let {
                loadUrl(it)
                lasturl = null
                return
            }

            val url = url
            if (url != null && url.startsWith("https://chat.openai.com/", true)) {
                reload()
                return
            }

            loadUrl("https://chat.openai.com/")
        }
    }

    fun action(): ApplicationAction {
        val url = url

        return when {
            url == null -> ApplicationAction.Close
            url.equals("http://erblock.crimea.com/", true) -> ApplicationAction.Update
            url.equals("https://chat.openai.com/", true) && canGoBack() -> ApplicationAction.Back
            url.startsWith("https://chat.openai.com/", true) -> ApplicationAction.Background
            else -> ApplicationAction.None
        }
    }
}