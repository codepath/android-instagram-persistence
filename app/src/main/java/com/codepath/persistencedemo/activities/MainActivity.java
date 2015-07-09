package com.codepath.persistencedemo.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;
import com.codepath.persistencedemo.R;
import com.codepath.persistencedemo.adapters.InstagramPostsAdapter;
import com.codepath.persistencedemo.helpers.Utils;
import com.codepath.persistencedemo.models.InstagramPost;
import com.codepath.persistencedemo.networking.InstagramClient;
import com.codepath.persistencedemo.persistence.InstagramClientDatabase;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private List<InstagramPost> posts;
    private InstagramPostsAdapter postsAdapter;

    RecyclerView rvInstagramPosts;
    InstagramClientDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvInstagramPosts = (RecyclerView) findViewById(R.id.rvInstagramPosts);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        rvInstagramPosts.setLayoutManager(layoutManager);

        database = new InstagramClientDatabase(this);

        posts = new ArrayList<>();
        postsAdapter = new InstagramPostsAdapter(posts);

        rvInstagramPosts.setAdapter(postsAdapter);
        fetchPopularPosts();

    }

    private void fetchPopularPosts() {
        if (isNetworkAvailable()) {
            Toast.makeText(this, getString(R.string.network_available_toast), Toast.LENGTH_LONG).show();
            fetchPopularPostsFromInstagramApi();
        } else {
            Toast.makeText(this, getString(R.string.network_unavailable_toast), Toast.LENGTH_LONG).show();
            fetchPopularPostsFromCache();
        }
    }

    // should be done off UI thread
    private void fetchPopularPostsFromCache() {

        posts.clear();
        posts.addAll(database.getAllInstagramPosts());
        postsAdapter.notifyDataSetChanged();
    }

    // Full refresh of cache
    // should be done off UI thread
    private void putPopularPostsInCache(List<InstagramPost> posts) {
        database.emptyAllTables();
        database.addInstagramPosts(posts);
    }

    private void fetchPopularPostsFromInstagramApi() {
        InstagramClient.getPopularPosts(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                posts.clear();
                List<InstagramPost> newPosts = Utils.decodePostsFromJsonResponse(response);
                posts.addAll(newPosts);
                postsAdapter.notifyDataSetChanged();

                putPopularPostsInCache(newPosts);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "Network error when fetching popular posts");
                throwable.printStackTrace();
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
