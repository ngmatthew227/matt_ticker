package com.matt.tickers;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.enums.SidePattern;
import com.lzf.easyfloat.interfaces.OnFloatCallbacks;
import com.lzf.easyfloat.interfaces.OnInvokeView;
import com.matt.tickers.databinding.ActivityMainBinding;
import com.matt.tickers.entity.MarketInfo;
import com.matt.tickers.entity.MarketTicker;
import com.matt.tickers.entity.SubTickerRequest;
import com.matt.tickers.service.ApiWebSocketService;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    private final String symbol1 = "btcusdt";
    private final String symbol2 = "btcusdt,fttusdt";
    Handler handler;
    ArrayList<MarketInfo> mData = new ArrayList<>();
    private CustomAdapter adapter;

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.open_btn)
    void openWindow() {
        MainActivity context = this;
        EasyFloat.with(this).setLayout(R.layout.market_float_app, new OnInvokeView() {
            @Override
            public void invoke(View view) {
                View hideBtn = view.findViewById(R.id.market_icon_iv);
                RecyclerView recycler_view = view.findViewById(R.id.market_float_rv);
                recycler_view.setLayoutManager(new LinearLayoutManager(context));
                recycler_view.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
                adapter = new CustomAdapter(mData);
                recycler_view.setAdapter(adapter);
                recycler_view.setVisibility(View.VISIBLE);


                handler = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        adapter.notifyItemChanged(0);
                        return true;
                    }
                });

                hideBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EasyFloat.show("min_app");
                        EasyFloat.hide("float_app");
                        EasyFloat.with(context)
                                .setLayout(R.layout.market_float_app_stow_left, new OnInvokeView() {
                                    @Override
                                    public void invoke(View view) {
                                        View marketLogo = view.findViewById(R.id.app_logo);
                                        marketLogo.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                EasyFloat.hide("min_app");
                                                EasyFloat.show("float_app");
                                            }
                                        });
                                    }
                                })
                                .setShowPattern(ShowPattern.ALL_TIME).setTag("min_app")
                                .setMatchParent(false, false)
                                .setImmersionStatusBar(false).show();
                    }
                });
            }
        }).setShowPattern(ShowPattern.ALL_TIME)
                .setTag("float_app").setMatchParent(false, false)
                .setImmersionStatusBar(false).registerCallbacks(new OnFloatCallbacks() {
            @Override
            public void createdResult(boolean isCreated, @Nullable String msg, @Nullable View view) {

            }

            @Override
            public void show(@NotNull View view) {

            }

            @Override
            public void hide(@NotNull View view) {

            }

            @Override
            public void dismiss() {

            }

            @Override
            public void touchEvent(@NotNull View view, @NotNull MotionEvent event) {

            }

            @Override
            public void drag(@NotNull View view, @NotNull MotionEvent event) {

            }

            @Override
            public void dragEnd(@NotNull View view) {

            }
        }).show();
        try {
            ApiWebSocketService.subTicker(new SubTickerRequest(symbol1),
                    (ticker) -> {
                        MarketTicker marketTicker = ticker.getMarketTicker();
                        MarketInfo updatedMarektInfo = new MarketInfo(marketTicker.getBid(), marketTicker.getOpen(), marketTicker.getHigh(), marketTicker.getLow(), marketTicker.getClose(), "BTC/USDT");
                        if (mData.size() == 0) {
                            mData.add(updatedMarektInfo);
                        } else {
                            mData.set(0, updatedMarektInfo);
                        }

                        handler.sendEmptyMessage(0);
                        Log.e("HIHIIHHI", ticker.getMarketTicker().toString());
                    });
        } catch (Exception e) {
            Log.e("ex", "here");
        }
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.show_btn)
    void showWindow() {
        EasyFloat.show("float_app");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.matt.tickers.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ButterKnife.bind(this);
    }
}