package com.immo2n.halalife.Main.DataObjects;

import com.immo2n.halalife.Core.AppState;
import com.immo2n.halalife.DataObjects.PostsObject;

public class ProfileGrid {
    com.immo2n.halalife.Core.Profile profile;
    AppState appState;
    boolean isInfoGreed;
    PostsObject postsObject;

    public ProfileGrid(AppState appState, boolean isInfoGreed, PostsObject postsObject) {
        this.profile = appState.getProfile();
        this.appState = appState;
        this.isInfoGreed = isInfoGreed;
        this.postsObject = postsObject;
    }

    public PostsObject getPostsObject() {
        return postsObject;
    }

    public void setPostsObject(PostsObject postsObject) {
        this.postsObject = postsObject;
    }

    public com.immo2n.halalife.Core.Profile getProfile() {
        return profile;
    }

    public void setProfile(com.immo2n.halalife.Core.Profile profile) {
        this.profile = profile;
    }

    public AppState getAppState() {
        return appState;
    }

    public void setAppState(AppState appState) {
        this.appState = appState;
    }

    public boolean isInfoGreed() {
        return isInfoGreed;
    }

    public void setInfoGreed(boolean infoGreed) {
        isInfoGreed = infoGreed;
    }
}
