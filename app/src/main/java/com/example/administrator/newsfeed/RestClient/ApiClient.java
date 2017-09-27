package com.example.administrator.newsfeed.RestClient;

import android.content.Context;

import com.example.administrator.newsfeed.Utility.Utils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 9/14/2017.
 */

public class ApiClient {
    public static final String BASE_URL = "http://starlord.hackerearth.com/";
    private static final String CACHE_CONTROL = "Cache-Control";
    private static final int STALE_PERIOD = 3;
    private static final int MAX_AGE = 2;

    public static Retrofit getRetrofit(Context context) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getOkHttpClient(context))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private static OkHttpClient getOkHttpClient(Context context) {
        return new OkHttpClient.Builder()
                .addInterceptor(getOfflineCacheInterceptor(context))
                .addNetworkInterceptor(getCacheInterceptor())
                .cache(getCache(context))
                .build();
    }

    private static Cache getCache(Context context) {
        Cache cache = null;
        try {
            cache = new Cache(new File(context.getCacheDir(), "news-cache"),
                    10 * 1024 * 1024);
        } catch (Exception e) {
        }
        return cache;
    }

    public static Interceptor getCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxAge(MAX_AGE, TimeUnit.MINUTES)
                        .build();

                return response.newBuilder()
                        .header(CACHE_CONTROL, cacheControl.toString())
                        .build();
            }
        };
    }

    public static Interceptor getOfflineCacheInterceptor(final Context context) {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (!Utils.checkIfHasNetwork(context)) {
                    CacheControl cacheControl = new CacheControl.Builder()
                            .maxStale(STALE_PERIOD, TimeUnit.DAYS)
                            .build();
                    request = request.newBuilder()
                            .cacheControl(cacheControl)
                            .build();
                }
                return chain.proceed(request);
            }
        };
    }
}
