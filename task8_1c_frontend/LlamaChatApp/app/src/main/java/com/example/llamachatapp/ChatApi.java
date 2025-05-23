package com.example.llamachatapp;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ChatApi {
    @POST("/chat")
    Call<ResponseBody> sendMessage(@Body ChatRequest request);
}
