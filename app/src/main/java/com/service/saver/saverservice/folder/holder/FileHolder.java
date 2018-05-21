package com.service.saver.saverservice.folder.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.service.saver.saverservice.R;
import com.service.saver.saverservice.fresco.drawable.CircleProgressBarDrawable;

public class FileHolder extends RecyclerView.ViewHolder {
    public final View vm;
    public TextView filename;
    public SimpleDraweeView draweeView;

    public FileHolder(View view) {
        super(view);
        vm = view;
        filename = view.findViewById(R.id.nombre_file);
        draweeView = view.findViewById(R.id.preview_file);
        draweeView.getHierarchy().setProgressBarImage(new CircleProgressBarDrawable());

    }
}