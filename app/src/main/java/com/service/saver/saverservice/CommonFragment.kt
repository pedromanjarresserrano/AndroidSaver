package com.service.saver.saverservice

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.service.saver.saverservice.tumblr.TumblrActivity
import com.service.saver.saverservice.tumblr.util.JumblrHolder
import com.service.saver.saverservice.util.ClipDataListener
import com.tumblr.loglr.Interfaces.LoginListener
import com.tumblr.loglr.LoginResult
import com.tumblr.loglr.Loglr
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
    var client = JumblrHolder()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_common, container, false)

        view.btn_tumblr.setOnClickListener(View.OnClickListener {
            Loglr
                    .setConsumerKey(JumblrHolder.CONSUMER_KEY)!!
                    .setConsumerSecretKey(JumblrHolder.CONSUMER_SECRET)!!
                    .enable2FA(true)!!
                    .setUrlCallBack("https://www.tumblr.com/dashboard")!!
                    .setLoginListener(object : LoginListener {
                        override fun onLoginSuccessful(loginResult: LoginResult) {
                            val oAuthToken = loginResult.getOAuthToken()
                            val oAuthTokenSecret = loginResult.getOAuthTokenSecret()
                            TumblrActivity.client.setToken(oAuthToken, oAuthTokenSecret)

                        }
                    })!!.initiate(activity!!.baseContext)

        })
        view.btn_twitter.setOnClickListener(View.OnClickListener{
             ClipDataListener.jtwitter.getOAuthRequestToken("https://twitter.com/");
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





