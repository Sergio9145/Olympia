package com.olympia;

import android.app.Application;

import com.olympia.oxfordapi.ApiClient;

public class SampleApp extends Application {
    private ApiClient apiClient;

    @Override
    public void onCreate() {
        super.onCreate();

        apiClient = new ApiClient();
    }

    public ApiClient apiClient() {
        return apiClient;
    }
}
