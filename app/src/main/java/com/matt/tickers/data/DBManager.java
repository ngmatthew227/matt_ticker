package com.matt.tickers.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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

    private static Set<String> getNamedParams(final String query) {
        Set<String> namedParameters = new HashSet<>();
        int pos = 0;
        while ((pos = query.indexOf(":", pos + 1)) != -1) {
            int end = query.substring(pos).indexOf(" ");
            if (end == -1)
                end = query.length();
            else
                end += pos;
            namedParameters.add(query.substring(pos + 1, end));
        }
        return namedParameters;
    }

    private static String preparedStatement(String orginQuery, Map<String, Object> params) {
        Set<String> namedParams = getNamedParams(orginQuery);
        for (String nameParam : namedParams) {
            String replaceName = ":" + nameParam;
            orginQuery = orginQuery.replace(replaceName, Objects.requireNonNull(params.get(nameParam)).toString());
        }
        Log.i("SQL", orginQuery);
        return orginQuery;
    }

    public void setSqliteDbOpen(Context context) {
        mDbHelper = new SqliteDbHelper(context.getApplicationContext());
        mDb = mDbHelper.getWritableDatabase();
    }

    public List<Map<String, Object>> getMarketDatas() {
        openDb();
        List<Map<String, Object>> marketList = new ArrayList<>();
        String sql = "SELECT * FROM market m WHERE show = 1 AND code <> 'hsi' ORDER BY display_idx";
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
        String sql = "SELECT * from market order by market.display_idx";
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

    public boolean ifHsiExist(){
        openDb();
        String sql = "SELECT * FROM market WHERE code='hsi' AND show=1";
        @SuppressLint("Recycle") Cursor cursor = mDb.rawQuery(sql, null);
        if (cursor != null) {
            return cursor.moveToNext();
        } else {
            return false;
        }
    }

    public Integer updateShowField(String code, Integer value) {
        openDb();
        ContentValues cv = new ContentValues();
        cv.put("show", value);
        return mDb.update("market", cv, "code= ?", new String[]{code});
    }

    public Long insertData(String code, String displayCode) {
        openDb();
        String sql = "SELECT id from market m order by id desc limit 1";
        Cursor cursor = mDb.rawQuery(sql, null);
        int lastId = 0;
        if (cursor.moveToFirst()) {
            lastId = cursor.getInt(0);
        }

        ContentValues cv = new ContentValues();

        cv.put("code", code);
        cv.put("display_code", displayCode);
        cv.put("display_idx", lastId + 1);
        cv.put("show", 0);
        cv.put("show_in_min", 0);
        return mDb.insert("market", null, cv);
    }

    public void swapPosition(Integer fromIdx, Integer toIdx) {
        openDb();
        String upSql = ""
                + " UPDATE"
                + "   market"
                + " SET"
                + "   display_idx = case"
                + "     when (display_idx > ( :toIdx -1) and display_idx < :fromIdx ) then display_idx + 1"
                + "     when display_idx = :fromIdx then :toIdx "
                + "   end"
                + " WHERE"
                + "   display_idx >= :toIdx "
                + "   and display_idx <= :fromIdx ";

        String downSql = ""
                + " UPDATE"
                + "   market"
                + " SET"
                + "   display_idx = case"
                + "     when (display_idx > :fromIdx and display_idx < ( :toIdx + 1)) then display_idx -1"
                + "     when display_idx = :fromIdx then :toIdx "
                + "   end"
                + " WHERE"
                + "   display_idx >= :fromIdx "
                + "   and display_idx <= :toIdx ";

        boolean upDirection = fromIdx > toIdx;
        String sql = "";
        if (upDirection) {
            sql = upSql;
        } else {
            sql = downSql;
        }
        Map<String, Object> args = new HashMap<>();
        args.put("fromIdx", fromIdx);
        args.put("toIdx", toIdx);
        sql = preparedStatement(sql, args);
        mDb.execSQL(sql);
        closeDb();
    }

    public void deleteByDisplayIdx(Integer idx) {
        openDb();
        String sql = "DELETE FROM market WHERE display_idx = :idx ";
        Map<String, Object> args = new HashMap<>();
        args.put("idx", idx);
        sql = preparedStatement(sql, args);
        mDb.execSQL(sql);

        String updateIdxSql = " UPDATE market"
                + " SET display_idx = display_idx -1"
                + " WHERE display_idx > :idx ";

        updateIdxSql = preparedStatement(updateIdxSql, args);
        mDb.execSQL(updateIdxSql);
        closeDb();

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
