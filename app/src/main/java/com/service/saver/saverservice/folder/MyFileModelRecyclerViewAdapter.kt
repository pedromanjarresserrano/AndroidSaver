package com.service.saver.saverservice.folder


import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.service.saver.saverservice.R
import com.service.saver.saverservice.folder.model.FileModel
import com.service.saver.saverservice.player.PlayerActivity
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
        holder.filename.text = item.getName()
        val uri = Uri.fromFile(File(item.filepath))
        holder.draweeView.setImageURI(uri, holder.mView.context)
        holder.mView.setOnClickListener({
            val intent = Intent(holder.mView.getContext(), PlayerActivity::class.java)
            intent.putExtra("position", position)
            holder.mView.getContext().startActivity(intent)
        })
    }

    override fun getItemCount(): Int = mValues.size

    inner class FileHolder(val mView: View) : RecyclerView.ViewHolder(mView) {

        val filename = mView.nombre_file
        val draweeView = mView.preview_file;

    }
}
