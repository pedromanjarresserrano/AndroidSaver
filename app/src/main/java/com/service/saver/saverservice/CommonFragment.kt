package com.service.saver.saverservice

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.service.saver.saverservice.services.SaverService
import com.service.saver.saverservice.tumblr.TumblrActivity
import com.service.saver.saverservice.tumblr.util.TumblrClient
import com.service.saver.saverservice.util.ClipDataListener
import com.tumblr.loglr.Interfaces.LoginListener
import com.tumblr.loglr.LoginResult
import com.tumblr.loglr.Loglr
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import kotlinx.android.synthetic.main.fragment_common.view.*




/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * [CommonFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CommonFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CommonFragment : Fragment() {
    var client = TumblrClient()
    var loggedtwiiter = false
    var loggedtumblr = false
    val TWITTER_CALLBACK_URL = "oauth://t4jsample"

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
        view.btn_twitter.callback = object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>) {
                ClipDataListener.setTokens(result.data.authToken.token, result.data.authToken.secret)
            }

            override fun failure(exception: TwitterException) {
                android.app.AlertDialog.Builder(activity).setTitle("Error").setMessage(exception.message).setNeutralButton("Ok", null).show()
            }
        }
        val service = Intent(activity, SaverService::class.java)
        view.service_switch.setOnCheckedChangeListener({ _, b: Boolean ->
            if (b)
                activity!!.startService(service)
            else
                activity!!.stopService(service)
        })

        return view
    }


    companion object {

        fun newInstance(): CommonFragment {
            val fragment = CommonFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

}





