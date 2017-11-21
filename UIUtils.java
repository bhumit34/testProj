/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.schindler.schindler.tag;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;


import com.schindler.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;



/**
 * An assortment of UI helpers.
 */
public class UIUtils {
    private static final String TAG ="Schindler";

    /**
     * Factor applied to session color to derive the background color on panels and when
     * a session photo could not be downloaded (or while it is being downloaded)
     */
    public static final float SESSION_BG_COLOR_SCALE_FACTOR = 0.75f;

    private static final float SESSION_PHOTO_SCRIM_ALPHA = 0.25f; // 0=invisible, 1=visible image
    private static final float SESSION_PHOTO_SCRIM_SATURATION = 0.2f; // 0=gray, 1=color image

    /**
     * Flags used with {@link DateUtils#formatDateRange}.
     */
    private static final int TIME_FLAGS = DateUtils.FORMAT_SHOW_TIME
            | DateUtils.FORMAT_SHOW_DATE;

    /**
     * Regex to search for HTML escape sequences.
     * <p>
     * <p></p>Searches for any continuous string of characters starting with an ampersand and ending with a
     * semicolon. (Example: &amp;amp;)
     */
    private static final Pattern REGEX_HTML_ESCAPE = Pattern.compile(".*&\\S;.*");
    public static final String MOCK_DATA_PREFERENCES = "mock_data";
    public static final String PREFS_MOCK_CURRENT_TIME = "mock_current_time";

    public static final String GOOGLE_PLUS_PACKAGE_NAME = "com.google.android.apps.plus";
    public static final String YOUTUBE_PACKAGE_NAME = "com.google.android.youtube";
    public static final String TWITTER_PACKAGE_NAME = "com.twitter.app";

    public static final String GOOGLE_PLUS_COMMON_NAME = "Google Plus";
    public static final String TWITTER_COMMON_NAME = "Twitter";

    /**
     * Format and return the given session time and {@link Rooms} values using
     * {@link AppConstants#CONFERENCE_TIMEZONE}.
     */


    /**
     * This allows the app to specify a {@code packageName} to handle the {@code intent}, if the
     * {@code packageName} is available on the device and can handle it. An example use is to open
     * a Google + stream directly using the Google + app.
     */
    public static void preferPackageForIntent(Context context, Intent intent, String packageName) {
        PackageManager pm = context.getPackageManager();
        if (pm != null) {
            for (ResolveInfo resolveInfo : pm.queryIntentActivities(intent, 0)) {
                if (resolveInfo.activityInfo.packageName.equals(packageName)) {
                    intent.setPackage(packageName);
                    break;
                }
            }
        }
    }

    private static final int BRIGHTNESS_THRESHOLD = 130;

    /**
     * Calculate whether a color is light or dark, based on a commonly known
     * brightness formula.
     *
     * @see {@literal http://en.wikipedia.org/wiki/HSV_color_space%23Lightness}
     */
    public static boolean isColorDark(int color) {
        return ((30 * Color.red(color) +
                59 * Color.green(color) +
                11 * Color.blue(color)) / 100) <= BRIGHTNESS_THRESHOLD;
    }

    public static boolean isTablet(Context context) {
        return context.getResources().getConfiguration().smallestScreenWidthDp >= 600;
    }

    // Shows whether a notification was fired for a particular session time block. In the
    // event that notification has not been fired yet, return false and set the bit.
    public static boolean isNotificationFiredForBlock(Context context, String blockId) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        final String key = String.format("notification_fired_%s", blockId);
        boolean fired = sp.getBoolean(key, false);
        sp.edit().putBoolean(key, true).apply();
        return fired;
    }

    private static final long sAppLoadTime = System.currentTimeMillis();

    /**
     * Retrieve the current time. If the current build is a debug build the mock time is returned
     * when set.
     */


    private static final int[] RES_IDS_ACTION_BAR_SIZE = {R.attr.actionBarSize};

    /**
     * Calculates the Action Bar height in pixels.
     */
    public static int calculateActionBarSize(Context context) {
        if (context == null) {
            return 0;
        }

        Resources.Theme curTheme = context.getTheme();
        if (curTheme == null) {
            return 0;
        }

        TypedArray att = curTheme.obtainStyledAttributes(RES_IDS_ACTION_BAR_SIZE);
        if (att == null) {
            return 0;
        }

        float size = att.getDimension(0, 0);
        att.recycle();
        return (int) size;
    }

    public static int setColorOpaque(int color) {
        return Color.argb(255, Color.red(color), Color.green(color), Color.blue(color));
    }

    public static int scaleColor(int color, float factor, boolean scaleAlpha) {
        return Color.argb(scaleAlpha ? (Math.round(Color.alpha(color) * factor)) : Color.alpha(color),
                Math.round(Color.red(color) * factor), Math.round(Color.green(color) * factor),
                Math.round(Color.blue(color) * factor));
    }

    public static int scaleSessionColorToDefaultBG(int color) {
        return scaleColor(color, SESSION_BG_COLOR_SCALE_FACTOR, false);
    }


    public static void fireSocialIntent(Context context, Uri uri, String packageName) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        UIUtils.preferPackageForIntent(context, intent, packageName);
        context.startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean isRtl(final Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return false;
        } else {
            return context.getResources().getConfiguration().getLayoutDirection()
                    == View.LAYOUT_DIRECTION_RTL;
        }
    }

    public static void setAccessibilityIgnore(View view) {
        view.setClickable(false);
        view.setFocusable(false);
        view.setContentDescription("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        }
    }

    /*public static void setUpButterBar(View butterBar, String messageText, String actionText,
                                      View.OnClickListener listener) {
        if (butterBar == null) {
            LOGE(TAG, "Failed to set up butter bar: it's null.");
            return;
        }

        TextView textView = (TextView) butterBar.findViewById(R.id.butter_bar_text);
        if (textView != null) {
            textView.setText(messageText);
        }

        Button button = (Button) butterBar.findViewById(R.id.butter_bar_button);
        if (button != null) {
            button.setText(actionText == null ? "" : actionText);
            button.setVisibility(!TextUtils.isEmpty(actionText) ? View.VISIBLE : View.GONE);
        }

        button.setOnClickListener(listener);
        butterBar.setVisibility(View.VISIBLE);
    }*/

    public static float getProgress(int value, int min, int max) {
        if (min == max) {
            throw new IllegalArgumentException("Max (" + max + ") cannot equal min (" + min + ")");
        }

        return (value - min) / (float) (max - min);
    }

    public static void setLanguage(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());

        Log.v(TAG, "==--------- Language set to English");
    }

    // Desaturates and color-scrims the image
    public static ColorFilter makeSessionImageScrimColorFilter(int sessionColor) {
        float a = SESSION_PHOTO_SCRIM_ALPHA;
//        return new ColorMatrixColorFilter(new float[]{
//                a, 0, 0, 0, 0,
//                0, a, 0, 0, 0,
//                0, 0, a, 0, 0,
//                0, 0, 0, 0, 255
//        });
//        return new ColorMatrixColorFilter(new float[]{
//                a, 0, 0, 0, Color.red(sessionColor) * (1 - a),
//                0, a, 0, 0, Color.green(sessionColor) * (1 - a),
//                0, 0, a, 0, Color.blue(sessionColor) * (1 - a),
//                0, 0, 0, 0, 255
//        });
//        return new ColorMatrixColorFilter(new float[]{
//                0.213f * a, 0.715f * a, 0.072f * a, 0, Color.red(sessionColor) * (1 - a),
//                0.213f * a, 0.715f * a, 0.072f * a, 0, Color.green(sessionColor) * (1 - a),
//                0.213f * a, 0.715f * a, 0.072f * a, 0, Color.blue(sessionColor) * (1 - a),
//                0, 0, 0, 0, 255
//        });
//        ColorMatrix cm = new ColorMatrix();
//        cm.setSaturation(0f);
//        cm.postConcat(alphaMatrix(0.5f, Color.WHITE));
//        cm.postConcat(multiplyBlendMatrix(sessionColor, 0.9f));
//        return new ColorMatrixColorFilter(cm);
        float sat = SESSION_PHOTO_SCRIM_SATURATION; // saturation (0=gray, 1=color)
        return new ColorMatrixColorFilter(new float[]{
                ((1 - 0.213f) * sat + 0.213f) * a, ((0 - 0.715f) * sat + 0.715f) * a, ((0 - 0.072f) * sat + 0.072f) * a, 0, Color.red(sessionColor) * (1 - a),
                ((0 - 0.213f) * sat + 0.213f) * a, ((1 - 0.715f) * sat + 0.715f) * a, ((0 - 0.072f) * sat + 0.072f) * a, 0, Color.green(sessionColor) * (1 - a),
                ((0 - 0.213f) * sat + 0.213f) * a, ((0 - 0.715f) * sat + 0.715f) * a, ((1 - 0.072f) * sat + 0.072f) * a, 0, Color.blue(sessionColor) * (1 - a),
                0, 0, 0, 0, 255
        });
//        a = 0.2f;
//        return new ColorMatrixColorFilter(new float[]{
//                0.213f * a, 0.715f * a, 0.072f * a, 0, Color.red(sessionColor) - 255 * a / 2,
//                0.213f * a, 0.715f * a, 0.072f * a, 0, Color.green(sessionColor) - 255 * a / 2,
//                0.213f * a, 0.715f * a, 0.072f * a, 0, Color.blue(sessionColor) - 255 * a / 2,
//                0, 0, 0, 0, 255
//        });
    }

//    private static final float[] mAlphaMatrixValues = {
//            0, 0, 0, 0, 0,
//            0, 0, 0, 0, 0,
//            0, 0, 0, 0, 0,
//            0, 0, 0, 1, 0
//    };
//    private static final ColorMatrix mMultiplyBlendMatrix = new ColorMatrix();
//    private static final float[] mMultiplyBlendMatrixValues = {
//            0, 0, 0, 0, 0,
//            0, 0, 0, 0, 0,
//            0, 0, 0, 0, 0,
//            0, 0, 0, 1, 0
//    };
//    private static final ColorMatrix mWhitenessColorMatrix = new ColorMatrix();
//
//    /**
//     * Simulates alpha blending an image with {@param color}.
//     */
//    private static ColorMatrix alphaMatrix(float alpha, int color) {
//        mAlphaMatrixValues[0] = 255 * alpha / 255;
//        mAlphaMatrixValues[6] = Color.green(color) * alpha / 255;
//        mAlphaMatrixValues[12] = Color.blue(color) * alpha / 255;
//        mAlphaMatrixValues[4] = 255 * (1 - alpha);
//        mAlphaMatrixValues[9] = 255 * (1 - alpha);
//        mAlphaMatrixValues[14] = 255 * (1 - alpha);
//        mWhitenessColorMatrix.set(mAlphaMatrixValues);
//        return mWhitenessColorMatrix;
//    }
//    /**
//     * Simulates multiply blending an image with a single {@param color}.
//     *
//     * Multiply blending is [Sa * Da, Sc * Dc]. See {@link android.graphics.PorterDuff}.
//     */
//    private static ColorMatrix multiplyBlendMatrix(int color, float alpha) {
//        mMultiplyBlendMatrixValues[0] = multiplyBlend(Color.red(color), alpha);
//        mMultiplyBlendMatrixValues[6] = multiplyBlend(Color.green(color), alpha);
//        mMultiplyBlendMatrixValues[12] = multiplyBlend(Color.blue(color), alpha);
//        mMultiplyBlendMatrix.set(mMultiplyBlendMatrixValues);
//        return mMultiplyBlendMatrix;
//    }
//
//    private static float multiplyBlend(int color, float alpha) {
//        return color * alpha / 255.0f + (1 - alpha);
//    }

    /**
     * This helper method creates a 'nice' scrim or background protection for layering text over
     * an image. This non-linear scrim is less noticable than a linear or constant one.
     * <p/>
     * Borrowed from github.com/romannurik/muzei
     * <p/>
     * Creates an approximated cubic gradient using a multi-stop linear gradient. See
     * <a href="https://plus.google.com/+RomanNurik/posts/2QvHVFWrHZf">this post</a> for more
     * details.
     */
    public static Drawable makeCubicGradientScrimDrawable(int baseColor, int numStops, int gravity) {
        numStops = Math.max(numStops, 2);

        PaintDrawable paintDrawable = new PaintDrawable();
        paintDrawable.setShape(new RectShape());

        final int[] stopColors = new int[numStops];

        int alpha = Color.alpha(baseColor);

        for (int i = 0; i < numStops; i++) {
            double x = i * 1f / (numStops - 1);
            double opacity = Math.max(0, Math.min(1, Math.pow(x, 3)));
            stopColors[i] = (baseColor & 0x00ffffff) | ((int) (alpha * opacity) << 24);
        }

        final float x0, x1, y0, y1;
        switch (gravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.LEFT:
                x0 = 1;
                x1 = 0;
                break;
            case Gravity.RIGHT:
                x0 = 0;
                x1 = 1;
                break;
            default:
                x0 = 0;
                x1 = 0;
                break;
        }
        switch (gravity & Gravity.VERTICAL_GRAVITY_MASK) {
            case Gravity.TOP:
                y0 = 1;
                y1 = 0;
                break;
            case Gravity.BOTTOM:
                y0 = 0;
                y1 = 1;
                break;
            default:
                y0 = 0;
                y1 = 0;
                break;
        }

        paintDrawable.setShaderFactory(new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                LinearGradient linearGradient = new LinearGradient(
                        width * x0,
                        height * y0,
                        width * x1,
                        height * y1,
                        stopColors, null,
                        Shader.TileMode.CLAMP);
                return linearGradient;
            }
        });

        return paintDrawable;
    }

    public static String convertDateStringToString(String strDate, String currentFormat, String parseFormat)

    {
        try {
            return convertDateToString(convertStringToDate(strDate, currentFormat), parseFormat);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String convertDateToString(Date objDate, String parseFormat) {
        try {
            return new SimpleDateFormat(parseFormat).format(objDate);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static Date convertStringToDate(String strDate, String parseFormat) {
        try {
            return new SimpleDateFormat(parseFormat).parse(strDate);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // decodes image and scales it to reduce memory consumption
    public static Bitmap decodeFile(String path) {
        File f = null;
        Bitmap bitmap = null;

        try {
            f = new File(path);

            if (f.exists())
                bitmap = BitmapFactory.decodeFile(f.getPath());
            else
                Log.e("File not Found:", f.getPath());

        } catch (Exception e) {
            Log.e(UIUtils.class + " :: decodeFile() :: ", "" + e);
        } finally {
        }

        // release
        f = null;

        return bitmap;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressWarnings("deprecation")
    public static void sendNotification(Context mContext, int mode, String title, String message) {

        String ns = Context.NOTIFICATION_SERVICE;
        // TODO GET REFE OF NOTIFICATION MANAGER
        NotificationManager nManager = (NotificationManager) mContext.getSystemService(ns);

        Intent intent = null;

        Log.v(mContext.getClass().getSimpleName() + "::", "  mode:: " + mode + ":: title  ::" + title + ":: message ::" + message);

//        intent = new Intent(mContext, Event.class);
        PendingIntent pi = PendingIntent.getActivity(mContext, 0, intent, 0);


        Notification noti = new Notification.Builder(mContext)
                .setContentTitle("New mail from " + message.toString())
                .setContentText(message)
                .setSmallIcon(R.drawable.logo)
                .build(); // available from API level 11 and onwards

//        Notification n = new Notification(R.drawable.logo, message, System.currentTimeMillis());

//        n.setLatestEventInfo(mContext, title, message, pi);
        noti.defaults = Notification.DEFAULT_ALL;

        // Play default notification sound
        noti.defaults |= Notification.DEFAULT_SOUND;
        // Vibrate if vibrate is enabled
        noti.defaults |= Notification.DEFAULT_VIBRATE;
        // n.sound = Uri.parse("android.resource://"+ mContext.getPackageName()
        // + "/" + R.raw.notific);
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        nManager.notify(0, noti);
    }


}
