package com.example.administrator.newsfeed;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.newsfeed.Utility.Utils;
import com.example.administrator.newsfeed.Utility.WebNewsClient;

/**
 * Created by Administrator on 9/13/2017.
 */

public class WebViewActivity extends AppCompatActivity implements WebNewsClient.ProgressListener {
    TextView mCategory, mTitle;
    WebView mWebView;
    LinearLayout mNoNetwork;
    ImageView mRetry;
    private ProgressBar mProgressBar;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_web_view);
        mCategory = (TextView) findViewById(R.id.txt_category);
        mTitle = (TextView) findViewById(R.id.txt_title);
        mWebView = (WebView) findViewById(R.id.web_news);
        mRetry = (ImageView) findViewById(R.id.btn_try_again);
        mNoNetwork = (LinearLayout) findViewById(R.id.no_network_available);
        mCategory.setText(getIntent().getStringExtra(Utils.NEWS_CATEGORY_NAME));
        mTitle.setText(getIntent().getStringExtra(Utils.NEWS_TITLE));
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mWebView.setWebChromeClient(new WebNewsClient(this));

        getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.change_bound));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.checkIfHasNetwork(WebViewActivity.this)) {
                    mWebView.setVisibility(View.VISIBLE);
                    mNoNetwork.setVisibility(View.GONE);
                    loadWebView(getIntent().getStringExtra(Utils.NEWS_TITLE));
                } else {
                    mWebView.setVisibility(View.GONE);
                    mNoNetwork.setVisibility(View.VISIBLE);
                }
            }
        });
        if (Utils.checkIfHasNetwork(this)) {
            mWebView.setVisibility(View.VISIBLE);
            mNoNetwork.setVisibility(View.GONE);
            loadWebView(getIntent().getStringExtra(Utils.NEWS_URL));
        } else {
            mWebView.setVisibility(View.GONE);
            mNoNetwork.setVisibility(View.VISIBLE);
        }
    }

    private void loadWebView(String url) {
        mWebView.setWebViewClient((new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mProgressBar.setVisibility(View.GONE);
            }
        }));
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(url);
        mWebView.setHorizontalScrollBarEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onUpdateProgress(int progressValue) {
        mProgressBar.setProgress(progressValue);
        if (progressValue == 100) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }
}
