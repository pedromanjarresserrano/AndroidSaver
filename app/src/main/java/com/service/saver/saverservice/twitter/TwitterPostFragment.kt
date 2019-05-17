package com.service.saver.saverservice.twitter

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.EditText
import com.service.saver.saverservice.MainTabActivity
import com.service.saver.saverservice.R
import com.service.saver.saverservice.domain.UserLink
import com.service.saver.saverservice.sqllite.AdminSQLiteOpenHelper
import com.service.saver.saverservice.twitter.dummy.DummyContent.DummyItem
import kotlinx.android.synthetic.main.fragment_filemodel_list.view.*
import twitter4j.Paging
import twitter4j.Status
import java.util.stream.Collectors


class TwitterPostFragment : Fragment() {

    // TODO: Customize parameters
    private var columnCount = 1

    private var listener: OnListFragmentInteractionListener? = null
    private var mScrollListener: EndlessRecyclerOnScrollListener? = null
    private var isAtBottom: Boolean = false


    private var user: String = ""
    private var db: AdminSQLiteOpenHelper? = null

    private val itemscount = 40

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_twitterpost_list, container, false)
        setHasOptionsMenu(true)
        var linearLayoutManager = LinearLayoutManager(this.context)
        with(view.list) {
            layoutManager = linearLayoutManager
            addItemDecoration(DividerItemDecoration(activity, layoutManager!!.layoutDirection))

        }
        mScrollListener = object : EndlessRecyclerOnScrollListener(linearLayoutManager) {
            override fun onLoadMore(current_page: Int) {
                view.list.stopScroll();
                loadPage()
                this.setLoading(false)
            }
        }

        view.list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (!recyclerView!!.canScrollVertically(1)) {
                    isAtBottom = true
                    view.list.scrollToPosition(paging.page * itemscount - 1);
                    loadPage()
                } else {
                    isAtBottom = false
                }
            }
        })
        view.list.adapter = TwitterPostRecyclerViewAdapter(POST_LIST)
        db = AdminSQLiteOpenHelper(this.context)

        return view
    }

    private var paging: Paging = Paging(1, itemscount)

    @RequiresApi(Build.VERSION_CODES.N)
    private fun loadUser(user: String, view: View) {
        paging = Paging(1, itemscount)
        val collect = MainTabActivity.jtwitter.jtwitter.getUserTimeline(user, paging).stream()/*.m  ap { e -> e.mediaEntities }*/.collect(Collectors.toList());
        POST_LIST.clear();
        POST_LIST.addAll(collect)
        view.list.adapter.notifyDataSetChanged()
        view.list.scrollToPosition(0)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun loadPage() {
        paging = Paging(paging.page + 1, itemscount)
        val collect = MainTabActivity.jtwitter.jtwitter.getUserTimeline(user, paging).stream()/*.m  ap { e -> e.mediaEntities }*/.collect(Collectors.toList());
        print(POST_LIST)
        POST_LIST.addAll(collect)
        print(POST_LIST)
        POST_LIST.sortByDescending {
            it.id
        }
        view!!.list.adapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.twitter_menu, menu)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.load_twitter -> {
                val builder = AlertDialog.Builder(activity)
                // Get the layout inflater
                val inflater = activity!!.layoutInflater
                var servername = EditText(this.context);
                builder.setTitle("Add user")
                builder.setView(servername);
                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                val view = inflater.inflate(R.layout.twitter_user_url, null)
                builder
                        .setPositiveButton("Ok") { dialog, id ->
                            val string = servername.text.toString()
                            val split = string.split("/");
                            var username = split.last()
                            var user = UserLink()
                            user.username = username
                            db!!.agregarUserLink(user)
                            this.view?.let { loadUser(username, it) };
                        }
                        .setNegativeButton("Cancel") { dialog, id -> dialog.cancel() }
                builder.create().show()
                return true
            }
            R.id.list_user_twitter -> {
                val builder = AlertDialog.Builder(activity)
                val usersTwitter : List<UserLink> = db!!.allUserLinks()
                builder.setTitle("User's")
                        .setItems(usersTwitter.map { it.toString() }.toTypedArray()) { dialog, which ->
                            loadUser(usersTwitter.get(which).username, this.view!!)
                        }
                builder.create().show()

                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: DummyItem?)
    }

    companion object {

        val POST_LIST: ArrayList<Status> = ArrayList();

    }
}
