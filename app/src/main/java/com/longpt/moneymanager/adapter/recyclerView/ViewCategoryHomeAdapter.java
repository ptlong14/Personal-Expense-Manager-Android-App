package com.longpt.moneymanager.adapter.recyclerView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.longpt.moneymanager.R;
import com.longpt.moneymanager.data.model.Category;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial;

import java.util.List;

public class ViewCategoryHomeAdapter extends RecyclerView.Adapter<ViewCategoryHomeAdapter.CategoryViewHolder> {
    private final List<Category> categoryList;
    private final Context context;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private CategoryItemListener categoryItemListener;

    public ViewCategoryHomeAdapter(List<Category> categoryList, Context context) {
        this.categoryList = categoryList;
        this.context = context;
        viewBinderHelper.setOpenOnlyOne(true);
    }

    public void setCategoryItemListener(CategoryItemListener categoryItemListener) {
        this.categoryItemListener = categoryItemListener;
    }

    public void closeOpenSwipe() {
        for (Category i : categoryList) {
            viewBinderHelper.closeLayout(i.getId());
        }
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_category_show, parent, false);
        return new CategoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewCategoryHomeAdapter.CategoryViewHolder holder, int position) {
        Category c = categoryList.get(position);
        SwipeRevealLayout swipeLayout = holder.itemView.findViewById(R.id.swipeLayoutShowCategory);
        viewBinderHelper.bind(swipeLayout, c.getId());

        holder.bind(c);

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }


    public interface CategoryItemListener {
        void onCategoryClick(View v, int position);

        void onDeleteCategoryClick(Category c, int position);
    }


    public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView tvName;
        private final ImageView ivIcon;
        private final LinearLayout layoutContent;
        private final LinearLayout layoutDelete;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvEditCateName);
            ivIcon = itemView.findViewById(R.id.ivEditCateIcon);
            layoutContent = itemView.findViewById(R.id.layoutContentCate);
            layoutDelete = itemView.findViewById(R.id.layoutDeleteCate);

            layoutContent.setOnClickListener(this);
            layoutDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && categoryItemListener != null) {
                    categoryItemListener.onDeleteCategoryClick(categoryList.get(position), position);
                }
            });
        }

        void bind(Category category) {
            tvName.setText(category.getName());
            try {
                IconicsDrawable iconDrawable = new IconicsDrawable(context, GoogleMaterial.Icon.valueOf(category.getIcon()));
                iconDrawable.setColorFilter(Color.parseColor(category.getColor()), PorterDuff.Mode.SRC_IN);
                iconDrawable.setBounds(0, 0, 48, 48);
                ivIcon.setImageDrawable(iconDrawable);
            } catch (Exception e) {
                ivIcon.setImageDrawable(null);
            }
        }

        @Override
        public void onClick(View v) {
            if (categoryItemListener != null) {
                categoryItemListener.onCategoryClick(v, getAdapterPosition());
            }
        }
    }
}