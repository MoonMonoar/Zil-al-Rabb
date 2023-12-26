package com.immo2n.halalife.Core;

import com.immo2n.halalife.Custom.Global;

public class Server {
    public static String apiEndPoint = "http://192.168.83.54"; //Main api end point
    public static String
            routeSignup = apiEndPoint+"/api/signup.php",
            routeGetProfile = apiEndPoint+"/api/getProfile.php",
            routeUpdateProfile = apiEndPoint+"/api/updateProfile.php";
    Global global;
    public Server(Global global){
        this.global = global;
    }
    public String getApiEndPoint() {
        return apiEndPoint;
    }
}
