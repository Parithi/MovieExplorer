package com.parithi.movieexplorer.models;

/**
 * Created by Parithi on 21/12/15.
 */
public class Trailer {
    private String id;
    private String name;
    private String site;
    private String type;
    private String key;

    public Trailer(String id, String name, String site, String type, String key) {
        this.id = id;
        this.name = name;
        this.site = site;
        this.type = type;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
