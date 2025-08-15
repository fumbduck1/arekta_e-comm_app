package com.arektaecomm.model;

public class Campaign {
    private String id;
    private String imageUrl;
    private String linkUrl;
    // Constructors, getters/setters

    public Campaign(String id, String imageUrl, String linkUrl) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }
}