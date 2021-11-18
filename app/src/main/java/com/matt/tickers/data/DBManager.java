package com.matt.tickers.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBManager {
    private static DBManager mInstance = null;
    private SQLiteDatabase mDb = null;
    private SqliteDbHelper mDbHelper = null;

    public static DBManager getInstance() {
        if (mInstance == null) {
            mInstance = new DBManager();
        }
        return mInstance;
    }

    public void setSqliteDbOpen(Context context) {
        mDbHelper = new SqliteDbHelper(context.getApplicationContext());
        mDb = mDbHelper.getWritableDatabase();
    }

    public List<Map<String, Object>> getMarketDatas() {
        openDb();
        List<Map<String, Object>> marketList = new ArrayList<>();
        String sql = "SELECT * from market m where show = 1";
        Cursor cursor = mDb.rawQuery(sql, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Map<String, Object> product = new HashMap<>();
                product.put("id", cursor.getInt(0));
                product.put("code", cursor.getString(1));
                product.put("display_code", cursor.getString(2));
                product.put("display_idx", cursor.getInt(3));
                product.put("show", cursor.getInt(4));
                product.put("show_in_min", cursor.getInt(5));
                marketList.add(product);
            }
            cursor.close();
        }
        closeDb();
        return marketList;
    }

    public List<Map<String, Object>> getProductList() {
        openDb();
        List<Map<String, Object>> marketList = new ArrayList<>();
        String sql = "SELECT * from market";
        Cursor cursor = mDb.rawQuery(sql, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Map<String, Object> product = new HashMap<>();
                product.put("id", cursor.getInt(0));
                product.put("code", cursor.getString(1));
                product.put("display_code", cursor.getString(2));
                product.put("display_idx", cursor.getInt(3));
                product.put("show", cursor.getInt(4));
                product.put("show_in_min", cursor.getInt(5));
                marketList.add(product);
            }
            cursor.close();
        }
        closeDb();
        return marketList;
    }

    public Integer updateShowField(String code, Integer value) {
        openDb();
        ContentValues cv = new ContentValues();
        cv.put("show", value);
        return mDb.update("market", cv, "code= ?", new String[]{code});
    }

    private void openDb() {
        if (this.mDbHelper != null) {
            try {
                mDb = mDbHelper.getWritableDatabase();
            } catch (Exception e) {
                mDb = mDbHelper.getReadableDatabase();
                e.printStackTrace();
            }
        }
    }

    private void closeDb() {
        try {
            if (mDb != null) {
                mDb.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
