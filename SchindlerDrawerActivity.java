package com.schindler.schindler;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.ServerError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.schindler.MyApplication;
import com.schindler.R;
import com.schindler.commnonclass.CommonFunctionality;
import com.schindler.commnonclass.ConstantData;
import com.schindler.commnonclass.Validation;
import com.schindler.gettersetter.Media;
import com.schindler.gettersetter.UserPermissionSubMenuModel;
import com.schindler.gettersetter.UserPermissionViewModel;
import com.schindler.schindler.MyProjects.MailboxView;
import com.schindler.schindler.MyProjects.NotificationList;
import com.schindler.schindler.MyProjects.OfficeManagers;
import com.schindler.schindler.MyProjects.ProjectList;
import com.schindler.schindler.MyProjects.ResourceCenter;
import com.schindler.schindler.tag.DrawShadowFrameLayout;
import com.schindler.schindler.tag.LUtils;
import com.schindler.schindler.tag.Log;
import com.schindler.schindler.tag.MySingleton;
import com.schindler.schindler.tag.Pref;
import com.schindler.schindler.tag.RoundedImageView;
import com.schindler.schindler.tag.UIUtils;
import com.schindler.schindler.tag.ViewUtils;
import com.schindler.schindler.tag.WebInterface;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.schindler.commnonclass.ConstantData.CHANGE_PASSWORD_FOR_USER;

@SuppressWarnings("ConstantConditions")
//public class SchindlerDrawerActivity extends ActionBarActivity implements SalesHubConstants,Serializable{
public class SchindlerDrawerActivity extends AppCompatActivity implements Serializable {
    String backStateName;
    public FragmentTransaction ft;
    private Dialog dialogChangePassword;
    protected PopupWindow popup;
    private Runnable mDeferredOnDrawerClosedRunnable;
    public static ImageView imgMenu;
    private RoundedImageView profile_image;
    private Handler mHandler;
    protected Activity activity;
    private LUtils mLUtils;
    private int mThemedStatusBarColor;
    private int mNormalStatusBarColor;
    private static final TypeEvaluator ARGB_EVALUATOR = new ArgbEvaluator();
    private static final int HEADER_HIDE_ANIM_DURATION = 300;
    private static final int MAIN_CONTENT_FADEIN_DURATION = 250;
    public FragmentTransaction fragmentTransaction;
    private RelativeLayout navdrawer_contacts_menu, navdrawer_Dashboard_menu, navdrawer_my_project_menu, navdrawer_resource_center_menu, navdrawer_office_managers_menu, navdrawer_sec_emp_management_menu, navdrawer_notification_menu, navdrawer_settings_menu, navdrawer_profile_menu, navdrawer_logout_menu;
    private LinearLayout llMainContainer;
    public static TextView txtTitle;
    public TextView profile_name_text;
    public TextView txtDashboard;
    public TextView txtMyProject;
    public TextView txtResourceCenter;
    public TextView txtOfficeManager;
    public TextView txtProfile;
    public TextView txtSettings;
    public TextView txtNotification;
    public TextView txtSchindlerEmpManagement;
    public static RelativeLayout rlSearch, rlMail, rlInfo;
    protected static final int NAVDRAWER_ITEM_INVALID = -1;
    protected static final int NAVDRAWER_ITEM_SEPARATOR = -2;
    private boolean mActionBarAutoHideEnabled = false;
    private ObjectAnimator mStatusBarColorAnimator;
    private ArrayList<View> mHideableHeaderViews = new ArrayList<View>();
    boolean isLetter = false;
    boolean isNumber = false;
    boolean isCapitalLetter = false;
    boolean isEightCharacter = false;
    private int mActionBarAutoHideSensivity = 0;
    private int mActionBarAutoHideMinY = 0;
    private int mActionBarAutoHideSignal = 0;
    private boolean mActionBarShown = true;

    private static final int MY_PERMISSIONS_REQUEST_CONTACT = 1;
    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 2;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 3;

    private String[] permissions_camera =
            {
                    Manifest.permission.CAMERA,
            };
    private String[] permissions =
            {
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS
            };

    private String[] storage_permissions =
            {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

    //    private boolean doubleBackToExitPressedOnce = false;
    private DrawerLayout mDrawerLayout;
    //    private ListView mDrawerList;
    private InputMethodManager inputMethodManager;
    private ArrayList<HashMap<String, String>> contactsArray;
    private ArrayList<String> arrayPic;
    private ArrayList<HashMap<String, String>> contactsProfileBCardArray;
    //    private de.hdodenhof.circleimageview.CircleImageView crclProfilePic;
    private String[] titles = {"Media", "Campaign", "Contacts", "Email History", "Notifications", "Setttings", "My Profile", "Logout"};
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    //    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mActionBarToolbar;
    private FragmentManager fragmentManager;
//    private SchindlerDashboard schindlerDashboardFragment = null;
//    private SchindlerDashboard fragmentTwo = null;

    ProjectFragment projectFragment = null;
    private Campaign_Fragment fragmentCamp = null;
    private ContactsFragment contactFragment = null;
    private EmailHistoryFragment emailHistoryFragment = null;
    private NotificationFragment notificationFragment = null;
    private ArrayList<HashMap<String, String>> fileArray = new ArrayList<>();
    private ArrayList<HashMap<String, String>> fileToDownloadArray = new ArrayList<>();
    private static ArrayList<Media> mediaInfoArrayList = new ArrayList<>();
    private String updatedBy, userId = "", deviceId = "", roleId = "", compId = "", emalId = "";
    private Context context;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "SchindlerDrawerActivity";
    private String message = "";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private String currentDate, currentTime;
    //    private String fileName[] = {"1.jpg", "1.docx", "1.odt", "3.jpg", "1.doc", "Holiday List 2015.xlsx", "1.doc", "2.jpg", "4.jpg", "5.jpg", "6.jpg", "7.jpg", "8.jpg"};
    private String PREF_NAME = "PREF_NAME_uName";
    private String PREF_PWD = "PREF_NAME_pwd";
    //    public ProgressBar prgBar1;
    private String fPath;
    int pos = 0;
    private SharedPreferences isFirstTimeSP;
    boolean isFirstTime = false;
    private SharedPreferences.Editor editorFirstTime;
    public static String PREF_ISFIRSTTIME;
    private StringBuffer url, contactID, campaignID, userID, deviceID, fName, lName, pOne, pTwo, title, emailAdd, company, addOne, addTwo, city, zip, state, website, notes, profilePic;
    private Dialog dialog;
    private Media media;

    public Dialog dialogContacts;

    public Dialog getDialogContacts() {
        return dialogContacts;
    }

    public void setDialogContacts(Dialog dialogContacts) {
        this.dialogContacts = dialogContacts;
    }

    String cPName = "";
    int cPPos = 0;

    public Dialog getDialog() {
        return dialog;
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public void onContactsClick() {
        //--start listening for gps update
        try {
            if ((int) Build.VERSION.SDK_INT >= 23) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider LOCATION
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS) && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_CONTACTS)) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS) && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_CONTACTS)) {
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                            builder.setMessage("To get contatcs you have to allow us access to your contacts.");
                            builder.setTitle("Contacts");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(SchindlerDrawerActivity.this, permissions, 0);
                                    onCameraClick();
                                }
                            });
                            builder.show();
                        } else {
                            ActivityCompat.requestPermissions(this, permissions, 0);
                            onCameraClick();
                        }
                    } else {
                        ActivityCompat.requestPermissions(SchindlerDrawerActivity.this,
                                permissions,
                                MY_PERMISSIONS_REQUEST_CONTACT);
                        onCameraClick();
                    }
                } else {
                    onCameraClick();
                }
            }
        } catch (Exception e) {
        }
    }

    public void onStoragePermissionClick() {
        //--start listening for gps update
        try {
            if ((int) Build.VERSION.SDK_INT >= 23) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider LOCATION
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                            builder.setMessage("To get storage access you have to allow us access to your sd card content.");
                            builder.setTitle("Storage");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(SchindlerDrawerActivity.this, storage_permissions, 0);
                                    onContactsClick();
                                }
                            });
                            builder.show();
                        } else {
                            ActivityCompat.requestPermissions(this, storage_permissions, 0);
                            onContactsClick();
                        }
                    } else {
                        ActivityCompat.requestPermissions(SchindlerDrawerActivity.this,
                                storage_permissions,
                                MY_PERMISSIONS_REQUEST_STORAGE);
                        onContactsClick();
                    }
                } else {
                    onContactsClick();
                }
            }
        } catch (Exception e) {
        }
    }

    public void onCameraClick() {
        //--start listening for gps update
        try {
            if ((int) Build.VERSION.SDK_INT >= 23) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider CAMERA
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                            builder.setMessage("To get camera access you have to allow us access to your camera.");
                            builder.setTitle("Contacts");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(SchindlerDrawerActivity.this, permissions_camera, 0);
                                }
                            });

                            builder.show();
                        } else {
                            ActivityCompat.requestPermissions(this, permissions_camera, 0);
                        }
                    } else {
                        ActivityCompat.requestPermissions(this,
                                permissions_camera,
                                MY_PERMISSIONS_REQUEST_CAMERA);
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_toolbar);
        onStoragePermissionClick();
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeButtonEnabled(false); // disable the button
            ab.setDisplayHomeAsUpEnabled(false); // remove the left caret
            ab.setDisplayShowHomeEnabled(false); // remove the icon
        }
        activity = SchindlerDrawerActivity.this;

        mHandler = new Handler();
        fragmentManager = getSupportFragmentManager();


        mLUtils = LUtils.getInstance(this);
        mThemedStatusBarColor = getResources().getColor(R.color.theme_primary_dark);
        mNormalStatusBarColor = mThemedStatusBarColor;

    }

    private void setupNavDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        profile_image = (RoundedImageView) findViewById(R.id.profile_image);
        profile_name_text = (TextView) findViewById(R.id.profile_name_text);
        txtDashboard = (TextView) findViewById(R.id.txtDashboard);
        txtMyProject = (TextView) findViewById(R.id.txtMyProject);
        txtResourceCenter = (TextView) findViewById(R.id.txtResourceCenter);
        txtOfficeManager = (TextView) findViewById(R.id.txtOfficeManager);
        txtProfile = (TextView) findViewById(R.id.txtProfile);
        txtSettings = (TextView) findViewById(R.id.txtSettings);
        txtNotification = (TextView) findViewById(R.id.txtNotification);
        txtSchindlerEmpManagement = (TextView) findViewById(R.id.txtSchindlerEmpManagement);
        navdrawer_Dashboard_menu = (RelativeLayout) findViewById(R.id.navdrawer_Dashboard_menu);
        navdrawer_contacts_menu = (RelativeLayout) findViewById(R.id.navdrawer_contacts_menu);
        navdrawer_my_project_menu = (RelativeLayout) findViewById(R.id.navdrawer_my_project_menu);
        navdrawer_resource_center_menu = (RelativeLayout) findViewById(R.id.navdrawer_resource_center_menu);
        navdrawer_office_managers_menu = (RelativeLayout) findViewById(R.id.navdrawer_office_managers_menu);
        navdrawer_sec_emp_management_menu = (RelativeLayout) findViewById(R.id.navdrawer_sec_emp_management_menu);
        navdrawer_notification_menu = (RelativeLayout) findViewById(R.id.navdrawer_notification_menu);
        navdrawer_settings_menu = (RelativeLayout) findViewById(R.id.navdrawer_settings_menu);
        navdrawer_profile_menu = (RelativeLayout) findViewById(R.id.navdrawer_profile_menu);
        navdrawer_logout_menu = (RelativeLayout) findViewById(R.id.navdrawer_logout_menu);
        rlSearch = (RelativeLayout) findViewById(R.id.rlSearch);
        rlMail = (RelativeLayout) findViewById(R.id.rlMail);
        rlInfo = (RelativeLayout) findViewById(R.id.rlInfo);

        profile_name_text.setText(Pref.getValue(activity, ConstantData.Pref_UserName, ""));

        Picasso.with(activity).load(Pref.getValue(activity, ConstantData.Pref_Profile_pic, "")).into(profile_image);

        ft = getSupportFragmentManager().beginTransaction();

        llMainContainer = (LinearLayout) findViewById(R.id.about_container);
        txtTitle.setText(getString(R.string.txt_dashboard));
        String tag = "Dashboard";
        ProjectFragment projectFragment = new ProjectFragment(context);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment_container, projectFragment, tag);
        fragmentTransaction.commit();

//role id=1234
        ArrayList<UserPermissionViewModel> userPermissionViewModels;

        userPermissionViewModels = ConstantData.userPermissionViewModelArrayList;
        if (userPermissionViewModels != null && userPermissionViewModels.size() > 0) {
            for (int i = 0; i < userPermissionViewModels.size(); i++) {

                if (userPermissionViewModels.get(i).getModulePermissionName().equals("Dashboard")) {
                    navdrawer_Dashboard_menu.setVisibility(View.VISIBLE);
                    txtDashboard.setText(userPermissionViewModels.get(i).getModuleDisplayName());
                }


            }
        }

        rlInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MailboxView mailboxView = new MailboxView(context);
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_fragment_container, mailboxView, "MailBoxView");
                fragmentTransaction.addToBackStack("MailBoxView");
                fragmentTransaction.commit();

            }
        });

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
                        imgMenu.setImageResource(R.drawable.ic_menu_white);
                        closeNavDrawer();
                    } else {
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            });
            navdrawer_Dashboard_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    projectFragment = new ProjectFragment();
//                    backStateName = projectFragment.getClass().getName();
                    txtTitle.setText(getString(R.string.txt_dashboard));
//                    fragmentManager.beginTransaction().replace(R.id.main_fragment_container, projectFragment).commit();
//                    ft.addToBackStack(backStateName);
//                    replaceFragment(new ProjectFragment(), false, FragmentTransaction.TRANSIT_NONE, "");
                    String tag = "Dashboard";
                    ProjectFragment projectFragment1 = new ProjectFragment(SchindlerDrawerActivity.this);
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_fragment_container, projectFragment1, tag);
                    fragmentTransaction.addToBackStack(tag);
                    fragmentTransaction.commit();

                    if (isNavDrawerOpen()) {
                        imgMenu.setImageResource(R.drawable.ic_menu_white);
                        closeNavDrawer();
                    } else {
                        imgMenu.setImageResource(R.drawable.ic_left_arrow);
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            });
            navdrawer_my_project_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtTitle.setText(getString(R.string.txt_my_project));
                    String tag = "My Project";
                    ProjectList projectList = new ProjectList();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_fragment_container, projectList, tag);
                    fragmentTransaction.commit();

                    if (isNavDrawerOpen()) {
                        imgMenu.setImageResource(R.drawable.ic_menu_white);
                        closeNavDrawer();
                    } else {

                        imgMenu.setImageResource(R.drawable.ic_left_arrow);
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            });

            navdrawer_contacts_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    MyProjectContact contacts = new MyProjectContact(SchindlerDrawerActivity.this);
                    txtTitle.setText(getString(R.string.txt_contacts));
//                    backStateName = contacts.getClass().getName();
//                    fragmentManager.beginTransaction().replace(R.id.main_fragment_container, contacts).commit();
//                    ft.addToBackStack(backStateName);

//                    replaceFragment(new MyProjectContact(SchindlerDrawerActivity.this), false, FragmentTransaction.TRANSIT_NONE, "");
                    String tag = "Contacts";
                    MyProjectContact projectContact = new MyProjectContact();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_fragment_container, projectContact, tag);
                    fragmentTransaction.commit();

                    if (isNavDrawerOpen()) {
                        imgMenu.setImageResource(R.drawable.ic_menu_white);
                        closeNavDrawer();
                    } else {

                        imgMenu.setImageResource(R.drawable.ic_left_arrow);
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            });
            navdrawer_resource_center_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    txtTitle.setText(getString(R.string.txt_resource_center));
                    String tag = "Resource Center";
                    ResourceCenter resourceCenter = new ResourceCenter();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_fragment_container, resourceCenter, tag);
                    fragmentTransaction.commit();

                    if (isNavDrawerOpen()) {
                        imgMenu.setImageResource(R.drawable.ic_menu_white);
                        closeNavDrawer();
                    } else {
                        imgMenu.setImageResource(R.drawable.ic_left_arrow);
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            });

            navdrawer_office_managers_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtTitle.setText(getString(R.string.txt_office_managers));
                    String tag = "officeManagers";
                    OfficeManagers officeManagers = new OfficeManagers(context);
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_fragment_container, officeManagers, tag);
                    fragmentTransaction.commit();

                    if (isNavDrawerOpen()) {
                        imgMenu.setImageResource(R.drawable.ic_menu_white);
                        closeNavDrawer();
                    } else {
                        imgMenu.setImageResource(R.drawable.ic_left_arrow);
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            });

            navdrawer_sec_emp_management_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isNavDrawerOpen()) {
                        imgMenu.setImageResource(R.drawable.ic_menu_white);
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
                    txtTitle.setText(getString(R.string.txt_notification));
                    String tag = "Notification";
                    NotificationList notificationList = new NotificationList();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_fragment_container, notificationList, tag);
                    fragmentTransaction.commit();
                    if (isNavDrawerOpen()) {
                        imgMenu.setImageResource(R.drawable.ic_menu_white);
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

                    txtTitle.setText(getString(R.string.txt_settings));
                    String tag = "Settings";
                    SettingsFragment settingsFragment = new SettingsFragment();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_fragment_container, settingsFragment, tag);
                    fragmentTransaction.addToBackStack(tag);
                    fragmentTransaction.commit();

                    if (isNavDrawerOpen()) {
                        imgMenu.setImageResource(R.drawable.ic_menu_white);
                        closeNavDrawer();
                    } else {
                        imgMenu.setImageResource(R.drawable.ic_left_arrow);
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            });


            navdrawer_profile_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tag = "User Profile";
                    UserProfileFragment userProfileFragment = new UserProfileFragment(context);
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_fragment_container, userProfileFragment, tag);
                    fragmentTransaction.addToBackStack(tag);
                    fragmentTransaction.commit();

                    if (isNavDrawerOpen()) {
                        imgMenu.setImageResource(R.drawable.ic_menu_white);
                        closeNavDrawer();
                    } else {
                        imgMenu.setImageResource(R.drawable.ic_left_arrow);
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            });

            navdrawer_logout_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isNavDrawerOpen()) {
                        imgMenu.setImageResource(R.drawable.ic_menu_white);
                        closeNavDrawer();
                        setLogOut();
                    } else {
                        imgMenu.setImageResource(R.drawable.ic_left_arrow);
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            });

            profile_name_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayPopupWindow(v);
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
                    imgMenu.setImageResource(R.drawable.ic_menu_white);
                } else {
                    imgMenu.setImageResource(R.drawable.ic_menu_white);
                }
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                onNavDrawerSlide(slideOffset);
            }
        });

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
//        createNavDrawerItems();

    }

    public void displayPopupWindow(View anchorView) {
        popup = new PopupWindow(activity);
        View layout = activity.getLayoutInflater().inflate(R.layout.drawer_popup_menu, (ViewGroup) findViewById(R.id.menu_layout));

        // TODO
        final TextView txtMyProfile, txtChangePassword, txtLogout;
        final RelativeLayout rlLogout, rlForgotPassword, rlMyProfile;

        txtMyProfile = (TextView) layout.findViewById(R.id.txtMyProfile);
        txtChangePassword = (TextView) layout.findViewById(R.id.txtChangePassword);
        txtLogout = (TextView) layout.findViewById(R.id.txtLogout);
        rlLogout = (RelativeLayout) layout.findViewById(R.id.rlLogout);
        rlForgotPassword = (RelativeLayout) layout.findViewById(R.id.rlForgotPassword);
        rlMyProfile = (RelativeLayout) layout.findViewById(R.id.rlMyProfile);

        rlMyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNavDrawerOpen()) {
                    imgMenu.setImageResource(R.drawable.ic_menu_white);
                    closeNavDrawer();
//                    UserProfileFragment userProfileFragment = new UserProfileFragment();
//                    txtTitle.setText(getString(R.string.txt_profile));
//                    fragmentManager.beginTransaction().replace(R.id.main_fragment_container, userProfileFragment).commit();
//                    replaceFragment(new UserProfileFragment(), false, FragmentTransaction.TRANSIT_NONE, "");

                    txtTitle.setText("Profile");
                    String tag = "User Profile";
                    UserProfileFragment userProfileFragment = new UserProfileFragment();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_fragment_container, userProfileFragment, tag);
                    fragmentTransaction.addToBackStack(tag);
                    fragmentTransaction.commit();


                } else {
                    imgMenu.setImageResource(R.drawable.ic_left_arrow);
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
                popup.dismiss();
            }
        });

        rlForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   rlUpcoming.setBackgroundColor(Color.parseColor("#C61E2C"));
                txtUpComingEvent.setTextColor(Color.parseColor("#ffffff"));
                rlPast.setBackgroundColor(Color.parseColor("#ffffff"));
                txtPastEvent.setTextColor(Color.parseColor("#000000"));*/

                if (isNavDrawerOpen()) {
                    imgMenu.setImageResource(R.drawable.ic_menu_white);
                    closeNavDrawer();
                    initiateForgotPasswordDialog();
                } else {
                    imgMenu.setImageResource(R.drawable.ic_left_arrow);
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
                popup.dismiss();
            }
        });


        rlLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNavDrawerOpen()) {
                    imgMenu.setImageResource(R.drawable.ic_menu_white);
                    closeNavDrawer();
                    setLogOut();
                }
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


    @Override
    public void onResume() {
        super.onResume();
        MyApplication.getInstance().trackScreenView("SchindlerDrawerActivity Screen");
        // configure the fragment's top clearance to take our overlaid controls (Action Bar
        // and spinner box) into account.
        int actionBarSize = UIUtils.calculateActionBarSize(activity);
        DrawShadowFrameLayout drawShadowFrameLayout =
                (DrawShadowFrameLayout) findViewById(R.id.main_content);
        if (drawShadowFrameLayout != null) {
            drawShadowFrameLayout.setShadowTopOffset(actionBarSize);
        }
        setContentTopClearance(actionBarSize);

        deviceId = FirebaseInstanceId.getInstance().getToken();

        if (deviceId == null) {
            deviceId = "xyz";
        } else if (deviceId.trim().isEmpty()) {
            deviceId = "xyz";
        }

        ConstantData.SHARED_PREF_username = getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        String uName = ConstantData.SHARED_PREF_username.getString("USERNAME", "");

        ConstantData.SHARED_PREF_password = getSharedPreferences(PREF_PWD, Activity.MODE_PRIVATE);
        String pwd = ConstantData.SHARED_PREF_password.getString("PASSWORD", "");
        get_Login(uName, pwd, deviceId);

    }

    public void get_Login(final String uname, final String pwd, final String deviceId) {
        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        MySingleton.getInstance(activity).getRequestQueue().start();
        final String currentDate = CommonFunctionality.CurrentDate();

        RequestQueue mRequestQueue = Volley.newRequestQueue(activity);
        StringRequest sr = new StringRequest(Request.Method.POST, ConstantData.loginUrl, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jArray;
                JSONArray jsonArray = null;
                JSONObject venuesObj = null;
//                JSONObject jObject = null;
                JSONObject venues;
                try {
                    venuesObj = new JSONObject(response);
                    String status = venuesObj.getString("Status");
                    String message = venuesObj.getString("Message");

                    Log.error("Login API : status", "" + status);
                    Log.error("Login API : message", "" + message);

                    if (status.equals("1")) {
                        progressDialog.dismiss();

//TODO
//                        Gson gson = new Gson();
//                        String jsonLoginData = gson.toJson(venuesObj.toString()+);
//                        Log.e("JsonLoginData",jsonLoginData);
//                        Pref.setValue(mActivity,ConstantData.PREF_JSON_LOGIN_DATA,jsonLoginData);

                        ConstantData.loginSuccess = true;
                        venues = venuesObj.getJSONObject("Data");

                        String userId = venues.opt("UserID").toString();
                        ConstantData.userId = userId;
                        Pref.setValue(activity, ConstantData.Pref_UserId, userId);

                        String roleID = venues.opt("RoleID").toString();
                        ConstantData.ROLEID = roleID;
                        Pref.setValue(activity, ConstantData.Pref_RoleId, roleID);

                        String email = venues.opt("Email").toString();
                        ConstantData.EMAIL = email;
                        Pref.setValue(activity, ConstantData.Pref_Email, email);

                        String passwordEncrypt = venues.opt("Password").toString();

                        String passwordSalt = venues.opt("PasswordSalt").toString();

                        String firstName = venues.opt("FirstName").toString();
                        ConstantData.FIRSTNAME = firstName;
                        Pref.setValue(activity, ConstantData.Pref_UserName, firstName);
                        Log.print("Pref_UserName", "" + Pref.getValue(activity, ConstantData.Pref_UserName, ""));

                        String middleName = venues.opt("MiddleName").toString();

                        String lastName = venues.opt("LastName").toString();
                        ConstantData.LASTNAME = lastName;

                        String companyID = venues.opt("CompanyID").toString();
                        ConstantData.COMPANYID = companyID;
                        Pref.setValue(activity, ConstantData.Pref_CompanyId, companyID);


                        String contactNumber = venues.opt("ContactNumber").toString();
                        ConstantData.Pref_Phone = contactNumber;
                        ConstantData.CONTACTNUMBER = contactNumber;
                        Pref.setValue(activity, ConstantData.Pref_Phone, contactNumber);
                        Log.print("Pref_Phone", "" + Pref.getValue(activity, ConstantData.Pref_Phone, ""));


                        String companyName = venues.opt("CompanyName").toString();
                        String address1 = venues.opt("Address1").toString();
                        String address2 = venues.opt("Address2").toString();

                        String full_address = address1;
                        Pref.setValue(activity, ConstantData.Pref_Address, full_address);
                        Log.print("Pref_Address", "" + Pref.getValue(activity, ConstantData.Pref_Address, ""));

                        String postalCode = venues.opt("PostalCode").toString();
                        Pref.setValue(activity, ConstantData.Pref_Zip, postalCode);
                        Log.print("postalCode", "" + Pref.getValue(activity, ConstantData.Pref_Zip, ""));

                        String city = venues.opt("City").toString();
                        Pref.setValue(activity, ConstantData.Pref_City, city);
                        Log.print("Pref_City", "" + Pref.getValue(activity, ConstantData.Pref_City, ""));

                        String state = venues.opt("State").toString();
                        Pref.setValue(activity, ConstantData.Pref_State, state);
                        Log.print("Pref_State", "" + Pref.getValue(activity, ConstantData.Pref_State, ""));


                        String stateID = venues.opt("StateID").toString();
                        Pref.setValue(activity, ConstantData.Pref_StateID, stateID);
                        Log.print("Pref_StateID", "" + Pref.getValue(activity, ConstantData.Pref_StateID, ""));

                        String accountStatus = venues.opt("AccountStatus").toString();

                        String profilePic = venues.opt("ProfilePic").toString();
                        ConstantData.PROFILEPIC = profilePic;
                        Pref.setValue(activity, ConstantData.Pref_Profile_pic, profilePic);
                        Log.print("Pref_Profile_pic", "" + Pref.getValue(activity, ConstantData.Pref_Profile_pic, ""));

                        ConstantData.companyId = ConstantData.COMPANYID;
                        ConstantData.email = email;

                        String designationId = venues.opt("DesignationId").toString();
                        Pref.setValue(activity, ConstantData.Pref_Designation_ID, designationId);
                        Log.print("Pref_Designation_ID", "" + Pref.getValue(activity, ConstantData.Pref_Designation_ID, ""));

                        String designation = venues.opt("Designation").toString();
                        Pref.setValue(activity, ConstantData.Pref_Designation, designation);
                        Log.print("Pref_Designation", "" + Pref.getValue(activity, ConstantData.Pref_Designation, ""));

                        jsonArray = venues.optJSONArray("userpermissionviewModel");
                        ConstantData.userPermissionViewModelArrayList = new ArrayList<>();
                        if (jsonArray != null && jsonArray.length() > 0) {

                            JSONObject jsonObject;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonObject = jsonArray.getJSONObject(i);
                                UserPermissionViewModel userPermissionViewModel = new UserPermissionViewModel();
                                userPermissionViewModel.setUserId(jsonObject.optString("UserID"));
                                userPermissionViewModel.setModuleId(jsonObject.optString("ModuleId"));
                                userPermissionViewModel.setModuleName(jsonObject.optString("ModuleName"));
                                userPermissionViewModel.setHasViewPermission(jsonObject.optString("HasViewPermission"));
                                userPermissionViewModel.setHasAddPermission(jsonObject.optString("HasAddPermission"));
                                userPermissionViewModel.setHasEditPermission(jsonObject.optString("HasEditPermission"));
                                userPermissionViewModel.setHasDeletePermission(jsonObject.optString("HasDeletePermission"));

                                userPermissionViewModel.setHasPrintPermission(jsonObject.optString("HasPrintPermission"));
                                userPermissionViewModel.setAppAuditID(jsonObject.optString("appAuditID"));
                                userPermissionViewModel.setSessionID(jsonObject.optString("SessionID"));
                                userPermissionViewModel.setCreatedBy(jsonObject.optString("CreatedBy"));
                                userPermissionViewModel.setUpdatedBy(jsonObject.optString("UpdatedBy"));
                                userPermissionViewModel.setIsUpdate(jsonObject.optString("IsUpdate"));

                                userPermissionViewModel.setModuleURL(jsonObject.optString("ModuleURL"));
                                userPermissionViewModel.setParentID(jsonObject.optString("ParentID"));
                                userPermissionViewModel.setModuleDisplayName(jsonObject.optString("ModuleDisplayName"));
                                userPermissionViewModel.setSequenceNO(jsonObject.optString("SequenceNO"));
                                userPermissionViewModel.setRestricted(jsonObject.optString("Restricted"));
                                userPermissionViewModel.setModulePermissionName(jsonObject.optString("ModulePermissionName"));
                                userPermissionViewModel.setVisibleOnMenu(jsonObject.optString("VisibleOnMenu"));
                                userPermissionViewModel.setHasChildMenu(jsonObject.optString("hasChildMenu"));


                                if (jsonObject.has("SubMenus")) {
                                    jArray = jsonObject.optJSONArray("SubMenus");
                                    ArrayList<UserPermissionSubMenuModel> userPermissionSubMenuModelArrayList = new ArrayList<>();
                                    if (jArray != null && jArray.length() > 0) {
                                        JSONObject jObject;

                                        for (int j = 0; j < jArray.length(); j++) {
                                            jObject = jArray.getJSONObject(j);

                                            UserPermissionSubMenuModel userPermissionSubMenuModel = new UserPermissionSubMenuModel();
                                            userPermissionSubMenuModel.setUserId(jObject.optString("UserID"));
                                            userPermissionSubMenuModel.setModuleId(jObject.optString("ModuleId"));
                                            userPermissionSubMenuModel.setModuleName(jObject.optString("ModuleName"));
                                            userPermissionSubMenuModel.setHasViewPermission(jObject.optString("HasViewPermission"));
                                            userPermissionSubMenuModel.setHasAddPermission(jObject.optString("HasAddPermission"));
                                            userPermissionSubMenuModel.setHasEditPermission(jObject.optString("HasEditPermission"));
                                            userPermissionSubMenuModel.setHasDeletePermission(jObject.optString("HasDeletePermission"));

                                            userPermissionSubMenuModel.setHasPrintPermission(jObject.optString("HasPrintPermission"));
                                            userPermissionSubMenuModel.setAppAuditID(jObject.optString("appAuditID"));
                                            userPermissionSubMenuModel.setSessionID(jObject.optString("SessionID"));
                                            userPermissionSubMenuModel.setCreatedBy(jObject.optString("CreatedBy"));
                                            userPermissionSubMenuModel.setUpdatedBy(jObject.optString("UpdatedBy"));
                                            userPermissionSubMenuModel.setIsUpdate(jObject.optString("IsUpdate"));

                                            userPermissionSubMenuModel.setModuleURL(jObject.optString("ModuleURL"));
                                            userPermissionSubMenuModel.setParentID(jObject.optString("ParentID"));
                                            userPermissionSubMenuModel.setModuleDisplayName(jObject.optString("ModuleDisplayName"));
                                            userPermissionSubMenuModel.setSequenceNO(jObject.optString("SequenceNO"));
                                            userPermissionSubMenuModel.setRestricted(jObject.optString("Restricted"));
                                            userPermissionSubMenuModel.setModulePermissionName(jObject.optString("ModulePermissionName"));
                                            userPermissionSubMenuModel.setVisibleOnMenu(jObject.optString("VisibleOnMenu"));
                                            userPermissionSubMenuModel.setHasChildMenu(jObject.optString("hasChildMenu"));
                                            userPermissionSubMenuModelArrayList.add(userPermissionSubMenuModel);
                                        }
                                        userPermissionViewModel.setUserPermissionSubMenuModels(userPermissionSubMenuModelArrayList);
                                    }
                                }
                                ConstantData.userPermissionViewModelArrayList.add(userPermissionViewModel);
                            }
                        }

                        final String PREF_NAME = "PREF_NAME_uid";
                        ConstantData.SHARED_PREF_userid = activity.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
                        final SharedPreferences.Editor editorTStamp = ConstantData.SHARED_PREF_userid.edit();
                        editorTStamp.putString("USERID", ConstantData.userId);
                        editorTStamp.commit();

                        final String PREF_NAME_COMPANYID = "PREF_NAME_CompanyId";
                        ConstantData.SHARED_PREF_CompanyId = activity.getSharedPreferences(PREF_NAME_COMPANYID, Activity.MODE_PRIVATE);
                        final SharedPreferences.Editor editorCTStamp = ConstantData.SHARED_PREF_CompanyId.edit();
                        editorCTStamp.putString("COMPANYID", ConstantData.companyId);
                        editorCTStamp.commit();

                        final String PREF_NAME_USER_EMAIL = "PREF_NAME_user_email";
                        ConstantData.SHARED_PREF_User_Email = activity.getSharedPreferences(PREF_NAME_USER_EMAIL, Activity.MODE_PRIVATE);
                        final SharedPreferences.Editor editoUEStamp = ConstantData.SHARED_PREF_User_Email.edit();
                        editoUEStamp.putString("EMAIL", ConstantData.email);
                        editoUEStamp.commit();

                        Pref.setValue(activity, ConstantData.PREF_LOGIN_OR_NOT, "True");

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();

                        if (message.equals("Invalid LoginID or Password")) {
                            Pref.setValue(SchindlerDrawerActivity.this, ConstantData.PREF_LOGIN_OR_NOT, "false");
                            Pref.setValue(SchindlerDrawerActivity.this, ConstantData.Pref_Address, "");
                            Pref.setValue(SchindlerDrawerActivity.this, ConstantData.Pref_UserId, "");
                            Pref.setValue(SchindlerDrawerActivity.this, ConstantData.Pref_RoleId, "");
                            Pref.setValue(SchindlerDrawerActivity.this, ConstantData.Pref_Email, "");
                            Pref.setValue(SchindlerDrawerActivity.this, ConstantData.Pref_UserName, "");

                            isFirstTimeSP = getSharedPreferences(PREF_ISFIRSTTIME, Activity.MODE_PRIVATE);
                            final SharedPreferences.Editor editorFirstTime = isFirstTimeSP.edit();
                            editorFirstTime.putBoolean("ISFIRSTTIME", true);
                            editorFirstTime.commit();

                            isFirstTimeSP = getSharedPreferences(PREF_ISFIRSTTIME, MODE_PRIVATE);
                            isFirstTime = isFirstTimeSP.getBoolean("ISFIRSTTIME", true);

                            Intent intent = new Intent(SchindlerDrawerActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
//                Toast.makeText(activity, "Errorrrrrrr", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                NetworkResponse response = volleyError.networkResponse;
                if (volleyError instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        Log.debug("res,", res);
//                        Toast.makeText(mActivity, "res" + res, Toast.LENGTH_LONG).show();
                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    }
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Email", uname);
                params.put("Password", pwd);
                params.put("DeviceID", deviceId);
                params.put("DeviceType", "Android");

                Log.print("Login API : Email", "" + uname);
                Log.print("Login API : Password", "" + pwd);
                Log.print("Login API : deviceId", "" + deviceId);
                Log.print("Login API : DeviceType", "" + "Android");

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type", "application/x-www-form-urlencoded");
//                params.put("Content-Type", "application/json; charset=utf-8");
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mRequestQueue.add(sr);
    }

    private void setContentTopClearance(int clearance) {
        if (llMainContainer != null) {
            llMainContainer.setPadding(llMainContainer.getPaddingLeft(), clearance,
                    llMainContainer.getPaddingRight(), llMainContainer.getPaddingBottom());
        }
    }

    @Override
    public void onBackPressed() {
        if (ConstantData.forEmailAttachment == false) {
            super.onBackPressed();
            try {
                Pref.setValue(SchindlerDrawerActivity.this, ConstantData.PREF_LOGIN_OR_NOT, "True");
            } catch (Exception e) {
                e.printStackTrace();
            }
            ConstantData.forEmailAttachment = false;
            overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
        }
        if (isNavDrawerOpen()) {
            closeNavDrawer();
        }
       /* else if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStackImmediate();
        } else {
            super.onBackPressed();
        }*/

//        if(getFragmentManager().getBackStackEntryCount() > 0){
//            getFragmentManager().popBackStackImmediate();
//        }
//        else{
//            super.onBackPressed();
//        }
    }

    protected void closeNavDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void selectItemFragment(int position) {
        switch (position) {
            case 0:
//                SchindlerDashboard schindlerDashboard = new SchindlerDashboard(SchindlerDrawerActivity.this);
                String tag = "Dashboard";
                ProjectFragment projectFragment = new ProjectFragment(SchindlerDrawerActivity.this);
                fragmentManager.beginTransaction().add(R.id.main_fragment_container, projectFragment).commit();
                break;
            case 1:
                ProjectList projectList = new ProjectList(SchindlerDrawerActivity.this);
                fragmentManager.beginTransaction().add(R.id.main_fragment_container, projectList).commit();
                break;
            case 2:
                MyProjectContact contacts = new MyProjectContact(SchindlerDrawerActivity.this);
                fragmentManager.beginTransaction().add(R.id.main_fragment_container, contacts).commit();
                break;

            case 3:
                ResourceCenter resourceCenter = new ResourceCenter(SchindlerDrawerActivity.this);
                fragmentManager.beginTransaction().add(R.id.main_fragment_container, resourceCenter).commit();
                break;

            case 4:
                AccountManagement accountManagement = new AccountManagement(SchindlerDrawerActivity.this);
                fragmentManager.beginTransaction().add(R.id.main_fragment_container, accountManagement).commit();
                break;

            case 5:
                EmployeeManagement employeeManagement = new EmployeeManagement(SchindlerDrawerActivity.this);
                fragmentManager.beginTransaction().add(R.id.main_fragment_container, employeeManagement).commit();
                break;

            case 6:
                break;

            case 7:
                Campaign_Fragment fragmentCamp = new Campaign_Fragment(SchindlerDrawerActivity.this);
                fragmentManager.beginTransaction().add(R.id.main_fragment_container, fragmentCamp).commit();
                break;
            case 8:
                ContactsFragment contactFragment = new ContactsFragment();
                fragmentManager.beginTransaction().add(R.id.main_fragment_container, contactFragment).commit();
                break;
            case 9:
                EmailHistoryFragment emailHistoryFragment = new EmailHistoryFragment();
                fragmentManager.beginTransaction().replace(R.id.main_fragment_container, emailHistoryFragment).commit();
                break;
            case 10:
                notificationFragment = new NotificationFragment();
                fragmentManager.beginTransaction().replace(R.id.main_fragment_container, notificationFragment).commit();
                break;
            case 11:
                SettingsFragment settingsFragment = new SettingsFragment();
                fragmentManager.beginTransaction().replace(R.id.main_fragment_container, settingsFragment).commit();
                break;
            case 12:
                UserProfileFragment userProfileFragment = new UserProfileFragment();
                fragmentManager.beginTransaction().replace(R.id.main_fragment_container, userProfileFragment).commit();
                // logoutDialog();
                break;
            case 13:
                SharedPreferences.Editor editor = ConstantData.sPreferenceLoginOrNot.edit();
                editor.clear();
                editor.putBoolean(ConstantData.LOGINORNOT, false);
                editor.commit();

                Pref.setValue(SchindlerDrawerActivity.this, ConstantData.PREF_LOGIN_OR_NOT, "false");

                isFirstTimeSP = getSharedPreferences(PREF_ISFIRSTTIME, Activity.MODE_PRIVATE);
                final SharedPreferences.Editor editorFirstTime = isFirstTimeSP.edit();
                editorFirstTime.putBoolean("ISFIRSTTIME", true);
                editorFirstTime.commit();

                isFirstTimeSP = getSharedPreferences(PREF_ISFIRSTTIME, MODE_PRIVATE);
                isFirstTime = isFirstTimeSP.getBoolean("ISFIRSTTIME", true);

                Intent intent = new Intent(SchindlerDrawerActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                this.finish();
                break;
        }

//        mDrawerList.setItemChecked(position, true);
        setTitle(titles[position]);
//        mDrawerLayout.closeDrawer(mDrawerList);
    }

    public void refreshFragment() {
        FragmentManager fragmentManager;
        fragmentManager = getSupportFragmentManager();

        ContactsFragment contactFragment = new ContactsFragment();
        fragmentManager.beginTransaction().replace(R.id.main_fragment_container, contactFragment).commit();

    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }


    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConstantData.allSelected = false;
        ConstantData.mediaSelection = false;
        ConstantData.fromSearch = false;
        overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
        // finish();
    }

    // Subclasses can override this for custom behavior
    protected void onNavDrawerStateChanged(boolean isOpen, boolean isAnimating) {
        if (mActionBarAutoHideEnabled && isOpen) {
            autoShowOrHideActionBar(true);
        }
    }

    protected void autoShowOrHideActionBar(boolean show) {
        if (show == mActionBarShown) {
            return;
        }
        mActionBarShown = show;
        onActionBarAutoShowOrHide(show);
    }

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

    protected boolean isNavDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    protected void onNavDrawerSlide(float offset) {
    }

    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_INVALID;
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getActionBarToolbar();
    }

    protected Toolbar getActionBarToolbar() {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(R.id.tool_bar);

            imgMenu = (ImageView) findViewById(R.id.imgMenu);
            txtTitle = (TextView) findViewById(R.id.txtTitle);
            rlSearch = (RelativeLayout) findViewById(R.id.rlSearch);
            rlMail = (RelativeLayout) findViewById(R.id.rlMail);
            rlInfo = (RelativeLayout) findViewById(R.id.rlInfo);

            if (mActionBarToolbar != null) {
                // Depending on which version of Android you are on the Toolbar or the ActionBar may be
                // active so the a11y description is set here.
                setSupportActionBar(mActionBarToolbar);
            }
        }
        return mActionBarToolbar;
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

    private void setLogOut() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        // set title
        alertDialogBuilder.setTitle(getString(R.string.app_name));
        // set dialog message
        alertDialogBuilder.setMessage(getString(R.string.want_to_logout)).setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if (isNavDrawerOpen()) {
                                    imgMenu.setImageResource(R.drawable.ic_menu_white);
                                    closeNavDrawer();
                                }
                                Pref.setValue(SchindlerDrawerActivity.this, ConstantData.PREF_LOGIN_OR_NOT, "false");
                                Pref.setValue(SchindlerDrawerActivity.this, ConstantData.Pref_Address, "");
                                Pref.setValue(SchindlerDrawerActivity.this, ConstantData.Pref_UserId, "");
                                Pref.setValue(SchindlerDrawerActivity.this, ConstantData.Pref_RoleId, "");
                                Pref.setValue(SchindlerDrawerActivity.this, ConstantData.Pref_Email, "");
                                Pref.setValue(SchindlerDrawerActivity.this, ConstantData.Pref_UserName, "");

                                isFirstTimeSP = getSharedPreferences(PREF_ISFIRSTTIME, Activity.MODE_PRIVATE);
                                final SharedPreferences.Editor editorFirstTime = isFirstTimeSP.edit();
                                editorFirstTime.putBoolean("ISFIRSTTIME", true);
                                editorFirstTime.commit();

                                isFirstTimeSP = getSharedPreferences(PREF_ISFIRSTTIME, MODE_PRIVATE);
                                isFirstTime = isFirstTimeSP.getBoolean("ISFIRSTTIME", true);

                                Intent intent = new Intent(SchindlerDrawerActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();


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

    private void setReloginForChangePassword() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        // set title
        alertDialogBuilder.setTitle(getString(R.string.app_name));
        // set dialog message
        alertDialogBuilder.setMessage(getString(R.string.relogin_for_change_pass)).setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Pref.setValue(SchindlerDrawerActivity.this, ConstantData.PREF_LOGIN_OR_NOT, "false");

                                isFirstTimeSP = getSharedPreferences(PREF_ISFIRSTTIME, Activity.MODE_PRIVATE);
                                final SharedPreferences.Editor editorFirstTime = isFirstTimeSP.edit();
                                editorFirstTime.putBoolean("ISFIRSTTIME", true);
                                editorFirstTime.commit();

                                isFirstTimeSP = getSharedPreferences(PREF_ISFIRSTTIME, MODE_PRIVATE);
                                isFirstTime = isFirstTimeSP.getBoolean("ISFIRSTTIME", true);

                                Intent intent = new Intent(SchindlerDrawerActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        }
                );

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    private void initiateForgotPasswordDialog() {
        try {
            dialogChangePassword = new Dialog(activity, android.R.style.Theme_NoTitleBar);
            dialogChangePassword.setContentView(R.layout.change_password_dialog);
            final TextView txtValidationOne, txtValidationTwo, txtValidationThree, txtValidationfour;
            final EditText edtNewPass = (EditText) dialogChangePassword.findViewById(R.id.edtNewPass);
            final EditText edtNewConfirmPass = (EditText) dialogChangePassword.findViewById(R.id.edtNewConfirmPass);
            final EditText edtOldPass = (EditText) dialogChangePassword.findViewById(R.id.edtOldPass);
            TextView txtSave = (TextView) dialogChangePassword.findViewById(R.id.txtSave);
            final RelativeLayout rlayoutPwdValidation = (RelativeLayout) dialogChangePassword.findViewById(R.id.rlayoutPwdValidation);
            TextView txtCancel = (TextView) dialogChangePassword.findViewById(R.id.txtCancel);
            txtValidationOne = (TextView) dialogChangePassword.findViewById(R.id.txtValidationOne);
            txtValidationTwo = (TextView) dialogChangePassword.findViewById(R.id.txtValidationTwo);
            txtValidationThree = (TextView) dialogChangePassword.findViewById(R.id.txtValidationThree);
            txtValidationfour = (TextView) dialogChangePassword.findViewById(R.id.txtValidationfour);

            RelativeLayout rl_listDialog = (RelativeLayout) dialogChangePassword.findViewById(R.id.rl_listDialog);

            dialogChangePassword.getWindow().setBackgroundDrawableResource(R.color.translucent_black);

            emalId = Pref.getValue(activity, ConstantData.Pref_Email, "").toString();
            userId = Pref.getValue(activity, ConstantData.Pref_UserId, "").toString();
            roleId = Pref.getValue(activity, ConstantData.Pref_RoleId, "").toString();
            compId = Pref.getValue(activity, ConstantData.Pref_CompanyId, "").toString();
            deviceId = Pref.getValue(activity, ConstantData.Pref_Device_Id, "").toString();

            edtNewPass.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    edtNewPass.requestFocus();

                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        if ((isCapitalLetter) && (isLetter) && (isNumber) && (isEightCharacter)) {
                            rlayoutPwdValidation.setVisibility(View.GONE);
                        } else {
                            rlayoutPwdValidation.setVisibility(View.VISIBLE);
                        }
                    }
                    return false;
                }
            });

            edtNewConfirmPass.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    edtNewConfirmPass.requestFocus();

                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        if ((isCapitalLetter) && (isLetter) && (isNumber) && (isEightCharacter)) {
                            rlayoutPwdValidation.setVisibility(View.GONE);
                        } else {
                            rlayoutPwdValidation.setVisibility(View.VISIBLE);
                        }
                    }
                    return false;
                }
            });

            edtNewPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        rlayoutPwdValidation.setVisibility(View.GONE);
                        edtNewConfirmPass.requestFocus();
                        return true;
                    }
                    return false;
                }
            });


            edtNewPass.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    isEightCharacter = edtNewPass.getText().toString().length() >= 8;
                    isCapitalLetter = edtNewPass.getText().toString().matches(".*[A-Z].*");
                    isLetter = edtNewPass.getText().toString().matches(".*[a-z].*");
                    isNumber = edtNewPass.getText().toString().matches(".*[0-9].*");

              /*  if((isCapitalLetter) && (isLetter) && (isNumber) && (isEightCharacter)){
                    rlayoutPwdValidation.setVisibility(View.GONE);
                }else{
                    rlayoutPwdValidation.setVisibility(View.VISIBLE);
                }*/

                    if (isLetter) {
                        txtValidationTwo.setTextColor(Color.GREEN);
                    } else {
                        txtValidationTwo.setTextColor(Color.RED);
                    }

                    if (isNumber) {
                        txtValidationfour.setTextColor(Color.GREEN);
                    } else {
                        txtValidationfour.setTextColor(Color.RED);
                    }

                    if (isCapitalLetter) {
                        txtValidationThree.setTextColor(Color.GREEN);
                    } else {
                        txtValidationThree.setTextColor(Color.RED);
                    }
                    if (isEightCharacter) {
                        txtValidationOne.setTextColor(Color.GREEN);
                    } else {
                        txtValidationOne.setTextColor(Color.RED);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });


            txtSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (edtOldPass.getText().toString().trim().length() <= 0) {
                        Toast.makeText(SchindlerDrawerActivity.this, getString(R.string.please_enter_old_password), Toast.LENGTH_LONG).show();
                    } else if (edtNewPass.getText().toString().trim().length() <= 0) {
                        Toast.makeText(SchindlerDrawerActivity.this, getString(R.string.please_enter_new_password), Toast.LENGTH_LONG).show();
                    } else if (edtNewConfirmPass.getText().toString().trim().length() <= 0) {
                        Toast.makeText(SchindlerDrawerActivity.this, getString(R.string.please_enter_confirm_password), Toast.LENGTH_LONG).show();
                    } else if (!ViewUtils.equals(edtNewPass.getText().toString().trim(), edtNewConfirmPass.getText().toString().trim())) {
                        Toast.makeText(SchindlerDrawerActivity.this, getString(R.string.password_do_not_match), Toast.LENGTH_LONG).show();
                    } else if (!Validation.isPasswordValid(edtNewPass.getText().toString().trim())) {
                        createDialogForPassword();
                    } else {
                        if (WebInterface.isOnline(activity)) {
                            ChangedPassword(v, edtOldPass.getText().toString().trim(), edtNewPass.getText().toString().trim());
                        } else {
//                            ViewUtils.showSnackbar(v, getString(R.string.no_internet_connection));
                            Toast.makeText(SchindlerDrawerActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });

            edtOldPass.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if ((edtOldPass.getText().toString() != null) && (edtOldPass.getText().toString().length() == 0)) {
                        edtOldPass.setError(null);
                    }
                }
            });

            edtNewPass.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if ((edtNewPass.getText().toString() != null) && (edtNewPass.getText().toString().length() == 0)) {
                        edtNewPass.setError(null);
                    }
                }
            });


            txtCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogChangePassword.dismiss();
                }
            });


            rl_listDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogChangePassword.dismiss();
                }
            });

            dialogChangePassword.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ChangedPassword(final View v, final String oldPassword, final String newPassword) {
        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        MySingleton.getInstance(activity).getRequestQueue().start();

        StringRequest sr = new StringRequest(Request.Method.POST, ConstantData.schindlerURL + CHANGE_PASSWORD_FOR_USER, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jArray;
                JSONArray jsonArray = null;
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("Status");
                    String message = jsonObject.getString("Message");
                    Log.print("Status,CHANGE_PASSWORD_FOR_USER", status);
                    Log.print("response,CHANGE_PASSWORD_FOR_USER", response);
                    Log.print("Message,CHANGE_PASSWORD_FOR_USER", message);
                    if (status.equals("1")) {
                        progressDialog.dismiss();
//                        ViewUtils.showSnackbar(v, message);
                        Toast.makeText(SchindlerDrawerActivity.this, message, Toast.LENGTH_LONG).show();
                        setReloginForChangePassword();

                    } else {
                        progressDialog.dismiss();
//                        ViewUtils.showSnackbar(v, message);
                        Toast.makeText(SchindlerDrawerActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(activity, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                NetworkResponse response = volleyError.networkResponse;
                if (volleyError instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        Log.print("res,", res);
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("OldPassword", oldPassword);
                params.put("NewPassword", newPassword);
                params.put("UserID", userId);
                params.put("Email", emalId);
                params.put("DeviceID", deviceId);

                Log.print("OldPassword,", oldPassword);
                Log.print("newPassword,", newPassword);
                Log.print("userId,", userId);
                Log.print("emalId,", emalId);
                Log.print("deviceId,", deviceId);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }
        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(activity);
        sr.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(sr);
    }

    private AlertDialog.Builder createDialogForPassword() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext(), R.style.MyAlertDialogStyle);
        builder.setTitle(getString(R.string.password_policy_title));
        builder.setMessage(getString(R.string.password_policy));
        builder.setPositiveButton("OK", null);
        builder.show();

        return builder;
    }

}
