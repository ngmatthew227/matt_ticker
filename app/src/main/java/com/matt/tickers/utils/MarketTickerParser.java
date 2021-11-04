package com.matt.tickers.utils;

import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.matt.tickers.entity.MarketTicker;
import com.matt.tickers.entity.TickerEvent;

import java.util.ArrayList;
import java.util.List;

public class MarketTickerParser {

    public TickerEvent parse(JSONObject json) {
        TickerEvent te = json.toJavaObject(TickerEvent.class);
        JSONObject mtJson = (JSONObject) JSONObject.parse(json.getString("tick"));
        te.setMarketTicker(mtJson.toJavaObject(MarketTicker.class));
        return te;
    }

    public MarketTicker parse(JSONArray json) {
        return null;
    }

    public List<MarketTicker> parseArray(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.size() <= 0) {
            return new ArrayList<>();
        }
        return jsonArray.toJavaList(MarketTicker.class);
    }

}
