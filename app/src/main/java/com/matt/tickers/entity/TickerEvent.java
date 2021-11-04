package com.matt.tickers.entity;

public class TickerEvent {

    private String ch;

    private Long ts;

    private MarketTicker marketTicker;

    public String getCh() {
        return ch;
    }

    public void setCh(String ch) {
        this.ch = ch;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public MarketTicker getMarketTicker() {
        return marketTicker;
    }

    public void setMarketTicker(MarketTicker marketTicker) {
        this.marketTicker = marketTicker;
    }

    public TickerEvent(String ch, Long ts, MarketTicker marketTicker) {
        this.ch = ch;
        this.ts = ts;
        this.marketTicker = marketTicker;
    }

    public TickerEvent() {
    }

    @Override
    public String toString() {
        return "TickerEvent{" +
                "ch='" + ch + '\'' +
                ", ts=" + ts +
                ", marketTicker=" + marketTicker +
                '}';
    }
}