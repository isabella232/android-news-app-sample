package com.taboola.multiple_tabs_sdk_api.main.utils;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

public class NetworkUtil {

    /**
     * this is naive implementation, please use your network layer in your app to perform this call.
     * @param pixelUrl
     * @throws IOException
     */
    public static void fireClickUrlAsPixel(String pixelUrl) throws IOException {
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(pixelUrl);
        HttpResponse response = client.execute(get);
        Log.d("fireClickUrlAsPixel", "response = " + response.getStatusLine().toString());
    }
}
