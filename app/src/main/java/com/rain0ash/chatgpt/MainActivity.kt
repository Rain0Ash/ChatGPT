package com.rain0ash.chatgpt

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebSettings
import androidx.appcompat.app.AppCompatActivity
import android.webkit.WebView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job
    private lateinit var vpn: VPNChecker
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var swipeLayout: SwipeRefreshLayout

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val webview: ChatGPTWebView = findViewById(R.id.webview)
        webview.settings.javaScriptEnabled = true
        webview.settings.domStorageEnabled = true
        webview.settings.cacheMode = WebSettings.LOAD_DEFAULT
        webview.settings.setSupportZoom(true)
        webview.settings.builtInZoomControls = true
        webview.settings.displayZoomControls = false
        webview.settings.useWideViewPort = true
        webview.settings.loadWithOverviewMode = true

        setupSettings(webview)

        val cookie: CookieManager = CookieManager.getInstance()
        cookie.setAcceptCookie(true)
        cookie.setAcceptThirdPartyCookies(webview, true)

        vpn = VPNChecker(this)

        job = Job()
        launch {
            val result = IPAddressValidator().country()
            val country = result.getOrNull()?.country2

            if (country == null || country == "RU" || country == "UA") {
                webview.loadUrl("http://erblock.crimea.com/")
                Log.e("Network", "Network Error", result.exceptionOrNull())
            } else {
                webview.webViewClient = ChatGPTWebViewClient(this@MainActivity)
                webview.webChromeClient = ChatGPTWebChromeClient(this@MainActivity)

                if (savedInstanceState == null) {
                    webview.loadUrl("https://chat.openai.com")
                } else {
                    webview.restoreState(savedInstanceState)
                }
            }
        }
    }

    fun setupSettings(webview: ChatGPTWebView) {
        swipeLayout = findViewById(R.id.swipe_layout)
        drawerLayout = findViewById(R.id.drawer_layout)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val params = navigationView.layoutParams
        params.width = (width * 0.5).toInt()
        navigationView.layoutParams = params

        swipeLayout.isEnabled = false
        swipeLayout.setOnRefreshListener {
            launch {
                webview.update()
                swipeLayout.isRefreshing = false
            }
        }

        val menuItem = navigationView.menu.findItem(R.id.menu_switch)
        val switch = menuItem.actionView as SwitchCompat

        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                switch.thumbTintList = ColorStateList.valueOf(Color.GREEN)
            } else {
                switch.thumbTintList = ColorStateList.valueOf(Color.RED)
            }
        }

        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_refresh -> {
                    update(webview)
                    true
                }
                R.id.menu_exit -> {
                    finishAffinity()
                    true
                }
                else -> false
            }
        }

        switch.thumbTintList = ColorStateList.valueOf(if (switch.isChecked) Color.GREEN else Color.RED)
    }

    override fun onResume() {
        super.onResume()
        vpn.handler.post(vpn.runnable)
    }

    override fun onPause() {
        super.onPause()
        vpn.handler.removeCallbacks(vpn.runnable)
    }

    fun update() {
        update(findViewById(R.id.webview))
    }

    private fun update(webview: ChatGPTWebView) {
        job.cancel()
        job = Job()

        val builder = AlertDialog.Builder(this@MainActivity)
        val dialog = layoutInflater.inflate(R.layout.progress_dialog_layout, null)
        builder.setView(dialog)
        builder.setCancelable(false)

        val progress = builder.create()
        progress.show()

        launch(Dispatchers.Main) {
            val start = System.currentTimeMillis()
            webview.update()
            delay(kotlin.math.max(750 + start - System.currentTimeMillis(), 0L))
            progress.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        }

        val webview: ChatGPTWebView = findViewById(R.id.webview)

        when (webview.action()) {
            ApplicationAction.None -> { }
            ApplicationAction.Update -> update(webview)
            ApplicationAction.Back -> webview.goBack()
            ApplicationAction.Background -> moveTaskToBack(true)
            ApplicationAction.Close -> super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val webview: WebView = findViewById(R.id.webview)
        webview.saveState(outState)
    }
}