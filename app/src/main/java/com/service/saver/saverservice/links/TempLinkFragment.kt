package com.service.saver.saverservice.links

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import com.service.saver.saverservice.R
import com.service.saver.saverservice.domain.TempLink
import com.service.saver.saverservice.sqllite.AdminSQLiteOpenHelper
import kotlinx.android.synthetic.main.fragment_item_link_list.view.*
import java.util.*


/**
 * A fragment representing a list of Items.
 */
class TempLinkFragment : Fragment() {

    private var columnCount = 1
    private var db: AdminSQLiteOpenHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    private var listItems: ArrayList<TempLink>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_link_list, container, false)
        db = AdminSQLiteOpenHelper(this.context)
        this.listItems = db!!.allTempLinks() as ArrayList<TempLink>?
        // Set th adapter
        if (listItems.isNullOrEmpty()) {
            view.title_nolinks.visibility = TextView.VISIBLE
        } else {
            view.title_nolinks.visibility = TextView.INVISIBLE
        }
        with(view.list) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            adapter = TempLinkRecyclerViewAdapter(
                listItems,
                db!!,
                object : OnUpdateListInteractionListener {
                    override fun onUpdateListInteractionListener(item: TempLink?) {
                        message()
                    }
                })
            val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    //Toast.makeText((viewHolder as TempLinkRecyclerViewAdapter.ViewHolder).contentView.context, "on Move", Toast.LENGTH_SHORT).show()
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                    Toast.makeText(
                        (viewHolder as TempLinkRecyclerViewAdapter.ViewHolder).contentView.context,
                        "Delete",
                        Toast.LENGTH_SHORT
                    ).show()
                    //Remove swiped item from list and notify the RecyclerView
                    val position = viewHolder.adapterPosition
                    (adapter as TempLinkRecyclerViewAdapter).deleteItem(position)
                    adapter!!.notifyDataSetChanged()

                }
            }
            val dividerItemDecoration =
                DividerItemDecoration(view.getContext(), DividerItemDecoration.HORIZONTAL)
            dividerItemDecoration.setDrawable(
                ColorDrawable(
                    resources.getColor(
                        R.color.accent_dark_light,
                        null
                    )
                )
            )

            addItemDecoration(dividerItemDecoration)

            ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(this)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        this.listItems!!.clear()
        this.listItems!!.addAll(db!!.allTempLinks())
        requireView().list.adapter!!.notifyDataSetChanged()
        message()
    }

    private fun message() {
        if (listItems.isNullOrEmpty()) {
            requireView().title_nolinks.visibility = TextView.VISIBLE
        } else {
            requireView().title_nolinks.visibility = TextView.INVISIBLE
        }
    }

    interface OnUpdateListInteractionListener {
        fun onUpdateListInteractionListener(item: TempLink?)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

    }
}