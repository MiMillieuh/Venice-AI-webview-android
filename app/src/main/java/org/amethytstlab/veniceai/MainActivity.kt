package org.amethytstlab.veniceai

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    
    private lateinit var webView: WebView
    private var fileChooserCallback: ValueCallback<Array<Uri>>? = null
    private lateinit var filechooserLauncher: ActivityResultLauncher<String>
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>
    
    companion object {
        private const val URL = "https://venice.ai/chat"
        private const val CHANNEL_ID = "veniceai_notifications"
        private const val CHANNEL_NAME = "Venice.AI Notifications"
        
        private const val NOTIFICATION_OVERRIDE = """
            (function() {
                if (typeof Notification !== 'undefined') {
                    Object.defineProperty(Notification, 'permission', {
                        get: function() { return 'granted'; },
                        configurable: true
                    });
                    Notification.requestPermission = function() {
                        return Promise.resolve('granted');
                    };
                    window.Notification = function(title, options) {
                        var notifOptions = options || {};
                        var body = notifOptions.body || '';
                        var icon = notifOptions.icon || '';
                        AndroidNotification.show(title, body, icon);
                    };
                    window.Notification.permission = 'granted';
                    window.Notification.requestPermission = function() {
                        return Promise.resolve('granted');
                    };
                }
            })();
        """
    }
    
    inner class NotificationInterface {
        @JavascriptInterface
        fun show(title: String, body: String, icon: String) {
            runOnUiThread {
                showNativeNotification(title, body)
            }
        }
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Venice.AI web notifications"
                enableLights(true)
                enableVibration(true)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun showNativeNotification(title: String, body: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
                NotificationManagerCompat.from(this).notify(System.currentTimeMillis().toInt(), notification)
            }
        } else {
            NotificationManagerCompat.from(this).notify(System.currentTimeMillis().toInt(), notification)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        createNotificationChannel()
        
        filechooserLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            if (uri != null) {
                fileChooserCallback?.onReceiveValue(arrayOf(uri))
            } else {
                fileChooserCallback?.onReceiveValue(arrayOf())
            }
            fileChooserCallback = null
        }
        
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { _ ->
            KeepAliveService.start(this)
            setupWebView()
            webView.evaluateJavascript(NOTIFICATION_OVERRIDE, null)
            webView.loadUrl(URL)
        }
        
        notificationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { _ -> }
        
        setContentView(R.layout.activity_main)
        
        val webView = findViewById<WebView>(R.id.webView)
        this.webView = webView
        
        requestPermissionsAndLoad()
    }

    @SuppressLint("SetJavaScriptEnabled", "HardwareAccelerated")
    private fun setupWebView() {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            setGeolocationEnabled(true)
            setAllowFileAccess(true)
            cacheMode = WebSettings.LOAD_DEFAULT
            mediaPlaybackRequiresUserGesture = false
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
            loadWithOverviewMode = true
            useWideViewPort = true
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            setNeedInitialFocus(false)
        }
        
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        
        webView.addJavascriptInterface(NotificationInterface(), "AndroidNotification")
        
        webView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (error?.errorCode == ERROR_HOST_LOOKUP || error?.errorCode == ERROR_TIMEOUT) {
                        view?.loadDataWithBaseURL(
                            URL,
                            "<html><body style='font-family:sans-serif;text-align:center;padding:50px;background:#1A1A2E;color:#FFF;'>" +
                            "<h2>Unable to load Venice.AI</h2>" +
                            "<p>Please check your internet connection and try again.</p>" +
                            "<button onclick='location.reload()' style='padding:12px 24px;font-size:16px;background:#333;color:#FFF;border:none;border-radius:8px;'>Retry</button>" +
                            "</body></html>",
                            "text/html",
                            "UTF-8",
                            null
                        )
                    }
                }
            }
            
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url?.toString() ?: return false
                val uri = Uri.parse(url)
                
                // Intercept custom URL schemes (wallet apps, etc.)
                if (uri.scheme != "http" && uri.scheme != "https") {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        startActivity(intent)
                    } catch (e: Exception) {
                        // No app can handle this URL
                    }
                    return true
                }
                
                return false
            }
        }
        
        webView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest?) {
                runOnUiThread {
                    request?.grant(request.resources)
                }
            }
            
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                fileChooserCallback?.onReceiveValue(arrayOf())
                fileChooserCallback = filePathCallback
                filechooserLauncher.launch("*/*")
                return true
            }
        }
        
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    finish()
                }
            }
        })
    }

    private fun requestPermissionsAndLoad() {
        val permissions = mutableListOf<String>()
        
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.RECORD_AUDIO)
        }
        
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.MODIFY_AUDIO_SETTINGS)
            != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.MODIFY_AUDIO_SETTINGS)
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        
        if (permissions.isNotEmpty()) {
            permissionLauncher.launch(permissions.toTypedArray())
        } else {
            KeepAliveService.start(this)
            setupWebView()
            webView.evaluateJavascript(NOTIFICATION_OVERRIDE, null)
            webView.loadUrl(URL)
        }
    }

    override fun onPause() {
        super.onPause()
    }
    
    override fun onResume() {
        super.onResume()
    }
    
    override fun onDestroy() {
        KeepAliveService.stop(this)
        webView.destroy()
        super.onDestroy()
    }
}
