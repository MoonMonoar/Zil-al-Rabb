package com.immo2n.halalife.Core;

public class Profile {
    String full_name;
    String phone;
    String email;
    String photo;
    String username;
    String email_verified;
    String verified_badge;
    String gender;
    String time_joined;
    String last_login;
    String ip_address;

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

    public String getVerified_badge() {
        return verified_badge;
    }

    public void setVerified_badge(String verified_badge) {
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
