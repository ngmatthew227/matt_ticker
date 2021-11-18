package com.matt.tickers.entity;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarketInfo {

    private String code;
    private BigDecimal price;
    private String changePrice;
    private String changePer;
    private BigDecimal high;
    private BigDecimal open;
    private BigDecimal low;
    private BigDecimal close;
    private Integer dayDirection;
    private String minCode;
//    Comment: 1=up, 0 = equal, -1 = down
    private Integer upDown;

    public MarketInfo(String minCode, BigDecimal price, BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close, String code, Integer upDown) {
        this.minCode = minCode;
        this.price = price;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.code = code;
        this.upDown = upDown;
    }

    public MarketInfo(Element block, String code) {
        Pattern closePattern = Pattern.compile("C︰([0-9]*,[0-9]*.[0-9]*)");
        Pattern openPattern = Pattern.compile("O︰([0-9]*,[0-9]*.[0-9]*)");
        Pattern highPattern = Pattern.compile("H / L︰([0-9]*,[0-9]*.[0-9]*)");
        Pattern lowPattern = Pattern.compile("H / L︰([0-9]*,[0-9]*.[0-9]*) / ([0-9]*,[0-9]*.[0-9]*)");
        Pattern changePattern = Pattern.compile("(.[0-9]*).*[(](.*%)[)]");
        Matcher m;

        this.code = code;
        Element mainPriceBlock = block.select(".FuturesQuoteBlock").get(1);
        Element otherInfoBlock = block.select(".FuturesQuoteBlock").get(2);
        this.price = BigDecimal.valueOf(Double.parseDouble(mainPriceBlock.selectFirst("span").text().replaceAll(",", "")));
        String changeText = mainPriceBlock.selectFirst(".FuturesQuoteChanged").text();
        m = changePattern.matcher(changeText);
        if (m.find()) {
            this.changePrice = m.group(1);
            this.changePer = m.group(2);
        }
        this.dayDirection = StringUtils.containsAny(changePrice, "+") == true ? 1 : 0;
        String otherInfoText = otherInfoBlock.select(".FuturesQuoteOthers").text();
        m = closePattern.matcher(otherInfoText);
        if (m.find()) {
            this.close = BigDecimal.valueOf(Double.parseDouble(m.group(1).replaceAll(",", "").replaceAll("　", "")));
        }
        m = openPattern.matcher(otherInfoText);
        if (m.find()) {
            this.open = BigDecimal.valueOf(Double.parseDouble(m.group(1).replaceAll(",", "").replaceAll("　", "")));
        }
        m = highPattern.matcher(otherInfoText);
        if (m.find()) {
            this.high = BigDecimal.valueOf(Double.parseDouble(m.group(1).replaceAll(",", "").replaceAll("　", "")));
        }
        m = lowPattern.matcher(otherInfoText);
        if (m.find()) {
            this.low = BigDecimal.valueOf(Double.parseDouble(m.group(2).replaceAll(",", "").replaceAll("　", "")));
        }

    }

    @Override
    public String toString() {
        return "MarketInfo [ code=" + code + ", price=" + price + ", changePrice=" + changePrice + ", dayDirection="
                + dayDirection + ", high=" + high + ", low=" + low + ", open=" + open + ", close=" + close + "]";
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getUpDown() {
        return upDown;
    }

    public void setUpDown(Integer upDown) {
        this.upDown = upDown;
    }

    public String getMinCode() {
        return minCode;
    }

    public void setMinCode(String minCode) {
        this.minCode = minCode;
    }


    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getChangePrice() {
        return changePrice;
    }

    public void setChangePrice(String changePrice) {
        this.changePrice = changePrice;
    }

    public String getChangePer() {
        return changePer;
    }

    public void setChangePer(String changePer) {
        this.changePer = changePer;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
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

    public Integer getDayDirection() {
        return dayDirection;
    }

    public void setDayDirection(Integer dayDirection) {
        this.dayDirection = dayDirection;
    }

}
