package com.longpt.moneymanager.adapter.recyclerView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.longpt.moneymanager.R;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial;

import java.util.List;

public class IconAddAdapter extends RecyclerView.Adapter<IconAddAdapter.IconViewHolder> {

    private final List<GoogleMaterial.Icon> icons;
    private final Context context;
    private int selectedPosition = 0;
    private int selectedColor = Color.BLACK;

    public IconAddAdapter(List<GoogleMaterial.Icon> icons, Context context) {
        this.icons = icons;
        this.context = context;
    }

    public void setColorForSelected(int color) {
        selectedColor = color;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IconViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_icon_add, parent, false);
        return new IconViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IconViewHolder holder, int position) {
        int color = position == selectedPosition ? selectedColor : Color.BLACK;
        holder.bind(icons.get(position), color, position == selectedPosition);

        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return icons.size();
    }

    public GoogleMaterial.Icon getSelectedIconEnum() {
        return icons.get(selectedPosition);
    }

    public int getSelectedPosition(){
        return selectedPosition;
    }
    public void setSelectedIcon(String iconName){
        for (int i=0; i<icons.size(); i++){
            if(icons.get(i).name().equals(iconName)){
                selectedPosition=i;
                notifyDataSetChanged();
                break;
            }
        }
    }

    public int getSelectedColor() {
        return selectedColor;
    }

    static class IconViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;

        public IconViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.ivIconAdd);
        }

        void bind(GoogleMaterial.Icon iconType, int color, boolean isSelected) {
            IconicsDrawable icon = new IconicsDrawable(itemView.getContext(), iconType);
            icon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            icon.setBounds(0, 0, 48, 48);
            imgIcon.setImageDrawable(icon);

            if (isSelected) {
                itemView.setBackgroundResource(R.drawable.bg_item_category_selected);
            } else {
                itemView.setBackgroundResource(R.drawable.bg_item_category);
            }
        }
    }
}