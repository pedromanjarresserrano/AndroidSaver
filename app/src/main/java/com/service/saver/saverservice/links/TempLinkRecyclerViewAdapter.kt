package com.service.saver.saverservice.links

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.service.saver.saverservice.R
import com.service.saver.saverservice.domain.TempLink
import com.service.saver.saverservice.sqllite.AdminSQLiteOpenHelper
import kotlinx.android.synthetic.main.fragment_item_link.view.*
import java.lang.Exception
import java.util.*


class TempLinkRecyclerViewAdapter(
        private val values: ArrayList<TempLink>?,
        private val db: AdminSQLiteOpenHelper,
        private val listener: TempLinkFragment.OnUpdateListInteractionListener)
    : RecyclerView.Adapter<TempLinkRecyclerViewAdapter.ViewHolder>() {
    init {
        setHasStableIds(true)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_item_link, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values!![position]
        holder.contentView.text = item.url
        holder.button_download.setOnClickListener {
            val postLink = db.getPostLink(item.url)
            Toast.makeText(holder.contentView.context, "Downloading", Toast.LENGTH_SHORT).show()
            postLink.save = false
            db.updatePostLink(postLink)
            db.deleteTempLink(item)
            try {
                values.removeAt(position)
            } catch (e: Exception) {
                Log.e("ERROR", "E/RR", e)
            }
            notifyDataSetChanged();
            this.listener.onUpdateListInteractionListener(item)
        }
    }

    override fun getItemId(position: Int): Long {

        return values!![position].id
    }

    override fun getItemCount(): Int = values!!.size
    fun deleteItem(position: Int) {
        values!!.removeAt(position)
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contentView: TextView = view.link_url
        val button_download: ImageButton = view.download_again

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }
}