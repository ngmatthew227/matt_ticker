package com.matt.tickers.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SqliteDbHelper extends SQLiteOpenHelper {

    public SqliteDbHelper(Context context) {
        super(context, "my.db", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE market( id INTEGER PRIMARY KEY AUTOINCREMENT, code VARCHAR(20) NOT NULL, display_code VARCHAR(200) NOT NULL, display_idx INTEGER NOT NULL, show TINYINT(2) DEFAULT 1, show_in_min TINYINT(2) DEFAULT 1)");
        db.execSQL("INSERT INTO market (code,display_code,display_idx,show,show_in_min) VALUES ('btcusdt','BTC/USDT',1,1,1)");
        db.execSQL("INSERT INTO market (code,display_code,display_idx,show,show_in_min) VALUES ('ethusdt','ETH/USDT',2,1,0)");
        db.execSQL("INSERT INTO market (code,display_code,display_idx,show,show_in_min) VALUES ('solusdt','SOL/USDT',3,0,0)");
        db.execSQL("INSERT INTO market (code,display_code,display_idx,show,show_in_min) VALUES ('dogeusdt','DOGE/USDT',4,0,0)");
        db.execSQL("INSERT INTO market (code,display_code,display_idx,show,show_in_min) VALUES ('fttusdt','FTT/USDT',5,1,0)");
        db.execSQL("INSERT INTO market (code,display_code,display_idx,show,show_in_min) VALUES ('manausdt','MANA/USDT',6,0,0)");
        db.execSQL("INSERT INTO market (code,display_code,display_idx,show,show_in_min) VALUES ('flowusdt','FLOW/USDT',7,0,0)");
        db.execSQL("INSERT INTO market (code,display_code,display_idx,show,show_in_min) VALUES ('sandusdt','SAND/USDT',8,1,0)");
        db.execSQL("INSERT INTO market (code,display_code,display_idx,show,show_in_min) VALUES ('hsi','HSIF/HSI',9,1,0)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("db", "upgrade");
        if (newVersion == 2){
            db.execSQL("DROP TABLE market");
            onCreate(db);
        }
    }
}
