package com.example.administrator.newsfeed.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.newsfeed.Models.CategoriesModel;
import com.example.administrator.newsfeed.R;

import java.util.List;

/**
 * Created by Administrator on 9/13/2017.
 */

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder> {

    List<CategoriesModel> categoriesModels;
    Context mContext;
    LayoutInflater layoutInflater;
    LoadMoreNewsListener loadMoreNewsListener;

    public CategoriesAdapter(Context context, List<CategoriesModel> categoriesModels) {
        mContext = context;
        this.categoriesModels = categoriesModels;
        layoutInflater = LayoutInflater.from(context);
        loadMoreNewsListener = (LoadMoreNewsListener) context;
    }

    @Override
    public CategoriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_category, parent, false);
        return new CategoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CategoriesViewHolder holder, final int position) {
        final CategoriesModel categoriesModel = categoriesModels.get(position);
        holder.mTxtCategory.setText(categoriesModel.getCategory().toUpperCase());
        holder.mTxtTitle.setText(categoriesModel.getCategoryName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoriesModel.setSelected(categoriesModel.isSelected() ? false : true);
                holder.itemView.setBackgroundResource(categoriesModel.isSelected() ? R.color.pressed : R.color.darkPurple);
                if (loadMoreNewsListener != null)
                    loadMoreNewsListener.loadSelectedCategoryNews(categoriesModel.getCategory(),categoriesModel.isSelected());
            }
        });
        if (categoriesModel.isSelected())
            holder.itemView.setBackgroundResource(R.color.pressed);
        else
            holder.itemView.setBackgroundResource(R.color.darkPurple);
    }

    @Override
    public int getItemCount() {
        return categoriesModels.size();
    }


    public class CategoriesViewHolder extends RecyclerView.ViewHolder {
        private TextView mTxtCategory;
        private TextView mTxtTitle;

        public CategoriesViewHolder(View convertView) {
            super(convertView);
            mTxtCategory = (TextView) convertView.findViewById(R.id.txt_category);
            mTxtTitle = (TextView) convertView.findViewById(R.id.txt_category_name);
        }
    }
}

