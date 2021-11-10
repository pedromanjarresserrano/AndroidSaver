package com.service.saver.saverservice.folder


import android.content.Intent
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.common.util.UriUtil
import com.service.saver.saverservice.FolderActivity
import com.service.saver.saverservice.R
import com.service.saver.saverservice.folder.model.FileModel
import com.service.saver.saverservice.player.PlayerActivity
import com.service.saver.saverservice.viewer.ViewerActivity
import kotlinx.android.synthetic.main.file_folder_item.view.*
import java.io.File
import kotlin.random.Random


class MyFileModelRecyclerViewAdapter(
    private val mValues: List<FileModel>
) : RecyclerView.Adapter<MyFileModelRecyclerViewAdapter.FileHolder>() {

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.file_folder_item, parent, false)
        return FileHolder(view)
    }

    override fun onBindViewHolder(holder: FileHolder, position: Int) {
        if (position < mValues.size) {
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
                        intent.putExtra("parent", item.parent)
                        holder.mView.context.startActivity(intent)
                    } else {
                        val intent = Intent(holder.mView.context, ViewerActivity::class.java)
                        intent.putExtra("filepath", item.filepath)
                        holder.mView.context.startActivity(intent)
                    }
                } else {
                    val intent = Intent(holder.mView.context, FolderActivity::class.java)
                    intent.putExtra("filepath", item.filepath)
                    holder.mView.context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return if (position < mValues.size)
            mValues!![position].id!!
        else
            Random(123).nextLong()
    }

    override fun getItemCount(): Int = mValues.size

    inner class FileHolder(val mView: View) : RecyclerView.ViewHolder(mView) {

        val filename = mView.nombre_file
        val draweeView = mView.preview_file;

    }
}
