package com.service.saver.saverservice.tumblr.model;

public class TumblrModel {

    private Long id;
    private String url;
    private String name;
    private String avatarurl;

    private byte[] avatar;

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

    public byte[] getAvatar() {
        return avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
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
/*
    public void loadPreview() throws IOException {
        final Object o = Utility.readObject(name + "-avatar.bmp");
        if (o == null) {
            URL url = new URL(avatarurl);
            URLConnection ucon = url.openConnection();
            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }
            avatar = baf.toByteArray();
            Utility.savefile(name + "-avatar.bmp", avatar);

        } else {
            avatar = (byte[]) o;
        }
    }*/
}
