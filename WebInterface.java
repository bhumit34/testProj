package com.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


@SuppressWarnings("deprecation")
public class WebInterface {

    public static boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (cm != null) {
                NetworkInfo[] info = cm.getAllNetworkInfo();
                if (info != null)
                    for (int i = 0; i < info.length; i++)
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.e("isonline() ", e + "");
            return false;
        }
        return false;
        // return Connectivity.isConnectedFast(context);
    }

    public static boolean wifiStatus(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = null;
            if (cm != null) {
                networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            }

            return networkInfo == null ? false : networkInfo.isConnected();
        } catch (Exception e) {
            return false;
        }
    }


    private static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public static String readStream(InputStream is) throws IOException {
        int ch = 0;
        String str = new String();

        while ((ch = is.read()) != -1) {
            str += (char) ch;
        }

        is.close();

        return str;
    }

    public static InputStream download(String paraURL) throws IOException {
        URL url = null;
        HttpURLConnection conn = null;

        url = new URL(paraURL);
        conn = (HttpURLConnection) url.openConnection();

        if ((conn).getResponseCode() == HttpURLConnection.HTTP_OK)
            return conn.getInputStream();
        else
            return null;
    }
}
