package com.service.saver.saverservice.tumblr.model;

public class PostModel {
    private byte[] preview;
    private Long id;
    private String blogname;
    private String name;
    private String type;
    private String filename;
    private String url;
    private String previewurl;
    boolean saved;

    public PostModel(Long id, String name, String type, String filename, String url, String previewurl, boolean saved) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.filename = filename;
        this.url = url;
        this.previewurl = previewurl;
        this.saved = saved;
    }

    public PostModel(Long id, String blogname, String name, String type, String filename, String url, String previewurl, boolean saved) {
        this.id = id;
        this.blogname = blogname;
        this.name = name;
        this.type = type;
        this.filename = filename;
        this.url = url;
        this.previewurl = previewurl;
        this.saved = saved;
    }

    public String getBlogname() {
        return blogname;
    }

    public void setBlogname(String blogname) {
        this.blogname = blogname;
    }

    public String getPreviewurl() {
        return previewurl;
    }

    public void setPreviewurl(String previewurl) {
        this.previewurl = previewurl;
    }

    public byte[] getPreview() {
        return preview;
    }

    public void setPreview(byte[] preview) {
        this.preview = preview;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public PostModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }
/*
    public void loadPreview() throws IOException {
        final Object o = Utility.readObject( "-pre.wwww");
        if (o == null) {

            URL url = new URL(getPreviewurl());
            URLConnection ucon = url.openConnection();
            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }
            preview = baf.toByteArray();
            Utility.savefile(name + "-pre.www", preview);

        } else {
            preview = (byte[]) o;
        }
    }*/
}