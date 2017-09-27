package com.example.administrator.newsfeed.Utility;

import android.content.Context;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class WebNewsClient extends WebChromeClient {
    private ProgressListener mListener;

    public WebNewsClient(Context listener) {
        mListener = (ProgressListener) listener;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        mListener.onUpdateProgress(newProgress);
        super.onProgressChanged(view, newProgress);
    }

    public interface ProgressListener {
        public void onUpdateProgress(int progressValue);
    }
}