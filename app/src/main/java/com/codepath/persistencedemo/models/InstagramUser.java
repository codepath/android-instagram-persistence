package com.codepath.persistencedemo.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InstagramUser implements Serializable {
    public String userName;
    public String profilePictureUrl;

    public static InstagramUser fromJson(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }

        InstagramUser user = new InstagramUser();

        try {
            user.userName = jsonObject.getString("username");
            user.profilePictureUrl = jsonObject.getString("profile_picture");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return user;
    }

    public static List<InstagramUser> fromJson(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }

        List<InstagramUser> users = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject;
            try {
                jsonObject = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            InstagramUser user = InstagramUser.fromJson(jsonObject);
            if (user != null) {
                users.add(user);
            }
        }
        return users;
    }

}
