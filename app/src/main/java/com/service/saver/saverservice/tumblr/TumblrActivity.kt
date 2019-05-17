package com.service.saver.saverservice.tumblr

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.service.saver.saverservice.MainTabActivity.client
import com.service.saver.saverservice.R
import com.service.saver.saverservice.domain.PostLink
import com.service.saver.saverservice.domain.UserLink
import com.service.saver.saverservice.sqllite.AdminSQLiteOpenHelper
import com.service.saver.saverservice.tumblr.util.TumblrClient
import needle.Needle
import java.net.URL

class TumblrActivity : AppCompatActivity() {
    private var db: AdminSQLiteOpenHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tumblr)
        db = AdminSQLiteOpenHelper(this)
        try {
            val text = intent.extras!!.get(Intent.EXTRA_TEXT) as String
            if (text.contains("tumblr")) {
                val url = URL(text)
                val host = url.host
                val path = url.path
                val hostsplit = host.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val pathsplit = path.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val id = pathsplit[2]
                val blog = hostsplit[0]
                Needle.onBackgroundThread().execute {
                    if (!(TumblrClient.TOKEN_SECRET!!.isEmpty() && TumblrClient.TOKEN_KEY!!.isEmpty())) {
                        val post = client!!.blogPost(blog, java.lang.Long.valueOf(id))
                        val urlFile = TumblrClient.getUrlFile(post)
                        urlFile.forEach {
                            var postlink = PostLink()
                            postlink.url = it
                            postlink.username = post.blogName
                            val postLink = this.db!!.getPostLink(it)
                            if (postLink == null) {
                                this.db!!.agregarPostLink(postlink)
                            }
                        }
                    }
                }

            }

            if (text.contains("twitter") and text.contains("@")) {
                var user = text.split("@")[1].split(")")[0];
                var userLink = UserLink();
                userLink.username = user;
                val findUserLink = this.db!!.getUserLink(user)
                if (findUserLink == null) {
                    this.db!!.agregarUserLink(userLink)
                }
            }
            finish()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}