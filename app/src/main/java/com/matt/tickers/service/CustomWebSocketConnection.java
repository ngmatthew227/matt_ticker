package com.matt.tickers.service;

import android.annotation.SuppressLint;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.matt.tickers.entity.ConnectionStateEnum;
import com.matt.tickers.entity.MarketTicker;
import com.matt.tickers.entity.Options;
import com.matt.tickers.entity.TickerEvent;
import com.matt.tickers.utils.IdGenerator;
import com.matt.tickers.utils.InternalUtils;
import com.matt.tickers.utils.MarketTickerParser;
import com.matt.tickers.utils.ResponseCallback;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class CustomWebSocketConnection extends WebSocketListener {

    public static final String HUOBI_TRADING_WEBSOCKET_PATH = "/ws/v1";
    public static final String HUOBI_TRADING_WEBSOCKET_V2_PATH = "/ws/v2";
    public static final String HUOBI_MARKET_WEBSOCKET_PATH = "/ws";

    public static final String AUTH_VERSION_V1 = "v1";
    public static final String AUTH_VERSION_V2 = "v2";

    private long lastReceivedTime;

    private WebSocket webSocket;

    private Request okhttpRequest;

    private ConnectionStateEnum state;

    private Long id;

    private List<String> commandList;

    private MarketTickerParser parser;

    private ResponseCallback callback;

    private boolean autoClose;

    private boolean authNeed;

    private Options options;

    private long delayInSecond;

    private String host;

    private String authVersion = AUTH_VERSION_V1;

    private CustomWebSocketConnection() {
    }

    public static CustomWebSocketConnection createConnection(Options options,
                                                             List<String> commandList,
                                                             ResponseCallback callback) {
        MarketTickerParser parser = new MarketTickerParser();
        Boolean autoClose = false;
        boolean authNeed = false;
        String authVersion = AUTH_VERSION_V1;

        CustomWebSocketConnection connection = new CustomWebSocketConnection();
        connection.setOptions(options);
        connection.setCommandList(commandList);
        connection.setParser(parser);
        connection.setCallback(callback);
        connection.setAuthNeed(authNeed);
        connection.setAutoClose(autoClose);
        connection.setId(IdGenerator.getNextId());
        connection.setAuthVersion(authVersion);

        // Create Websocket Connect
        String url = options.getWebSocketHost() + HUOBI_MARKET_WEBSOCKET_PATH;
        Request request = new Request.Builder().url(url).build();
        connection.setOkhttpRequest(request);

        try {
            connection.setHost(new URL(options.getRestHost()).getHost());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // Open Connection
        connection.connect();
        return connection;
    }


    void connect() {
        if (state == ConnectionStateEnum.CONNECTED) {
            Log.i("websocket", "[Connection][" + this.getId() + "] Already connected");
            return;
        }
        Log.i("websocket", "[Connection][" + this.getId() + "] Connecting...");
        webSocket = ConnectionFactory.createWebSocket(okhttpRequest, this);
    }

    public void reConnect(int delayInSecond) {
        Log.w("websocket", "[Sub][" + this.getId() + "] Reconnecting after "
                + delayInSecond + " seconds later");
        if (webSocket != null) {
            webSocket.cancel();
            webSocket = null;
        }
        this.delayInSecond = delayInSecond;
        state = ConnectionStateEnum.DELAY_CONNECT;
    }

    public void reConnect() {
        if (delayInSecond != 0) {
            delayInSecond--;
        } else {
            connect();
        }
    }


    public long getLastReceivedTime() {
        return this.lastReceivedTime;
    }

    public void setLastReceivedTime(long lastReceivedTime) {
        this.lastReceivedTime = lastReceivedTime;
    }

    void send(List<String> commandList) {
        if (commandList == null || commandList.size() <= 0) {
            return;
        }
        for (String command : commandList) {
            send(command);
        }
    }

    public void send(String str) {
        boolean result = false;
        Log.i("websocket", "[Connection Send]{}" + str);
        if (webSocket != null) {
            result = webSocket.send(str);
        }
        if (!result) {
            Log.e("websocket", "[Connection Send][" + this.getId() + "] Failed to send message");
            closeOnError();
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
        lastReceivedTime = System.currentTimeMillis();

        Log.d("[On Message Text]:{}", text);
        try {
            JSONObject json = JSON.parseObject(text);

            if (json.containsKey("action")) {
                String action = json.getString("action");
                if ("ping".equals(action)) {
                    processPingOnV2TradingLine(json, webSocket);
                } else if ("push".equals(action)) {
                    onReceive(json);
                }
                if ("req".equals(action)) {
                    String ch = json.getString("ch");
                    if ("auth".equals(ch)) {
                        send(commandList);
                    }

                }

            }

        } catch (Exception e) {
            Log.e("websocket", "[On Message][{}]: catch exception:");
            closeOnError();
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        super.onMessage(webSocket, bytes);
        try {

            lastReceivedTime = System.currentTimeMillis();

            String data;
            try {
                data = new String(InternalUtils.decode(bytes.toByteArray()));
            } catch (IOException e) {
                Log.e("websokcet", "[Connection On Message][" + this.getId() + "] Receive message error: ");
                closeOnError();
                return;
            }
            Log.d("websocket", "[Connection On Message][{}] {}" + data);
            JSONObject jsonObject = JSON.parseObject(data);

            if (jsonObject.containsKey("status") && !"ok".equals(jsonObject.getString("status"))) {
                String errorCode = jsonObject.getString("err-code");
                String errorMsg = jsonObject.getString("err-msg");
                onError(errorCode + ": " + errorMsg, null);
                Log.e("websocket", "[Connection On Message][" + this.getId() + "] Got error from server: " + errorCode + "; " + errorMsg);
                close();
            } else if (jsonObject.containsKey("op")) {
                String op = jsonObject.getString("op");
                switch (op) {
                    case "notify":
                        onReceive(jsonObject);
                        break;
                    case "ping":
                        processPingOnTradingLine(jsonObject, webSocket);
                        break;
                    case "auth":
                        send(commandList);
                        break;
                    case "req":
                        onReceiveAndClose(jsonObject);
                        break;
                }
            } else if (jsonObject.containsKey("ch") || jsonObject.containsKey("rep")) {
                onReceiveAndClose(jsonObject);
            } else if (jsonObject.containsKey("ping")) {
                processPingOnMarketLine(jsonObject, webSocket);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("websocket", "[Connection On Message][" + this.getId() + "] Unexpected error: " + e.getMessage());
            closeOnError();
        }
    }

    private void onError(String errorMessage, Throwable e) {
        Log.e("websocket", "[Connection error][" + this.getId() + "] " + errorMessage);
        closeOnError();
    }

    private void onReceiveAndClose(JSONObject jsonObject) {
        onReceive(jsonObject);
        if (autoClose) {
            close();
        }
    }

    @SuppressWarnings("unchecked")
    private void onReceive(JSONObject jsonObject) {
        Object obj = null;

        try {
            TickerEvent ticker = (TickerEvent) parser.parse(jsonObject);
            callback.onResponse(ticker);
        } catch (Exception e) {
            onError("Process error: " + e.getMessage() + " You should capture the exception in your error handler", e);
        }


    }

    @SuppressLint("DefaultLocale")
    private void processPingOnTradingLine(JSONObject jsonObject, WebSocket webSocket) {
        long ts = jsonObject.getLong("ts");
        webSocket.send(String.format("{\"op\":\"pong\",\"ts\":%d}", ts));
    }

    @SuppressLint("DefaultLocale")
    private void processPingOnMarketLine(JSONObject jsonObject, WebSocket webSocket) {
        long ts = jsonObject.getLong("ping");
        webSocket.send(String.format("{\"pong\":%d}", ts));
    }

    @SuppressLint("DefaultLocale")
    private void processPingOnV2TradingLine(JSONObject jsonWrapper, WebSocket webSocket) {
        long ts = jsonWrapper.getJSONObject("data").getLong("ts");
        String pong = String.format("{\"action\": \"pong\",\"params\": {\"ts\": %d}}", ts);
        webSocket.send(pong);
    }

    public ConnectionStateEnum getState() {
        return state;
    }

    public void setState(ConnectionStateEnum state) {
        this.state = state;
    }


    public void close() {
        Log.e("websocket", "[Connection close][" + this.getId() + "] Closing normally");
        webSocket.cancel();
        webSocket = null;
//        WebSocketWatchDog.onClosedNormally(this);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        super.onClosed(webSocket, code, reason);
        if (state == ConnectionStateEnum.CONNECTED) {
            state = ConnectionStateEnum.IDLE;
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        this.webSocket = webSocket;
        Log.i("websocket", "[Connection][" + this.getId() + "] Connected to server");
        if (options.isWebSocketAutoConnect()) {
//            WebSocketWatchDog.onConnectionCreated(this);
        }

        state = ConnectionStateEnum.CONNECTED;
        lastReceivedTime = System.currentTimeMillis();

        for (String command : commandList) {
            send(command);
        }
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        onError("Unexpected error: " + t.getMessage(), t);
        closeOnError();
    }

    private void closeOnError() {
        if (webSocket != null) {
            this.webSocket.cancel();
            state = ConnectionStateEnum.CLOSED_ON_ERROR;
            callback.onResponse(null);
            Log.e("websocket", "[Connection error][" + this.getId() + "] Connection is closing due to error");
        }
    }

    public WebSocket getWebSocket() {
        return webSocket;
    }

    public void setWebSocket(WebSocket webSocket) {
        this.webSocket = webSocket;
    }

    public Request getOkhttpRequest() {
        return okhttpRequest;
    }

    public void setOkhttpRequest(Request okhttpRequest) {
        this.okhttpRequest = okhttpRequest;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getCommandList() {
        return commandList;
    }

    public void setCommandList(List<String> commandList) {
        this.commandList = commandList;
    }

    public MarketTickerParser getParser() {
        return parser;
    }

    public void setParser(MarketTickerParser parser) {
        this.parser = parser;
    }

    public ResponseCallback getCallback() {
        return callback;
    }

    public void setCallback(ResponseCallback callback) {
        this.callback = callback;
    }

    public boolean isAutoClose() {
        return autoClose;
    }

    public void setAutoClose(boolean autoClose) {
        this.autoClose = autoClose;
    }

    public boolean isAuthNeed() {
        return authNeed;
    }

    public void setAuthNeed(boolean authNeed) {
        this.authNeed = authNeed;
    }

    public Options getOptions() {
        return options;
    }

    public void setOptions(Options options) {
        this.options = options;
    }

    public long getDelayInSecond() {
        return delayInSecond;
    }

    public void setDelayInSecond(long delayInSecond) {
        this.delayInSecond = delayInSecond;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getAuthVersion() {
        return authVersion;
    }

    public void setAuthVersion(String authVersion) {
        this.authVersion = authVersion;
    }
}

