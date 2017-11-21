package com.yes2malaysia.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.yes2malaysia.R;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * User: mcxiaoke
 * Date: 14-3-26
 * Time: 16:08
 */
public class ViewUtils {

    // temp variable
    private static TypedValue mTmpValue = new TypedValue();

    // This intro hides the system bars.
    @TargetApi(VERSION_CODES.KITKAT)
    public static void hideSystemUI(Activity activity) {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hideSelf and show.
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hideSelf nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hideSelf status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
        );
    }

    // This intro shows the system bars. It does this by removing all the flags
// except for the ones that make the content appear under the system bars.
    @TargetApi(VERSION_CODES.KITKAT)
    public static void showSystemUI(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }

    /**
     * 23      * Returns true if view's layout direction is right-to-left.
     * 24      *
     * 25      * @param view the View whose layout is being considered
     * 26
     */
    public static boolean isLayoutRtl(View view) {
        if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
            return view.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
        } else {
            // All layouts are LTR before JB MR1.
            return false;
        }
    }

    public static Point getScreenRawSize(Display display) {
        if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
            Point outPoint = new Point();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getRealMetrics(metrics);
            outPoint.x = metrics.widthPixels;
            outPoint.y = metrics.heightPixels;
            return outPoint;
        } else {
            Point outPoint = new Point();
            Method mGetRawH;
            try {
                mGetRawH = Display.class.getMethod("getRawHeight");
                Method mGetRawW = Display.class.getMethod("getRawWidth");
                outPoint.x = (Integer) mGetRawW.invoke(display);
                outPoint.y = (Integer) mGetRawH.invoke(display);
                return outPoint;
            } catch (Throwable e) {
                return new Point(0, 0);
            }
        }
    }

    public static SpannableStringBuilder etSetHint(Context mContext, String hint) {

        SpannableStringBuilder builder = new SpannableStringBuilder();

        String blue = hint;
        SpannableString blueSpannable = new SpannableString(blue);
        blueSpannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.register_edittext_color)), 0, blue.length(), 0);
        builder.append(blueSpannable);


        String red = "*";
        SpannableString redSpannable = new SpannableString(red);
        redSpannable.setSpan(new ForegroundColorSpan(Color.RED), 0, red.length(), 0);
        builder.append(redSpannable);

        return builder;
    }

    public static int getActionBarHeightInDp(Context context) {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        if (Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
            if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv,
                    true))
                actionBarHeight = (int) TypedValue.complexToFloat(tv.data);
        } else {
            tv.data = 48;
            actionBarHeight = (int) TypedValue.complexToFloat(tv.data);
        }
        return actionBarHeight;
    }

    public static int getActionBarHeight(Context context) {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        if (Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
            if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv,
                    true))
                actionBarHeight = TypedValue.complexToDimensionPixelSize(
                        tv.data, dm);
        } else {
            tv.data = 48;
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
                    dm);
        }
        return actionBarHeight;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {

            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getSystemBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("system_bar_height", "dimen", "android");

        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getStatusBarHeightInDp(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResourceValue(context, resourceId);
        }
        return result;
    }

    public static int getSystemBarHeightInDp(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("system_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResourceValue(context, resourceId);
        }
        return result;
    }

    /**
     * 获取资源中的数值，没有经过转换，比如dp,sp等
     */
    public static int getResourceValue(Context context, int resId) {
        TypedValue value = mTmpValue;
        context.getResources().getValue(resId, value, true);
        return (int) TypedValue.complexToFloat(value.data);
    }

    public static int dpToPx(Context context, int dp) {
        return (int) ((dp * context.getResources().getDisplayMetrics().density) + 0.5);
    }

    public static int pxToDp(Context context, int px) {
        return (int) ((px / context.getResources().getDisplayMetrics().density) + 0.5);
    }

    public static int pxToDp(float px) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return Math.round(dp);
    }

    public static int dpToPx(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    @SuppressWarnings({"unchecked", "UnusedDeclaration"})
    public static <T extends View> T findById(View view, int id) {
        return (T) view.findViewById(id);
    }

    @SuppressWarnings({"unchecked", "UnusedDeclaration"})
    public static <T extends View> T findById(Activity activity, int id) {
        return (T) activity.findViewById(id);
    }

    public static void showToast(Context mContext, String message, int length) {
        Toast.makeText(mContext, message, length).show();
    }

    private static String strPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(strPattern);

    public static boolean checkEmail(String strEmail) {
        try {
            if (strEmail != null) {

                LogUtils.LOGD("UTILS :", "checkEmail() ==> EMAIL :" + strEmail);
                return EMAIL_ADDRESS_PATTERN.matcher(strEmail).matches();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void KeyboadOff(EditText editText, InputMethodManager objInputMethodManager, Activity activity) {
        objInputMethodManager = (InputMethodManager) activity.getSystemService(Service.INPUT_METHOD_SERVICE);
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        objInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }


    public static void KeyboadOff(View view, InputMethodManager objInputMethodManager, Activity activity) {
        objInputMethodManager = (InputMethodManager) activity.getSystemService(Service.INPUT_METHOD_SERVICE);
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        objInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
