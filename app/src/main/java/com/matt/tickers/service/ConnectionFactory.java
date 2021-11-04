package com.matt.tickers.service;


import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ConnectionFactory {

    private static Boolean LATENCY_DEBUG_SWATCH = Boolean.FALSE;

    private static final LinkedBlockingQueue<NetworkLatency> LATENCY_DEBUG_QUEUE = new LinkedBlockingQueue<>();

    private static final ConnectionPool connectionPool =
            new ConnectionPool(20, 300, TimeUnit.SECONDS);

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .followSslRedirects(false)
            .followRedirects(false)
            .connectTimeout(5000, TimeUnit.MILLISECONDS)
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .writeTimeout(5000, TimeUnit.MILLISECONDS)
            .connectionPool(connectionPool)
            .addNetworkInterceptor(new Interceptor() {
                @NotNull
                @Override
                public Response intercept(@NotNull Chain chain) throws IOException {
                    Request request = chain.request();

                    Long startNano = System.nanoTime();

                    Response response = chain.proceed(request);

                    Long endNano = System.nanoTime();

                    if (LATENCY_DEBUG_SWATCH) {
                        LATENCY_DEBUG_QUEUE.add(new NetworkLatency(request.url().url().getPath(), startNano, endNano));
                    }

                    return response;
                }
            })
            .build();

    public static String execute(Request request) throws Exception {

        Response response = null;
        String str = null;
        try {
            Log.d("[Request URL]{}", String.valueOf(request.url()));
            response = client.newCall(request).execute();
            if (response.code() != 200) {
                throw new Exception("[Execute] Response Status Error : " + response.code() + " message:" + response.message());
            }
            if (response != null && response.body() != null) {
                str = response.body().string();
                response.close();
                return str;
            } else {
                throw new Exception("[Execute] Cannot get the response from server");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("[Execute] Cannot get the response from server");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static WebSocket createWebSocket(Request request, WebSocketListener listener) {
        return client.newWebSocket(request, listener);
    }

    public static void setLatencyDebug() {
        LATENCY_DEBUG_SWATCH = Boolean.TRUE;
    }

    public static LinkedBlockingQueue<NetworkLatency> getLatencyDebugQueue() {
        return LATENCY_DEBUG_QUEUE;
    }

    public static void clearLatencyDebugQueue() {
        LATENCY_DEBUG_QUEUE.clear();
    }

    public static class NetworkLatency {

        private String path;

        private Long startNanoTime;

        private Long endNanoTime;

        public NetworkLatency(String path, Long startNanoTime, Long endNanoTime) {
            this.path = path;
            this.startNanoTime = startNanoTime;
            this.endNanoTime = endNanoTime;
        }

        public NetworkLatency() {
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public Long getStartNanoTime() {
            return startNanoTime;
        }

        public void setStartNanoTime(Long startNanoTime) {
            this.startNanoTime = startNanoTime;
        }

        public Long getEndNanoTime() {
            return endNanoTime;
        }

        public void setEndNanoTime(Long endNanoTime) {
            this.endNanoTime = endNanoTime;
        }

    }
}
