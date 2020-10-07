package com.service.saver.saverservice

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
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
import com.service.saver.saverservice.MainTabActivity.JTUMBLR
import com.service.saver.saverservice.MainTabActivity.JTWITTER
import com.service.saver.saverservice.services.SaverService
import com.service.saver.saverservice.tumblr.util.TumblrClient
import com.tumblr.loglr.Interfaces.LoginListener
import com.tumblr.loglr.LoginResult
import com.tumblr.loglr.Loglr
import kotlinx.android.synthetic.main.fragment_common.view.*
import needle.Needle
import twitter4j.TwitterFactory
import twitter4j.auth.AccessToken
import twitter4j.auth.RequestToken
import twitter4j.conf.ConfigurationBuilder


/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * [CommonFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CommonFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CommonFragment : Fragment() {
    var loggedtwiiter = false
    var loggedtumblr = false
    var statusservice = false
    private var settings: SharedPreferences? = null
    var webView: WebView? = null


    private val STATUS_SERVICE_KEY = "statusservice"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        settings = requireContext().getSharedPreferences("settings", 0)
        webView = WebView(requireContext())
        val view = inflater.inflate(R.layout.fragment_common, container, false)

        view.btn_tumblr.setOnClickListener {
            if (!JTUMBLR.isAuthenticate()) {

                Loglr.setConsumerKey(TumblrClient.CONSUMER_KEY!!)!!
                        .setConsumerSecretKey(TumblrClient.CONSUMER_SECRET!!)!!
                        .enable2FA(true)!!
                        .setUrlCallBack("https://www.tumblr.com/dashboard")!!
                        .setLoginListener(object : LoginListener {
                            override fun onLoginSuccessful(loginResult: LoginResult) {
                                val oAuthToken = loginResult.getOAuthToken()
                                val oAuthTokenSecret = loginResult.getOAuthTokenSecret()
                                JTUMBLR!!.setToken(oAuthToken, oAuthTokenSecret)
                                loggedtumblr == true
                                view.btn_tumblr.setText("Logged in Tumblr")
                            }
                        })!!.initiate(requireActivity())
            }
        }
        try {
            oAuthRequestToken = JTWITTER.getOAuthRequestToken()
        } catch (e: Exception) {
            //  e.printStackTrace()
        }
        loadBtnLoginTwitter(view, oAuthRequestToken)

        val service = Intent(activity, SaverService::class.java)
        view.service_switch.setOnCheckedChangeListener { _, b: Boolean ->
            if (b)
                requireActivity().startService(service)
            else
                requireActivity().stopService(service)
            settings!!.edit().putBoolean(STATUS_SERVICE_KEY, b).apply()
        }




        statusservice = settings!!.getBoolean(STATUS_SERVICE_KEY, false)
        view.service_switch.isChecked = statusservice
        if (JTUMBLR.isAuthenticate()) {
            loggedtumblr == true
            view.btn_tumblr.setText("Logged in Tumblr")
            view.btn_tumblr.setOnClickListener {
                AlertDialog.Builder(requireActivity()).setMessage("Logout Tumblr?").setPositiveButton("Logout") { _, _ ->
                    JTUMBLR!!.setToken("", "")
                    loggedtumblr == false
                    view.btn_tumblr.setText("Login Tumblr")
                }.setNegativeButton("Cancel", { _, _ -> }).show()
            }
        }

        isAuthenticate(view)

        return view
    }

    private fun loadBtnLoginTwitter(view: View, oAuthRequestToken: RequestToken?) {
        view.btn_twitter.setOnClickListener {
            if (oAuthRequestToken != null) {
                val redirectURL = oAuthRequestToken.getAuthenticationURL()
                var create: AlertDialog = AlertDialog.Builder(requireActivity()).create();
                val builder = activity?.let { AlertDialog.Builder(it) }

                val lp = WindowManager.LayoutParams()

                if (builder != null) {
                    builder.setTitle("Login twitter")

                    builder.setView(webView)
                    webView!!.loadUrl(redirectURL)

                    webView!!.webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
                            if (url.startsWith(TWITTER_CALLBACK_URL))
                                Needle.onBackgroundThread().execute {
                                    JTWITTER.handleTwitterCallback(url)
                                    create.cancel()
                                    isAuthenticate(view)
                                }
                            else
                                webView.loadUrl(url)
                            return true
                        }
                    }

                    // Inflate and set the layout for the dialog
                    // Pass null as the parent view because its going in the dialog layout
                    //val view = inflater.inflate(R.layout.twitter_user_url, null)

                    create = builder.create()

                    lp.copyFrom(create.window!!.attributes)
                    lp.width = 150
                    lp.height = 500
                    lp.x = -170
                    lp.y = 100
                    create.window!!.attributes = lp
                    create.show();
                } else {
                    Toast.makeText(activity, "Already logged in Twitter", Toast.LENGTH_LONG).show()

                }
            }
        }
    }

    private fun isAuthenticate(view: View) {
        if (JTWITTER.isAuthenticate()) {
            loggedtwiiter == true
            view.btn_twitter.setText("Logged in Twitter")
            view.btn_twitter.setOnClickListener {
                AlertDialog.Builder(requireActivity()).setMessage("Logout Twitter?").setPositiveButton("Logout") { _, _ ->
                    JTWITTER.jtwitter.oAuthAccessToken = null
                    loggedtwiiter == false
                    view.btn_twitter.setText("Login Twitter")
                    oAuthRequestToken = null;
                    loadBtnLoginTwitter(view, JTWITTER.getOAuthRequestToken())

                }.setNegativeButton("Cancel", { _, _ -> }).show()
            }
        }
    }

    companion object {
        const val TWITTER_CALLBACK_URL = "https://saverservice"
        var oAuthRequestToken: RequestToken? = null

        fun newInstance(): CommonFragment {
            return CommonFragment()
        }
    }


}







