package com.service.saver.saverservice.folder

import android.content.Context
import android.content.res.Configuration
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.service.saver.saverservice.R
import com.service.saver.saverservice.folder.model.FileModel
import com.service.saver.saverservice.util.Files
import com.service.saver.saverservice.util.LoadingDialog
import com.service.saver.saverservice.util.Util
import kotlinx.android.synthetic.main.fragment_filemodel_list.view.*
import java.lang.Exception
import java.util.stream.Collectors

open class FileModelFragment : Fragment() {

    val FILE_MODEL_LIST = ArrayList<FileModel>();
    var loading = false;
    var location = Files.getAbsolutePath();

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_filemodel_list, container, false)
        setHasOptionsMenu(true)
        with(view.list) {
            layoutManager = androidx.recyclerview.widget.StaggeredGridLayoutManager(
                when {
                    requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT -> 3
                    else -> 5
                }, when {
                    requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT -> androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
                    else -> androidx.recyclerview.widget.StaggeredGridLayoutManager.HORIZONTAL
                }
            )
        }
        view.list.adapter = MyFileModelRecyclerViewAdapter(FILE_MODEL_LIST)
        val intent = requireActivity().intent
        if (intent != null) {
            val extras = intent.extras
            if (extras != null) {
                val stringExtra = extras.getString("filepath")
                if (stringExtra != null)
                    location = stringExtra
            }
        }
        loadFiles()
        return view
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.folder_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.reload_files_folder -> {
                loadFiles()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    private fun loadFiles() {
        Util.Companion.Task {
            if (!loading) {
                try {
//                    requireView().progressBarFolderFiles_Container.visibility = ConstraintLayout.VISIBLE
                    loading = true
                    val fileList = Files.getfiles(location)
                    fileList.sortBy { it.lastModified() }
                    fileList.reverse()
                    FILE_MODEL_LIST.clear()
                    FILE_MODEL_LIST.addAll(fileList.stream()
                        .map {
                            FileModel(
                                fileList.indexOf(it) + 0L,
                                it.name,
                                it.absolutePath,
                                it.isDirectory,
                                it.parent
                            )
                        }
                        .collect(Collectors.toList()))
                    requireActivity().runOnUiThread {
                        this.requireView().list.recycledViewPool.clear()
                        this.requireView().list.adapter!!.notifyDataSetChanged()
                        requireView().progressBarFolderFiles_Container.visibility =
                            ConstraintLayout.INVISIBLE
                    }
                } catch (e: Exception) {
                    Log.e("Error", "E/RR", e)
                }
                loading = false
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    override fun onResume() {
        super.onResume()
        this.loadFiles()
    }


}
