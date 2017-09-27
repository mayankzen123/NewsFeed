package com.example.administrator.newsfeed.Adapter;

public interface LoadMoreNewsListener {
    void loadMoreNews();

    void loadSelectedCategoryNews(String categoryName, boolean isSelected);
}