package com.longpt.moneymanager.adapter.recyclerView;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.longpt.moneymanager.R;

import java.util.List;

public class ColorAddAdapter extends RecyclerView.Adapter<ColorAddAdapter.ColorViewHolder> {
    private final List<Integer> colors;
    private final OnColorClickListener listener;

    public ColorAddAdapter(List<Integer> colors, OnColorClickListener listener) {
        this.colors = colors;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_color_add, parent, false);
        return new ColorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
        if (position == colors.size() - 1) {
            holder.vColor.setBackgroundColor(Color.TRANSPARENT);
            holder.vBorder.setVisibility(View.VISIBLE);
        } else {
            holder.vColor.setBackgroundColor(colors.get(position));
            holder.vBorder.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            int color;
            if (position == colors.size() - 1) {
                color = 0;
            } else {
                color = colors.get(position);
            }
            listener.onColorClick(color, position == colors.size() - 1);
        });
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }

    public interface OnColorClickListener {
        void onColorClick(int color, boolean isCustomColor);
    }

    static class ColorViewHolder extends RecyclerView.ViewHolder {
        View vColor;
        View vBorder;

        public ColorViewHolder(@NonNull View itemView) {
            super(itemView);
            vColor = itemView.findViewById(R.id.vColor);
            vBorder = itemView.findViewById(R.id.vMoreColor);
        }
    }
}