package com.service.saver.saverservice

import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.service.saver.saverservice.MainTabActivity.JTWITTER
import com.service.saver.saverservice.services.SaverService
import kotlinx.android.synthetic.main.fragment_common.view.*
import twitter4j.auth.RequestToken
import android.view.MotionEvent
import com.service.saver.saverservice.services.UtilService


class CommonFragment : Fragment() {
    var loggedtwiiter = false
    var statusservice = false
    private var settings: SharedPreferences? = null
    var webView: WebView? = null


    private val STATUS_SERVICE_KEY = "statusservice"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settings = requireContext().getSharedPreferences("settings", 0)
        webView = object : WebView(requireContext()) {
            override fun onCheckIsTextEditor(): Boolean {
                return true
            }
        }
        webView!!.settings.javaScriptEnabled = true
        webView!!.isFocusable = true
        webView!!.isFocusableInTouchMode = true
        webView!!.requestFocus(View.FOCUS_DOWN)
        webView!!.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_UP -> if (!v.hasFocus()) {
                    v.requestFocus()
                }
            }
            false
        }
        val view = inflater.inflate(R.layout.fragment_common, container, false)
        try {
            oAuthRequestToken = JTWITTER.getOAuthRequestToken()
        } catch (e: Exception) {
            //  e.printStackTrace()
        }
        loadBtnLoginTwitter(view, oAuthRequestToken)

        val service = Intent(activity, SaverService::class.java)
        view.service_switch.setOnCheckedChangeListener { _, b: Boolean ->
            if (b)
                UtilService.scheduleJob(requireContext())
            else
                UtilService.stopJob(requireContext())

            settings!!.edit().putBoolean(STATUS_SERVICE_KEY, b).apply()
        }




        statusservice = settings!!.getBoolean(STATUS_SERVICE_KEY, false)
        view.service_switch.isChecked = statusservice

        isAuthenticate(view)

        return view
    }

    private fun loadBtnLoginTwitter(view: View, oAuthRequestToken: RequestToken?) {
        view.btn_twitter.setOnClickListener {
            if (oAuthRequestToken != null) {
                val redirectURL = oAuthRequestToken.authenticationURL
                var create: AlertDialog = AlertDialog.Builder(requireActivity()).create();
                val builder = activity?.let { AlertDialog.Builder(it) }

                val lp = WindowManager.LayoutParams()

                if (builder != null) {
                    builder.setTitle("Login twitter")

                    builder.setView(webView)
                    webView!!.loadUrl(redirectURL)

                    webView!!.webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            webView: WebView,
                            url: String
                        ): Boolean {
                            if (url.startsWith(TWITTER_CALLBACK_URL))
                                com.service.saver.saverservice.util.Util.Companion.Task {
                                    JTWITTER.handleTwitterCallback(url)
                                    create.cancel()
                                    isAuthenticate(view)
                                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                            else
                                webView.loadUrl(url)
                            return true
                        }
                    }

                    create = builder.create()

                    lp.copyFrom(create.window!!.attributes)
                    lp.width = 150
                    lp.height = 500
                    lp.x = -170
                    lp.y = 100
                    create.window!!.attributes = lp
                    create.window!!.setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
                    );
                    create.show();
                } else {
                    Toast.makeText(activity, "Already logged in Twitter", Toast.LENGTH_LONG).show()

                }
            }
        }
    }

    private fun isAuthenticate(view: View) {
        if (JTWITTER.isAuthenticate()) {
            loggedtwiiter = true
            view.btn_twitter.text = "Logged in Twitter"
            view.btn_twitter.setOnClickListener {
                AlertDialog.Builder(requireActivity()).setMessage("Logout Twitter?")
                    .setPositiveButton("Logout") { _, _ ->
                        JTWITTER.jtwitter.oAuthAccessToken = null
                        loggedtwiiter = false
                        view.btn_twitter.text = "Login Twitter"
                        oAuthRequestToken = null;
                        loadBtnLoginTwitter(view, JTWITTER.getOAuthRequestToken())

                    }.setNegativeButton("Cancel", { _, _ -> }).show()
            }
        }
    }

    companion object {
        const val TWITTER_CALLBACK_URL = "https://saverservice"
        var oAuthRequestToken: RequestToken? = null

    }


}







