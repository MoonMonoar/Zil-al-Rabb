package com.immo2n.halalife.DataObjects;

import com.immo2n.halalife.Core.Profile;

public class RegisterOBJ {
    String token;
    boolean status;
    Profile profile;

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isStatusTrue() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
