package com.service.saver.saverservice.twitter

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import com.service.saver.saverservice.MyApp
import needle.Needle
import twitter4j.MediaEntity
import twitter4j.Twitter
import twitter4j.TwitterException
import twitter4j.TwitterFactory
import twitter4j.auth.AccessToken
import twitter4j.auth.RequestToken
import twitter4j.conf.ConfigurationBuilder
import java.io.IOException
import java.util.*

class  TwitterClient {

    var twitteraccesstoken = ""
    var twitteraccessSecret = ""
    var consumerKey = "kcj3ehA8TNqzdaTaCGVdIHBQt"
    var twitterconsumerSecret = "ZrKtiohW60UO83DEeRw2ftqshcAq1aXzbxlYJCOMgbPZNuIa34"
    var jtwitter: Twitter
    private var context: Context? = null
    private var settings: SharedPreferences? = null

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
                val twitteraccesstoken = settings!!.getString("twitteraccesstoken", "")
                val twitteraccessSecret = settings!!.getString("twitteraccessSecret", "")
                if (!(twitteraccessSecret.isNullOrEmpty() || twitteraccesstoken.isNullOrEmpty())) {
                    jtwitter.oAuthAccessToken = AccessToken(twitteraccesstoken as String, twitteraccessSecret as String)
                    this.twitteraccesstoken = twitteraccesstoken
                    this.twitteraccessSecret = twitteraccessSecret

                }
            } catch (e: Exception) {
                e.printStackTrace()

            }
        }
    }

    fun setTokens(twitteraccesstoken: String, twitteraccessSecret: String) {
        val edit = settings!!.edit()

        edit.putString("twitteraccesstoken", twitteraccesstoken).commit()
        edit.putString("twitteraccessSecret", twitteraccessSecret).commit()
        this.twitteraccesstoken = twitteraccesstoken
        this.twitteraccessSecret = twitteraccessSecret
        jtwitter.oAuthAccessToken = AccessToken(twitteraccesstoken, twitteraccessSecret)

    }

    fun saveTweet(url: String) {
        Needle.onBackgroundThread().execute {
            try {
                val split1 = getID(url)
                if (jtwitter.getOAuthAccessToken() != null) {

                    val status = jtwitter.showStatus(java.lang.Long.parseLong(split1))
                    val mediaEntities = Arrays.asList<MediaEntity>(*status.getMediaEntities())
                    if (!mediaEntities.isEmpty()) {
                        entites(mediaEntities)
                    }
                }
            } catch (e: TwitterException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @Throws(IOException::class)
    private fun entites(mediaEntities: List<MediaEntity>) {
        mediaEntities.sortedBy { it.videoAspectRatioHeight }
        val mediaEntity = mediaEntities[0]
        if (mediaEntity.type.equals("photo", ignoreCase = true))

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mediaEntities.stream().map<String>({ it.getMediaURL() }).forEach({ MyApp.add(it) })
            }else{
                mediaEntities.let { (e)->{MyApp.add(e.mediaURL)} }
            }
        else {
            val videoVariants = Arrays.asList(*mediaEntity.videoVariants)
            videoVariants.sortBy {  it.getBitrate() }
            val split = videoVariants[videoVariants.size - 1].url.split("\\?".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val link = split[0]
            MyApp.add(link)
        }
    }

    fun getOAuthRequestToken(): RequestToken {
        return jtwitter.getOAuthRequestToken();
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

}