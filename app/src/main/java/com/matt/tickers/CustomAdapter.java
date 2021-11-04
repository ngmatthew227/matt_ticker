package com.matt.tickers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.matt.tickers.entity.MarketInfo;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private final List<MarketInfo> mData;

    public CustomAdapter(List<MarketInfo> mData) {
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
        holder.change.setText(marketInfo.getChangePrice());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView code;
        private final TextView price;
        private final TextView change;

        ViewHolder(View itemView) {
            super(itemView);
            code = (TextView) itemView.findViewById(R.id.market_float_asset);
            price = (TextView) itemView.findViewById(R.id.market_float_asset_price);
            change = (TextView) itemView.findViewById(R.id.market_float_asset_change);
        }
    }


}
