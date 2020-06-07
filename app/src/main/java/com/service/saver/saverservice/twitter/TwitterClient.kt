package com.service.saver.saverservice.twitter

import androidx.appcompat.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.service.saver.saverservice.domain.PostLink
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

    var twitteraccesstoken = ""
    var twitteraccessSecret = ""
    var consumerKey = "kcj3ehA8TNqzdaTaCGVdIHBQt"
    var twitterconsumerSecret = "ZrKtiohW60UO83DEeRw2ftqshcAq1aXzbxlYJCOMgbPZNuIa34"
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
        db = AdminSQLiteOpenHelper(this.context)
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
                if (jtwitter.oAuthAccessToken != null && !split1.isNullOrEmpty()) {
                    val status = jtwitter.showStatus(java.lang.Long.parseLong(split1))
                    val mediaEntities = Arrays.asList<MediaEntity>(*status.mediaEntities)
                    if (mediaEntities.isNotEmpty()) {
                        val user = status.user;
                        if (user != null)
                            entites(mediaEntities, true, user.screenName)
                        else
                            entites(mediaEntities,false, "")
                    }
                }
            } catch (e: Exception) {
                Log.e("ERROR", e.message)
            }
        }
    }

    @Throws(IOException::class)
    fun entites(mediaEntities: List<MediaEntity>, onfolder: Boolean, user: String) {
        mediaEntities.sortedBy { it.videoAspectRatioHeight }
        val mediaEntity = mediaEntities[0]
        if (mediaEntity.type.equals("photo", ignoreCase = true)) {
            mediaEntities.forEach { e ->
                var postlink = PostLink()
                postlink.url = e.mediaURL
                if (onfolder)
                    postlink.username = user
                savePostLink(e.mediaURL, postlink)
            }
        } else {
            val videoVariants = Arrays.asList(*mediaEntity.videoVariants)
            videoVariants.sortBy { it.bitrate }
            val split = videoVariants[videoVariants.size - 1].url.split("\\?".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val link = split[0]
            var postlink = PostLink()
            postlink.url = link
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
                    builder.setTitle("Already download").setMessage("The file " + postlink.url + " is already download, download again?")
                        .setPositiveButton("Ok") { dialog, e ->
                            findlink.save = false
                            this.db!!.updatePostLink(findlink)
                        }
                        .setNegativeButton("Cancel") { dialog, e -> };
                    builder.create().show()
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

}