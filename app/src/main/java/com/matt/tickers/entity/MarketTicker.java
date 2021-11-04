package com.matt.tickers.entity;

import java.math.BigDecimal;

public class MarketTicker {

    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;
    private BigDecimal amount;
    private BigDecimal vol;
    private Long count;

    @Override
    public String toString() {
        return "MarketTicker{" +
                "open=" + open +
                ", high=" + high +
                ", low=" + low +
                ", close=" + close +
                ", amount=" + amount +
                ", vol=" + vol +
                ", count=" + count +
                ", bid=" + bid +
                ", bidSize=" + bidSize +
                ", ask=" + ask +
                ", askSize=" + askSize +
                ", lastPrice=" + lastPrice +
                ", lastSize=" + lastSize +
                '}';
    }

    private BigDecimal bid;
    private BigDecimal bidSize;
    private BigDecimal ask;
    private BigDecimal askSize;
    private BigDecimal lastPrice;
    private BigDecimal lastSize;

    public MarketTicker() {
    }

    public MarketTicker(BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close, BigDecimal amount, BigDecimal vol, Long count, BigDecimal bid, BigDecimal bidSize, BigDecimal ask, BigDecimal askSize, BigDecimal lastPrice, BigDecimal lastSize) {
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.amount = amount;
        this.vol = vol;
        this.count = count;
        this.bid = bid;
        this.bidSize = bidSize;
        this.ask = ask;
        this.askSize = askSize;
        this.lastPrice = lastPrice;
        this.lastSize = lastSize;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getClose() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getVol() {
        return vol;
    }

    public void setVol(BigDecimal vol) {
        this.vol = vol;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public BigDecimal getBid() {
        return bid;
    }

    public void setBid(BigDecimal bid) {
        this.bid = bid;
    }

    public BigDecimal getBidSize() {
        return bidSize;
    }

    public void setBidSize(BigDecimal bidSize) {
        this.bidSize = bidSize;
    }

    public BigDecimal getAsk() {
        return ask;
    }

    public void setAsk(BigDecimal ask) {
        this.ask = ask;
    }

    public BigDecimal getAskSize() {
        return askSize;
    }

    public void setAskSize(BigDecimal askSize) {
        this.askSize = askSize;
    }

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(BigDecimal lastPrice) {
        this.lastPrice = lastPrice;
    }

    public BigDecimal getLastSize() {
        return lastSize;
    }

    public void setLastSize(BigDecimal lastSize) {
        this.lastSize = lastSize;
    }

    public static final class MarketTickerBuilder {
        private BigDecimal open;
        private BigDecimal close;
        private BigDecimal low;
        private BigDecimal high;
        private BigDecimal amount;
        private Long count;
        private BigDecimal vol;
        private BigDecimal bid;
        private BigDecimal bidSize;
        private BigDecimal ask;
        private BigDecimal askSize;

        private MarketTickerBuilder() {
        }

        public MarketTickerBuilder(BigDecimal open, BigDecimal close, BigDecimal low, BigDecimal high, BigDecimal amount, Long count, BigDecimal vol, BigDecimal bid, BigDecimal bidSize, BigDecimal ask, BigDecimal askSize) {
            this.open = open;
            this.close = close;
            this.low = low;
            this.high = high;
            this.amount = amount;
            this.count = count;
            this.vol = vol;
            this.bid = bid;
            this.bidSize = bidSize;
            this.ask = ask;
            this.askSize = askSize;
        }

        public static MarketTickerBuilder aMarketTicker() {
            return new MarketTickerBuilder();
        }

        public MarketTickerBuilder withOpen(BigDecimal open) {
            this.open = open;
            return this;
        }

        public MarketTickerBuilder withClose(BigDecimal close) {
            this.close = close;
            return this;
        }

        public MarketTickerBuilder withLow(BigDecimal low) {
            this.low = low;
            return this;
        }

        public MarketTickerBuilder withHigh(BigDecimal high) {
            this.high = high;
            return this;
        }

        public MarketTickerBuilder withAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public MarketTickerBuilder withCount(Long count) {
            this.count = count;
            return this;
        }

        public MarketTickerBuilder withVol(BigDecimal vol) {
            this.vol = vol;
            return this;
        }

        public MarketTickerBuilder withBid(BigDecimal bid) {
            this.bid = bid;
            return this;
        }

        public MarketTickerBuilder withBidSize(BigDecimal bidSize) {
            this.bidSize = bidSize;
            return this;
        }

        public MarketTickerBuilder withAsk(BigDecimal ask) {
            this.ask = ask;
            return this;
        }

        public MarketTickerBuilder withAskSize(BigDecimal askSize) {
            this.askSize = askSize;
            return this;
        }

        public MarketTicker build() {
            MarketTicker marketTicker = new MarketTicker();
            marketTicker.setOpen(open);
            marketTicker.setClose(close);
            marketTicker.setLow(low);
            marketTicker.setHigh(high);
            marketTicker.setAmount(amount);
            marketTicker.setCount(count);
            marketTicker.setVol(vol);
            marketTicker.setBid(bid);
            marketTicker.setBidSize(bidSize);
            marketTicker.setAsk(ask);
            marketTicker.setAskSize(askSize);
            return marketTicker;
        }
    }
}
