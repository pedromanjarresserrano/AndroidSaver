package com.service.saver.saverservice.folder.model;

import java.io.Serializable;

public class FileModel implements Serializable {
    private Long id;
    private String name;
    private String filepath;

    public FileModel() {
    }

    public FileModel(Long id, String name, String filepath) {
        this.id = id;
        this.name = name;
        this.filepath = filepath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }


}
