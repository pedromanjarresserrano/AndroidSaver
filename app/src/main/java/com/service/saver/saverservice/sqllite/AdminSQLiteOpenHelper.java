package com.service.saver.saverservice.sqllite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.collection.ArraySet;

import com.service.saver.saverservice.domain.PostLink;
import com.service.saver.saverservice.domain.TempLink;
import com.service.saver.saverservice.domain.UserLink;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "saver_db";
    private static final int DATABASE_VERSION = 7;

    public AdminSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public AdminSQLiteOpenHelper(Context context, String nombre, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, nombre, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PostLink.TABLE_CREATE);
        db.execSQL(UserLink.TABLE_CREATE);
        db.execSQL(TempLink.TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //  db.execSQL(UserLink.DROP_TABLE);
        //  db.execSQL(UserLink.TABLE_CREATE);
        if (newVersion > oldVersion) {
            db.execSQL(PostLink.DROP_TABLE);
            db.execSQL(PostLink.TABLE_CREATE);
            //  db.execSQL("ALTER TABLE "+UserLink.TABLE_NAME +" ADD COLUMN avatar_url TEXT");
            //db.execSQL(PostLink.ALTER_TABLE);
            db.execSQL(TempLink.DROP_TABLE);
            db.execSQL(TempLink.TABLE_CREATE);

        }
    }

    public long agregarUserLink(UserLink userLink) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("username", userLink.getUsername());
        values.put("createDate", getDate(userLink.getCreateDate()));
        long id = db.insert(UserLink.TABLE_NAME, null, values);
        return id;

    }

    public long agregarUserLink(String username, String date) {
        UserLink userLink = new UserLink();
        userLink.setUsername(username);
        userLink.setCreateDate(getDate(date));
        return agregarUserLink(userLink);
    }


    public long agregarPostLink(PostLink postLink) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("url", postLink.getUrl());
        values.put("parent_url", postLink.getParent_url());
        values.put("username", postLink.getUsername());
        values.put("createDate", getDate(postLink.getCreateDate()));
        values.put("save", postLink.getSave() ? 1 : 0);
        long id = db.insert(PostLink
                .TABLE_NAME, null, values);
        return id;
    }


    public long agregarTempLink(TempLink templink) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("url", templink.getUrl());
            values.put("parent_url", templink.getParent_url());
            values.put("createDate", getDate(templink.getCreateDate()));
            long id = db.insert(TempLink
                    .TABLE_NAME, null, values);
            return id;
        } catch (Exception e) {
            Log.e("error", "ERR/R", e);
            return -1;
        }
    }

    public List<PostLink> getAllPostLinkByParentLink(String parent_url) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(PostLink.TABLE_NAME,
                new String[]{"id", "url", "parent_url", "username", "createDate", "save"},
                "parent_url" + "=?",
                new String[]{String.valueOf(parent_url)}, null, null, null, null);
        List<PostLink> postLinks = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    PostLink postLink = new PostLink(
                            cursor.getLong(cursor.getColumnIndex("id")),
                            cursor.getString(cursor.getColumnIndex("url")),
                            cursor.getString(cursor.getColumnIndex("parent_url")),
                            cursor.getString(cursor.getColumnIndex("username")),
                            getDate(cursor.getString(cursor.getColumnIndex("createDate"))),
                            cursor.getInt(cursor.getColumnIndex("save")) == 1);
                    postLinks.add(postLink);
                } while (cursor.moveToNext());
            }
        }
        // close the db connection
        cursor.close();
        return postLinks;
    }


    public PostLink getPostLink(String url) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(PostLink.TABLE_NAME,
                new String[]{"id", "url", "parent_url", "username", "createDate", "save"},
                "url" + "=?",
                new String[]{String.valueOf(url)}, null, null, null, null);
        PostLink postLink = null;
        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() > 0)
                // prepare postLink objectparent_url
                postLink = new PostLink(
                        cursor.getLong(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("url")),
                        cursor.getString(cursor.getColumnIndex("parent_url")),
                        cursor.getString(cursor.getColumnIndex("username")),
                        getDate(cursor.getString(cursor.getColumnIndex("createDate"))),
                        cursor.getInt(cursor.getColumnIndex("save")) == 1 ? true : false);
        }
        // close the db connection
        cursor.close();
        return postLink;
    }

    public UserLink getUserLink(String username) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(UserLink.TABLE_NAME,
                new String[]{"id", "username", "avatar_url", "createDate"},
                "username" + "=?",
                new String[]{String.valueOf(username)}, null, null, null, null);
        UserLink userLink = null;
        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() > 0)
            // prepare note object
            {
                String avatar_url = cursor.getString(cursor.getColumnIndex("avatar_url"));
                userLink = new UserLink(
                        cursor.getLong(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("username")),
                        avatar_url == null ? "" : avatar_url,
                        getDate(cursor.getString(cursor.getColumnIndex("createDate"))));
            }
        }
        // close the db connection
        cursor.close();
        return userLink;
    }

    public List<PostLink> getAllPostLinks() {
        List<PostLink> postLinks = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + PostLink.TABLE_NAME + " ORDER BY  id DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PostLink postLink = new PostLink(
                        cursor.getLong(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("url")),
                        cursor.getString(cursor.getColumnIndex("parent_url")),
                        cursor.getString(cursor.getColumnIndex("username")),
                        getDate(cursor.getString(cursor.getColumnIndex("createDate"))),
                        cursor.getInt(cursor.getColumnIndex("save")) == 1 ? true : false);
                postLinks.add(postLink);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // close db connection
        // return notes list
        return postLinks;
    }


    public List<PostLink> getAllUnSavePostLinks() {
        List<PostLink> postLinks = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + PostLink.TABLE_NAME + " WHERE save = 0 ORDER BY  id DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PostLink postLink = new PostLink(
                        cursor.getLong(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("url")),
                        cursor.getString(cursor.getColumnIndex("parent_url")),
                        cursor.getString(cursor.getColumnIndex("username")),
                        getDate(cursor.getString(cursor.getColumnIndex("createDate"))),
                        cursor.getInt(cursor.getColumnIndex("save")) == 1 ? true : false);
                postLinks.add(postLink);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // close db connection
        // return notes list
        return postLinks;
    }

    private String getDate(Date date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            return dateFormat.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    private Date getDate(String date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            return dateFormat.parse(date);
        } catch (Exception e) {
            return new Date();
        }
    }

    public List<UserLink> allUserLinks() {
        List<UserLink> postLinks = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + UserLink.TABLE_NAME + " ORDER BY  id DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String avatar_url = cursor.getString(cursor.getColumnIndex("avatar_url"));
                UserLink userLink = new UserLink(
                        cursor.getLong(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("username")),
                        avatar_url == null ? "" : avatar_url,
                        getDate(cursor.getString(cursor.getColumnIndex("createDate"))));
                postLinks.add(userLink);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // close db connection
        // return notes list
        return postLinks;
    }

    public int updatePostLink(PostLink postLink) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("url", postLink.getUrl());
        values.put("username", postLink.getUsername());
        values.put("createDate", getDate(postLink.getCreateDate()));
        values.put("save", postLink.getSave() ? 1 : 0);
        // updating row
        return db.update(PostLink.TABLE_NAME, values, "id = ?",
                new String[]{String.valueOf(postLink.getId())});
    }


    public int updateUserLink(UserLink userLink) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("username", userLink.getUsername());
        values.put("avatar_url", userLink.getAvatar_url());
        values.put("createDate", getDate(userLink.getCreateDate()));
        // updating row
        return db.update(UserLink.TABLE_NAME, values, "id = ?",
                new String[]{String.valueOf(userLink.getId())});
    }

    public void deleteUserLink(UserLink userLink) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(UserLink.TABLE_NAME, "id = ?",
                new String[]{String.valueOf(userLink.getId())});
    }


    public ArrayList<TempLink> allTempLinks() {
        List<TempLink> postLinks = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TempLink.TABLE_NAME + " ORDER BY  id DESC";
        String del = "DELETE FROM " + TempLink.TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                TempLink userLink = new TempLink(
                        cursor.getLong(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("url")),
                        cursor.getString(cursor.getColumnIndex("parent_url")),
                        getDate(cursor.getString(cursor.getColumnIndex("createDate"))));
                postLinks.add(userLink);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // close db connection
        // return notes list
        return (ArrayList<TempLink>) postLinks;
    }

    public int updateTempLink(TempLink postLink) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("url", postLink.getUrl());
        values.put("createDate", getDate(postLink.getCreateDate()));
        // updating row
        return db.update(TempLink.TABLE_NAME, values, "id = ?",
                new String[]{String.valueOf(postLink.getId())});
    }


    public void deleteTempLink(TempLink tem) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TempLink.TABLE_NAME, "id = ?",
                new String[]{String.valueOf(tem.getId())});
    }
}