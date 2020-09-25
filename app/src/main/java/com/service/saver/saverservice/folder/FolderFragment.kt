package com.service.saver.saverservice.folder

import androidx.fragment.app.Fragment
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.view.*
import androidx.annotation.RequiresApi
import com.service.saver.saverservice.R
import com.service.saver.saverservice.folder.model.FileModel
import com.service.saver.saverservice.util.Files
import kotlinx.android.synthetic.main.fragment_filemodel_list.view.*
import needle.Needle

@RequiresApi(Build.VERSION_CODES.N)
class FolderFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_filemodel_list, container, false)
        setHasOptionsMenu(true)
        with(view.list) {
            layoutManager = androidx.recyclerview.widget.StaggeredGridLayoutManager(when {
                activity!!.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT -> 3
                else -> 5
            }, when {
                activity!!.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT -> androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
                else -> androidx.recyclerview.widget.StaggeredGridLayoutManager.HORIZONTAL
            })
            addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(activity, layoutManager!!.layoutDirection))
        }
        view.list.adapter = MyFileModelRecyclerViewAdapter(FILE_MODEL_LIST)
        val intent = activity!!.intent
        if (intent != null) {
            val extras = intent.extras
            if (extras != null) {
                val stringExtra = extras.getString("filepath")
                if (stringExtra != null)
                    location = stringExtra
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

    override fun onAttach(context: Context?) {
        super.onAttach(context)

    }

    override fun onStart() {
        super.onStart()
        loadFiles()
    }

    private fun loadFiles() {

        Needle.onBackgroundThread().execute {
            if (!loading) {
                loading = true
                val fileList = Files.getfiles(location)
                fileList.sortBy { it.lastModified() }
                fileList.reverse()
                FILE_MODEL_LIST.clear()
                for (f in fileList) {
                    val fileModel = FileModel(FILE_MODEL_LIST.size + 0L, f.name, f.absolutePath, f.isDirectory)
                    FILE_MODEL_LIST.add(fileModel)
                    Needle.onMainThread().execute {
                        this.view!!.list.adapter!!.notifyDataSetChanged()
                    }
                }
                loading = false


            }
        }


    }


    companion object {
        val FILE_MODEL_LIST = ArrayList<FileModel>();
        var loading = false;
        var location = Files.getAbsolutePath();
        @JvmStatic
        fun newInstance(): FolderFragment {
            val fragment = FolderFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
