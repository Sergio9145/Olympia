package com.olympia.cloud9_api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ICloud9 {
    @FormUrlEncoded
    @POST("/register")
    Call<User> createUser(
        @Field("username") String username,
        @Field("password") String password,
        @Field("id") long id
    );
}