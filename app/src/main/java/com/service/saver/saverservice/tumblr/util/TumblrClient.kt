package com.service.saver.saverservice.tumblr.util

import android.content.Context
import android.content.SharedPreferences
import com.tumblr.jumblr.JumblrClient
import com.tumblr.jumblr.request.RequestBuilder
import com.tumblr.jumblr.types.*
import org.jsoup.Jsoup
import org.scribe.model.Token
import java.io.IOException
import java.util.*

class TumblrClient {

    private var settings: SharedPreferences? = null
    private var context: Context? = null
    internal var client = JumblrClient(
            CONSUMER_KEY,
            CONSUMER_SECRET
    )

    var requestBuilder: RequestBuilder
        get() = client.requestBuilder
        set(builder) {
            client.requestBuilder = builder
        }

    constructor(context: Context?) {
        this.context = context
    }


    init {
        /*
        client.setToken(
                TOKEN_KEY,
                TOKEN_SECRET
        );*/
        if (context != null) {
            settings = context!!.getSharedPreferences("settings", 0)

            var TOKEN_KEY = settings!!.getString("tumblraccesstoken", "")
            var TOKEN_SECRET = settings!!.getString("tumblraccessSecret", "")
            if (!(TOKEN_KEY == null || TOKEN_SECRET == null)) {
                val token_secret = TOKEN_SECRET as String
                val token_key = TOKEN_KEY as String
                client.setToken(token_key, token_secret)
                TumblrClient.TOKEN_KEY = token_key
                TumblrClient.TOKEN_SECRET = token_secret
            }
        }
    }

    fun setToken(token: String, tokenSecret: String) {
        settings = context!!.getSharedPreferences("settings", 0)
        val edit = settings!!.edit()
        edit.putString("tumblraccesstoken", token).commit()
        edit.putString("tumblraccessSecret", tokenSecret).commit()
        TumblrClient.TOKEN_KEY = token
        TumblrClient.TOKEN_SECRET = tokenSecret

        client.setToken(token, tokenSecret)
    }

    fun setToken(token: Token) {
        client.setToken(token)
    }

    fun xauth(email: String, password: String) {
        client.xauth(email, password)
    }

    fun user(): User {
        return client.user()
    }

    fun userDashboard(options: Map<String, *>): List<Post> {
        return client.userDashboard(options)
    }

    fun userDashboard(): List<Post> {
        return client.userDashboard()
    }

    fun userFollowing(options: Map<String, *>): List<Blog> {
        return client.userFollowing(options)
    }

    fun userFollowing(): List<Blog> {
        return client.userFollowing()
    }

    fun tagged(tag: String, options: Map<String, *>): List<Post> {
        return client.tagged(tag, options)
    }

    fun tagged(tag: String): List<Post> {
        return client.tagged(tag)
    }

    fun blogInfo(blogName: String): Blog {
        return client.blogInfo(blogName)
    }

    fun blogFollowers(blogName: String, options: Map<String, *>): List<User> {
        return client.blogFollowers(blogName, options)
    }

    fun blogFollowers(blogName: String): List<User> {
        return client.blogFollowers(blogName)
    }

    fun blogLikes(blogName: String, options: Map<String, *>): List<Post> {
        return client.blogLikes(blogName, options)
    }

    fun blogLikes(blogName: String): List<Post> {
        return client.blogLikes(blogName)
    }

    fun blogPosts(blogName: String, options: Map<String, *>): List<Post> {
        return client.blogPosts(blogName, options)
    }

    fun blogPosts(blogName: String): List<Post> {
        return client.blogPosts(blogName)
    }

    fun blogPost(blogName: String, postId: Long?): Post {
        return client.blogPost(blogName, postId!!)
    }

    fun blogQueuedPosts(blogName: String, options: Map<String, *>): List<Post> {
        return client.blogQueuedPosts(blogName, options)
    }

    fun blogQueuedPosts(blogName: String): List<Post> {
        return client.blogQueuedPosts(blogName)
    }

    fun blogDraftPosts(blogName: String, options: Map<String, *>): List<Post> {
        return client.blogDraftPosts(blogName, options)
    }

    fun blogDraftPosts(blogName: String): List<Post> {
        return client.blogDraftPosts(blogName)
    }

    fun blogSubmissions(blogName: String, options: Map<String, *>): List<Post> {
        return client.blogSubmissions(blogName, options)
    }

    fun blogSubmissions(blogName: String): List<Post> {
        return client.blogSubmissions(blogName)
    }

    fun userLikes(options: Map<String, *>): List<Post> {
        return client.userLikes(options)
    }

    fun userLikes(): List<Post> {
        return client.userLikes()
    }

    fun blogAvatar(blogName: String, size: Int?): String {
        return client.blogAvatar(blogName, size)
    }

    fun blogAvatar(blogName: String): String {
        return client.blogAvatar(blogName)
    }

    fun like(postId: Long?, reblogKey: String) {
        client.like(postId!!, reblogKey)
    }

    fun unlike(postId: Long?, reblogKey: String) {
        client.unlike(postId!!, reblogKey)
    }

    fun follow(blogName: String) {
        client.follow(blogName)
    }

    fun unfollow(blogName: String) {
        client.unfollow(blogName)
    }

    fun postDelete(blogName: String, postId: Long?) {
        client.postDelete(blogName, postId!!)
    }

    fun postReblog(blogName: String, postId: Long?, reblogKey: String, options: Map<String, *>): Post {
        return client.postReblog(blogName, postId!!, reblogKey, options)
    }

    fun postReblog(blogName: String, postId: Long?, reblogKey: String): Post {
        return client.postReblog(blogName, postId, reblogKey)
    }

    @Throws(IOException::class)
    fun postEdit(blogName: String, id: Long?, detail: Map<String, *>) {
        client.postEdit(blogName, id, detail)
    }

    @Throws(IOException::class)
    fun postCreate(blogName: String, detail: Map<String, *>): Long? {
        return client.postCreate(blogName, detail)
    }

    @Throws(IllegalAccessException::class, InstantiationException::class)
    fun <T : Post> newPost(blogName: String, klass: Class<T>): T {
        return client.newPost(blogName, klass)
    }

    fun isAuthenticate(): Boolean {
        if (!(TOKEN_KEY.isNullOrEmpty() || TOKEN_SECRET.isNullOrEmpty())) {
            val token_secret = TOKEN_SECRET as String
            val token_key = TOKEN_KEY as String
            setToken(token_key, token_secret)
            return true
        } else
            return false
    }

    companion object {
        var TOKEN_KEY: String? = ""

        var TOKEN_SECRET: String? = ""

        val CONSUMER_KEY: String? = "oiK8MFj4VQX52JEvfyKWi0CvdoZyYATq4SjnRj9fXkMV8T4X1g"
        val CONSUMER_SECRET: String? = "c72SlqTby5ejAMizhv0Pj6IYenCxGBZ2oF5UU6NymOiFPs5dYo"

        fun getUrlFile(p: Post): List<String> {
            val list = ArrayList<String>()
            val type = p.type
            if (type == Post.PostType.PHOTO) {
                val aux = p as PhotoPost
                for (e in aux.photos) {
                    val url = e.originalSize.url
                    val filename = url.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    list.add(aux.blogName + " - " + filename[filename.size - 1] + ":NAME:" + url)
                }

            }
            if (type == Post.PostType.VIDEO) {
                val aux = p as VideoPost
                val videos = aux.videos
                val video = videos[videos.size - 1]
                val doc = Jsoup.parse(video.embedCode)
                val elements = doc.select("source")
                val src = elements.attr("src")
                if (!src.isEmpty())
                    list.add(aux.blogName + " - " + aux.id + ".mp4" + ":NAME:" + src)
            }
            return list
        }


    }
}
