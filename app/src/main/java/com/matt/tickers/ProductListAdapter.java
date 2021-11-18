package com.matt.tickers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.matt.tickers.data.DBManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

    private Map<String, Integer> resourcesMap;
    private DBManager dbManager;
    private List<Map<String, Object>> mData;
    private Context context;


    public ProductListAdapter(List<Map<String, Object>> mData, DBManager dbManager) {
        this.mData = mData;
        this.dbManager = dbManager;
    }

    public void updateShowField(Integer idx) {
        Map<String, Object> data = mData.get(idx);
        String code = (String) data.get("code");
        Integer currentValue = (Integer) data.get("show");
        Integer newValue = currentValue == 1 ? 0 : 1;
        dbManager.updateShowField(code, newValue);
        refreshData();
        notifyItemChanged(idx);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        context = parent.getContext();
        initMap();
        return new ViewHolder(view);
    }

    public void initMap() {
        resourcesMap = new HashMap<>();
        resourcesMap.put("btcusdt", R.drawable.btcusdt);
        resourcesMap.put("ethusdt", R.drawable.ethusdt);
        resourcesMap.put("solusdt", R.drawable.solusdt);
        resourcesMap.put("xrpusdt", R.drawable.xrpusdt);
        resourcesMap.put("dogeusdt", R.drawable.dogeusdt);
        resourcesMap.put("shibusdt", R.drawable.shibusdt);
        resourcesMap.put("lunausdt", R.drawable.lunausdt);
        resourcesMap.put("uniusdt", R.drawable.uniusdt);
        resourcesMap.put("linkusdt", R.drawable.linkusdt);
        resourcesMap.put("axsusdt", R.drawable.axsusdt);
        resourcesMap.put("xlmusdt", R.drawable.xlmusdt);
        resourcesMap.put("atomusdt", R.drawable.atomusdt);
        resourcesMap.put("trxusdt", R.drawable.trxusdt);
        resourcesMap.put("thetausdt", R.drawable.thetausdt);
        resourcesMap.put("fttusdt", R.drawable.fttusdt);
        resourcesMap.put("filusdt", R.drawable.filusdt);
        resourcesMap.put("ftmusdt", R.drawable.ftmusdt);
        resourcesMap.put("manausdt", R.drawable.manausdt);
        resourcesMap.put("eosusdt", R.drawable.eosusdt);
        resourcesMap.put("flowusdt", R.drawable.flowusdt);
        resourcesMap.put("miotausdt", R.drawable.miotausdt);
        resourcesMap.put("sandusdt", R.drawable.sandusdt);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> product = mData.get(position);
        holder.coinCode.setText((String) product.get("display_code"));
        holder.coinIcon.setImageResource(resourcesMap.get(product.get("code")));
        Integer notCheckColor = ContextCompat.getColor(context, R.color.Gray_900);
        Integer checkColor = ContextCompat.getColor(context, R.color.Yellow_600);
        if ((Integer) product.get("show") == 0) {
            holder.showBtn.setColorFilter(notCheckColor, android.graphics.PorterDuff.Mode.SRC_IN);
        } else if ((Integer) product.get("show") == 1) {
            holder.showBtn.setColorFilter(checkColor, android.graphics.PorterDuff.Mode.SRC_IN);
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void refreshData() {
        this.mData = dbManager.getProductList();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView showBtn;
        private final ImageView coinIcon;
        private final TextView coinCode;

        ViewHolder(View itemView) {
            super(itemView);
            showBtn = itemView.findViewById(R.id.show_btn);
            coinIcon = itemView.findViewById(R.id.coin_icon);
            coinCode = itemView.findViewById(R.id.coin_code);

            showBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Log.e("position", String.valueOf(getAdapterPosition()));
                    updateShowField(getAdapterPosition());
                }
            });
        }
    }


}
