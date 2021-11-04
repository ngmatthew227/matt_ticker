package com.matt.tickers.entity;

public class Options {
    private String restHost = "https://api.huobi.pro";

    private String websocketHost = "wss://api.huobi.pro";

    private String apiKey;

    private String secretKey;

    private boolean websocketAutoConnect = true;

    public String getApiKey() {
        return this.apiKey;
    }

    public String getSecretKey() {
        return this.secretKey;
    }

    public String getRestHost() {
        return this.restHost;
    }

    public String getWebSocketHost() {
        return this.websocketHost;
    }

    public boolean isWebSocketAutoConnect() {
        return this.websocketAutoConnect;
    }
}
