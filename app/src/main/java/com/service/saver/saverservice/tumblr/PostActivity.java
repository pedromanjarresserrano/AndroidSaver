package com.service.saver.saverservice.tumblr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.service.saver.saverservice.R;
import com.service.saver.saverservice.tumblr.adapter.PostAdapter;
import com.service.saver.saverservice.tumblr.model.PostModel;
import com.service.saver.saverservice.tumblr.util.JumblrHolder;
import com.tumblr.jumblr.types.Photo;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.PhotoSize;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.Video;
import com.tumblr.jumblr.types.VideoPost;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import needle.Needle;

public class PostActivity extends AppCompatActivity {
    JumblrHolder client = new JumblrHolder();
    private RecyclerView listView;
    private List<Post> posts = new ArrayList<>();
    ArrayList<PostModel> list = new ArrayList();
    private PostAdapter postAdapter;
    //   private SwipyRefreshLayout swipyRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Map<String, Object> map;
        String blog = getIntent().getExtras().getString("BLOG");
        map = new HashMap<>();
        final long[] value = {0L};
        map.put("limit", 20L);
        map.put("offset", value);
        String blogName = blog;

        listView = findViewById(R.id.list_post);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        listView.setLayoutManager(layoutManager);
        postAdapter = new PostAdapter(list);
        listView.setAdapter(postAdapter);
        Runnable runnable = () -> {
            //  swipyRefreshLayout.setRefreshing(true);
            posts = client.blogPosts(blogName, map);
            System.err.println("PST");
            for (Post p : posts) {
                Post.PostType type = p.getType();
                if (type == Post.PostType.PHOTO) {
                    PhotoPost aux = (PhotoPost) p;
                    for (Photo e : aux.getPhotos()) {
                        List<PhotoSize> sizes = e.getSizes();
                        String url = e.getOriginalSize().getUrl();
                        String[] split = url.split("/");
                        String s = split[split.length - 1];
                        int index = sizes.size() / 2;
                        list.add(new PostModel((long) list.size(), aux.getBlogName(), aux.getBlogName() + "www" + s, type.getValue(), e.getCaption(), url, sizes.get(index).getUrl(), false));
                    }

                }
                if (type == Post.PostType.VIDEO) {
                    VideoPost aux = (VideoPost) p;
                    List<Video> videos = aux.getVideos();
                    Video video = videos.get(videos.size() - 1);
                    Document doc = Jsoup.parse(video.getEmbedCode());
                    Elements elements = doc.select("source");
                    String src = elements.attr("src");
                    Elements vid = doc.select("video");
                    String pre = vid.attr("poster");
                    if (!src.isEmpty())
                        list.add(new PostModel((long) list.size(), aux.getBlogName(), aux.getBlogName() + "www" + aux.getId(), type.getValue(), aux.getCaption(), src, pre, false));


                }

            }
            runOnUiThread(() -> {
                PostAdapter postAdapter1 = new PostAdapter(list);
                listView.setAdapter(postAdapter1);
            });
/*
            runOnUiThread(() -> {

                postAdapter.notifyDataSetChanged();
                listView.post(() -> {
                    //            listView.smoothScrollToPosition(lastVisiblePosition);
                });
                //   swipyRefreshLayout.setRefreshing(false);
            });
*/
        };
        // swipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.list_post_refresh);
      /*  swipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                value[0] = value[0] + 20;
                map.put("offset", value[0]);

                Needle.onBackgroundThread().execute(runnable);
            }


        });*/
        Needle.onBackgroundThread().withTaskType("image-processing")
                .execute(runnable);

    }
}
