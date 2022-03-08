package com.matt.tickers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.matt.tickers.entity.MarketInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private final List<MarketInfo> mData;
    private Context context;
    private AppCompatImageView dragBtn;

    public CustomAdapter(Context context,  List<MarketInfo> mData) {
        this.context = context;
        this.mData = mData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.market_float_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MarketInfo marketInfo = mData.get(position);

        holder.code.setText(marketInfo.getCode());
        holder.price.setText(String.valueOf(marketInfo.getPrice()));
        Integer direction = marketInfo.getUpDown();
        Integer directionColor = 0;

        try {
            if (direction == 1){
                directionColor = ContextCompat.getColor(context, R.color.Color_Green);
            } else if (direction == 0){
                directionColor = ContextCompat.getColor(context, R.color.Color_TextOnGray);
            } else if (direction == -1){
                directionColor =  ContextCompat.getColor(context, R.color.Color_Red);
            }
        } catch (NullPointerException e){
            e.printStackTrace();
        }


        holder.price.setTextColor(directionColor);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView code;
        private final TextView price;
//        private final TextView change;

        ViewHolder(View itemView) {
            super(itemView);
            code = (TextView) itemView.findViewById(R.id.market_float_asset);
            price = (TextView) itemView.findViewById(R.id.market_float_asset_price);
//            change = (TextView) itemView.findViewById(R.id.market_float_asset_change);
        }
    }


}
