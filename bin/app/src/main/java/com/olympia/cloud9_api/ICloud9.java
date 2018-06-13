package com.olympia.cloud9_api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ICloud9 {
    @FormUrlEncoded
    @POST("/register")
    Call<C9Token> registerUser(
        @Field("firstName") String firstName,
        @Field("lastName") String lastName,
        @Field("email") String email,
        @Field("username") String username,
        @Field("password") String password
    );

    @FormUrlEncoded
    @POST("/login")
    Call<C9Token> signUserIn(
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("/passwordreset")
    Call<C9User> resetPassword(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("/changename")
    Call<C9FirstLastNames> changeName(
            @Field("firstName") String firstName,
            @Field("lastName") String lastName,
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("/changeemail")
    Call<C9Email> changeEmail(
            @Field("email") String email,
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("/changepassword")
    Call<C9NewPassword> changePassword(
            @Field("username") String username,
            @Field("password") String password,
            @Field("newpassword") String newpassword
    );

    @FormUrlEncoded
    @POST("/deleteaccount")
    Call<C9User> deleteAccount(
            @Field("username") String username,
            @Field("password") String password
    );
}