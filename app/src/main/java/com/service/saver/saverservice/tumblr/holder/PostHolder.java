package com.service.saver.saverservice.tumblr.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.service.saver.saverservice.R;
import com.service.saver.saverservice.fresco.drawable.CircleProgressBarDrawable;

public class PostHolder extends RecyclerView.ViewHolder {

    public TextView caption;
    public SimpleDraweeView draweeView;
    public TextView blog_name;
    public Button saveButton;

    public PostHolder(View view) {
        super(view);
        caption = view.findViewById(R.id.caption);
        blog_name = view.findViewById(R.id.blog_name);
        saveButton = view.findViewById(R.id.save_button);
        draweeView = view.findViewById(R.id.imageView);
        draweeView.getHierarchy().setProgressBarImage(new CircleProgressBarDrawable());

    }
}