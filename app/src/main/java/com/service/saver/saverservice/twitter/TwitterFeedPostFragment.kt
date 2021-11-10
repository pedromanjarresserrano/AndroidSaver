package com.service.saver.saverservice.twitter

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
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
import kotlinx.android.synthetic.main.fragment_filemodel_list.view.list
import kotlinx.android.synthetic.main.fragment_twitterpost_list.view.*
import needle.Needle
import twitter4j.Paging
import twitter4j.Status
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap


class TwitterFeedPostFragment : Fragment() {

    private var mScrollListener: EndlessRecyclerOnScrollListener? = null
    private var isAtBottom: Boolean = false


    private var db: AdminSQLiteOpenHelper? = null

    private val itemscount = 25
    private var loadingDialog: LoadingDialog? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        view.list.viewTreeObserver.addOnGlobalLayoutListener { loadingDialog!!.dismissDialog() }
        view.btn_open_user.text = "Load Own Timeline"
        view.btn_open_user.setOnClickListener {
            view.btn_open_user.visibility = View.INVISIBLE
            loadPage()
        }
        return view
    }

    override fun onStart() {
        super.onStart()

//        loadUser(this.user)

    }

    override fun onResume() {
        super.onResume()
        if (POST_LIST.isNotEmpty()) {
            requireView().btn_open_user.visibility = ImageButton.INVISIBLE
        }
    }

    private var paging: Paging = Paging(1, itemscount)


    private fun loadUser(user: String) {
        try {
            Needle.onMainThread().execute {
                loadingDialog!!.startLoadingDialog()
            }
            USERNAME = user;
            paging = Paging(1, itemscount)
            val collect = MainTabActivity.JTWITTER.jtwitter.getUserTimeline(user, paging)
            POST_LIST.clear();
            POST_LIST.addAll(collect)
            requireView().list.adapter!!.notifyDataSetChanged()
            requireView().list.scrollToPosition(0)
            requireView().btn_open_user.visibility = ImageButton.INVISIBLE
            Needle.onMainThread().execute {
                loadingDialog!!.dismissDialog()
            }
        } catch (e: Exception) {
            Log.e("Error", "ERROR LOAD USER", e);
            Toast.makeText(this.context, "Error with user " + user, Toast.LENGTH_LONG)
            loadingDialog!!.dismissDialog()

        }
    }


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
                if (POST_LIST.isNotEmpty())
                    paging.sinceId = POST_LIST.last.id
                val collect = MainTabActivity.JTWITTER.jtwitter.getHomeTimeline(paging)
                    .stream()/*.m  ap { e -> e.mediaEntities }*/
                    .sorted { o1, o2 -> o1.createdAt.compareTo(o2.createdAt) }
                    .collect(Collectors.toList());

                collect.forEach { e ->
                    var find = POST_LIST.find { f -> f.id == e.id }
                    if (find == null)
                        POST_LIST.add(e)
                }
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


    companion object {

        val POST_LIST: LinkedList<Status> = LinkedList();
        var updating: Boolean = false
        private var USERNAME: String = ""

    }
}
