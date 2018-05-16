package com.service.saver.saverservice.tumblr.adapter;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.service.saver.saverservice.MainTabActivity;
import com.service.saver.saverservice.R;
import com.service.saver.saverservice.tumblr.model.PostModel;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends BaseAdapter {

    private List<PostModel> dataSet;
    Context mContext;
    private int id;
    private List<Integer> ids = new ArrayList<>();


    public PostAdapter(List<PostModel> dataSet, Context context) {
        this.dataSet = dataSet;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public PostModel getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        // Get the data item for this position
        final PostModel item = (PostModel) getItem(position);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.post_tumblr, viewGroup, false);
        }


        ImageView imagenpreview = (ImageView) view.findViewById(R.id.imageView);

        //  TextView textView = (TextView) view.findViewById(R.id.tipo_post);
        //      textView.setText(item.getType());
        TextView caption = (TextView) view.findViewById(R.id.caption);
        TextView blog_name = (TextView) view.findViewById(R.id.blog_name);
        blog_name.setText(item.getBlogname());
        caption.setText(Html.fromHtml(item.getFilename()));
        caption.setMovementMethod(LinkMovementMethod.getInstance());
        Button b = (Button) view.findViewById(R.id.save_button);
        View.OnClickListener onClickListener = v -> {
            FancyToast.makeText(MainTabActivity.activity, "Downloading", Toast.LENGTH_SHORT, FancyToast.INFO,true);
            ids.add(position);

        };
        b.setOnClickListener(onClickListener);
      /*  Glide
                .with(imagenpreview.getContext())
                .load(item.getPreviewurl())
                .into(imagenpreview);*/
        return view;
    }



}