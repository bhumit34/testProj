package com.schindler.ui.utils;

import android.app.Activity;
import android.app.Service;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.File;

import static com.schindler.schindler.tag.ViewUtils.EMAIL_ADDRESS_PATTERN;

public class Utils {
	
	
	/**
	 * Convert Dp to Pixel
	 */
	public static int dpToPx(float dp, Resources resources){
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
		return (int) px;
	}
	
	public static int getRelativeTop(View myView) {
//	    if (myView.getParent() == myView.getRootView())
	    if(myView.getId() == android.R.id.content)
	        return myView.getTop();
	    else
	        return myView.getTop() + getRelativeTop((View) myView.getParent());
	}
	
	public static int getRelativeLeft(View myView) {
//	    if (myView.getParent() == myView.getRootView())
		if(myView.getId() == android.R.id.content)
			return myView.getLeft();
		else
			return myView.getLeft() + getRelativeLeft((View) myView.getParent());
	}
	public static boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
			return dir.delete();
		} else if(dir!= null && dir.isFile()) {
			return dir.delete();
		} else {
			return false;
		}
	}

/*	public static void KeyboadOff(EditText editText, InputMethodManager objInputMethodManager, Activity activity) {
		objInputMethodManager = (InputMethodManager) activity.getSystemService(Service.INPUT_METHOD_SERVICE);
		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		objInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}*/

	public static void KeyboadOff( InputMethodManager objInputMethodManager, Activity activity,View view) {
		objInputMethodManager = (InputMethodManager) activity.getSystemService(Service.INPUT_METHOD_SERVICE);
		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		objInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public static boolean checkEmail(String strEmail) {
		try {
			if (strEmail != null) {

				Log.v("UTILS :", "checkEmail() ==> EMAIL :" + strEmail);
				return EMAIL_ADDRESS_PATTERN.matcher(strEmail).matches();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String pad(int c) {
		return c >= 10 ? String.valueOf(c) : "0" + String.valueOf(c);
	}
}
