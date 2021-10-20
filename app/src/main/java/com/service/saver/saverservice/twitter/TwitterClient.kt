package com.service.saver.saverservice.twitter

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.service.saver.saverservice.CommonFragment
import com.service.saver.saverservice.MainTabActivity
import com.service.saver.saverservice.domain.PostLink
import com.service.saver.saverservice.domain.TempLink
import com.service.saver.saverservice.sqllite.AdminSQLiteOpenHelper
import needle.Needle
import twitter4j.MediaEntity
import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.auth.AccessToken
import twitter4j.auth.RequestToken
import twitter4j.conf.ConfigurationBuilder
import java.io.IOException
import java.util.*

class TwitterClient {

    private var twitteraccesstoken = ""
    private var twitteraccessSecret = ""
    private var consumerKey = "kcj3ehA8TNqzdaTaCGVdIHBQt"
    private var twitterconsumerSecret = "ZrKtiohW60UO83DEeRw2ftqshcAq1aXzbxlYJCOMgbPZNuIa34"
    var jtwitter: Twitter
    private var context: Context? = null
    private var settings: SharedPreferences? = null
    private var db: AdminSQLiteOpenHelper? = null

    constructor(context: Context?) {
        this.context = context
        val cb = ConfigurationBuilder()
            .setIncludeEntitiesEnabled(true)
            .setIncludeMyRetweetEnabled(true)
            .setIncludeExtAltTextEnabled(true)
            .setTweetModeExtended(true)
            .setIncludeEmailEnabled(true)
        jtwitter = TwitterFactory(cb.build()).instance
        jtwitter.setOAuthConsumer(consumerKey, twitterconsumerSecret)
        if (context != null) {
            settings = context.getSharedPreferences("settings", 0)
            try {
                val twitteraccesstoken = settings!!.getString(PREF_KEY_OAUTH_TOKEN, "")
                val twitteraccessSecret = settings!!.getString(PREF_KEY_OAUTH_SECRET, "")
                if (!(twitteraccessSecret.isNullOrEmpty() || twitteraccesstoken.isNullOrEmpty())) {
                    jtwitter.oAuthAccessToken =
                        AccessToken(twitteraccesstoken as String, twitteraccessSecret as String)
                    this.twitteraccesstoken = twitteraccesstoken
                    this.twitteraccessSecret = twitteraccessSecret

                }
            } catch (e: Exception) {
                e.printStackTrace()

            }
        }
        db = AdminSQLiteOpenHelper(this.context)
    }

    fun setTokens(twitteraccesstoken: String, twitteraccessSecret: String) {
        val edit = settings!!.edit()

        edit.putString(PREF_KEY_OAUTH_TOKEN, twitteraccesstoken).apply()
        edit.putString(PREF_KEY_OAUTH_SECRET, twitteraccessSecret).apply()
        this.twitteraccesstoken = twitteraccesstoken
        this.twitteraccessSecret = twitteraccessSecret
        jtwitter.oAuthAccessToken = AccessToken(twitteraccesstoken, twitteraccessSecret)

    }

    fun saveTweet(url: String) {
        Needle.onBackgroundThread().execute {
            try {
                val split1 = getID(url)
                if (jtwitter.oAuthAccessToken != null && !split1.isNullOrEmpty()) {
                    val status = jtwitter.showStatus(java.lang.Long.parseLong(split1))
                    val mediaEntities = Arrays.asList<MediaEntity>(*status.mediaEntities)
                    if (mediaEntities.isNotEmpty()) {
                        val user = status.user;
                        if (user != null)
                            entites(mediaEntities, true, user.screenName, url)
                        else
                            entites(mediaEntities, false, "", url)
                    }
                }
            } catch (e: Exception) {
                Log.e("ERROR", e.message)
            }
        }
    }

    @Throws(IOException::class)
    fun entites(
        mediaEntities: List<MediaEntity>,
        onfolder: Boolean,
        user: String,
        parent_url: String
    ) {
        mediaEntities.sortedBy { it.videoAspectRatioHeight }
        val mediaEntity = mediaEntities[0]
        if (mediaEntity.type.equals("photo", ignoreCase = true)) {
            mediaEntities.forEach { e ->
                var postlink = PostLink()
                postlink.url = e.mediaURL
                postlink.parent_url = parent_url
                if (onfolder)
                    postlink.username = user
                savePostLink(e.mediaURL, postlink)
            }
        } else {
            val videoVariants = Arrays.asList(*mediaEntity.videoVariants)
            videoVariants.sortBy { it.bitrate }
            val split = videoVariants[videoVariants.size - 1].url.split("\\?".toRegex())
                .dropLastWhile { it.isEmpty() }.toTypedArray()
            val link = split[0]
            var postlink = PostLink()
            postlink.url = link
            postlink.parent_url = parent_url
            if (onfolder)
                postlink.username = user
            savePostLink(link, postlink)

        }
    }

    private fun savePostLink(link: String, postlink: PostLink) {
        val findlink = this.db!!.getPostLink(link);
        if (findlink == null) {
            this.db!!.agregarPostLink(postlink)
        } else {
            Needle.onMainThread().execute {
                val builder = this.context?.let { AlertDialog.Builder(it) }
                if (builder != null) {
                    try {
                        this.db!!.agregarTempLink(
                            TempLink(
                                -1,
                                findlink.url,
                                postlink.parent_url,
                                Date()
                            )
                        )
                    } catch (e: Exception) {
                        Log.d("Log.ERROR", e.toString())
                    }
                }
            }
        }
    }

    fun getOAuthRequestToken(): RequestToken {
        return jtwitter.oAuthRequestToken;
    }

    fun getOAuthAccessToken(requestToken: RequestToken?, url: String?): AccessToken? {
        return jtwitter.getOAuthAccessToken(requestToken, url)
    }

    private fun getID(url: String): String {
        val strings = url.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val string = strings[strings.size - 1]
        return string.split("\\?".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
    }

    fun isAuthenticate(): Boolean {
        return !(this.twitteraccessSecret.isNullOrEmpty() || this.twitteraccesstoken.isNullOrEmpty())
    }

    companion object {
        const val TWITTER_CALLBACK_URL = "https://saverservice"
        const val PREFERENCE_NAME = "settings"
        const val PREF_KEY_OAUTH_TOKEN = "twitteraccesstoken"
        const val PREF_KEY_OAUTH_SECRET = "twitteraccessSecret"

        const val URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier"

        fun newInstance(): CommonFragment {
            return CommonFragment()
        }
    }

    fun handleTwitterCallback(url: String?) {
        val uri: Uri = Uri.parse(url)

        // oAuth verifier
        val verifier: String? = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER)
        try {

            // Get the access token
            val accessTokenResponse = MainTabActivity.JTWITTER.getOAuthAccessToken(
                CommonFragment.oAuthRequestToken,
                verifier
            )


            // After getting access token, access token secret
            // store them in application preferences
            setTokens(accessTokenResponse!!.token, accessTokenResponse.tokenSecret)
            // Store login status - true
            Log.e("Twitter OAuth Token", "> " + accessTokenResponse.token)

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

}