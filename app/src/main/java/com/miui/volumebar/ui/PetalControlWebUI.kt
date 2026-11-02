package com.miui.volumebar.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import android.view.MotionEvent
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.viewinterop.AndroidView

/** Opt-in HyperOS skin using Blurify's existing accessibility-overlay host and blur. */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PetalControlWebUI(
    backdrop: Bitmap?,
    visible: Boolean,
    bridge: PetalNativeActions,
    onCreated: (WebView) -> Unit,
    onDisposed: (WebView) -> Unit,
    onInteract: () -> Unit,
    onSwipeUpExit: () -> Unit = {}
) {
    val currentVisibility = remember { arrayOfNulls<Boolean>(1) }
    Box(Modifier.fillMaxSize()) {
        if (backdrop != null && !backdrop.isRecycled) {
            Image(
                bitmap = backdrop.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    setBackgroundColor(AndroidColor.TRANSPARENT)
                    overScrollMode = View.OVER_SCROLL_NEVER
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = false
                    settings.allowContentAccess = false
                    settings.allowFileAccess = true
                    settings.allowFileAccessFromFileURLs = false
                    settings.allowUniversalAccessFromFileURLs = false
                    settings.setSupportZoom(false)
                    addJavascriptInterface(bridge, "Android")
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView, request: WebResourceRequest
                        ): Boolean = true
                        override fun onPageFinished(view: WebView, url: String?) {
                            if (url?.endsWith("petal/index.html") == true) {
                                view.evaluateJavascript("window.configureApp('overlay');", null)
                                bridge.updateLevels(view)
                            }
                        }
                    }
                    // Native exit path that never depends on the page's JavaScript: an upward swipe that
                    // starts in the bottom edge zone (the gesture-navigation area) closes the panel.
                    var downY = 0f
                    var fromBottom = false
                    var exited = false
                    setOnTouchListener { v, event ->
                        when (event.actionMasked) {
                            MotionEvent.ACTION_DOWN -> {
                                onInteract()
                                downY = event.y
                                fromBottom = event.y > v.height * 0.88f
                                exited = false
                            }
                            MotionEvent.ACTION_MOVE -> if (fromBottom && !exited &&
                                downY - event.y > 72 * v.resources.displayMetrics.density) {
                                exited = true
                                onSwipeUpExit()
                            }
                        }
                        false
                    }
                    onCreated(this)
                    loadUrl("file:///android_asset/petal/index.html")
                }
            },
            update = { web ->
                if (currentVisibility[0] != visible) {
                    currentVisibility[0] = visible
                    if (!visible) web.evaluateJavascript(
                        "if(window.setOpen) window.setOpen(false);", null
                    )
                }
            },
            onRelease = { web ->
                onDisposed(web)
                web.stopLoading()
                web.removeJavascriptInterface("Android")
                web.destroy()
            }
        )
    }
}
