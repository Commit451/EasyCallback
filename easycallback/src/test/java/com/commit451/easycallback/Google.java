package com.commit451.easycallback;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

interface Google {
    @GET("about")
    Call<ResponseBody> getAbout();

    @GET("home")
    Call<Void> getVoid();

    @GET("404")
    Call<ResponseBody> get404Error();
}
