package com.archiveapp.app

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.webkit.*
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var offlineLayout: View
    private lateinit var fabShare: FloatingActionButton

    // ==================== VIVEK ARYA VEDIC LIBRARY ====================
    // Aapki archive.org collection ka final link
    private val WEBSITE_URL = "https://archive.org/details/@vivek_arya_vedic_lib"
    // ========================================================================

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        progressBar = findViewById(R.id.progressBar)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        offlineLayout = findViewById(R.id.offlineLayout)
        fabShare = findViewById(R.id.fabShare)
        val retryButton: Button = findViewById(R.id.retryButton)

        setupWebView()
        setupListeners(retryButton)

        if (isInternetAvailable()) {
            loadWebsite()
        } else {
            showOffline()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.allowFileAccess = true
        webSettings.allowContentAccess = true
        webSettings.allowFileAccessFromFileURLs = true
        webSettings.allowUniversalAccessFromFileURLs = true
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.userAgentString = webSettings.userAgentString + " ArchiveApp/1.0"

        // Enable offline cache
        webSettings.setAppCacheEnabled(true)
        webSettings.setAppCachePath(cacheDir.path)

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
                offlineLayout.visibility = View.GONE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
                swipeRefresh.isRefreshing = false
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                if (request?.isForMainFrame == true) {
                    if (!isInternetAvailable()) {
                        showOffline()
                    }
                }
            }

            // External links ko bahar kholne se roke, sirf archive.org ke andar rakhe
            // Agar youtube, playstore etc ho to bahar khole
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()
                return if (url.contains("archive.org")) {
                    false // WebView ke andar hi khole
                } else if (url.startsWith("tel:") || url.startsWith("mailto:") || url.startsWith("https://play.google.com")) {
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    } catch (e: Exception) {}
                    true
                } else {
                    false
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progressBar.progress = newProgress
                if (newProgress == 100) {
                    progressBar.visibility = View.GONE
                }
            }

            // File upload support for archive.org
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                // Simple implementation - you can extend with ActivityResultLauncher
                return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
            }
        }

        // Download listener - archive.org se books, audio, video download karne ke liye
        webView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            try {
                val request = DownloadManager.Request(Uri.parse(url))
                request.setMimeType(mimetype)
                request.addRequestHeader("User-Agent", userAgent)
                request.setDescription("Downloading file...")
                request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype))
                request.allowScanningByMediaScanner()
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimetype))
                
                val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                dm.enqueue(request)
                Toast.makeText(this, "Download shuru ho gaya...", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                // Agar DownloadManager fail ho to browser me khol de
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                } catch (ex: Exception) {
                    Toast.makeText(this, "Download nahi ho paya: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupListeners(retryButton: Button) {
        swipeRefresh.setOnRefreshListener {
            if (isInternetAvailable()) {
                webView.reload()
            } else {
                swipeRefresh.isRefreshing = false
                showOffline()
            }
        }

        retryButton.setOnClickListener {
            if (isInternetAvailable()) {
                loadWebsite()
            } else {
                Toast.makeText(this, "Abhi bhi internet nahi hai", Toast.LENGTH_SHORT).show()
            }
        }

        fabShare.setOnClickListener {
            val currentUrl = webView.url ?: WEBSITE_URL
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Archive.org se")
                putExtra(Intent.EXTRA_TEXT, "${getString(R.string.share_text)} $currentUrl")
            }
            startActivity(Intent.createChooser(shareIntent, "Share karein"))
        }
    }

    private fun loadWebsite() {
        offlineLayout.visibility = View.GONE
        webView.visibility = View.VISIBLE
        webView.loadUrl(WEBSITE_URL)
    }

    private fun showOffline() {
        webView.visibility = View.GONE
        offlineLayout.visibility = View.VISIBLE
        swipeRefresh.isRefreshing = false
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // Back button se WebView me piche jaye, app band na ho
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
