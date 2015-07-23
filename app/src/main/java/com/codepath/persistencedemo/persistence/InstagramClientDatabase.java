package com.codepath.persistencedemo.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.codepath.persistencedemo.models.InstagramPost;
import com.codepath.persistencedemo.models.InstagramUser;

import java.util.List;

public class InstagramClientDatabase extends SQLiteOpenHelper {
    private static final String TAG = "InstagramClientDatabase";

    // Constants
    private static final String DATABASE_NAME = "instagramClientDatabase";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_POSTS = "posts";
    private static final String TABLE_USERS = "users";

    // Posts table columns
    private static final String KEY_POST_ID = "id";
    private static final String KEY_POST_USER_ID_FK = "userId";
    private static final String KEY_POST_CREATED_TIME = "createdTime";

    // Users table columns
    private static final String KEY_USER_ID = "id";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_PROFILE_PICTURE_URL = "profilePictureUrl";

    private static String CREATE_POSTS_TABLE = "CREATE TABLE " + TABLE_POSTS +
            "(" +
            KEY_POST_ID + " INTEGER PRIMARY KEY," +
            KEY_POST_USER_ID_FK + " INTEGER REFERENCES " + TABLE_USERS + "," +
            KEY_POST_CREATED_TIME + " INTEGER" +
            ")";

    private static String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS +
            "(" +
            KEY_USER_ID + " INTEGER PRIMARY KEY," +
            KEY_USER_NAME + " TEXT," +
            KEY_USER_PROFILE_PICTURE_URL + " TEXT" +
            ")";

    /*   SELECT * FROM POSTS
     *   LEFT OUTER JOIN USERS
     *   ON POSTS.KEY_POST_USER_ID_FK = USERS.KEY_USER_ID
     */
    private static String POSTS_SELECT_QUERY =
            String.format("SELECT * FROM %s LEFT OUTER JOIN %s ON %s.%s = %s.%s",
                    TABLE_POSTS,
                    TABLE_USERS,
                    TABLE_POSTS, KEY_POST_USER_ID_FK,
                    TABLE_USERS, KEY_USER_ID);

    // Singleton instance
    private static InstagramClientDatabase sInstance;

    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static method "getInstance()" instead.
     */
    private InstagramClientDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized InstagramClientDatabase getInstance(Context context) {
        if (sInstance == null) {
            // Use the application context, which will ensure that you
            // don't accidentally leak an Activity's context.
            // See this article for more information: http://bit.ly/6LRzfx
            sInstance = new InstagramClientDatabase(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // TODO: Implement this method
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO: Implement this method
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: Implement this method
    }

    // Create methods
    public void addInstagramPosts(List<InstagramPost> posts) {
        if (posts == null) {
            throw new IllegalArgumentException(String.format("Attemping to add a null list of posts to %s", DATABASE_NAME));
        }

        // should be done off UI thread
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            for (InstagramPost post : posts) {
                long userId = addUser(post.user);

                ContentValues values = new ContentValues();
                values.put(KEY_POST_USER_ID_FK, userId);
                values.put(KEY_POST_CREATED_TIME, post.createdTime);

                db.insert(TABLE_POSTS, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.wtf(TAG, "Error while trying to add posts to database");
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    private long addUser(InstagramUser user) {
        if (user == null) {
            throw new IllegalArgumentException(String.format("Attemping to add a null user to %s", DATABASE_NAME));
        }
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_NAME, user.userName);
        values.put(KEY_USER_PROFILE_PICTURE_URL, user.profilePictureUrl);

        return db.insert(TABLE_USERS, null, values);
    }

    // Read method
    public List<InstagramPost> getAllInstagramPosts() {
        // TODO: Implement this method
        return null;
    }

    // Delete method
    public void emptyAllTables() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_POSTS, null, null);
            db.delete(TABLE_USERS, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.wtf(TAG, "Error while trying to empty all tables in database");
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    // Helper method
    public void closeCursor(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }
}
