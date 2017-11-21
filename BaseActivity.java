package com.ui;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.R;
import com.ui.widget.CustomTextView;
import com.util.AppConstants;
import com.util.LUtils;
import com.util.Pref;

import java.util.ArrayList;

/**
 * Created by Bhumit Patel on 11/2/16.
 */

public class BaseActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String HTTPS = "https://";
    private static final String HTTP = "http://";
    // Durations for certain animations we use:
    private static final int HEADER_HIDE_ANIM_DURATION = 300;
    // Primary toolbar and drawer toggle
    private Toolbar mActionBarToolbar;
    protected ImageView imgMenu;
    protected TextView txtTitle, txtFilterType;
    protected ImageView imgDown;
    protected LinearLayout llEventFiltering;
    protected PopupWindow popup;
    // Navigation drawer:
    protected DrawerLayout mDrawerLayout;
    private ProgressDialog mProgressDialog;
    private boolean mActionBarShown = true;
    private ObjectAnimator mStatusBarColorAnimator;
    public GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private ArrayList<View> mHideableHeaderViews = new ArrayList<View>();
    private static final String TAG = "BaseActivity";
    // A Runnable that we should execute when the navigation drawer finishes its closing animation
    private Runnable mDeferredOnDrawerClosedRunnable;
    // Helper methods for L APIs
    private LUtils mLUtils;
    private RelativeLayout navdrawer_newsfeed_menu, navdrawer_qr_code_menu, navdrawer_vendor_menu, navdrawer_profile_menu, navdrawer_educationProfile_menu, navdrawer_notification_menu, navdrawer_settings_menu, navdrawer_login_menu, navdrawer_socialmenu_facebook, navdrawer_socialmenu_whatsapp, navdrawer_socialmenu_email, navdrawer_socialmenu_message, navdrawer_change_password_menu;
    protected static final int NAVDRAWER_EVENT = 0;
    protected static final int NAVDRAWER_ITEM_INVALID = -1;
    // variables that control the Action Bar auto hide behavior (aka "quick recall")
    private boolean mActionBarAutoHideEnabled = false;
    private int mActionBarAutoHideSignal = 0;
    private static final TypeEvaluator ARGB_EVALUATOR = new ArgbEvaluator();
    private int mThemedStatusBarColor;
    private Handler mHandler;
    private int mActionBarAutoHideSensivity = 0;
    private int mActionBarAutoHideMinY = 0;
    private int mNormalStatusBarColor;
    // different Activities of the app through the Nav Drawer
    private static final int MAIN_CONTENT_FADEIN_DURATION = 250;
    // list of navdrawer items that were actually added to the navdrawer, in order
    protected Activity mActivity;
    private String app_link = "http://play.google.com/store/apps/details?id=";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        mHandler = new Handler();

        mLUtils = LUtils.getInstance(this);
        mThemedStatusBarColor = getResources().getColor(R.color.theme_accent_1);
        mNormalStatusBarColor = mThemedStatusBarColor;
        // For google plus login
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
//         [END build_client]
    }

    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_INVALID;
    }

    private void setupNavDrawer() {
        // What nav drawer item should be selected?
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navdrawer_notification_menu = (RelativeLayout) findViewById(R.id.navdrawer_notification_menu);
        navdrawer_newsfeed_menu = (RelativeLayout) findViewById(R.id.navdrawer_newsfeed_menu);
        navdrawer_vendor_menu = (RelativeLayout) findViewById(R.id.navdrawer_vendor_menu);
        navdrawer_profile_menu = (RelativeLayout) findViewById(R.id.navdrawer_profile_menu);
        navdrawer_educationProfile_menu = (RelativeLayout) findViewById(R.id.navdrawer_educationProfile_menu);
        navdrawer_qr_code_menu = (RelativeLayout) findViewById(R.id.navdrawer_qr_code_menu);
        navdrawer_login_menu = (RelativeLayout) findViewById(R.id.navdrawer_login_menu);
        navdrawer_settings_menu = (RelativeLayout) findViewById(R.id.navdrawer_settings_menu);
        navdrawer_socialmenu_email = (RelativeLayout) findViewById(R.id.navdrawer_socialmenu_email);
        navdrawer_socialmenu_message = (RelativeLayout) findViewById(R.id.navdrawer_socialmenu_message);
        navdrawer_socialmenu_whatsapp = (RelativeLayout) findViewById(R.id.navdrawer_socialmenu_whatsapp);
        navdrawer_socialmenu_facebook = (RelativeLayout) findViewById(R.id.navdrawer_socialmenu_facebook);
        navdrawer_change_password_menu = (RelativeLayout) findViewById(R.id.navdrawer_change_password_menu);

        if (mDrawerLayout == null) {
            return;
        }
        mDrawerLayout.setStatusBarBackgroundColor(
                getResources().getColor(R.color.theme_primary_dark));


        if (mActionBarToolbar != null) {
            imgMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isNavDrawerOpen()) {
                        imgMenu.setImageResource(R.drawable.ic_menu_indicator);
                        closeNavDrawer();
                    } else {
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            });

            navdrawer_profile_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createBackStack(new Intent(mActivity, Profile.class));

                    if (isNavDrawerOpen()) {
                        imgMenu.setImageResource(R.drawable.ic_menu_indicator);
                        closeNavDrawer();
                    } else {
                        imgMenu.setImageResource(R.drawable.ic_left_arrow);
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            });
            navdrawer_educationProfile_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createBackStack(new Intent(mActivity, EducationProfile.class));
                    if (isNavDrawerOpen()) {
                        imgMenu.setImageResource(R.drawable.ic_menu_indicator);
                        closeNavDrawer();
                    } else {

                        imgMenu.setImageResource(R.drawable.ic_left_arrow);
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            });
            navdrawer_change_password_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createBackStack(new Intent(mActivity, ChangePassword.class));
                    if (isNavDrawerOpen()) {
                        imgMenu.setImageResource(R.drawable.ic_menu_indicator);
                        closeNavDrawer();
                    } else {
                        imgMenu.setImageResource(R.drawable.ic_left_arrow);
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            });

            navdrawer_qr_code_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createBackStack(new Intent(mActivity, GenerateQRCode.class));
                    if (isNavDrawerOpen()) {
                        imgMenu.setImageResource(R.drawable.ic_menu_indicator);
                        closeNavDrawer();
                    } else {
                        imgMenu.setImageResource(R.drawable.ic_left_arrow);
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            });

            navdrawer_newsfeed_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createBackStack(new Intent(mActivity, Newsfeed.class));
                    if (isNavDrawerOpen()) {
                        imgMenu.setImageResource(R.drawable.ic_menu_indicator);
                        closeNavDrawer();
                    } else {
                        imgMenu.setImageResource(R.drawable.ic_left_arrow);
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            });

            navdrawer_vendor_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createBackStack(new Intent(mActivity, Vendor.class));
                    if (isNavDrawerOpen()) {
                        imgMenu.setImageResource(R.drawable.ic_menu_indicator);
                        closeNavDrawer();
                    } else {
                        imgMenu.setImageResource(R.drawable.ic_left_arrow);
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            });
            navdrawer_notification_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createBackStack(new Intent(mActivity, Notification.class));
                    if (isNavDrawerOpen()) {
                        imgMenu.setImageResource(R.drawable.ic_menu_indicator);
                        closeNavDrawer();
                    } else {
                        imgMenu.setImageResource(R.drawable.ic_left_arrow);
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            });

            navdrawer_settings_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createBackStack(new Intent(mActivity, Settings.class));
                    if (isNavDrawerOpen()) {
                        imgMenu.setImageResource(R.drawable.ic_menu_indicator);
                        closeNavDrawer();
                    } else {
                        imgMenu.setImageResource(R.drawable.ic_left_arrow);
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            });

            navdrawer_login_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isNavDrawerOpen()) {
                        imgMenu.setImageResource(R.drawable.ic_menu_indicator);
                        closeNavDrawer();
                        setLogOut();
                    } else {
                        imgMenu.setImageResource(R.drawable.ic_left_arrow);
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            });

            navdrawer_socialmenu_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isNavDrawerOpen()) {
                        imgMenu.setImageResource(R.drawable.ic_menu_indicator);
                        closeNavDrawer();
                        Intent sendIntent = new Intent();
                        sendIntent.setType("text/plain");
                        sendIntent.setAction(Intent.ACTION_SEND);
                        String sMessage = "\nLet me recommend you this application\n\n";
                        sMessage = sMessage + app_link + mActivity.getPackageName() + "\n\n";
                        sendIntent.putExtra(Intent.EXTRA_TEXT, sMessage);
                        mActivity.startActivity(Intent.createChooser(sendIntent, "Via Yes2Malaysia"));
                    } else {
                        imgMenu.setImageResource(R.drawable.ic_left_arrow);
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            });

            navdrawer_socialmenu_email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isNavDrawerOpen()) {
                        imgMenu.setImageResource(R.drawable.ic_menu_indicator);
                        closeNavDrawer();
                        boolean installed = appinstall_or_not("com.whatsapp");
                        if (installed) {
                            try {
                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("text/plain");
                                i.putExtra(Intent.EXTRA_SUBJECT, "Via Yes2Malaysia");
                                String sAux = "\nLet me recommend you this application\n\n";
                                sAux = sAux + app_link + mActivity.getPackageName() + "\n\n";
                                i.putExtra(Intent.EXTRA_TEXT, sAux);
                                startActivity(Intent.createChooser(i, "choose one"));
                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(mActivity, R.string.whatsapp_not_installed, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(mActivity, R.string.whatsapp_not_installed, Toast.LENGTH_SHORT).show();
                            openBrowser(mActivity, "https://play.google.com/store/apps/details?id=com.whatsapp&hl=en");
                        }

                    } else {
                        imgMenu.setImageResource(R.drawable.ic_left_arrow);
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            });

            navdrawer_socialmenu_whatsapp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isNavDrawerOpen()) {
                        imgMenu.setImageResource(R.drawable.ic_menu_indicator);
                        closeNavDrawer();
                        try {
                            final ComponentName name = new ComponentName("com.whatsapp", "com.whatsapp.ContactPicker");
                            Intent oShareIntent = new Intent();
                            oShareIntent.setComponent(name);
                            oShareIntent.setType("text/plain");
                            oShareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Via Yes2Malaysia");
                            oShareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Check out Yes2Malaysia application: " + app_link + mActivity.getPackageName());
                            startActivity(oShareIntent);
                        } catch (Exception e) {

                        }
                    } else {
                        imgMenu.setImageResource(R.drawable.ic_left_arrow);
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            });

            navdrawer_socialmenu_facebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isNavDrawerOpen()) {
                        imgMenu.setImageResource(R.drawable.ic_menu_indicator);
                        closeNavDrawer();

                        shareAppLinkViaFacebook();

                    } else {
                        imgMenu.setImageResource(R.drawable.ic_left_arrow);
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            });
        }

        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                // run deferred action, if we have one
                if (mDeferredOnDrawerClosedRunnable != null) {
                    mDeferredOnDrawerClosedRunnable.run();
                    mDeferredOnDrawerClosedRunnable = null;
                }
                onNavDrawerStateChanged(false, false);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                onNavDrawerStateChanged(true, false);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                onNavDrawerStateChanged(isNavDrawerOpen(), newState != DrawerLayout.STATE_IDLE);
                if (isNavDrawerOpen()) {
                    imgMenu.setImageResource(R.drawable.ic_left_arrow);
                } else {
                    imgMenu.setImageResource(R.drawable.ic_menu_indicator);
                }
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                onNavDrawerSlide(slideOffset);
            }
        });

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // populate the nav drawer with the correct items
//        populateNavDrawer();
    }

    protected void onNavDrawerStateChanged(boolean isOpen, boolean isAnimating) {
        if (mActionBarAutoHideEnabled && isOpen) {
            autoShowOrHideActionBar(true);
        }
    }

    protected void onNavDrawerSlide(float offset) {
    }

    protected boolean isNavDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    protected void closeNavDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getActionBarToolbar();
    }

    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            closeNavDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupNavDrawer();

        View mainContent = findViewById(R.id.main_content);
        if (mainContent != null) {
            mainContent.setAlpha(0);
            mainContent.animate().alpha(1).setDuration(MAIN_CONTENT_FADEIN_DURATION);
        } else {
        }
    }

    protected void autoShowOrHideActionBar(boolean show) {
        if (show == mActionBarShown) {
            return;
        }
        mActionBarShown = show;
        onActionBarAutoShowOrHide(show);
    }

    /**
     * Sets up the given navdrawer item's appearance to the selected state. Note: this could
     * also be accomplished (perhaps more cleanly) with state-based layouts.
     */

    protected void onActionBarAutoShowOrHide(boolean shown) {
        if (mStatusBarColorAnimator != null) {
            mStatusBarColorAnimator.cancel();
        }
        mStatusBarColorAnimator = ObjectAnimator.ofInt(
                (mDrawerLayout != null) ? mDrawerLayout : mLUtils,
                (mDrawerLayout != null) ? "statusBarBackgroundColor" : "statusBarColor",
                shown ? Color.BLACK : mNormalStatusBarColor,
                shown ? mNormalStatusBarColor : Color.BLACK)
                .setDuration(250);
        if (mDrawerLayout != null) {
            mStatusBarColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ViewCompat.postInvalidateOnAnimation(mDrawerLayout);
                }
            });
        }
        mStatusBarColorAnimator.setEvaluator(ARGB_EVALUATOR);
        mStatusBarColorAnimator.start();

        for (final View view : mHideableHeaderViews) {
            if (shown) {
                ViewCompat.animate(view)
                        .translationY(0)
                        .alpha(1)
                        .setDuration(HEADER_HIDE_ANIM_DURATION)
                        .setInterpolator(new DecelerateInterpolator())
                                // Setting Alpha animations should be done using the
                                // layer_type set to layer_type_hardware for the duration of the animation.
                        .withLayer();
            } else {
                ViewCompat.animate(view)
                        .translationY(-view.getBottom())
                        .alpha(0)
                        .setDuration(HEADER_HIDE_ANIM_DURATION)
                        .setInterpolator(new DecelerateInterpolator())
                                // Setting Alpha animations should be done using the
                                // layer_type set to layer_type_hardware for the duration of the animation.
                        .withLayer();
            }
        }
    }

    private void createBackStack(Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            TaskStackBuilder builder = TaskStackBuilder.create(this);
            builder.addNextIntentWithParentStack(intent);
            builder.startActivities();
        } else {
            startActivity(intent);
            // finish();
        }
    }

    protected Toolbar getActionBarToolbar() {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);

            imgMenu = (ImageView) findViewById(R.id.imgMenu);
            imgDown = (ImageView) findViewById(R.id.imgDown);
            txtTitle = (TextView) findViewById(R.id.txtTitle);
            txtFilterType = (TextView) findViewById(R.id.txtFilterType);
            llEventFiltering = (LinearLayout) findViewById(R.id.llEventFiltering);
            if (mActionBarToolbar != null) {
                // Depending on which version of Android you are on the Toolbar or the ActionBar may be
                // active so the a11y description is set here.
                setSupportActionBar(mActionBarToolbar);
                //centerToolbarTitle(mActionBarToolbar);
            }

            llEventFiltering.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayPopupWindow(v);
                }
            });
        }
        return mActionBarToolbar;
    }

    public void displayPopupWindow(View anchorView) {
        popup = new PopupWindow(mActivity);
        View layout = mActivity.getLayoutInflater().inflate(R.layout.toolbar_setting_menu, (ViewGroup) findViewById(R.id.menu_layout));

        // TODO
        final CustomTextView txtPastEvent, txtUpComingEvent;
        final RelativeLayout rlUpcoming, rlPast;

        txtPastEvent = (CustomTextView) layout.findViewById(R.id.txtPastEvent);
        txtUpComingEvent = (CustomTextView) layout.findViewById(R.id.txtUpComingEvent);
        rlUpcoming = (RelativeLayout) layout.findViewById(R.id.rlUpcoming);
        rlPast = (RelativeLayout) layout.findViewById(R.id.rlPast);
        rlPast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rlPast.setBackgroundColor(Color.parseColor("#C61E2C"));
                txtPastEvent.setTextColor(Color.parseColor("#ffffff"));
                rlUpcoming.setBackgroundColor(Color.parseColor("#ffffff"));
                txtUpComingEvent.setTextColor(Color.parseColor("#000000"));
                popup.dismiss();
            }
        });

        rlUpcoming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlUpcoming.setBackgroundColor(Color.parseColor("#C61E2C"));
                txtUpComingEvent.setTextColor(Color.parseColor("#ffffff"));
                rlPast.setBackgroundColor(Color.parseColor("#ffffff"));
                txtPastEvent.setTextColor(Color.parseColor("#000000"));
                popup.dismiss();
            }
        });

        //Check condition here
        popup.setContentView(layout);
        // Set content width and height
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        popup.update();
        // Show anchored to button
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.showAsDropDown(anchorView);
        popup.showAtLocation(layout, Gravity.RIGHT, 100, 100);
    }


    public void showSnackbar(View v, String message) {
        Snackbar snackbar = Snackbar.make(v, message, Snackbar.LENGTH_LONG);
        ViewGroup group = (ViewGroup) snackbar.getView();
        group.setBackgroundColor(Color.parseColor("#C61E2C"));
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.show();
    }

    private void setLogOut() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set title
        alertDialogBuilder.setTitle(getString(R.string.app_name));

        // set dialog message
        alertDialogBuilder.setMessage(getString(R.string.want_to_logout)).setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {


                                if (isNavDrawerOpen()) {
                                    imgMenu.setImageResource(R.drawable.ic_menu_indicator);
                                    closeNavDrawer();


                                } else {
                                    imgMenu.setImageResource(R.drawable.ic_left_arrow);
                                    mDrawerLayout.openDrawer(GravityCompat.START);
                                }


                                if (Pref.getValue(mActivity, AppConstants.PREF_LOGIN_BY, "").equals("1")) {
                                    LoginManager.getInstance().logOut();
                                    Pref.setValue(mActivity, AppConstants.PREF_LOGIN_BY, "");
                                    Pref.setValue(mActivity, AppConstants.PREF_USERID, "");
                                    Pref.setValue(mActivity, AppConstants.PREF_USERNAME, "");
                                    Pref.setValue(mActivity, AppConstants.PREF_USERIMAGE, "");
                                    Pref.setValue(mActivity, AppConstants.PREF_USEREMAIL, "");
                                    Pref.setValue(mActivity, AppConstants.PREF_USERTYPE, "");
                                    Pref.setValue(mActivity, AppConstants.PREF_APP_CONTACT_NUM, "");
                                    Pref.setValue(mActivity, AppConstants.PREF_APP_CONTACT_EMAIL, "");
                                    Pref.setValue(mActivity, AppConstants.PREF_GENDERID, "");
                                    Pref.setValue(mActivity, AppConstants.PREF_NEWSFEED, "");
                                    Intent intent = new Intent(getApplicationContext(), Login.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                    overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
                                } else if (Pref.getValue(mActivity, AppConstants.PREF_LOGIN_BY, "").equals("2")) {
                                    googleSignOut();
                                } else if (Pref.getValue(mActivity, AppConstants.PREF_LOGIN_BY, "").equals("3")) {
                                    logoutTwitter();
                                } else if (Pref.getValue(mActivity, AppConstants.PREF_LOGIN_BY, "").equals("0")) {
                                    Pref.setValue(mActivity, AppConstants.PREF_LOGIN_BY, "");
                                    Pref.setValue(mActivity, AppConstants.PREF_USERID, "");
                                    Pref.setValue(mActivity, AppConstants.PREF_USERNAME, "");
                                    Pref.setValue(mActivity, AppConstants.PREF_USERIMAGE, "");
                                    Pref.setValue(mActivity, AppConstants.PREF_USEREMAIL, "");
                                    Pref.setValue(mActivity, AppConstants.PREF_USERTYPE, "");
                                    Pref.setValue(mActivity, AppConstants.PREF_APP_CONTACT_NUM, "");
                                    Pref.setValue(mActivity, AppConstants.PREF_APP_CONTACT_EMAIL, "");
                                    Pref.setValue(mActivity, AppConstants.PREF_GENDERID, "");
                                    Pref.setValue(mActivity, AppConstants.PREF_NEWSFEED, "");
                                    Intent mIntent = new Intent(mActivity, Login.class);
                                    mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(mIntent);
                                    finish();
                                    overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
                                }


                            }
                        }
                ).
                setNegativeButton(getString(R.string.no),

                        new DialogInterface.OnClickListener()

                        {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }
                );

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void logoutTwitter() {
        TwitterSession twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
        if (twitterSession != null) {
            ClearCookies(getApplicationContext());
            Twitter.getSessionManager().clearActiveSession();
            Twitter.logOut();
            Pref.setValue(mActivity, AppConstants.PREF_LOGIN_BY, "");
            Pref.setValue(mActivity, AppConstants.PREF_USERID, "");
            Pref.setValue(mActivity, AppConstants.PREF_USERNAME, "");
            Pref.setValue(mActivity, AppConstants.PREF_USERIMAGE, "");
            Pref.setValue(mActivity, AppConstants.PREF_USEREMAIL, "");
            Pref.setValue(mActivity, AppConstants.PREF_USERTYPE, "");
            Pref.setValue(mActivity, AppConstants.PREF_APP_CONTACT_NUM, "");
            Pref.setValue(mActivity, AppConstants.PREF_APP_CONTACT_EMAIL, "");
            Pref.setValue(mActivity, AppConstants.PREF_GENDERID, "");
            Pref.setValue(mActivity, AppConstants.PREF_NEWSFEED, "");
            Intent mIntent = new Intent(mActivity, Login.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mIntent);
            finish();
            overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
        }
    }

    public static void ClearCookies(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    private void googleSignOut() {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.d(TAG, " Successfully signout");
                        Pref.setValue(mActivity, AppConstants.PREF_LOGIN_BY, "");
                        Pref.setValue(mActivity, AppConstants.PREF_USERID, "");
                        Pref.setValue(mActivity, AppConstants.PREF_USERNAME, "");
                        Pref.setValue(mActivity, AppConstants.PREF_USERIMAGE, "");
                        Pref.setValue(mActivity, AppConstants.PREF_USEREMAIL, "");
                        Pref.setValue(mActivity, AppConstants.PREF_USERTYPE, "");
                        Pref.setValue(mActivity, AppConstants.PREF_APP_CONTACT_NUM, "");
                        Pref.setValue(mActivity, AppConstants.PREF_APP_CONTACT_EMAIL, "");
                        Pref.setValue(mActivity, AppConstants.PREF_GENDERID, "");
                        Pref.setValue(mActivity, AppConstants.PREF_NEWSFEED, "");
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
                    }
                });
    }

    private void shareAppLinkViaFacebook() {
        String urlToShare = app_link + mActivity.getPackageName();

        try {
            Intent intent1 = new Intent();
            intent1.setClassName("com.facebook.katana", "com.facebook.katana.activity.composer.ImplicitShareIntentHandler");
            intent1.setAction("android.intent.action.SEND");
            intent1.setType("text/plain");
            intent1.putExtra("android.intent.extra.TEXT", urlToShare);
            startActivity(intent1);
        } catch (Exception e) {
            // If we failed (not native FB app installed), try share through SEND
            Intent intent = new Intent(Intent.ACTION_SEND);
            String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + urlToShare;
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
            startActivity(intent);
        }
    }

    @SuppressWarnings("static-access")
    private boolean appinstall_or_not(String uri) {
        PackageManager packagemanager = getPackageManager();
        boolean app_installed = false;
        try {
            packagemanager.getPackageInfo(uri, packagemanager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public void openBrowser(Context context, String url) {
        if (!url.startsWith(HTTP) && !url.startsWith(HTTPS)) {
            url = HTTP + url;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(Intent.createChooser(intent, "Chose browser"));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(getClass().getSimpleName(), "onConnectionFailed:" + connectionResult);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(getClass().getSimpleName(), "handleSignInResult:" + result.isSuccess());
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
}
