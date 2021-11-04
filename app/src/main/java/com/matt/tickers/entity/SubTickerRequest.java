package com.matt.tickers.entity;

public class SubTickerRequest {
    private String symbol;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public SubTickerRequest(String symbol) {
        this.symbol = symbol;
    }

    public SubTickerRequest() {
    }

}
