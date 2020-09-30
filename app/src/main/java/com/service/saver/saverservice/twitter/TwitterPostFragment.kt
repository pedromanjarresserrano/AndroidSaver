package com.service.saver.saverservice.twitter

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.service.saver.saverservice.MainTabActivity
import com.service.saver.saverservice.R
import com.service.saver.saverservice.domain.UserLink
import com.service.saver.saverservice.sqllite.AdminSQLiteOpenHelper
import com.service.saver.saverservice.twitter.userlink.UserRecyclerViewAdapter
import com.service.saver.saverservice.util.LoadingDialog
import kotlinx.android.synthetic.main.fragment_filemodel_list.view.*
import needle.Needle
import twitter4j.Paging
import twitter4j.Status
import java.util.*
import java.util.stream.Collectors


class TwitterPostFragment : Fragment() {

    private var mScrollListener: EndlessRecyclerOnScrollListener? = null
    private var isAtBottom: Boolean = false


    private var user: String = ""
    private var db: AdminSQLiteOpenHelper? = null

    private val itemscount = 10
    private var loadingDialog: LoadingDialog? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_twitterpost_list, container, false)
        setHasOptionsMenu(true)
        var gridLayoutManager = GridLayoutManager(this.context, 2)
        with(view.list) {
            layoutManager = gridLayoutManager
            addItemDecoration(DividerItemDecoration(activity, layoutManager!!.layoutDirection))

        }
        mScrollListener = object : EndlessRecyclerOnScrollListener(gridLayoutManager) {
            override fun onLoadMore(current_page: Int) {
                view.list.stopScroll();
                loadPage()
                this.setLoading(false)
            }
        }

        view.list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (!recyclerView.canScrollVertically(1)) {
                    isAtBottom = true
                    loadPage()
                    //  view.list.scrollToPosition((paging.page-1) * itemscount );
                } else {
                    isAtBottom = false
                }
            }
        })
        view.list.adapter = TwitterPostRecyclerViewAdapter(POST_LIST)
        db = AdminSQLiteOpenHelper(this.context)
        this.loadingDialog = LoadingDialog(this.activity)

        view.list.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                loadingDialog!!.dismissDialog()
            }
        })
        return view
    }

    override fun onStart() {
        super.onStart()

//        loadUser(this.user)

    }

    private var paging: Paging = Paging(1, itemscount)

    @RequiresApi(Build.VERSION_CODES.N)
    private fun loadUser(user: String) {
        try {
            loadingDialog!!.startLoadingDialog()
            this.user = user;
            paging = Paging(1, itemscount)
            val collect = MainTabActivity.jtwitter.jtwitter.getUserTimeline(user, paging).stream()/*.m  ap { e -> e.mediaEntities }*/.collect(Collectors.toList())
            POST_LIST.clear();
            POST_LIST.addAll(collect)
            requireView().list.adapter!!.notifyDataSetChanged()
            requireView().list.scrollToPosition(0)
        } catch (e: Exception) {
            Log.e("Error", "ERROR LOAD USER", e);
            loadingDialog!!.dismissDialog()

        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun loadPage() {
        val adapter = requireView().list.adapter
        Needle.onBackgroundThread().execute {
            if (!updating) {
                Needle.onMainThread().execute {
                    loadingDialog!!.startLoadingDialog()
                }

                requireView().list.stopScroll()
                updating = true

                paging = Paging(paging.page + 1, itemscount)
                val collect = MainTabActivity.jtwitter.jtwitter.getUserTimeline(user, paging).stream()/*.m  ap { e -> e.mediaEntities }*/.collect(Collectors.toList());
                collect.forEach { e -> POST_LIST.add(e) }
                Needle.onMainThread().execute {
                    adapter!!.notifyDataSetChanged()
                    updating = false
                    requireView().list.stopScroll()
                    requireView().list.scrollToPosition(itemscount * (paging.page - 1))
                    requireView().list.stopScroll()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.twitter_menu, menu)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.load_twitter -> {
                val builder = activity?.let { AlertDialog.Builder(it) }
                // Get the layout inflater
                //val inflater = requireActivity().layoutInflater
                var servername = EditText(this.context);
                if (builder != null) {
                    builder.setTitle("Add user")

                    builder.setView(servername)
                    // Inflate and set the layout for the dialog
                    // Pass null as the parent view because its going in the dialog layout
                    //val view = inflater.inflate(R.layout.twitter_user_url, null)
                    builder
                            .setPositiveButton("Ok") { _, _ ->
                                val string = servername.text.toString()
                                val split = string.split("/");
                                var username = split.last()
                                var user = UserLink()
                                user.username = username
                                db!!.agregarUserLink(user)
                                this.view?.let { loadUser(username) };
                            }
                            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

                    builder.create().show()
                }
                return true
            }
            R.id.list_user_twitter -> {
                val inflater = requireActivity().layoutInflater
                var create: AlertDialog = AlertDialog.Builder(requireActivity()).create();
                val usersTwitter: List<UserLink> = db!!.allUserLinks()
                val view = inflater.inflate(R.layout.user_view_list, null)
                val alertDialog = AlertDialog.Builder(requireActivity())
                val list = view.findViewById<RecyclerView>(R.id.user_view_list_reecycler)
                list.layoutManager = LinearLayoutManager(this.context);
                list.setHasFixedSize(true)
                val userAdapter = UserRecyclerViewAdapter(usersTwitter, object : OnUserListListInteractionListener {
                    override fun onUserListListInteractionListener(user: UserLink?) {
                        if (user != null) {
                            create?.cancel()

                            loadUser(user.username)

                        }
                        create?.cancel()
                    }
                })
                list.adapter = userAdapter
                alertDialog.setView(view)
                val lp = WindowManager.LayoutParams()
                create = alertDialog.create()

                lp.copyFrom(create.window!!.attributes)
                lp.width = 150
                lp.height = 500
                lp.x = -170
                lp.y = 100
                create.window!!.attributes = lp
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
        fun onUserListListInteractionListener(item: UserLink?)
    }

    companion object {

        val POST_LIST: LinkedList<Status> = LinkedList();
        var updating: Boolean = false

    }
}
