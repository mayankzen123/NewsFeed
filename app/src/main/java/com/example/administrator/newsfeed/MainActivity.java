package com.example.administrator.newsfeed;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.newsfeed.Adapter.CategoriesAdapter;
import com.example.administrator.newsfeed.Adapter.LoadMoreNewsListener;
import com.example.administrator.newsfeed.Adapter.NewsAdapter;
import com.example.administrator.newsfeed.Models.CategoriesModel;
import com.example.administrator.newsfeed.Models.NewsModel;
import com.example.administrator.newsfeed.RestClient.ApiClient;
import com.example.administrator.newsfeed.RestClient.ApiInterface;
import com.example.administrator.newsfeed.Utility.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements LoadMoreNewsListener, View.OnClickListener {

    //RecyclerView to Populate news
    RecyclerView mRecyclerView, mCategoryRecyclerView;
    //Initial load news item
    int loadItems = 30;
    //Load more news item
    int loadMoreItems = 20;
    //List contain news item according to pagination size
    List<NewsModel> tempNewsArticles = new ArrayList<>();
    //List contain news fetched from API
    static List<NewsModel> newsArticles = new ArrayList<>();
    ArrayList<NewsModel> filteredNews = new ArrayList<>();
    boolean sorting = true;

    ArrayList<CategoriesModel> categoriesModels;

    NewsAdapter newsAdapter;
    LinearLayout mNoNetwork;
    ProgressDialog mProgressDialog;
    ImageView mBtnTryAgain;
    TextView mCategory;
    CoordinatorLayout mCoordinatorLayout;
    private BottomSheetBehavior mNewsCategories;
    CategoriesAdapter categoriesAdapter;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSharedElementExitTransition(TransitionInflater.from(this).inflateTransition(R.transition.change_bound));
        mRecyclerView = (RecyclerView) findViewById(R.id.list_news);
        mCategoryRecyclerView = (RecyclerView) findViewById(R.id.list_categories);
        mNoNetwork = (LinearLayout) findViewById(R.id.no_network_available);
        mBtnTryAgain = (ImageView) findViewById(R.id.btn_try_again);
        mCategory = (TextView) findViewById(R.id.categoriesHeading);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinate_layout);
        mBtnTryAgain.setOnClickListener(this);

        mCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mCategoryRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        mNewsCategories = BottomSheetBehavior.from(findViewById(R.id.topCategoriesLayout));

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mProgressDialog = new ProgressDialog(this);
        //Hitting News API
        showProgressDialog();
        fetchNewsArticles();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        setUpCategories();
    }

    private void setUpCategories() {
        mCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNewsCategories.setState(mNewsCategories.getState() == BottomSheetBehavior.STATE_COLLAPSED ? BottomSheetBehavior.STATE_EXPANDED : BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        CoordinatorLayout.LayoutParams lp = new CoordinatorLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0, 0, 0, mCategory.getHeight());
        mRecyclerView.setLayoutParams(lp);

        mNewsCategories.setPeekHeight(mCategory.getHeight());
        mCategory.requestLayout();

        mNewsCategories.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        mNewsCategories.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        break;
                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {

            }
        });
    }

    private void showProgressDialog() {
        mProgressDialog.setMessage(getString(R.string.fetch_news_msg));
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        mProgressDialog.dismiss();
    }

    private void fetchNewsArticles() {
        ApiInterface apiService =
                ApiClient.getRetrofit(this).create(ApiInterface.class);
        Call<List<NewsModel>> call = apiService.getNews();
        call.enqueue(new Callback<List<NewsModel>>() {
            @Override
            public void onResponse(Call<List<NewsModel>> call, Response<List<NewsModel>> response) {
                //Success
                onSuccess(response.body());
                Utils.showLogs(response.message());
            }

            @Override
            public void onFailure(Call<List<NewsModel>> call, Throwable t) {
                //Failure
                onFail(t);
            }
        });
    }

    private void onSuccess(List<NewsModel> body) {
        hideProgressDialog();
        mRecyclerView.setVisibility(View.VISIBLE);
        mNoNetwork.setVisibility(View.GONE);
        mCoordinatorLayout.setBackgroundResource(R.color.white);
        newsArticles = body;
        setCategoriesList(body);
        setNewsArticles(loadNews(body, loadItems, "All", false));
    }

    private void setCategoriesList(List<NewsModel> body) {
        ArrayList<String> category = new ArrayList<>();
        categoriesModels = new ArrayList<>();
        for (int i = 0; i < body.size(); i++) {
            if (!category.contains(body.get(i).getCATEGORY())) {
                category.add(body.get(i).getCATEGORY());
            }
        }
        category.add("F");
        for (int i = 0; i < category.size(); i++) {
            String nameOfCategory;
            if (category.get(i).equalsIgnoreCase("T")) {
                nameOfCategory = "Science and Technology";
            } else if (category.get(i).equalsIgnoreCase("B")) {
                nameOfCategory = "Business ";
            } else if (category.get(i).equalsIgnoreCase("E")) {
                nameOfCategory = "Entertainment";
            } else if (category.get(i).equalsIgnoreCase("M")) {
                nameOfCategory = "Health";
            } else if (category.get(i).equalsIgnoreCase("F")) {
                nameOfCategory = "Bookmarks";
            } else {
                nameOfCategory = "Default";
            }
            CategoriesModel categoriesModel = new CategoriesModel();
            categoriesModel.setSelected(false);
            categoriesModel.setCategory(category.get(i));
            categoriesModel.setCategoryName(nameOfCategory);
            categoriesModels.add(categoriesModel);
        }
        categoriesAdapter = new CategoriesAdapter(this, categoriesModels);
        mCategoryRecyclerView.setAdapter(categoriesAdapter);
    }

    private void onFail(Throwable t) {
        Utils.showLogs(t.getMessage());
        hideProgressDialog();
        if (!Utils.checkIfHasNetwork(MainActivity.this)) {
            mRecyclerView.setVisibility(View.GONE);
            mNoNetwork.setVisibility(View.VISIBLE);
        }
    }

    private List<NewsModel> loadNews(List<NewsModel> body, int loadItems, String type, boolean isFromMoreNews) {
        int size = tempNewsArticles.size() + loadItems;
        if (!isFromMoreNews) {
            filteredNews.clear();
            for (int i = 0; i < body.size(); i++) {
                if (type.equalsIgnoreCase("All"))
                    filteredNews.add(body.get(i));
                else if (type.equalsIgnoreCase(body.get(i).getCATEGORY())) {
                    filteredNews.add(body.get(i));
                } else if (body.get(i).isBookMark() && type.equalsIgnoreCase("F")) {
                    filteredNews.add(body.get(i));
                }
            }
        }
        if (size > filteredNews.size())
            size = filteredNews.size();
        for (int i = tempNewsArticles.size(); i < size; i++) {
            if (filteredNews.get(i) == null) break;
            else {
                tempNewsArticles.add(filteredNews.get(i));
            }
        }
        return tempNewsArticles;
    }

    public void setNewsArticles(List<NewsModel> newsArticles) {
        newsAdapter = new NewsAdapter(mRecyclerView, this, newsArticles);
        mRecyclerView.setAdapter(newsAdapter);
    }

    @Override
    public void loadMoreNews() {
        tempNewsArticles.add(null);
        newsAdapter.notifyItemInserted(tempNewsArticles.size());
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tempNewsArticles.remove(tempNewsArticles.size() - 1);
                newsAdapter.notifyItemRemoved(tempNewsArticles.size());
                loadNews(newsArticles, loadMoreItems, null, true);
                newsAdapter.notifyItemInserted(tempNewsArticles.size());
                newsAdapter.setLoaded();
            }
        }, 2000);
    }

    @Override
    public void loadSelectedCategoryNews(String categoryName, boolean isSelected) {
        tempNewsArticles.clear();
        for (int i = 0; i < categoriesModels.size(); i++) {
            if (categoryName.equalsIgnoreCase(categoriesModels.get(i).getCategory())) {
                if (!isSelected) {
                    categoryName = "All";
                }
            } else {
                categoriesModels.get(i).setSelected(false);
            }
        }
        categoriesAdapter.notifyDataSetChanged();
        mNewsCategories.setState(BottomSheetBehavior.STATE_COLLAPSED);
        loadNews(newsArticles, loadItems, categoryName, false);
        newsAdapter.notifyDataSetChanged();
        mRecyclerView.smoothScrollToPosition(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_try_again:
                showProgressDialog();
                fetchNewsArticles();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_sort:
                if (tempNewsArticles.size() == 0)
                    return true;
                if (!sorting) {
                    //Ascending
                    ascendingSorting();
                    sorting = true;
                } else {
                    //Descending
                    descendingSorting();
                    sorting = false;
                }
                loadNews(tempNewsArticles, 0, null, true);
                newsAdapter.notifyDataSetChanged();
                mRecyclerView.smoothScrollToPosition(0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void descendingSorting() {
        Collections.sort(tempNewsArticles, new Comparator<NewsModel>() {
            @Override
            public int compare(NewsModel o1, NewsModel o2) {
                if (o1 != null && o2 != null) {
                    long time1 = o1.getTIMESTAMP();
                    long time2 = o2.getTIMESTAMP();
                    if (time1 < time2)
                        return 1;
                    else if (time2 < time1)
                        return -1;
                    else
                        return 0;
                }
                return 0;
            }
        });
    }

    private void ascendingSorting() {
        Collections.sort(tempNewsArticles, new Comparator<NewsModel>() {
            @Override
            public int compare(NewsModel o1, NewsModel o2) {
                if (o1 != null && o2 != null) {
                    long time1 = o1.getTIMESTAMP();
                    long time2 = o2.getTIMESTAMP();
                    if (time1 > time2)
                        return 1;
                    else if (time2 > time1)
                        return -1;
                    else
                        return 0;
                }
                return 0;
            }
        });
    }


}
