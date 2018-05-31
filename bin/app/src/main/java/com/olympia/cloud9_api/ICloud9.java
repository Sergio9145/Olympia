package com.olympia.cloud9_api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ICloud9 {
    @FormUrlEncoded
    @POST("/register")
    Call<User> registerUser(
        @Field("firstName") String firstName,
        @Field("lastName") String lastName,
        @Field("email") String email,
        @Field("username") String username,
        @Field("password") String password
    );

    @FormUrlEncoded
    @POST("/login")
    Call<User> signUserIn(
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("/passwordreset")
    Call<User> resetPassword(
            @Field("email") String email,
            @Field("password") String password
    );
}