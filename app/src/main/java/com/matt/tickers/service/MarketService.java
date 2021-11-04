package com.matt.tickers.service;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.matt.tickers.entity.MarketInfo;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MarketService {

  private final LocalTime dayOpenTime = LocalTime.of(8, 0, 0);
  private final LocalTime dayEndTime = LocalTime.of(16, 0, 0);

  public Boolean isDay() {
    LocalTime currentTime = LocalTime.now();
    return currentTime.isAfter(dayOpenTime) && currentTime.isBefore(dayEndTime);
  }

  public Map<String, MarketInfo> getHsinHsif() {
    Map<String, MarketInfo> marketInfoMap = new HashMap<>();
    try {
      Document document = Jsoup.connect("https://www.etnet.com.hk/www/eng/futures/index.php").get();
      Elements outerBlock = document.select(".FuturesQuoteLeft").select(".FuturesQuoteContent");
      Element hsifBlock = isDay() ? outerBlock.get(0) : outerBlock.get(1);
      Element hsiBlock = outerBlock.get(2);

      MarketInfo hsi =  new MarketInfo(hsifBlock, "hsi");
      MarketInfo hsif = new MarketInfo(hsiBlock, "hsif");

      System.out.println(hsi);
      System.out.println(hsif);

      marketInfoMap.put("hsi", hsi);
      marketInfoMap.put("hsif", hsif);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return marketInfoMap;
  }

//  @Scheduled(fixedRate = 10000)
  public void sendMessage() {
    Map<String, MarketInfo> marketInfoMap = getHsinHsif();
    String time = new SimpleDateFormat("HH:mm:ss").format(new Date());

  }

}
