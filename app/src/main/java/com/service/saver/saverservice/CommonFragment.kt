package com.service.saver.saverservice

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.service.saver.saverservice.MainTabActivity.client
import com.service.saver.saverservice.MainTabActivity.jtwitter
import com.service.saver.saverservice.services.SaverService
import com.service.saver.saverservice.tumblr.util.TumblrClient
import com.tumblr.loglr.Interfaces.LoginListener
import com.tumblr.loglr.LoginResult
import com.tumblr.loglr.Loglr
import kotlinx.android.synthetic.main.fragment_common.view.*
import needle.Needle
import twitter4j.auth.RequestToken


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

    private val STATUS_SERVICE_KEY = "statusservice"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        settings = context!!.getSharedPreferences("settings", 0)

        val view = inflater.inflate(R.layout.fragment_common, container, false)

        view.btn_tumblr.setOnClickListener({
            if (!client.isAuthenticate()) {

                Loglr
                        .setConsumerKey(TumblrClient.CONSUMER_KEY!!)!!
                        .setConsumerSecretKey(TumblrClient.CONSUMER_SECRET!!)!!
                        .enable2FA(true)!!
                        .setUrlCallBack("https://www.tumblr.com/dashboard")!!
                        .setLoginListener(object : LoginListener {
                            override fun onLoginSuccessful(loginResult: LoginResult) {
                                val oAuthToken = loginResult.getOAuthToken()
                                val oAuthTokenSecret = loginResult.getOAuthTokenSecret()
                                client!!.setToken(oAuthToken, oAuthTokenSecret)
                                loggedtumblr == true
                                view.btn_tumblr.setText("Logged in Tumblr")
                            }
                        })!!.initiate(activity!!)
            }
        })
        var oAuthRequestToken: RequestToken? = null
        try {
            oAuthRequestToken = jtwitter.getOAuthRequestToken()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        view.btn_twitter.setOnClickListener({
            if (oAuthRequestToken != null) {
                val redirectURL = oAuthRequestToken!!.getAuthenticationURL()
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(redirectURL)
                activity!!.startActivity(i)
            }else{
                Toast.makeText(activity, "Already logged in Twitter", Toast.LENGTH_LONG).show()

            }
        })

        view.btn_load_pin.setOnClickListener({
            val text = view.txt_twitter_pin.editableText.toString()
            if (!text.trim({ it <= ' ' }).isEmpty()) {
                Needle.onBackgroundThread().execute({
                    val replace = text.replace("\n", "")
                    val accessToken = jtwitter.getOAuthAccessToken(oAuthRequestToken, replace)
                    jtwitter.setTokens(accessToken!!.token, accessToken.tokenSecret)
                    loggedtumblr == true
                    view.btn_tumblr.setText("Logged in Tumblr")
                    view.btn_tumblr.setOnClickListener({
                        AlertDialog.Builder(activity!!).setMessage("Logout Tumblr?").setPositiveButton("Logout", DialogInterface.OnClickListener({ _, _ ->
                            client!!.setToken("", "")
                            loggedtumblr == false
                            view.btn_tumblr.setText("Login Tumblr")
                        })).setNegativeButton("Cancel", { _, _ -> }).show()
                    })
                })
            } else {
                Toast.makeText(activity, "Enter twitter PIN!", Toast.LENGTH_LONG).show()
            }
        })
        val service = Intent(activity, SaverService::class.java)
        view.service_switch.setOnCheckedChangeListener({ _, b: Boolean ->
            if (b)
                activity!!.startService(service)
            else
                activity!!.stopService(service)
            settings!!.edit().putBoolean(STATUS_SERVICE_KEY, b).commit()
        })




        statusservice = settings!!.getBoolean(STATUS_SERVICE_KEY, false)
        view.service_switch.isChecked = statusservice
        if (client.isAuthenticate()) {
            loggedtumblr == true
            view.btn_tumblr.setText("Logged in Tumblr")
            view.btn_tumblr.setOnClickListener({
                AlertDialog.Builder(activity!!).setMessage("Logout Tumblr?").setPositiveButton("Logout", DialogInterface.OnClickListener({ _, _ ->
                    client!!.setToken("", "")
                    loggedtumblr == false
                    view.btn_tumblr.setText("Login Tumblr")
                })).setNegativeButton("Cancel", { _, _ -> }).show()
            })
        }

        if (jtwitter.isAuthenticate()) {
            loggedtwiiter == true
            view.btn_twitter.setText("Logged in Twitter")
            view.btn_twitter.setOnClickListener({
                AlertDialog.Builder(activity!!).setMessage("Logout Twitter?").setPositiveButton("Logout", DialogInterface.OnClickListener({ _, _ ->
                    jtwitter.jtwitter.oAuthAccessToken = null
                    loggedtwiiter == false
                    view.btn_twitter.setText("Login Twitter")

                })).setNegativeButton("Cancel", { _, _ -> }).show()
            })
        }

        return view
    }

    companion object {

        fun newInstance(): CommonFragment {
            return CommonFragment()
        }
    }

}





