package com.service.saver.saverservice.twitter.userlink


import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.facebook.drawee.view.SimpleDraweeView
import com.service.saver.saverservice.MainTabActivity
import com.service.saver.saverservice.R
import com.service.saver.saverservice.domain.UserLink
import com.service.saver.saverservice.sqllite.AdminSQLiteOpenHelper
import com.service.saver.saverservice.twitter.TwitterPostFragment
import kotlinx.android.synthetic.main.user_view_item.view.*



class UserRecyclerViewAdapter(
    private val mValues: ArrayList<UserLink>,
    private val mOnClickListener: TwitterPostFragment.OnUserListListInteractionListener
) : androidx.recyclerview.widget.RecyclerView.Adapter<UserRecyclerViewAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_view_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.text.text = item.username
        val db = AdminSQLiteOpenHelper(holder.mView.context)
        if (item.avatar_url.isNullOrEmpty()) {
            try {
                com.service.saver.saverservice.util.Util.Companion.Task {

                    try {

                        val profileImageURL =
                            MainTabActivity.JTWITTER.jtwitter.showUser(item.username)
                                .get400x400ProfileImageURL()
                        item.avatar_url = profileImageURL
                        val imageuri = Uri.parse(profileImageURL)
                        holder.image.setImageURI(imageuri, holder.mView.context)
                        db.updateUserLink(item)
                    } catch (e: Exception) {
                        if (e.message == "User not found.")
                            db.deleteUserLink(item);
                    }
                }.execute()

            } catch (e: Exception) {
                if (e.message == "User not found.") {
                    db.deleteUserLink(item);
                }
            }
        } else {
            val imageuri = Uri.parse(item.avatar_url)
            holder.image.setImageURI(imageuri, holder.mView.context)
        }

        holder.mView.setOnClickListener {
            mOnClickListener.onUserListListInteractionListener(item)
        }


        holder.button_delete.setOnClickListener {
            val builder = holder.mView.context?.let { AlertDialog.Builder(it) }
            builder!!.setTitle("Delete user?")

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            //val view = inflater.inflate(R.layout.twitter_user_url, null)
            builder
                .setPositiveButton("Delete") { _, _ ->
                    db.deleteUserLink(item)
                    deleteItem(position)
                }
                .setNegativeButton("No") { dialog, _ -> dialog.cancel() }

            builder.create().show()
        }


    }

    fun deleteItem(position: Int) {
        mValues.removeAt(position)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(mView) {
        val text: TextView = mView.username_list
        val image: SimpleDraweeView = mView.iamge_user
        val button_delete: Button = mView.delete_button_user_list

    }
}
