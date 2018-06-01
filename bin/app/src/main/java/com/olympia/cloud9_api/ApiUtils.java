package com.olympia.cloud9_api;

public class ApiUtils {

    private ApiUtils() {}

    public static final String BASE_URL = "https://olympia-opryshko.c9users.io/";

    public static ICloud9 getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(ICloud9.class);
    }
}