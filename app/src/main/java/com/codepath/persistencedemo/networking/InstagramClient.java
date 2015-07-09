package com.codepath.persistencedemo.networking;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class InstagramClient {
    private static final String BASE_URL = "https://api.instagram.com/v1/";
    private static final String CLIENT_ID = "e05c462ebd86446ea48a5af73769b602";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void getPopularPosts(AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl("media/popular"), getDefaultRequestParams(), responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    private static RequestParams getDefaultRequestParams() {
        RequestParams params = new RequestParams();
        params.put("client_id", CLIENT_ID);
        return params;
    }
}
