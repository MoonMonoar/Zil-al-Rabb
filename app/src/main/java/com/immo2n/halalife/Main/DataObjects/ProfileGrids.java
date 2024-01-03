package com.immo2n.halalife.Main.DataObjects;

import com.immo2n.halalife.Core.AppState;
import com.immo2n.halalife.Main.Profile;

public class ProfileGrids {
    Profile profile;
    AppState appState;
    boolean isInfoGreed = false;

    public ProfileGrids(Profile profile, AppState appState, boolean isInfoGreed) {
        this.profile = profile;
        this.appState = appState;
        this.isInfoGreed = isInfoGreed;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
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
