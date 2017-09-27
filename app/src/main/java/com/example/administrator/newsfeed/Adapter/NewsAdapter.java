package com.example.administrator.newsfeed.Adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.newsfeed.Models.NewsModel;
import com.example.administrator.newsfeed.R;
import com.example.administrator.newsfeed.Utility.Utils;
import com.example.administrator.newsfeed.WebViewActivity;

import java.util.List;

/**
 * Created by Administrator on 9/13/2017.
 */

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<NewsModel> newsModels;
    Context mContext;
    LayoutInflater layoutInflater;
    private int lastPosition = -1;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    LoadMoreNewsListener loadMoreNewsListener;

    public NewsAdapter(RecyclerView recyclerView, Context context, List<NewsModel> newsModels) {
        mContext = context;
        this.newsModels = newsModels;
        layoutInflater = LayoutInflater.from(context);
        loadMoreScrollView(recyclerView);
        loadMoreNewsListener = (LoadMoreNewsListener) context;
    }

    private void loadMoreScrollView(RecyclerView recyclerView) {
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (loadMoreNewsListener != null) {
                        loadMoreNewsListener.loadMoreNews();
                    }
                    isLoading = true;
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return newsModels.get(position) == null ? VIEW_PROG : VIEW_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            View view = layoutInflater.inflate(R.layout.item_news_article, parent, false);
            return new NewsViewHolder(view);
        } else if (viewType == VIEW_PROG) {
            View view = layoutInflater.inflate(R.layout.item_news_loading, parent, false);
            return new LoadMoreNewsViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof NewsViewHolder) {
            final NewsModel newsModel = newsModels.get(position);
            final NewsViewHolder newsViewHolder = (NewsViewHolder) holder;
            newsViewHolder.mTxtCategory.setText(newsModel.getCATEGORY().toUpperCase());
            newsViewHolder.mTxtTitle.setText(newsModel.getTITLE());
            newsViewHolder.mTxtPublisher.setText(newsModel.getPUBLISHER());
            newsViewHolder.mTxtTime.setText(Utils.getTime(mContext, newsModel.getTIMESTAMP()));
            newsViewHolder.mImgBookmark.setImageResource(R.drawable.ic_bookmark);
            newsViewHolder.mImgLike.setImageResource(R.drawable.ic_thumb);

            if (newsModel.isBookMark())
                DrawableCompat.setTint(newsViewHolder.mImgBookmark.getDrawable(), ContextCompat.getColor(mContext, R.color.pressed));
            else
                DrawableCompat.setTint(newsViewHolder.mImgBookmark.getDrawable(), ContextCompat.getColor(mContext, R.color.purple));

            if (newsModel.isLike())
                DrawableCompat.setTint(newsViewHolder.mImgLike.getDrawable(), ContextCompat.getColor(mContext, R.color.pressed));
            else
                DrawableCompat.setTint(newsViewHolder.mImgLike.getDrawable(), ContextCompat.getColor(mContext, R.color.purple));

            newsViewHolder.mImgBookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!newsModels.get(position).isBookMark()) {
                        newsModels.get(position).setBookMark(true);
                        DrawableCompat.setTint(newsViewHolder.mImgBookmark.getDrawable(), ContextCompat.getColor(mContext, R.color.pressed));
                    } else {
                        newsModels.get(position).setBookMark(false);
                        DrawableCompat.setTint(newsViewHolder.mImgBookmark.getDrawable(), ContextCompat.getColor(mContext, R.color.purple));
                    }
                }
            });
            newsViewHolder.mImgLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!newsModels.get(position).isLike()) {
                        newsModels.get(position).setLike(true);
                        DrawableCompat.setTint(newsViewHolder.mImgLike.getDrawable(), ContextCompat.getColor(mContext, R.color.pressed));
                    } else {
                        newsModels.get(position).setLike(false);
                        DrawableCompat.setTint(newsViewHolder.mImgLike.getDrawable(), ContextCompat.getColor(mContext, R.color.purple));
                    }
                }
            });
            newsViewHolder.mImgShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = newsModel.getURL();
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, newsModel.getTITLE());
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    mContext.startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }
            });
            newsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {
                    newsViewHolder.mTxtCategory.setTransitionName("category");
//                    newsViewHolder.mTxtTitle.setTransitionName("title");
                    Pair<View, String> pairCategory = Pair.create((View) newsViewHolder.mTxtCategory, newsViewHolder.mTxtCategory.getTransitionName());
//                    Pair<View, String> pairTitle = Pair.create((View) newsViewHolder.mTxtTitle, newsViewHolder.mTxtTitle.getTransitionName());
                    ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation((Activity) mContext, pairCategory);
                    Intent intent = new Intent(mContext, WebViewActivity.class);
                    intent.putExtra(Utils.NEWS_CATEGORY_NAME, newsModel.getCATEGORY().toUpperCase());
                    intent.putExtra(Utils.NEWS_TITLE, newsModel.getTITLE());
                    intent.putExtra(Utils.NEWS_URL, newsModel.getURL());
                    mContext.startActivity(intent, transitionActivityOptions.toBundle());
                }
            });

            setAnimation(holder.itemView, position);
        } else if (holder instanceof LoadMoreNewsViewHolder) {
            LoadMoreNewsViewHolder loadMoreNewsViewHolder = (LoadMoreNewsViewHolder) holder;
            loadMoreNewsViewHolder.mProgressBar.setIndeterminate(true);
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder instanceof NewsViewHolder) {
            NewsViewHolder newsViewHolder = (NewsViewHolder) holder;
            newsViewHolder.itemView.clearAnimation();
        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_right);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return newsModels == null ? 0 : newsModels.size();
    }

    public void setLoaded() {
        isLoading = false;
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder {
        private TextView mTxtCategory;
        private TextView mTxtTitle;
        private TextView mTxtPublisher;
        private TextView mTxtTime;
        private ImageView mImgLike;
        private ImageView mImgBookmark;
        private ImageView mImgShare;

        public NewsViewHolder(View convertView) {
            super(convertView);
            mTxtCategory = (TextView) convertView.findViewById(R.id.txt_category);
            mTxtTitle = (TextView) convertView.findViewById(R.id.txt_title);
            mTxtPublisher = (TextView) convertView.findViewById(R.id.txt_publisher);
            mTxtTime = (TextView) convertView.findViewById(R.id.txt_time);
            mImgLike = (ImageView) convertView.findViewById(R.id.img_like);
            mImgBookmark = (ImageView) convertView.findViewById(R.id.img_bookmark);
            mImgShare = (ImageView) convertView.findViewById(R.id.img_share);
        }
    }

    public class LoadMoreNewsViewHolder extends RecyclerView.ViewHolder {
        ProgressBar mProgressBar;

        public LoadMoreNewsViewHolder(View itemView) {
            super(itemView);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }
    }
}

