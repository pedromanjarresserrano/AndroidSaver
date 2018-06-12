package com.service.saver.saverservice.folder

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.view.*
import com.service.saver.saverservice.R
import com.service.saver.saverservice.folder.model.FileModel
import com.service.saver.saverservice.util.Files
import kotlinx.android.synthetic.main.fragment_filemodel_list.view.*
import java.io.File
import java.util.Comparator.comparingLong


class FileModelFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_filemodel_list, container, false)
        setHasOptionsMenu(true)
        with(view.list) {
            layoutManager = GridLayoutManager(context, when {
                activity!!.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT -> 3
                else -> 5
            })
            addItemDecoration(DividerItemDecoration(activity, layoutManager!!.layoutDirection))
        }
        view.list.adapter = MyFileModelRecyclerViewAdapter(FILE_MODEL_LIST)

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

    override fun onAttach(context: Context?) {
        super.onAttach(context)

    }

    override fun onStart() {
        super.onStart()
        loadFiles()
    }

    private fun loadFiles() {
        val fileList = Files.getfiles(Files.getRunningDirByFile())
        fileList.sortWith(comparingLong<File>({ it.lastModified() }).reversed())
        if (fileList.size > FILE_MODEL_LIST.size) {
            FILE_MODEL_LIST.clear()
            for (f in fileList) {
                val fileModel = FileModel(FILE_MODEL_LIST.size + 0L, f.name, f.absolutePath)
                FILE_MODEL_LIST.add(fileModel)
            }
        }


        view!!.list.adapter.notifyDataSetChanged()

    }


    companion object {
        val FILE_MODEL_LIST = ArrayList<FileModel>();

        @JvmStatic
        fun newInstance(): FileModelFragment {
            val fragment = FileModelFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
