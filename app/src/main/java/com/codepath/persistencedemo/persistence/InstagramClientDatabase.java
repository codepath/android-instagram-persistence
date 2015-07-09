package com.codepath.persistencedemo.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.codepath.persistencedemo.models.InstagramPost;
import com.codepath.persistencedemo.models.InstagramUser;

import java.util.ArrayList;
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
    private static final String KEY_POST_MEDIA_ID = "mediaId";
    private static final String KEY_POST_USER_ID_FK = "userId";
    private static final String KEY_POST_CREATED_TIME = "createdTime";

    // Users table columns
    private static final String KEY_USER_ID = "id";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_PROFILE_PICTURE_URL = "profilePictureUrl";
    // SQLiteOpenHelper methods
    public InstagramClientDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_POSTS_TABLE = "CREATE TABLE " + TABLE_POSTS +
                "(" +
                KEY_POST_ID + " INTEGER PRIMARY KEY," +
                KEY_POST_MEDIA_ID + " TEXT," +
                KEY_POST_USER_ID_FK + " INTEGER REFERENCES " + TABLE_USERS + "," +
                KEY_POST_CREATED_TIME + " INTEGER" +
                ")";

        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS +
                "(" +
                KEY_USER_ID + " INTEGER PRIMARY KEY," +
                KEY_USER_NAME + " TEXT," +
                KEY_USER_PROFILE_PICTURE_URL + " TEXT" +
                ")";

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_POSTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // On version change wipe old data and start new
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(db);
        }
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
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
                // Problem: Need to check if user already exists
                long userId = addUser(post.user, db);

                ContentValues values = new ContentValues();
                values.put(KEY_POST_MEDIA_ID, post.mediaId);
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
            closeDatabase(db);
        }
    }

    private long addUser(InstagramUser user, SQLiteDatabase writeableDb) {
        if (user == null) {
            throw new IllegalArgumentException(String.format("Attemping to add a null user to %s", DATABASE_NAME));
        }

        ContentValues values = new ContentValues();
        values.put(KEY_USER_NAME, user.userName);
        values.put(KEY_USER_PROFILE_PICTURE_URL, user.profilePictureUrl);

        return writeableDb.insert(TABLE_USERS, null, values);
    }

    // Read methods

    public List<InstagramPost> getAllInstagramPosts() {
        List<InstagramPost> posts = new ArrayList<>();

        /*   SELECT * FROM TABLE_POSTS
         *   LEFT OUTER JOIN TABLE_USERS
         *   ON TABLE_POSTS.KEY_POST_USER_ID_FK = TABLE_USERS.KEY_USER_ID
         */

        String userJoin = String.format("LEFT OUTER JOIN %s ON %s.%s = %s.%s",
                TABLE_USERS,
                TABLE_POSTS, KEY_POST_USER_ID_FK,
                TABLE_USERS, KEY_USER_ID);

        String postsSelectQuery = "SELECT * FROM " + TABLE_POSTS + " " + userJoin;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(postsSelectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    InstagramPost post = new InstagramPost();
                    post.mediaId = cursor.getString(cursor.getColumnIndexOrThrow(KEY_POST_MEDIA_ID));
                    post.createdTime = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_POST_CREATED_TIME));

                    InstagramUser user = new InstagramUser();
                    user.userName = cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_NAME));
                    user.profilePictureUrl = cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_PROFILE_PICTURE_URL));
                    post.user = user;

                    posts.add(post);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.wtf(TAG, "Error while trying to get posts from database");
            e.printStackTrace();
        } finally {
            closeCursor(cursor);
            closeDatabase(db);
        }
        return posts;
    }
    // Delete methods

    public void emptyAllTables() {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_POSTS, null, null);
        db.delete(TABLE_USERS, null, null);

        closeDatabase(db);
    }
    // Helper methods
    public void closeCursor(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    public void closeDatabase(SQLiteDatabase db) {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
