package com.service.saver.saverservice.twitter

import androidx.appcompat.app.AlertDialog
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.*
import android.widget.EditText
import com.service.saver.saverservice.MainTabActivity
import com.service.saver.saverservice.R
import com.service.saver.saverservice.domain.UserLink
import com.service.saver.saverservice.sqllite.AdminSQLiteOpenHelper
import com.service.saver.saverservice.twitter.userlink.UserRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_filemodel_list.view.*
import twitter4j.Paging
import twitter4j.Status
import java.util.stream.Collectors
import android.view.WindowManager
import needle.Needle
import java.util.*


class TwitterPostFragment : Fragment() {

    private var mScrollListener: EndlessRecyclerOnScrollListener? = null
    private var isAtBottom: Boolean = false


    private var user: String = ""
    private var db: AdminSQLiteOpenHelper? = null

    private val itemscount = 10

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_twitterpost_list, container, false)
        setHasOptionsMenu(true)
        var linearLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(this.context)
        with(view.list) {
            layoutManager = linearLayoutManager
            addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(activity, layoutManager!!.layoutDirection))

        }
        mScrollListener = object : EndlessRecyclerOnScrollListener(linearLayoutManager) {
            override fun onLoadMore(current_page: Int) {
                view.list.stopScroll();
                loadPage()
                this.setLoading(false)
            }
        }

        view.list.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: androidx.recyclerview.widget.RecyclerView, newState: Int) {
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

    override fun onStart() {
        super.onStart()

//        loadUser(this.user)

    }

    private var paging: Paging = Paging(1, itemscount)

    @RequiresApi(Build.VERSION_CODES.N)
    private fun loadUser(user: String) {
        paging = Paging(1, itemscount)
        val collect = MainTabActivity.jtwitter.jtwitter.getUserTimeline(user, paging).stream()/*.m  ap { e -> e.mediaEntities }*/.collect(Collectors.toList());
        POST_LIST.clear();
        POST_LIST.addAll(collect)
        view!!.list.adapter!!.notifyDataSetChanged()
        view!!.list.scrollToPosition(0)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun loadPage() {
        val adapter = view!!.list.adapter
        Needle.onBackgroundThread().execute {
            if (!updating) {
                updating = true

                paging = Paging(paging.page + 1, itemscount)
                val collect = MainTabActivity.jtwitter.jtwitter.getUserTimeline(user, paging).stream()/*.m  ap { e -> e.mediaEntities }*/.collect(Collectors.toList());
                collect.forEach { e -> POST_LIST.add(e) }
                Needle.onMainThread().execute {
                    adapter!!.notifyDataSetChanged()
                    updating = false
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.twitter_menu, menu)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.load_twitter -> {
                val builder = activity?.let { AlertDialog.Builder(it) }
                // Get the layout inflater
                val inflater = activity!!.layoutInflater
                var servername = EditText(this.context);
                if (builder != null) {
                    builder.setTitle("Add user")

                    builder.setView(servername)
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
                                this.view?.let { loadUser(username) };
                            }
                            .setNegativeButton("Cancel") { dialog, id -> dialog.cancel() }

                    builder.create().show()
                }
                return true
            }
            R.id.list_user_twitter -> {
                val inflater = activity!!.layoutInflater
                var create: AlertDialog = AlertDialog.Builder(activity!!).create();
                val usersTwitter: List<UserLink> = db!!.allUserLinks()
                val view = inflater.inflate(R.layout.user_view_list, null)
                val alertDialog = AlertDialog.Builder(activity!!)
                val list = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.user_view_list_reecycler)
                list.setLayoutManager(androidx.recyclerview.widget.LinearLayoutManager(this.context));
                list.setHasFixedSize(true)
                val userAdapter = UserRecyclerViewAdapter(usersTwitter, object : OnUserListListInteractionListener {
                    override fun OnUserListListInteractionListener(user: UserLink?) {
                        if (user != null) {

                            loadUser(user!!.username)
                        }
                        create?.cancel()
                    }
                })
                list.adapter = userAdapter
                alertDialog.setView(view)
                val lp = WindowManager.LayoutParams()
                create = alertDialog.create()

                lp.copyFrom(create.getWindow().getAttributes())
                lp.width = 150
                lp.height = 500
                lp.x = -170
                lp.y = 100
                create.window.attributes = lp
                create.show();

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
    interface OnUserListListInteractionListener {
        fun OnUserListListInteractionListener(item: UserLink?)
    }

    companion object {

        val POST_LIST: LinkedList<Status> = LinkedList();
        var updating: Boolean = false

    }
}
