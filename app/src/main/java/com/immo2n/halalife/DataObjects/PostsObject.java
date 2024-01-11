package com.immo2n.halalife.DataObjects;

import com.immo2n.halalife.Core.Profile;

import java.util.List;

public class PostsObject {
    private int id;
    private int user;
    private String title;
    private String body;
    private String privacy;
    private List<String> file_array;
    private String time;
    private String type;
    private boolean isBanned;
    private int sponsorLevel;
    private int likes;
    private int comments;
    private int shares;
    private int views;
    private int rank;
    private Profile user_profile;

    public Profile getUser_profile() {
        return user_profile;
    }

    public void setUser_profile(Profile user_profile) {
        this.user_profile = user_profile;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public int getShares() {
        return shares;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public List<String> getFile_array() {
        return file_array;
    }

    public void setFile_array(List<String> file_array) {
        this.file_array = file_array;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    public int getSponsorLevel() {
        return sponsorLevel;
    }

    public void setSponsorLevel(int sponsorLevel) {
        this.sponsorLevel = sponsorLevel;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
