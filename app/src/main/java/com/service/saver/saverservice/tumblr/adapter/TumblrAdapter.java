package com.service.saver.saverservice.tumblr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.service.saver.saverservice.R;
import com.service.saver.saverservice.tumblr.model.TumblrModel;

import java.util.List;

public class TumblrAdapter extends BaseAdapter {

    private List<TumblrModel> dataSet;
    Context mContext;
    private int id;

    public TumblrAdapter(List<TumblrModel> dataSet, Context context) {
        this.dataSet = dataSet;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public TumblrModel getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final TumblrModel item = (TumblrModel) getItem(position);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.tumblr, viewGroup, false);
        }
        ImageView imagenpreview = (ImageView) view.findViewById(R.id.image_avatar);
        TextView textView = (TextView) view.findViewById(R.id.txtitemtumblr);
        textView.setText(item.getName());
       /* Glide
                .with(imagenpreview.getContext())
                .load(item.getAvatar())
                .into(imagenpreview);*/
        return view;
    }
}