package com.pintarmedia.derinnet

import android.Manifest
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


class WebActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private var mainUrl : String = "https://derin.rlradius.com/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.webview_activity)
        progressBar = findViewById(R.id.progressBarId)
        webView = findViewById(R.id.webId)
        if (Build.VERSION.SDK_INT >= 33) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {

                exitOnBackPressed()
            }
        } else {
            onBackPressedDispatcher.addCallback(
                this,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {

                        Log.i("TAG", "handleOnBackPressed: Exit")
                        exitOnBackPressed()
                    }
                })
        }
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), 0
        )
        webView.settings.javaScriptEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.webViewClient = object : WebViewClient(){
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                if(request!!.url.host == mainUrl){
                    return false
                }
                view!!.loadUrl(request.url.toString())
                //Intent(Intent.ACTION_VIEW, request.url).apply { start}
                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
            }
        }
        webView.webChromeClient = object : WebChromeClient(){
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if(newProgress < 100){
                    progressBar.visibility = View.VISIBLE
                    progressBar.setProgress(newProgress)
                }else if (newProgress == 100){
                    progressBar.visibility = View.GONE
                }
                super.onProgressChanged(view, newProgress)
            }

            override fun onGeolocationPermissionsShowPrompt(
                origin: String?,
                callback: GeolocationPermissions.Callback?
            ) {
                callback!!.invoke(origin,true,false)
               // super.onGeolocationPermissionsShowPrompt(origin, callback)
            }
        }
        webView.loadUrl(mainUrl)
        progressBar.setProgress(0)
        progressBar.max = 100

    }

    private fun exitOnBackPressed() {
        if(webView.canGoBack()){
            Log.d("izhu","exitOnBackPressed OK")
            webView.goBack()
        }else{
            Log.d("izhu","exitOnBackPressed NO")
            //finish()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        Log.d("izhu","onKeyDown")
        if (event!!.action == KeyEvent.ACTION_DOWN){
            when(keyCode){
              KeyEvent.KEYCODE_BACK -> if (webView.canGoBack()){
                  Log.d("izhu","onKeyDown OK")
                  webView.goBack()
              }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

}