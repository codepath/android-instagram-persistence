package com.codepath.persistencedemo.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InstagramPost implements Serializable {
    public String mediaId;
    public InstagramUser user;
    public long createdTime;

    public static InstagramPost fromJson(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }

        InstagramPost post = new InstagramPost();

        try {
            post.mediaId = jsonObject.getString("id");
            post.user = InstagramUser.fromJson(jsonObject.getJSONObject("user"));
            post.createdTime = jsonObject.getLong("created_time");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return post;
    }

    public static List<InstagramPost> fromJson(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }

        List<InstagramPost> posts = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject;
            try {
                jsonObject = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            InstagramPost post = InstagramPost.fromJson(jsonObject);
            if (post != null) {
                posts.add(post);
            }
        }
        return posts;
    }
}
