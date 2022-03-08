package com.matt.tickers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.matt.tickers.data.DBManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder>{

    private Map<String, Integer> resourcesMap;
    private final DBManager dbManager;
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
        return new ViewHolder(view);
    }

    private Integer getIconId(String code) {
        String cryptoCode = code.trim().replaceAll("usdt", "");
        Integer resourcesId = context.getResources().getIdentifier(cryptoCode, "drawable", "com.matt.tickers");
        return resourcesId;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> product = mData.get(position);
        holder.coinCode.setText((String) product.get("display_code"));
        holder.coinIcon.setImageResource(getIconId((String) product.get("code")));
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
        private final AppCompatImageView dragBtn;

        @SuppressLint("ClickableViewAccessibility")
        ViewHolder(View itemView) {
            super(itemView);
            showBtn = itemView.findViewById(R.id.show_btn);
            coinIcon = itemView.findViewById(R.id.coin_icon);
            coinCode = itemView.findViewById(R.id.coin_code);
            dragBtn = itemView.findViewById(R.id.imgDragHandler);

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
