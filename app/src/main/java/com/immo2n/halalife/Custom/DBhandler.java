package com.immo2n.halalife.Custom;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBhandler extends SQLiteOpenHelper {
    //FLAGS -- DO NOT ALTER THESE VALUES
    public static String userLoggedInEntryName = "userLogged";
    public static String userProfileEntryName = "profile";
    public static String signalTrue = "true";
    public static String signalFalse = "false";
    //FLAGS
    private static final String DB_NAME = "HALALiFE-LDB";
    private static final int DB_VERSION = 1;

    //Settings table
    private final String SETTINGS_TABLE_NAME = "user_settings";
    //SETTING NAME
    private final String SETTINGS_NAME_COL = "name";
    //SETTING VALUE
    private final String SETTINGS_VAL_COL = "value";

    //NET cache table
    private final String NET_CACHE_TABLE_NAME = "net_cache";
    //SETTING NAME
    private final String NET_CACHE_NAME_COL = "hash";
    //SETTING VALUE
    private final String NET_CACHE_VAL_COL = "value";

    private final String ID_COL = "id";

    public DBhandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public interface DBhandlerCallback {
        void onDone(String result);
        void onFail(String error);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query1 = "CREATE TABLE " + SETTINGS_TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SETTINGS_NAME_COL + " TEXT,"
                + SETTINGS_VAL_COL + " TEXT)";
        db.execSQL(query1);
        String query2 = "CREATE TABLE " + NET_CACHE_TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NET_CACHE_NAME_COL + " TEXT,"
                + NET_CACHE_VAL_COL + " TEXT)";
        db.execSQL(query1);
    }

    //Table operations
    public void clearCache() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + NET_CACHE_TABLE_NAME);
    }

    public void getCache(String hash, DBhandlerCallback callback){
        try {
            String result = null;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT " + NET_CACHE_VAL_COL + " FROM " + NET_CACHE_TABLE_NAME + " WHERE " + NET_CACHE_NAME_COL + " = ? ORDER BY id DESC LIMIT 1 OFFSET 0", new String[]{hash});
            if (cursor.moveToFirst()) {
                do {
                    result = cursor.getString(0);
                } while (cursor.moveToNext());
            }
            cursor.close();
            callback.onDone(result);
        }
        catch (Exception e){
            callback.onFail(e.toString());
        }
    }

    public void addCache(String hash, String value) {
        // DB
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // VALUES
        values.put(NET_CACHE_NAME_COL, hash);
        values.put(NET_CACHE_VAL_COL, value);

        // Check the current row count in the table
        String countQuery = "SELECT COUNT(*) FROM " + NET_CACHE_TABLE_NAME;
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = 0;
        if (cursor.moveToFirst()) {
            rowCount = cursor.getInt(0);
        }
        cursor.close();

        // If the row count is equal to 100, delete the last row
        if (rowCount == 100) {
            String deleteQuery = "DELETE FROM " + NET_CACHE_TABLE_NAME + " WHERE id = (SELECT MIN(id) FROM " + NET_CACHE_TABLE_NAME + ")";
            db.execSQL(deleteQuery);
        }

        // Insert the new row
        db.insert(NET_CACHE_TABLE_NAME, null, values);
    }

    public String getSettings(String settingName){
        try {
            String result = null;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT " + SETTINGS_VAL_COL + " FROM " + SETTINGS_TABLE_NAME + " WHERE " + SETTINGS_NAME_COL + " = ? ORDER BY id DESC LIMIT 1 OFFSET 0", new String[]{settingName});
            if (cursor.moveToFirst()) {
                do {
                    result = cursor.getString(0);
                } while (cursor.moveToNext());
            }
            cursor.close();
            return result;
        }
        catch (Exception e){
            return null;
        }
    }

    public void addSetting(String settingName, String value){
        //DB
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //VALUES
        values.put(SETTINGS_NAME_COL, settingName);
        values.put(SETTINGS_VAL_COL, value);

        //Pass
        if(getSettings(settingName) != null ){
            db.update(SETTINGS_TABLE_NAME, values, SETTINGS_NAME_COL+" = ?", new String[]{settingName});
        }
        else {
            db.insert(SETTINGS_TABLE_NAME, null, values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SETTINGS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + NET_CACHE_TABLE_NAME);
        onCreate(db);
    }

}
