package com.matt.tickers.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SqliteDbHelper extends SQLiteOpenHelper {

    public SqliteDbHelper(Context context) {
        super(context, "my.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE market( id INTEGER PRIMARY KEY AUTOINCREMENT, code VARCHAR(20) NOT NULL, display_code VARCHAR(200) NOT NULL, display_idx INTEGER NOT NULL, show TINYINT(2) DEFAULT 1, show_in_min TINYINT(2) DEFAULT 1)");
        db.execSQL("INSERT INTO market (code,display_code,display_idx,show,show_in_min) VALUES ('btcusdt','BTC/USDT',0,1,1)");
        db.execSQL("INSERT INTO market (code,display_code,display_idx,show,show_in_min) VALUES ('ethusdt','ETH/USDT',1,1,0)");
        db.execSQL("INSERT INTO market (code,display_code,display_idx,show,show_in_min) VALUES ('solusdt','SOL/USDT',2,0,0)");
        db.execSQL("INSERT INTO market (code,display_code,display_idx,show,show_in_min) VALUES ('xrpusdt','XRP/USDT',3,0,0)");
        db.execSQL("INSERT INTO market (code,display_code,display_idx,show,show_in_min) VALUES ('dogeusdt','DOGE/USDT',4,0,0)");
        db.execSQL("INSERT INTO market (code,display_code,display_idx,show,show_in_min) VALUES ('shibusdt','SHIB/USDT',5,0,0)");
        db.execSQL("INSERT INTO market (code,display_code,display_idx,show,show_in_min) VALUES ('lunausdt','LUNA/USDT',6,0,0)");
        db.execSQL("INSERT INTO market (code,display_code,display_idx,show,show_in_min) VALUES ('uniusdt','UNI/USDT',7,0,0)");
        db.execSQL("INSERT INTO market (code,display_code,display_idx,show,show_in_min) VALUES ('linkusdt','LINK/USDT',8,0,0)");
        db.execSQL("INSERT INTO market (code,display_code,display_idx,show,show_in_min) VALUES ('axsusdt','AXS/USDT',9,0,0)");
        db.execSQL("INSERT INTO market (code,display_code,display_idx,show,show_in_min) VALUES ('xlmusdt','XLM/USDT',10,0,0)");
        db.execSQL("INSERT INTO market (code,display_code,display_idx,show,show_in_min) VALUES ('atomusdt','ATOM/USDT',11,0,0)");
        db.execSQL("INSERT INTO market (code,display_code,display_idx,show,show_in_min) VALUES ('trxusdt','TRX/USDT',12,0,0)");
        db.execSQL("INSERT INTO market (code,display_code,display_idx,show,show_in_min) VALUES ('thetausdt','THETA/USDT',13,0,0)");
        db.execSQL("INSERT INTO market (code,display_code,display_idx,show,show_in_min) VALUES ('fttusdt','FTT/USDT',14,1,0)");
        db.execSQL("INSERT INTO market (code,display_code,display_idx,show,show_in_min) VALUES ('filusdt','FIL/USDT',15,0,0)");
        db.execSQL("INSERT INTO market (code,display_code,display_idx,show,show_in_min) VALUES ('ftmusdt','FTM/USDT',16,0,0)");
        db.execSQL("INSERT INTO market (code,display_code,display_idx,show,show_in_min) VALUES ('manausdt','MANA/USDT',17,0,0)");
        db.execSQL("INSERT INTO market (code,display_code,display_idx,show,show_in_min) VALUES ('eosusdt','EOS/USDT',18,0,0)");
        db.execSQL("INSERT INTO market (code,display_code,display_idx,show,show_in_min) VALUES ('flowusdt','FLOW/USDT',19,0,0)");
        db.execSQL("INSERT INTO market (code,display_code,display_idx,show,show_in_min) VALUES ('miotausdt','MIOTA/USDT',20,0,0)");
        db.execSQL("INSERT INTO market (code,display_code,display_idx,show,show_in_min) VALUES ('sandusdt','SAND/USDT',21,1,0)");

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("db", "upgrade");
        db.execSQL("ALTER TABLE person ADD phone VARCHAR(12)");
    }
}
