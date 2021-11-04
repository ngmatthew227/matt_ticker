package com.matt.tickers.utils;

@FunctionalInterface
public interface ResponseCallback<T> {

    /**
     * Be called when the request successful.
     *
     */
    void onResponse(T response);
}
