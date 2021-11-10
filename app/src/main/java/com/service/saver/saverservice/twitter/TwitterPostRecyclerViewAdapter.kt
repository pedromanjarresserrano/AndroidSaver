package com.service.saver.saverservice.twitter


import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import com.service.saver.saverservice.MainTabActivity.JTWITTER
import com.service.saver.saverservice.R
import kotlinx.android.synthetic.main.fragment_twitterpost.view.*
import twitter4j.MediaEntity
import twitter4j.Status


class TwitterPostRecyclerViewAdapter(
    private val mValues: List<Status>
) : RecyclerView.Adapter<TwitterPostRecyclerViewAdapter.ViewHolder>() {

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_twitterpost, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.content_text.text = item.text
        holder.text.text = item.user.name
        var mediaEntities: List<MediaEntity> = item.mediaEntities.asList()
        if (item.isRetweet) {
            println("TTTT")
            mediaEntities = item.retweetedStatus.mediaEntities.toList()
        }
        if (!mediaEntities.isNullOrEmpty()) {

            val mediaEntity = mediaEntities[0]
            val imageuri = Uri.parse(mediaEntity.mediaURL)
            if (mediaEntity.type.equals("photo", ignoreCase = true)) {
                //  holder.image.setOnClickListener { it.context.startActivity(Intent(Intent.ACTION_VIEW, imageuri)) }
                holder.image.setImageURI(imageuri, holder.mView.context)
            } else {
                holder.image.setOnClickListener {
                    it.context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(mediaEntity.videoVariants.sortedBy { it.bitrate }.last().url)
                        )
                    )
                }
                holder.image.setImageURI(imageuri, holder.mView.context)
            }

            holder.image.setColorFilter(Color.TRANSPARENT)

        } else {
            //  holder.image.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            holder.image.setColorFilter(Color.TRANSPARENT)
            holder.image.visibility = View.INVISIBLE
            holder.image.maxHeight = 0
            // holder.button_download.visibility = View.INVISIBLE
        }

        holder.button_download.setOnClickListener {
            val url = "https://twitter.com/" + item.user.screenName
                .toString() + "/status/" + item.id;
            JTWITTER.saveTweet(url);
            Toast.makeText(holder.mView.context, "Downloading", Toast.LENGTH_SHORT).show()

        }

    }

    override fun getItemCount(): Int = mValues.size

    override fun getItemId(position: Int): Long {

        return mValues.get(position).id
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val text: TextView = mView.text
        val image: SimpleDraweeView = mView.iamge_post
        val content_text: TextView = mView.content_text
        val button_download: ImageButton = mView.button_download

    }
}
