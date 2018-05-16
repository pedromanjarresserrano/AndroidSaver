package com.service.saver.saverservice.tumblr.util;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.request.RequestBuilder;
import com.tumblr.jumblr.types.Blog;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.User;

import org.scribe.model.Token;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JumblrHolder {
    JumblrClient client = new JumblrClient(
            "oiK8MFj4VQX52JEvfyKWi0CvdoZyYATq4SjnRj9fXkMV8T4X1g",
            "c72SlqTby5ejAMizhv0Pj6IYenCxGBZ2oF5UU6NymOiFPs5dYo"
    );
    public JumblrHolder() {
        client.setToken(
                "uxdOglGfxNpMlRjuwcNOQ6fTQC9egcnC9nDaEkYJvYRopRArET",
                "HNxH7lIXDUtJ9SN0M5Djg3B1D2s5Vf6jxAhZTPUg7KAZ70eoz6"
        );
    }

    public void setToken(String token, String tokenSecret) {

        client.setToken(token, tokenSecret);
    }

    public void setToken(Token token) {
        client.setToken(token);
    }

    public void xauth(String email, String password) {
        client.xauth(email, password);
    }

    public User user() {
        return client.user();
    }

    public List<Post> userDashboard(Map<String, ?> options) {
        return client.userDashboard(options);
    }

    public List<Post> userDashboard() {
        return client.userDashboard();
    }

    public List<Blog> userFollowing(Map<String, ?> options) {
        return client.userFollowing(options);
    }

    public List<Blog> userFollowing() {
        return client.userFollowing();
    }

    public List<Post> tagged(String tag, Map<String, ?> options) {
        return client.tagged(tag, options);
    }

    public List<Post> tagged(String tag) {
        return client.tagged(tag);
    }

    public Blog blogInfo(String blogName) {
        return client.blogInfo(blogName);
    }

    public List<User> blogFollowers(String blogName, Map<String, ?> options) {
        return client.blogFollowers(blogName, options);
    }

    public List<User> blogFollowers(String blogName) {
        return client.blogFollowers(blogName);
    }

    public List<Post> blogLikes(String blogName, Map<String, ?> options) {
        return client.blogLikes(blogName, options);
    }

    public List<Post> blogLikes(String blogName) {
        return client.blogLikes(blogName);
    }

    public List<Post> blogPosts(String blogName, Map<String, ?> options) {
        return client.blogPosts(blogName, options);
    }

    public List<Post> blogPosts(String blogName) {
        return client.blogPosts(blogName);
    }

    public Post blogPost(String blogName, Long postId) {
        return client.blogPost(blogName, postId);
    }

    public List<Post> blogQueuedPosts(String blogName, Map<String, ?> options) {
        return client.blogQueuedPosts(blogName, options);
    }

    public List<Post> blogQueuedPosts(String blogName) {
        return client.blogQueuedPosts(blogName);
    }

    public List<Post> blogDraftPosts(String blogName, Map<String, ?> options) {
        return client.blogDraftPosts(blogName, options);
    }

    public List<Post> blogDraftPosts(String blogName) {
        return client.blogDraftPosts(blogName);
    }

    public List<Post> blogSubmissions(String blogName, Map<String, ?> options) {
        return client.blogSubmissions(blogName, options);
    }

    public List<Post> blogSubmissions(String blogName) {
        return client.blogSubmissions(blogName);
    }

    public List<Post> userLikes(Map<String, ?> options) {
        return client.userLikes(options);
    }

    public List<Post> userLikes() {
        return client.userLikes();
    }

    public String blogAvatar(String blogName, Integer size) {
        return client.blogAvatar(blogName, size);
    }

    public String blogAvatar(String blogName) {
        return client.blogAvatar(blogName);
    }

    public void like(Long postId, String reblogKey) {
        client.like(postId, reblogKey);
    }

    public void unlike(Long postId, String reblogKey) {
        client.unlike(postId, reblogKey);
    }

    public void follow(String blogName) {
        client.follow(blogName);
    }

    public void unfollow(String blogName) {
        client.unfollow(blogName);
    }

    public void postDelete(String blogName, Long postId) {
        client.postDelete(blogName, postId);
    }

    public Post postReblog(String blogName, Long postId, String reblogKey, Map<String, ?> options) {
        return client.postReblog(blogName, postId, reblogKey, options);
    }

    public Post postReblog(String blogName, Long postId, String reblogKey) {
        return client.postReblog(blogName, postId, reblogKey);
    }

    public void postEdit(String blogName, Long id, Map<String, ?> detail) throws IOException {
        client.postEdit(blogName, id, detail);
    }

    public Long postCreate(String blogName, Map<String, ?> detail) throws IOException {
        return client.postCreate(blogName, detail);
    }

    public <T extends Post> T newPost(String blogName, Class<T> klass) throws IllegalAccessException, InstantiationException {
        return client.newPost(blogName, klass);
    }

    public void setRequestBuilder(RequestBuilder builder) {
        client.setRequestBuilder(builder);
    }

    public RequestBuilder getRequestBuilder() {
        return client.getRequestBuilder();
    }
}
