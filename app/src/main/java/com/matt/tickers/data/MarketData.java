package com.matt.tickers.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketData {

    private final List<Map<String, Object>> marketDatas;
    private Map<String, Integer> productIdxMap;
    private Map<String, String> productDisplayMap;
    private String showCode;
    private String symbol;

    public MarketData(List<Map<String, Object>> marketDatas) {
        this.marketDatas = marketDatas;
        initData();
    }

    private void initData() {
        productIdxMap = new HashMap<>();
        productDisplayMap = new HashMap<>();
        showCode = "";
        StringBuilder symbolBuilder = new StringBuilder();

        for (int i = 0; i < marketDatas.size() ; i++) {
            Map<String, Object> product = marketDatas.get(i);
            String code = (String) product.get("code");
            String displayCode = (String) product.get("display_code");
            Integer displayIdx = i;
            Integer showCodeInt = (Integer) product.get("show_in_min");
            symbolBuilder.append(code);
            if (i != marketDatas.size() - 1) {
                symbolBuilder.append(",");
            }
            productIdxMap.put(code, displayIdx);
            productDisplayMap.put(code, displayCode);
            if (showCodeInt == 1) {
                showCode = code;
            }
        }
        this.symbol = symbolBuilder.toString();
    }

    public String getMinShowCode() {
        return showCode;
    }

    public Integer getProductListSize() {
        return marketDatas.size();
    }

    public Integer getCodeIdx(String code) {
        return productIdxMap.get(code);
    }

    public String getDisplayName(String code) {
        return productDisplayMap.get(code);
    }

    public String getSymbol() {
        return symbol;
    }

    public Integer getSymbolSize() {
        return marketDatas.size();
    }
}
