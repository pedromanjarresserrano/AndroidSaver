package com.service.saver.saverservice.tumblr.adapter;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.service.saver.saverservice.MainTabActivity;
import com.service.saver.saverservice.R;
import com.service.saver.saverservice.tumblr.holder.PostHolder;
import com.service.saver.saverservice.tumblr.model.PostModel;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostHolder> {

    private List<PostModel> dataSet;

    public PostAdapter(List<PostModel> dataSet) {
        this.dataSet = dataSet;
    }

    public int getCount() {
        return dataSet.size();
    }

    public PostModel getItem(int position) {
        return dataSet.get(position);
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_tumblr, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        PostModel item = getItem(position);
        holder.blog_name.setText(item.getBlogname());
        holder.caption.setText(Html.fromHtml(item.getFilename()));
        holder.caption.setMovementMethod(LinkMovementMethod.getInstance());
        View.OnClickListener onClickListener = v -> {
            FancyToast.makeText(MainTabActivity.activity, "Downloading", Toast.LENGTH_SHORT, FancyToast.INFO, true);
            // ids.add(position);

        };
        holder.b.setOnClickListener(onClickListener);
        Uri uri = Uri.parse(item.getPreviewurl());
        holder.draweeView.setImageURI(uri);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


}