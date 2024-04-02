package com.rain0ash.chatgpt

import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import androidx.appcompat.app.AppCompatActivity

class ChatGPTWebChromeClient(private val activity: AppCompatActivity) : WebChromeClient() {
    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        val logTag = "WebViewConsole"
        consoleMessage?.let {
            val message = "${consoleMessage.sourceId()} (${consoleMessage.lineNumber()}): ${consoleMessage.message()}"
            when (consoleMessage.messageLevel()) {
                null -> null
                ConsoleMessage.MessageLevel.LOG -> Log.i(logTag, message)
                ConsoleMessage.MessageLevel.TIP -> Log.v(logTag, message)
                ConsoleMessage.MessageLevel.DEBUG -> Log.d(logTag, message)
                ConsoleMessage.MessageLevel.ERROR -> Log.e(logTag, message)
                ConsoleMessage.MessageLevel.WARNING -> Log.w(logTag, message)
            }
        }

        return super.onConsoleMessage(consoleMessage)
    }
}