package com.example.administrator.newsfeed.RestClient;

import com.example.administrator.newsfeed.Models.NewsModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {
    @GET("newsjson")
    Call<List<NewsModel>> getNews();
}