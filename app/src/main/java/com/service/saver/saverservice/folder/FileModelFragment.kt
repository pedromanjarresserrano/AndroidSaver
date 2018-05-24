package com.service.saver.saverservice.folder

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.service.saver.saverservice.R
import com.service.saver.saverservice.folder.model.FileModel
import com.service.saver.saverservice.util.Files
import java.io.File
import java.util.Comparator
import kotlin.collections.ArrayList

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [FileModelFragment.OnListFragmentInteractionListener] interface.
 */
class FileModelFragment : Fragment() {

    // TODO: Customize parameters
    private var columnCount = 1
    private var FILE_MODEL_LIST = ArrayList<FileModel>();


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_filemodel_list, container, false)
        setHasOptionsMenu(true)
        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MyFileModelRecyclerViewAdapter(FILE_MODEL_LIST)
            }
        }
        return view
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.folder_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.reload_files_folder -> {
                loadFiles()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun loadFiles() {
        val fileList = Files.getfiles(Files.getRunningDirByFile())
        fileList.sortWith(Comparator.comparingLong<File>({ it.lastModified() }).reversed())
        if (fileList.size > FILE_MODEL_LIST.size) {
            for (f in fileList) {
                val fileModel = FileModel(FILE_MODEL_LIST.size + 0L, f.name, f.absolutePath)
                FILE_MODEL_LIST.add(fileModel)
                FolderFragment.FILE_MODEL_LIST.add(fileModel)
            }
        }

        if (view is RecyclerView) {
            (view as RecyclerView).adapter.notifyDataSetChanged()
        }
    }


    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
                FileModelFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }
}
