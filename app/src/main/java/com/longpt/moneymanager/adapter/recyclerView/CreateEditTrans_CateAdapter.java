package com.longpt.moneymanager.adapter.recyclerView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.longpt.moneymanager.R;
import com.longpt.moneymanager.data.model.Category;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial;

import java.util.List;

public class CreateEditTrans_CateAdapter extends RecyclerView.Adapter<CreateEditTrans_CateAdapter.CategoryViewHolder> {

    private static final int TYPE_CATEGORY = 0;
    private static final int TYPE_REDIRECT_TEXT = 1;

    private final List<Category> categoryList;
    private final Context context;
    private CategoryItemListener categoryItemListener;

    private int selectedPosition =0;

    public CreateEditTrans_CateAdapter(List<Category> categoryList, Context context) {
        this.categoryList = categoryList;
        this.context = context;
    }

    public void setCategoryItemListener(CategoryItemListener categoryItemListener) {
        this.categoryItemListener = categoryItemListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == categoryList.size()) {
            return TYPE_REDIRECT_TEXT;
        }
        return TYPE_CATEGORY;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (viewType == TYPE_REDIRECT_TEXT) {
            v = LayoutInflater.from(context).inflate(R.layout.item_category_redirect, parent, false);
        } else {
            v = LayoutInflater.from(context).inflate(R.layout.item_category_create_trans, parent, false);
        }
        return new CategoryViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_CATEGORY) {
            Category c = categoryList.get(position);
            if (c != null) {
                boolean isSelected= (position==selectedPosition);
                holder.bind(c, isSelected);
            }
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size() + 1;
    }
    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public void resetSelected() {
        int previous = selectedPosition;
        selectedPosition = 0;
        notifyItemChanged(previous);
        notifyItemChanged(selectedPosition);
    }

    public interface CategoryItemListener {
        void onCategoryClick(View v, int position);

        void onEditCategoryTextClick(View v);
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvName;
        private ImageView ivIcon;

        public CategoryViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            if (viewType == TYPE_CATEGORY) {
                tvName = itemView.findViewById(R.id.tvCategoryName);
                ivIcon = itemView.findViewById(R.id.ivCategoryIcon);
            }
            itemView.setOnClickListener(this);
        }

        void bind(Category category, boolean isSelected) {
            tvName.setText(category.getName());

            try {
                IconicsDrawable iconDrawable = new IconicsDrawable(context, GoogleMaterial.Icon.valueOf(category.getIcon()));
                iconDrawable.setColorFilter(Color.parseColor(category.getColor()), PorterDuff.Mode.SRC_IN);
                iconDrawable.setBounds(0, 0, 48, 48);
                ivIcon.setImageDrawable(iconDrawable);
            } catch (Exception e) {
                ivIcon.setImageDrawable(null);
            }

            if (isSelected) {
                itemView.setBackgroundResource(R.drawable.bg_item_category_selected);
            } else {
                itemView.setBackgroundResource(R.drawable.bg_item_category);
            }
        }
        int getDrawableId(String name) {
            return itemView.getContext().getResources().getIdentifier(name, "drawable", itemView.getContext().getPackageName());
        }

        @Override
        public void onClick(View v) {
            if (categoryItemListener != null) {
                if (getItemViewType() == TYPE_REDIRECT_TEXT) {
                    int previousPosition = selectedPosition;
                    selectedPosition = 0;
                    notifyItemChanged(previousPosition);
                    notifyItemChanged(selectedPosition);
                    categoryItemListener.onEditCategoryTextClick(v);
                } else {
                    int previousPosition = selectedPosition;
                    selectedPosition = getAdapterPosition();
                    notifyItemChanged(previousPosition);
                    notifyItemChanged(selectedPosition);
                    categoryItemListener.onCategoryClick(v, getAdapterPosition());
                }
            }
        }
    }
}
