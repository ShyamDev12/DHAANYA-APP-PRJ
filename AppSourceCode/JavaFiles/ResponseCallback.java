package com.example.grower1;

public interface ResponseCallback {
    void onResponse(String response);
    void onError(Throwable throwable);
}