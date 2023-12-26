package com.immo2n.halalife.DataObjects;

import com.immo2n.halalife.Core.Profile;

public class ProfileResponse {
    boolean status;
    Profile profile;

    public boolean isStatusOk() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
