package com.example.administrator.newsfeed.Utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.example.administrator.newsfeed.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 9/10/2017.
 */

public class Utils {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final String TAG = "NewsFeed";
    private static final int MIN_DAYS = 10;
    private static final String DATE_FORMAT = "dd MMMM yyyy";
    public static final String NEWS_CATEGORY_NAME = "category";
    public static final String NEWS_TITLE = "title";
    public static final String NEWS_URL = "url";


    public static String getTime(Context context, long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return context.getString(R.string.duration_now);
        } else if (diff < 2 * MINUTE_MILLIS) {
            return context.getString(R.string.duration_minute);
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + context.getString(R.string.duration_minutes);
        } else if (diff < 90 * MINUTE_MILLIS) {
            return context.getString(R.string.duration_hour);
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + context.getString(R.string.duration_hours);
        } else if (diff < 48 * HOUR_MILLIS) {
            return context.getString(R.string.duration_yesterday);
        } else {
            long days = diff / DAY_MILLIS;
            if (days > MIN_DAYS) {
                Date currentDate = new Date(time);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
                return simpleDateFormat.format(currentDate);
            } else {
                return days + context.getString(R.string.duration_days);
            }
        }
    }

    public static boolean checkIfHasNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static void showLogs(String message) {
        Log.d(TAG, message);
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
