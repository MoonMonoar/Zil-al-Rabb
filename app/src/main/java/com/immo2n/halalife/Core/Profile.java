package com.immo2n.halalife.Core;

public class Profile {
    int id;
    int posts;
    int followers;
    int followings;
    String full_name;
    String phone;
    String face;
    String bio;
    String email;
    String photo;
    String username;
    String email_verified;
    Boolean verified_badge;
    Boolean professional_mode;
    String gender;
    String time_joined;
    String last_login;
    String ip_address;
    Boolean is_banned;
    String skip_photo_update;
    String address;
    String living;
    String institute;
    String work;

    public String getAddress() {
        return address;
    }

    public Boolean getIs_banned() {
        return is_banned;
    }

    public void setIs_banned(Boolean is_banned) {
        this.is_banned = is_banned;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLiving() {
        return living;
    }

    public void setLiving(String living) {
        this.living = living;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public Boolean getProfessional_mode() {
        return professional_mode;
    }

    public void setProfessional_mode(Boolean professional_mode) {
        this.professional_mode = professional_mode;
    }

    public int getPosts() {
        return posts;
    }

    public void setPosts(int posts) {
        this.posts = posts;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getFollowings() {
        return followings;
    }

    public void setFollowings(int followings) {
        this.followings = followings;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSkip_photo_update() {
        return skip_photo_update;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public void setSkip_photo_update(String skip_photo_update) {
        this.skip_photo_update = skip_photo_update;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getEmail_verified() {
        return email_verified;
    }

    public void setEmail_verified(String email_verified) {
        this.email_verified = email_verified;
    }

    public Boolean getVerified_badge() {
        return verified_badge;
    }

    public void setVerified_badge(Boolean verified_badge) {
        this.verified_badge = verified_badge;
    }

    public String getTime_joined() {
        return time_joined;
    }

    public void setTime_joined(String time_joined) {
        this.time_joined = time_joined;
    }

    public String getLast_login() {
        return last_login;
    }

    public void setLast_login(String last_login) {
        this.last_login = last_login;
    }

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }
    public String getPhoto() {
        return photo;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
