package com.service.saver.saverservice.tumblr.model;

import java.io.Serializable;

public class TumblrModel implements Serializable {

    private Long id;
    private String url;
    private String name;
    private String avatarurl;


    public TumblrModel(Long id, String name, String avatarurl) {
        this.id = id;
        this.name = name;
        this.avatarurl = avatarurl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setUrl(String url) {
        this.url = url;
    }

    public String getAvatarurl() {
        return avatarurl;
    }

    public void setAvatarurl(String avatarurl) {
        this.avatarurl = avatarurl;
    }

}
