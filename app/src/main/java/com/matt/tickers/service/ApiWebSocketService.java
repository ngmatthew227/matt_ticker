package com.matt.tickers.service;

import com.alibaba.fastjson.JSONObject;
import com.matt.tickers.entity.Options;
import com.matt.tickers.entity.SubTickerRequest;
import com.matt.tickers.entity.TickerEvent;
import com.matt.tickers.utils.InputChecker;
import com.matt.tickers.utils.ResponseCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.WebSocketListener;


public class ApiWebSocketService extends WebSocketListener {

    public static final String WEBSOCKET_TICKER_TOPIC = "market.$symbol$.ticker";

    public static void subTicker(SubTickerRequest request, ResponseCallback<TickerEvent> callback) throws Exception {

        // Check Params
        InputChecker.checker().shouldNotNull(request.getSymbol(), "symbol");
        // Change into List
        List<String> symbolList = Arrays.asList(request.getSymbol().split("[,]"));

        // Check List
        InputChecker.checker().checkSymbolList(symbolList);

        List<String> commandList = new ArrayList<>(symbolList.size());
        for (String symbol : symbolList) {
            String topic = WEBSOCKET_TICKER_TOPIC
                    .replace("$symbol$", symbol);

            JSONObject command = new JSONObject();
            command.put("sub", topic);
            command.put("id", System.nanoTime());
            commandList.add(command.toJSONString());
        }

        CustomWebSocketConnection.createConnection(new Options(), commandList, callback);
    }


}
