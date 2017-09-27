package com.example.administrator.newsfeed.Models;

/**
 * Created by Administrator on 9/14/2017.
 */

public class CategoriesModel {
    boolean isSelected;
    String category, categoryName;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
