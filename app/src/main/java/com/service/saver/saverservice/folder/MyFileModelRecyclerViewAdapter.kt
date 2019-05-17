package com.service.saver.saverservice.folder


import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.common.util.UriUtil
import com.service.saver.saverservice.R
import com.service.saver.saverservice.folder.model.FileModel
import com.service.saver.saverservice.player.PlayerActivity
import com.service.saver.saverservice.viewer.ViewerActivity
import kotlinx.android.synthetic.main.file_folder_item.view.*
import java.io.File


/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyFileModelRecyclerViewAdapter(
        private val mValues: List<FileModel>) : RecyclerView.Adapter<MyFileModelRecyclerViewAdapter.FileHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.file_folder_item, parent, false)
        return FileHolder(view)
    }

    override fun onBindViewHolder(holder: FileHolder, position: Int) {
        val item = mValues[position]
        holder.filename.text = item.name
        if (!item.isFolder) {
            val uri = Uri.fromFile(File(item.filepath))
            holder.draweeView.setImageURI(uri, holder.mView.context)
        } else {
            val uri = Uri.Builder()
                    .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
                    .path(R.drawable.ic_folder.toString())
                    .build()
            holder.draweeView.setImageURI(uri, holder.mView.context)
        }
        holder.mView.setOnClickListener {
            if (!item.isFolder) {
                if (item.name!!.endsWith("mp4")) {
                    val intent = Intent(holder.mView.context, PlayerActivity::class.java)
                    intent.putExtra("position", position)
                    holder.mView.context.startActivity(intent)
                } else {
                    val intent = Intent(holder.mView.context, ViewerActivity::class.java)
                    intent.putExtra("filepath", item.filepath)
                    holder.mView.context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class FileHolder(val mView: View) : RecyclerView.ViewHolder(mView) {

        val filename = mView.nombre_file
        val draweeView = mView.preview_file;

    }
}
