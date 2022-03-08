package com.matt.tickers;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.interfaces.OnFloatCallbacks;
import com.lzf.easyfloat.interfaces.OnInvokeView;
import com.lzf.easyfloat.interfaces.OnTouchRangeListener;
import com.lzf.easyfloat.utils.DragUtils;
import com.lzf.easyfloat.widget.BaseSwitchView;
import com.matt.tickers.data.DBManager;
import com.matt.tickers.data.MarketData;
import com.matt.tickers.databinding.ActivityMainBinding;
import com.matt.tickers.entity.Crypto;
import com.matt.tickers.entity.MarketInfo;
import com.matt.tickers.entity.MarketTicker;
import com.matt.tickers.entity.SubTickerRequest;
import com.matt.tickers.service.ApiWebSocketService;
import com.matt.tickers.service.MarketService;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    final Handler hsiHandler = new Handler();
    final int delay = 5000; // 5000 milliseconds == 5 second
    String symbol;
    Handler handlerForMax;
    Handler handlerForMin;
    Handler handlerForRequest;
    ArrayList<MarketInfo> mData;
    CustomAdapter adapter;
    ProductListAdapter plAdapter;
    String tagForMaxWin = "max_app";
    String tagForMinWin = "min_app";
    DBManager dbManager;
    MarketData marketData;
    AutoCompleteTextView actv;
    @BindView(R.id.add_crypto_btn)
    Button addCryptoBtn;
    ScheduledExecutorService worker2;
    Crypto selectedCrypto;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.matt.tickers.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ButterKnife.bind(this);
        this.dbManager = DBManager.getInstance();
        dbManager.setSqliteDbOpen(this);
        ScheduledExecutorService cryptoRequest = Executors.newSingleThreadScheduledExecutor();
        actv = findViewById(R.id.auto_complete_txt_view);
        handlerForRequest = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                List<Crypto> cryptoList = (List<Crypto>) msg.obj;
                CryptoListAdapter cryptoAdp = new CryptoListAdapter(getApplicationContext(), cryptoList);
                actv.setAdapter(cryptoAdp);
                return true;
            }
        });
        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Crypto crypto = (Crypto) parent.getAdapter().getItem(position);
                String lbl1 = crypto.getCode().toUpperCase();
                String lbl2 = crypto.getSymbol().replaceAll(crypto.getCode(), crypto.getCode() + "/").toUpperCase();

                actv.setText(String.format("%s   [ %s ]", lbl1, lbl2));
                selectedCrypto = crypto;
                addCryptoBtn.setClickable(true);
            }
        });

        cryptoRequest.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Crypto> cryptoList = getListOfCrypto();
                    Message msg = new Message();
                    msg.obj = cryptoList;
                    handlerForRequest.sendMessage(msg);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        RecyclerView recycler_view = this.findViewById(R.id.product_list_rv);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        plAdapter = new ProductListAdapter(dbManager.getProductList(), dbManager);
        recycler_view.setAdapter(plAdapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {

            int dragFrom = -1;
            int dragTo = -1;

            @Override
            public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    assert viewHolder != null;
                    viewHolder.itemView.setAlpha(0.5f);
                }
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder from, @NonNull RecyclerView.ViewHolder target) {
                ProductListAdapter innerAdapter = (ProductListAdapter) recyclerView.getAdapter();
                int fromPosition = from.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                if (dragFrom == -1) {
                    dragFrom = fromPosition;
                }
                dragTo = toPosition;
                assert innerAdapter != null;
                innerAdapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Integer displayIdx = viewHolder.getAdapterPosition();
                dbManager.deleteByDisplayIdx(displayIdx + 1);
                plAdapter.notifyItemRemoved(displayIdx);
                plAdapter.refreshData();
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                if (dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
                    Log.e("Position", "Drag From:" + (dragFrom + 1));
                    Log.e("Position", "Drag To:" + (dragTo + 1));
                    dbManager.swapPosition(dragFrom + 1, dragTo + 1);
                    dragFrom = -1;
                    dragTo = -1;
                }
                viewHolder.itemView.setAlpha(1.0f);
            }
        });
        mItemTouchHelper.attachToRecyclerView(recycler_view);
    }

    private void initData() {
        this.symbol = marketData.getSymbol();
        this.mData = new ArrayList<>(marketData.getSymbolSize());
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.open_btn)
    void openWindow() {
        marketData = new MarketData(dbManager.getMarketDatas());
        initData();
        MainActivity context = this;
        EasyFloat.with(this).setLayout(R.layout.market_float_app, new OnInvokeView() {
            @Override
            public void invoke(View view) {
                ImageView hideBtn = view.findViewById(R.id.market_icon_iv);
                RecyclerView recycler_view = view.findViewById(R.id.market_float_rv);
                ImageView statusView = view.findViewById(R.id.connection_status);
                ImageView refreshBtn = view.findViewById(R.id.refresh_btn);
                recycler_view.setLayoutManager(new LinearLayoutManager(context));
                adapter = new CustomAdapter(context, mData);
                recycler_view.setAdapter(adapter);
                recycler_view.setVisibility(View.VISIBLE);
                Integer errorColor = ContextCompat.getColor(context, R.color.Red_500);
                Integer okColor = ContextCompat.getColor(context, R.color.Green_500);
                handlerForMax = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        if (msg.what != 100) {
                            statusView.setColorFilter(okColor, android.graphics.PorterDuff.Mode.SRC_IN);
                            adapter.notifyItemChanged(msg.what);
                        } else {
                            statusView.setColorFilter(errorColor, android.graphics.PorterDuff.Mode.SRC_IN);
                        }
                        return true;
                    }
                });
                hideBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int[] maxWinLocation = new int[2];
                        Objects.requireNonNull(EasyFloat.getFloatView(tagForMaxWin)).getLocationOnScreen(maxWinLocation);
                        int x = maxWinLocation[0];
                        int y = maxWinLocation[1] - 100;
                        View minWin = EasyFloat.getFloatView(tagForMinWin);
                        if (minWin == null) {
                            EasyFloat.with(context)
                                    .setLayout(R.layout.market_float_app_min, new OnInvokeView() {
                                        @Override
                                        public void invoke(View view) {
                                            View marketLogo = view.findViewById(R.id.app_logo);
                                            TextView min_price = view.findViewById(R.id.min_price);
                                            handlerForMin = new Handler(new Handler.Callback() {
                                                @SuppressLint("SetTextI18n")
                                                @Override
                                                public boolean handleMessage(Message msg) {
                                                    String code = marketData.getMinShowCode();
                                                    String msgCode = ((MarketInfo) msg.obj).getMinCode();
                                                    Integer direction = ((MarketInfo) msg.obj).getUpDown();
                                                    Integer directionColor = 0;
                                                    if (direction == 1) {
                                                        directionColor = ContextCompat.getColor(context, R.color.Color_Green);
                                                    } else if (direction == 0) {
                                                        directionColor = ContextCompat.getColor(context, R.color.Color_TextOnGray);
                                                    } else if (direction == -1) {
                                                        directionColor = ContextCompat.getColor(context, R.color.Color_Red);
                                                    }
                                                    if (code.equals(msgCode)) {
                                                        min_price.setTextColor(directionColor);
                                                        min_price.setText(((MarketInfo) msg.obj).getPrice().toString());
                                                    }
                                                    return true;
                                                }
                                            });
                                            marketLogo.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Objects.requireNonNull(EasyFloat.getFloatView(tagForMinWin)).getLocationOnScreen(maxWinLocation);
                                                    int x = maxWinLocation[0];
                                                    int y = maxWinLocation[1] - 100;

                                                    EasyFloat.hide(tagForMinWin);
                                                    EasyFloat.updateFloat(tagForMaxWin, x, y);
                                                    EasyFloat.show(tagForMaxWin);
                                                }
                                            });

                                        }
                                    })
                                    .setShowPattern(ShowPattern.ALL_TIME).setTag(tagForMinWin)
                                    .setMatchParent(false, false)
                                    .setImmersionStatusBar(false).setLocation(x, y).show();
                        } else {
                            EasyFloat.updateFloat(tagForMinWin, x, y);
                            EasyFloat.show(tagForMinWin);
                        }
                        EasyFloat.hide(tagForMaxWin);

                    }
                });

                refreshBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ApiWebSocketService.reconnect();
                    }
                });
            }
        }).setShowPattern(ShowPattern.ALL_TIME)
                .setTag(tagForMaxWin).setMatchParent(false, false)
                .setImmersionStatusBar(false)
                .registerCallbacks(new OnFloatCallbacks() {
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
                        DragUtils.INSTANCE.registerDragClose(event, new OnTouchRangeListener() {
                            @Override
                            public void touchInRange(boolean inRange, @NonNull BaseSwitchView baseSwitchView) {
                            }

                            @Override
                            public void touchUpInRange() {
                                EasyFloat.dismiss(tagForMaxWin, true);
                                EasyFloat.dismiss(tagForMinWin, true);
                                ApiWebSocketService.dismissConnection();
                                if (worker2 != null) {
                                    worker2.shutdown();
                                }
                            }

                        }, R.layout.default_close_layout, ShowPattern.ALL_TIME);
                    }

                    @Override
                    public void dragEnd(@NotNull View view) {

                    }
                }).setLocation(100, 300).show();

        ScheduledExecutorService worker1 = Executors.newSingleThreadScheduledExecutor();
        worker1.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.e("total symbol", symbol);
                    ApiWebSocketService.subTicker(new SubTickerRequest(symbol),
                            (ticker) -> {
                                if (ticker != null) {
                                    String product = ticker.getCh().replaceAll("market.", "").replaceAll(".ticker", "");
                                    Integer productIdx = marketData.getCodeIdx(product);
                                    String displayName = marketData.getDisplayName(product);
                                    MarketTicker marketTicker = ticker.getMarketTicker();
                                    int upDown = 0;
                                    int res = marketTicker.getBid().compareTo(marketTicker.getLastPrice());
                                    if (res > 0) {
                                        upDown = 1;
                                    } else {
                                        upDown = -1;
                                    }
                                    MarketInfo updatedMarektInfo = new MarketInfo(product,
                                            marketTicker.getBid(), marketTicker.getOpen(),
                                            marketTicker.getHigh(), marketTicker.getLow(),
                                            marketTicker.getClose(), displayName, upDown);
                                    if (mData.size() < marketData.getProductListSize()) {
                                        mData.add(updatedMarektInfo);
                                    } else {
                                        mData.set(productIdx, updatedMarektInfo);
                                    }
                                    if (EasyFloat.isShow(tagForMaxWin)) {
                                        handlerForMax.sendEmptyMessage(productIdx);
                                    } else if (EasyFloat.isShow(tagForMinWin)) {
                                        Message msg = new Message();
                                        msg.obj = updatedMarektInfo;
                                        handlerForMin.sendMessage(msg);
                                    }
                                } else {
                                    if (EasyFloat.isShow(tagForMaxWin)) {
                                        handlerForMax.sendEmptyMessage(100);
                                    } else if (EasyFloat.isShow(tagForMinWin)) {
                                        handlerForMin.sendEmptyMessage(100);
                                    }
                                }

                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if (dbManager.ifHsiExist()) {
            worker2 = Executors.newSingleThreadScheduledExecutor();
            worker2.scheduleAtFixedRate(
                    new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Map<String, MarketInfo> hsiMap = MarketService.getHsinHsif();
                                MarketInfo hsif = hsiMap.get("hsif");
                                hsif.setCode("HSIF");
                                hsif.setUpDown(0);
                                if (mData.size() < marketData.getProductListSize() + 1) {
                                    mData.add(hsif);
                                } else {
                                    mData.set(marketData.getProductListSize(), hsif);
                                }
                                if (EasyFloat.isShow(tagForMaxWin)) {
                                    handlerForMax.sendEmptyMessage(marketData.getProductListSize());
                                }
                                Log.w("hsiHandler", hsiMap.toString());
                            } catch (Exception e) {
                                Log.e("hsiHandler", "hihi");
                                e.printStackTrace();
                            }

                        }
                    },
                    0,  //initial delay
                    3, //run every 10 seconds
                    TimeUnit.SECONDS);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.add_crypto_btn)
    void addCrypto(View v) {
        if (selectedCrypto != null) {
            String displayCode = selectedCrypto.getSymbol().replaceAll(selectedCrypto.getCode(), selectedCrypto.getCode() + "/").toUpperCase();
            String code = selectedCrypto.getSymbol();
            dbManager.insertData(code, displayCode);
            actv.setText("");
            plAdapter.refreshData();
            v.setClickable(false);
        }
    }


    private List<Crypto> getListOfCrypto() throws IOException {
        OkHttpClient client = new OkHttpClient();
        List<Crypto> cryptoList = new ArrayList<>();
        Request request = new Request.Builder().url("https://api.huobi.pro/v1/common/symbols").build();
        try (Response response = client.newCall(request).execute()) {
            String responseString = response.body().string();
            Map<String, Object> json = JSONObject.parseObject(responseString);
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) json.get("data");

            for (Map<String, Object> data : dataList) {
                Crypto coin = new Crypto((String) data.get("base-currency"), (String) data.get("symbol"));
                cryptoList.add(coin);
            }

            return cryptoList;
        }

    }

}