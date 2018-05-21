package com.service.saver.saverservice.tumblr;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.service.saver.saverservice.R;
import com.service.saver.saverservice.tumblr.adapter.TumblrAdapter;
import com.service.saver.saverservice.tumblr.model.TumblrModel;
import com.service.saver.saverservice.tumblr.util.JumblrHolder;
import com.service.saver.saverservice.util.Files;
import com.tumblr.jumblr.types.Blog;
import com.tumblr.loglr.Loglr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import needle.Needle;


public class TumblrFragment extends Fragment {
    public static String TUMBLRLIST = "ListTumblrs.sss";

    private OnFragmentInteractionListener mListener;
    private List<TumblrModel> tumblrList = new ArrayList<>();
    private JumblrHolder cl = new JumblrHolder();
    private TumblrAdapter tumblrAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<TumblrModel> list = (List<TumblrModel>) Files.readObject(TUMBLRLIST);
        setHasOptionsMenu(true);
        if (list != null) {
            tumblrList.addAll(list);
        }
    }

    public boolean add(TumblrModel s) {
        Optional<TumblrModel> tumblrModel = tumblrList.stream().filter(e -> e.getName().compareToIgnoreCase(s.getName()) == 0).findFirst();
        if (!tumblrModel.isPresent()) {
            boolean add = tumblrList.add(s);
            Files.savefile(TUMBLRLIST, tumblrList);
            return add;
        } else
            return false;
    }

    public boolean remove(TumblrModel o) {
        boolean remove = tumblrList.remove(o);
        Files.savefile(TUMBLRLIST, tumblrList);
        return remove;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tumblr, container, false);
        RecyclerView listView = view.findViewById(R.id.list_tumblrs);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        listView.setLayoutManager(layoutManager);
        tumblrAdapter = new TumblrAdapter(tumblrList);
        listView.setAdapter(tumblrAdapter);
        listView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.get_from_user: {
                loadFromUser();
                return true;
            }
            case R.id.login_with_tumblr: {
                Loglr.INSTANCE
                        .setConsumerKey(JumblrHolder.CONSUMER_KEY)
                        .setConsumerSecretKey(JumblrHolder.CONSUMER_SECRET)
                        .setLoginListener(loginResult -> {
                            cl.setToken(loginResult.getOAuthToken(), loginResult.getOAuthTokenSecret());
                        })
                        .enable2FA(true)
                        .setUrlCallBack("https://www.tumblr.com/dashboard").initiate(getActivity());
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadFromUser() {
        Needle.onBackgroundThread().execute(() -> {
            Map<String, Object> map = new HashMap<>();
            final long[] value = {0L};
            map.put("limit", 20L);
            map.put("offset", value);
            List<Blog> blogs = cl.userFollowing(map);
            while (!blogs.isEmpty()) {
                blogs.stream().forEach(e -> {
                    this.add(new TumblrModel(this.tumblrList.size() + 0L, e.getName(), e.avatar()));
                    getActivity().runOnUiThread(() -> {
                        tumblrAdapter.notifyDataSetChanged();
                    });
                });
                value[0] = value[0] + 20;
                map.put("offset", value[0]);
                blogs = cl.userFollowing(map);
            }
            getActivity().runOnUiThread(() -> {
                tumblrAdapter.notifyDataSetChanged();
            });
        });
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_tumblr_tab, menu);
    }


}
