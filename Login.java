package com.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;
import com.R;
import com.ui.widget.CustomEditTextView;
import com.ui.widget.CustomTextView;
import com.util.AppConstants;
import com.util.LogUtils;
import com.util.Pref;
import com.util.WebInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

import static com.util.LogUtils.LOGD;
import static com.util.LogUtils.LOGE;
import static com.util.ViewUtils.KeyboadOff;
import static com.util.ViewUtils.checkEmail;


/**
 * Created by Krishan on 11/02/16.
 */
public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private CustomEditTextView edtEmail, edtPassword;
    private CustomTextView txtForgotPassword, txtSignUp, txtSignIn, txtShowPassword;
    private Activity mActivity;
    private InputMethodManager objectInputMethod;
    private boolean show_password = true;
    private ImageView imgGooglePlus, imgFaceBook;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "Login";
    private ProgressDialog mProgressDialog;
    public GoogleApiClient mGoogleApiClient;
    public CallbackManager callbackManager;
    private LoginButton loginButtonFacebook;
    private String mId, mName, mEmail, mMobileNumber = "", mProfile, mGender, mGenderId, screenname, location, timeZone, description, mFollowersCount, mFavouritesCount;
    private TwitterLoginButton twitterButton;
    private TwitterSession session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mActivity = Login.this;
        FacebookSdk.sdkInitialize(mActivity);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(getString(R.string.twitter_consumer_key), getString(R.string.twitter_consumer_secret));
        Fabric.with(this, new Twitter(authConfig));

        setContentView(R.layout.login);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

        imgGooglePlus = (ImageView) findViewById(R.id.imgGooglePlus);
        imgFaceBook = (ImageView) findViewById(R.id.imgFaceBook);
        loginButtonFacebook = (LoginButton) findViewById(R.id.loginButtonFacebook);

        edtEmail = (CustomEditTextView) findViewById(R.id.edtEmail);
        edtPassword = (CustomEditTextView) findViewById(R.id.edtPassword);

        txtForgotPassword = (CustomTextView) findViewById(R.id.txtForgotPassword);
        txtSignUp = (CustomTextView) findViewById(R.id.txtSignUp);
        txtSignIn = (CustomTextView) findViewById(R.id.txtSignIn);
        txtShowPassword = (CustomTextView) findViewById(R.id.txtShowPassword);

        txtForgotPassword.setPaintFlags(txtForgotPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        String mString = String.valueOf(Html.fromHtml(getString(R.string.dont_have_an_account)));
        txtSignUp.setMovementMethod(LinkMovementMethod.getInstance());
        txtSignUp.setText(mString, TextView.BufferType.SPANNABLE);
        Spannable clickSpannable = (Spannable) txtSignUp.getText();

        callbackManager = CallbackManager.Factory.create();
        clickSpannable.setSpan(new mClickableSpan(mString), 27, 34, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        txtForgotPassword.setOnClickListener(this);
        txtSignIn.setOnClickListener(this);
        txtSignUp.setOnClickListener(this);
        txtShowPassword.setOnClickListener(this);
        imgGooglePlus.setOnClickListener(this);
        imgFaceBook.setOnClickListener(this);

        //twitter integration
        twitterButton = (TwitterLoginButton) findViewById(R.id.twitter_button);
        twitterButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {

                session = Twitter.getSessionManager().getActiveSession();

                Twitter.getApiClient(session).getAccountService()
                        .verifyCredentials(true, false, new Callback<User>() {
                            @Override
                            public void success(Result<User> userResult) {
                                User user = userResult.data;
                                mProfile = user.profileImageUrl;
                                screenname = user.screenName;
                                mName = user.name;
                                location = user.location;
                                timeZone = user.timeZone;
                                description = user.description;
                                mId = String.valueOf(user.id);
                                mEmail = user.email;
                                mFollowersCount = String.valueOf(user.followersCount);
                                mFavouritesCount = String.valueOf(user.favouritesCount);

                                Log.d("user_picture : ", mProfile.toString());
                                Log.d("Username : ", screenname);
                                Log.d("FollowersCount : ", mFollowersCount);
                                Log.d("FavouritesCount : ", mFavouritesCount);
                                Log.d("Name : ", mName);
                                Log.d("Id : ", mId);
                                Log.d("Location : ", location);
                                Log.d("Timezone : ", timeZone);
                                Log.d("Description : ", description);

                                // getSocialMediaLogin(mActivity, mId, "3", mEmail, mName, mProfile, "1", "");
                                Pref.setValue(getApplicationContext(), AppConstants.PREF_LOGIN_BY, "3");// Twitter Login set 1
                                Intent mIntent = new Intent(Login.this, Newsfeed.class);
                                startActivity(mIntent);
                                finish();
                            }

                            @Override
                            public void failure(TwitterException e) {
                            }
                        });
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent mIntent;
        try {
            switch (v.getId()) {
                case R.id.edtEmail:
                    edtEmail.setCursorVisible(true);
                    break;
                case R.id.imgFaceBook:
                    loginButtonFacebook.setReadPermissions(Arrays.asList("public_profile, email, user_birthday, user_friends"));
                    loginButtonFacebook.performClick();
                    loginButtonFacebook.setPressed(true);
                    loginButtonFacebook.invalidate();
                    loginButtonFacebook.registerCallback(callbackManager, mCallBack);
                    loginButtonFacebook.setPressed(false);
                    loginButtonFacebook.invalidate();
                    break;
                case R.id.imgGooglePlus:
                    signIn();
                    break;
                case R.id.txtSignIn:
                    KeyboadOff(v, objectInputMethod, mActivity);
                    if (TextUtils.isEmpty(edtEmail.getText().toString().trim())) {
                        edtEmail.requestFocus();
                        edtEmail.setCursorVisible(true);
                        showSnackbar(v, getString(R.string.enter_email_address));
                    } else if (!checkEmail(edtEmail.getText().toString().trim())) {
                        edtEmail.requestFocus();
                        showSnackbar(v, getString(R.string.enter_valid_email_address));
                    } else if (TextUtils.isEmpty(edtPassword.getText().toString().trim())) {
                        edtPassword.requestFocus();
                        showSnackbar(v, getString(R.string.please_enter_password));
                    } else {
                        if (WebInterface.isOnline(mActivity)) {
                            getLogin(v, mActivity, edtEmail.getText().toString().trim(), edtPassword.getText().toString().trim(), "1");
                        } else {
                            showSnackbar(txtSignIn, getString(R.string.Check_Internet_connection));
                        }
                    }
                    break;
                case R.id.txtForgotPassword:
                    mIntent = new Intent(mActivity, ForgotPassword.class);
                    startActivity(mIntent);
                    break;
                case R.id.txtShowPassword:
                    if (!edtPassword.getText().toString().equals("")) {
                        if (show_password == false) {
                            edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            txtShowPassword.setText(getString(R.string.show));
                            edtPassword.setSelection(edtPassword.getText().length());
                            show_password = true;
                        } else {
                            edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            txtShowPassword.setText(getString(R.string.hide));
                            edtPassword.setSelection(edtPassword.getText().length());
                            show_password = false;
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGE(this.getClass().getSimpleName(), e.toString());
            LOGD(this.getClass().getSimpleName(), e + "");
        }
    }

    private FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            String accessToken = loginResult.getAccessToken().getToken();
            Log.d("accessToken>> ", accessToken + "");
            // App code
            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            // Application code
                            Log.d("Login", response.toString());
                            try {
                                mId = object.getString("id").toString();
                                mEmail = object.getString("email").toString();
                                mName = object.getString("name").toString();
                                mProfile = "https://graph.facebook.com/" + mId + "/picture?type=large";
                                mGender = object.getString("gender").toString();

                                Log.d(TAG + " id:::>> ", mId);
                                Log.d(TAG + " name:::>> ", mName);
                                Log.d(TAG + " Email::>> ", mEmail);
                                Log.d(TAG + " MobileNumber::>> ", mMobileNumber);
                                Log.d(TAG + " Profile::>> ", mProfile);
                                Log.d(TAG + " Gender::>> ", mGender);
                                if (mGender.equalsIgnoreCase("male")) {
                                    mGenderId = "1";
                                } else if (mGender.equalsIgnoreCase("female")) {
                                    mGenderId = "2";
                                }
                                //getSocialMediaLogin(mActivity, mId, "1", mEmail, mName, mProfile, "1", mGenderId);
                                Pref.setValue(getApplicationContext(), AppConstants.PREF_LOGIN_BY, "1"); // Facebook Login set 1
                                Intent mIntent = new Intent(Login.this, Newsfeed.class);
                                startActivity(mIntent);
                                finish();
                            } catch (
                                    JSONException e
                                    ) {
                                e.printStackTrace();
                            }
                        }
                    }
            );
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender, birthday");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
            Intent mIntent = new Intent();
            mIntent.putExtra("RESPONSE", "cancel");
        }

        @Override
        public void onError(FacebookException e) {
            Intent mIntent = new Intent();
            mIntent.putExtra("RESPONSE", "error");
        }
    };

    private void getSocialMediaLogin(final Context context, final String id, final String login_type, final String email, final String user_name, final String user_image, final String app_type, final String gender_id) {

        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Loading...");
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest sr = new StringRequest(Request.Method.POST, AppConstants.API_SOCIAL_MEDIA_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jArray;
                JSONArray jsonArray = null;
                JSONObject jsonObject = null;
                try {
                    jArray = new JSONArray(response);
                    JSONObject jObj = jArray.getJSONObject(0);

                    int code = jObj.getInt("code");
                    if (code == 1) {
                        if (jObj.has("result")) {
                            jsonArray = jObj.getJSONArray("result");
                            System.out.print("=API_SOCAIL_LOGIN ARRAY=" + jsonArray.length() + "");

                            if (jsonArray != null && jsonArray.length() > 0) {

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    //LoginBean mLoginBean=new LoginBean();
                                    jsonObject = jsonArray.getJSONObject(i);
                                    Pref.setValue(context, AppConstants.PREF_USERID, jsonObject.optString("user_id"));
                                    Pref.setValue(context, AppConstants.PREF_USERNAME, jsonObject.optString("user_name"));
                                    Pref.setValue(context, AppConstants.PREF_USERIMAGE, jsonObject.optString("user_image"));
                                    Pref.setValue(context, AppConstants.PREF_USEREMAIL, jsonObject.optString("email"));
                                    Pref.setValue(context, AppConstants.PREF_LOGIN_BY, jsonObject.optString("login_by"));
                                    Pref.setValue(context, AppConstants.PREF_USERTYPE, jsonObject.optString("user_type"));
                                    Pref.setValue(context, AppConstants.PREF_APP_CONTACT_NUM, jsonObject.optString("app_contact_number"));
                                    Pref.setValue(context, AppConstants.PREF_APP_CONTACT_EMAIL, jsonObject.optString("app_contact_email"));
                                    Pref.setValue(context, AppConstants.PREF_GENDERID, jsonObject.optString("gender_id"));

                                    pDialog.dismiss();
                                    Intent mIntent = new Intent(Login.this, Newsfeed.class);
                                    startActivity(mIntent);
                                    finish();
                                }
                            }
                        }
                    } else {
                        pDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, context.getString(R.string.Check_Internet_connection), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                params.put("login_type", login_type);
                params.put("email", email);
                params.put("user_name", user_name);
                params.put("user_image", user_image);
                params.put("app_type", app_type);
                params.put("gender_id", gender_id);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    private class mClickableSpan extends ClickableSpan {

        public mClickableSpan(String string) {
            // TODO Auto-generated constructor stub
            super();
        }

        public void onClick(View tv) {
            Intent mIntent = new Intent(mActivity, SignUp.class);
            startActivity(mIntent);
        }

        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(true);
            ds.setColor(Color.parseColor("#232323"));
        }
    }

    public void showSnackbar(View v, String message) {
        Snackbar snackbar = Snackbar.make(v, message, Snackbar.LENGTH_LONG);
        ViewGroup group = (ViewGroup) snackbar.getView();
        group.setBackgroundColor(Color.parseColor("#C61E2C"));
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.show();
    }

    //google signin
    private void signIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        // [END build_client]

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.d("NAME====", acct.getDisplayName());
            Log.d("EMAIL====", acct.getEmail());
            Log.d("ID====", acct.getId());
            Log.d("PHOTO====", acct.getPhotoUrl() + "");
            mName = acct.getDisplayName();
            mEmail = acct.getEmail();
            mId = acct.getId();
            mProfile = String.valueOf(acct.getPhotoUrl());

//            Pref.setValue(getApplicationContext(), AppConstants.PREF_LOGIN_BY, acct.getId());
            // getSocialMediaLogin(mActivity, mId, "2", mEmail, mName, mProfile, "1", "");
            Pref.setValue(getApplicationContext(), AppConstants.PREF_LOGIN_BY, "2"); // // GooglePlus Login set 2
            Intent mIntent = new Intent(Login.this, Newsfeed.class);
            startActivity(mIntent);
            finish();
        } else {
            // Signed out, show unauthenticated UI.
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(Login.this);
            mProgressDialog.setMessage(getString(R.string.loading_message));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
        }
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
        twitterButton.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    private void getLogin(final View v, Context context, final String email, final String password, final String app_type) {

        final ProgressDialog pDialog = new ProgressDialog(mActivity);
        pDialog.setMessage("Loading...");
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.POST, AppConstants.API_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jArray;
                JSONArray jsonArray = null;
                JSONObject jsonObject = null;
                try {
                    jArray = new JSONArray(response);
                    JSONObject jObj = jArray.getJSONObject(0);

                    int code = jObj.getInt("code");
                    if (code == 1) {
                        if (jObj.has("result")) {
                            jsonArray = jObj.getJSONArray("result");
                            LogUtils.LOGV("=API_LOGIN ARRAY=", jsonArray.length() + "");

                            if (jsonArray != null && jsonArray.length() > 0) {

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    jsonObject = jsonArray.getJSONObject(i);
                                    Pref.getValue(mActivity, AppConstants.PREF_LOGIN_BY, "0");// Normal Login set 0
                                    Log.d("user_id :: :: ::", jsonObject.getString("user_id"));
                                    Log.d("LoginBy:: :: ::", AppConstants.PREF_LOGIN_BY + "");
                                    Pref.setValue(mActivity, AppConstants.PREF_USERID, jsonObject.getString("user_id"));
                                    Pref.setValue(mActivity, AppConstants.PREF_USERNAME, jsonObject.getString("user_name"));
                                    Pref.setValue(mActivity, AppConstants.PREF_USERIMAGE, jsonObject.getString("user_image"));
                                    Pref.setValue(mActivity, AppConstants.PREF_USEREMAIL, jsonObject.getString("email"));
                                    Pref.setValue(mActivity, AppConstants.PREF_GENDERID, jsonObject.getString("gender_id"));
                                    Pref.setValue(mActivity, AppConstants.PREF_USERTYPE, jsonObject.getString("user_type"));
                                    Pref.setValue(mActivity, AppConstants.PREF_APP_CONTACT_NUM, jsonObject.getString("app_contact_number"));
                                    Pref.setValue(mActivity, AppConstants.PREF_APP_CONTACT_EMAIL, jsonObject.getString("app_contact_email"));

                                    Pref.setValue(getApplicationContext(), AppConstants.PREF_LOGIN_BY, "0");
                                    Intent intent = new Intent(mActivity, Newsfeed.class);
                                    startActivity(intent);
                                    pDialog.dismiss();
                                    finish();
                                }
                            }
                        }
                    } else {
                        showSnackbar(v, getString(R.string.invalid_email_password));
                        pDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mActivity, getString(R.string.Check_Internet_connection), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);
                params.put("app_type", app_type);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }
}
