package com.codepath.persistencedemo.helpers;

import com.codepath.persistencedemo.models.InstagramPost;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    private static final String TAG = "Utils";

    public static List<InstagramPost> decodePostsFromJsonResponse(JSONObject jsonObject) {
        List<InstagramPost> posts = InstagramPost.fromJson(getDataJsonArray(jsonObject));
        return posts == null ? new ArrayList<InstagramPost>() : posts;
    }

    private static JSONArray getDataJsonArray(JSONObject jsonObject) {
        JSONArray jsonArray = null;
        if (jsonObject != null) {
            jsonArray = jsonObject.optJSONArray("data");
        }
        return jsonArray;
    }
}
