package com.schindler.schindler.tag;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.cache.DiskBasedCache;
import com.android.volley.cache.NoCache;
import com.android.volley.cache.plus.ImageLoader;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

/**
 * Created by Bhumit on 21/03/2016.
 */
public class MySingleton {
    private static MySingleton mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;

    private MySingleton(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

        /*mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageContainer(){
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    public Bitmap getBitmap(@NonNull String url) {
                        return cache.get(url);
                    }

                    public void putBitmap(@NonNull String url, @NonNull Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });*/
    }

    public static synchronized MySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MySingleton(context);

        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
            // Instantiate the cache
            Cache cache = new DiskBasedCache(mCtx.getCacheDir(), 2 * 1024 * 1024); // 2MB cap
            // Set up the network to use HttpURLConnection as the HTTP client.
            Network network = new BasicNetwork(new HurlStack());
            // Instantiate the RequestQueue with the cache and network.
            mRequestQueue = new RequestQueue(new NoCache(), network);
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

}
