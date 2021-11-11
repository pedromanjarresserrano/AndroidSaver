package com.service.saver.saverservice.twitter;

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.service.saver.saverservice.MainTabActivity
import com.service.saver.saverservice.R
import com.service.saver.saverservice.domain.PostLink
import com.service.saver.saverservice.domain.TempLink
import com.service.saver.saverservice.sqllite.AdminSQLiteOpenHelper

class TwitterActivity : AppCompatActivity() {
    private var db: AdminSQLiteOpenHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        twitterActivity = this
        setContentView(R.layout.activity_twitter)
        db = AdminSQLiteOpenHelper(this)
        try {
            val text = intent.extras!!.get(Intent.EXTRA_TEXT) as String
            if (text.contains("//twitter.com/") && text.contains("status")) {
                if (!checkOnList(text)) {
                    runOnUiThread {
                        Toast.makeText(
                            this,
                            "Link Capture",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    if (MainTabActivity.JTWITTER == null)
                        MainTabActivity.JTWITTER = TwitterClient(this)
                    MainTabActivity.JTWITTER.saveTweet(text)

                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            finish()
        }

    }

    private fun checkOnList(url: String): Boolean {
        val postLink = db!!.getAllPostLinkByParentLink(url)
        val first1 = postLink.stream().filter { e: PostLink -> e.save == false }.findFirst()
        return if (first1.isPresent) {
            true
        } else {
            val tempLink: List<TempLink> = db!!.allTempLinks()
            val first = tempLink.stream().filter { e: TempLink ->
                e.parent_url.equals(
                    url,
                    ignoreCase = true
                )
            }.findFirst()
            first.isPresent
        }
    }


    companion object {
        private lateinit var twitterActivity: TwitterActivity

        fun getInstance(): TwitterActivity {
            return twitterActivity!!
        }
    }
}
