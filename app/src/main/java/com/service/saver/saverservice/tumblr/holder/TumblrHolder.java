package com.service.saver.saverservice.tumblr.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.service.saver.saverservice.R;

public class TumblrHolder extends RecyclerView.ViewHolder {

    public final View vm;
    public TextView textView;
    public SimpleDraweeView draweeView;

    public TumblrHolder(View itemView) {
        super(itemView);
        vm = itemView;
        textView = itemView.findViewById(R.id.txtitemtumblr);
         draweeView = itemView.findViewById(R.id.image_avatar);
    }
}