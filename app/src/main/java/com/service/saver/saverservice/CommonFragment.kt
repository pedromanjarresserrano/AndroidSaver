package com.service.saver.saverservice

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.service.saver.saverservice.services.SaverService
import com.service.saver.saverservice.tumblr.TumblrActivity
import com.service.saver.saverservice.tumblr.util.TumblrClient
import com.service.saver.saverservice.util.ClipDataListener
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


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_common, container, false)


        if (TumblrClient.isAuthenticate()) {
            loggedtumblr == true
            view.btn_tumblr.setText("Logged in Tumblr")
        }

        if (!(ClipDataListener.twitteraccesstoken!!.isEmpty() && ClipDataListener.twitteraccessSecret!!.isEmpty())) {
            loggedtumblr == true
            view.btn_tumblr.setText("Logged in Twitter")
        }

        view.btn_tumblr.setOnClickListener({
            if (!TumblrClient.isAuthenticate()) {

                Loglr
                        .setConsumerKey(TumblrClient.CONSUMER_KEY!!)!!
                        .setConsumerSecretKey(TumblrClient.CONSUMER_SECRET!!)!!
                        .enable2FA(true)!!
                        .setUrlCallBack("https://www.tumblr.com/dashboard")!!
                        .setLoginListener(object : LoginListener {
                            override fun onLoginSuccessful(loginResult: LoginResult) {
                                val oAuthToken = loginResult.getOAuthToken()
                                val oAuthTokenSecret = loginResult.getOAuthTokenSecret()
                                TumblrActivity.client.setToken(oAuthToken, oAuthTokenSecret)
                                loggedtumblr == true
                                view.btn_tumblr.setText("Logged in Tumblr")
                            }
                        })!!.initiate(activity!!)
            }
        })
        val oAuthRequestToken: RequestToken? = null
        try {
            val oAuthRequestToken = ClipDataListener.jtwitter.getOAuthRequestToken() as RequestToken
        } catch (e: Exception) {
        }
        view.btn_twitter.setOnClickListener({
            val redirectURL = oAuthRequestToken!!.getAuthenticationURL()
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(redirectURL)
            activity!!.startActivity(i)

        })

        view.btn_load_pin.setOnClickListener({
            val text = view.txt_twitter_pin.editableText.toString()
            if (!text.trim({ it <= ' ' }).isEmpty()) {
                Needle.onBackgroundThread().execute({
                    val replace = text.replace("\n", "")
                    val jtwitter = ClipDataListener.jtwitter
                    val accessToken = jtwitter.getOAuthAccessToken(oAuthRequestToken, replace)
                    ClipDataListener.setTokens(accessToken.token, accessToken.tokenSecret)
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
        })

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


    }


    companion object {

        fun newInstance(): CommonFragment {
            return CommonFragment()
        }
    }

}





