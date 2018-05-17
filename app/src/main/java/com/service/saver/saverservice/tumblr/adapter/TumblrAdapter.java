package com.service.saver.saverservice.tumblr.adapter;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.service.saver.saverservice.R;
import com.service.saver.saverservice.tumblr.PostActivity;
import com.service.saver.saverservice.tumblr.holder.TumblrHolder;
import com.service.saver.saverservice.tumblr.model.TumblrModel;

import java.util.List;

public class TumblrAdapter extends RecyclerView.Adapter<TumblrHolder> {

    private List<TumblrModel> dataSet;
    private int id;

    public TumblrAdapter(List<TumblrModel> dataSet) {
        this.dataSet = dataSet;
    }

    public int getCount() {
        return dataSet.size();
    }

    public TumblrModel getItem(int position) {
        return dataSet.get(position);
    }

    @NonNull
    @Override
    public TumblrHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TumblrHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tumblr, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TumblrHolder holder, int position) {
        TumblrModel item = getItem(position);
        holder.textView.setText(item.getName());
        Uri uri = Uri.parse(item.getAvatarurl());
        holder.draweeView.setImageURI(uri);
        holder.vm.setOnClickListener((v) -> {
            Intent intent = new Intent(v.getContext(),PostActivity.class);
            intent.putExtra("BLOG",item.getName());
            v.getContext().startActivity(intent);
        });
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