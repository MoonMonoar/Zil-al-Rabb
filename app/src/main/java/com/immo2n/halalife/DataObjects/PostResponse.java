package com.immo2n.halalife.DataObjects;

import java.util.List;

public class PostResponse {
    boolean success;
    int count;
    List<PostsObject> posts;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<PostsObject> getPosts() {
        return posts;
    }

    public void setPosts(List<PostsObject> posts) {
        this.posts = posts;
    }
}
