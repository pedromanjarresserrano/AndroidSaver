package com.service.saver.saverservice.links

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import com.service.saver.saverservice.R
import com.service.saver.saverservice.domain.TempLink
import com.service.saver.saverservice.sqllite.AdminSQLiteOpenHelper
import java.util.ArrayList

/**
 * A fragment representing a list of Items.
 */
class ItemLinkFragment : Fragment() {

    private var columnCount = 1
    private var db: AdminSQLiteOpenHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    private var list: ArrayList<TempLink>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_item_link_list, container, false)
        db = AdminSQLiteOpenHelper(this.context)
        this.list = db!!.allTempLinks() as ArrayList<TempLink>?
        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = TempLinkRecyclerViewAdapter(list, db!!)
                val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                        //Toast.makeText((viewHolder as TempLinkRecyclerViewAdapter.ViewHolder).contentView.context, "on Move", Toast.LENGTH_SHORT).show()
                        return false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                        Toast.makeText((viewHolder as TempLinkRecyclerViewAdapter.ViewHolder).contentView.context, "Delete", Toast.LENGTH_SHORT).show()
                        //Remove swiped item from list and notify the RecyclerView
                        val position = viewHolder.adapterPosition
                        with(view as RecyclerView)  {
                            (adapter as TempLinkRecyclerViewAdapter).deleteItem(position)
                            adapter!!.notifyDataSetChanged()
                        }
                    }
                }

                ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(view)
            }
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        this.list!!.clear()
        this.list!!.addAll(db!!.allTempLinks())
        if (view is RecyclerView) {
            (view as RecyclerView).adapter!!.notifyDataSetChanged()
        }
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
                ItemLinkFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }
}