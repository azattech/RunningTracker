package com.azat.runningtracker.ui.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.azat.runningtracker.R
import kotlinx.android.synthetic.main.fragment_privacy_policy.*


class PrivacyPolicyFragment : Fragment(R.layout.fragment_privacy_policy) {

    companion object {
        const val URL =
            "https://www.notion.so/theazat/Privacy-Policy-of-Running-Tracker-da067a1a02a54e79b81e3f1124e15e5d/"
    }

    // private lateinit var webView: WebView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return true
            }
        }
        webView.loadUrl("https://www.notion.so/")
    }
}
