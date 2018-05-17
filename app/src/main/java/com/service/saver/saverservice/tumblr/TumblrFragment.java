package com.service.saver.saverservice.tumblr;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.service.saver.saverservice.R;
import com.service.saver.saverservice.tumblr.adapter.TumblrAdapter;
import com.service.saver.saverservice.tumblr.model.TumblrModel;
import com.service.saver.saverservice.tumblr.util.JumblrHolder;
import com.tumblr.jumblr.types.Blog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import needle.Needle;


public class TumblrFragment extends Fragment {

    private OnFragmentInteractionListener mListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tumblr, container, false);
        RecyclerView listView = (RecyclerView) view.findViewById(R.id.list_tumblrs);
        ArrayList<TumblrModel> dataSet = new ArrayList<>(0);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        listView.setLayoutManager(layoutManager);
        JumblrHolder cl = new JumblrHolder();
        TumblrAdapter tumblrAdapter = new TumblrAdapter(dataSet);
        Needle.onBackgroundThread().execute(() -> {
            Map<String, Object> map = new HashMap<>();
            final long[] value = {0L};
            map.put("limit", 20L);
            map.put("offset", value);
            List<Blog> blogs = cl.userFollowing(map);
              String blogAvatar = cl.blogAvatar("fappqueens");
               dataSet.add(new TumblrModel(dataSet.size() + 0L, "fappqueens", blogAvatar));

          /*  while (!blogs.isEmpty()) {
                blogs.stream().forEach(e -> {
                    dataSet.add(new TumblrModel(dataSet.size() + 0L, e.getName(), e.avatar()));
                    getActivity().runOnUiThread(() -> {
                        tumblrAdapter.notifyDataSetChanged();
                    });
                });
                value[0] = value[0] + 20;
                map.put("offset", value[0]);
                blogs = cl.userFollowing(map);
            }*/
            getActivity().runOnUiThread(() -> {
                TumblrAdapter tumblrAdapter2 = new TumblrAdapter(dataSet);
                listView.setAdapter(tumblrAdapter2);

            });

        });
        listView.setAdapter(tumblrAdapter);
        listView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            //throw new RuntimeException(context.toString()                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
