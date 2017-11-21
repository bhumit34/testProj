package com.schindler.schindler.MyProjects;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.daimajia.swipe.SwipeLayout;
import com.schindler.R;
import com.schindler.commnonclass.ConstantData;
import com.schindler.gettersetter.MyProjectListBean;
import com.schindler.gettersetter.ProjectStatusBean;
import com.schindler.schindler.SchindlerDrawerActivity;
import com.schindler.schindler.tag.MySingleton;
import com.schindler.schindler.tag.Pref;
import com.schindler.schindler.tag.PullListView;
import com.schindler.schindler.tag.WebInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.schindler.commnonclass.ConstantData.MY_PROJECT_LIST;

/**
 * Created by BJPatel on 14-03-2017.
 */

public class ProjectList extends Fragment implements PullListView.PullListViewListener {
    private Activity mActivity;
    private Context context;
    private TextView txtMessage, txtProjNum, txtProjName, txtStatus;
    private PullListView lvProjectList;
    private ArrayList<MyProjectListBean> myProjectListArrayList;
    private ArrayList<ProjectStatusBean> projectStatusBeanArrayList = new ArrayList<>();
    private ArrayList<MyProjectListBean> arrList = null;
    public ArrayList<MyProjectListBean> arrSearchedProjectList = null;
    private int TOTAL_COUNT = 0, currentPageNo = 0, totalNoOfRecords = 25;
    private String sortingVal = "", emailId = "", userId = "", roleId = "", compId = "", deviceId = "", projNo = "", designationID = "", notifyUsers = "", searchText = "";
    private EditText edtSearch;
    private boolean FlagRefreshLoad = false, isSearch = false;
    private MyProjectListAdapter myProjectListAdapter;
    private ImageView imgSearch;
    private RelativeLayout rlSearchView;
    private FragmentTransaction fragmentTransaction;

    public ProjectList() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mActivity = getActivity();
    }

    @SuppressLint("ValidFragment")
    public ProjectList(Context context) {
        // Required empty public constructor
        setHasOptionsMenu(true);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        mActivity.setTitle("My Project");
        view = inflater.inflate(R.layout.my_project_list, container, false);
        txtMessage = (TextView) view.findViewById(R.id.txtMessage);
        txtProjNum = (TextView) view.findViewById(R.id.txtProjNum);
        txtProjName = (TextView) view.findViewById(R.id.txtProjName);
        txtStatus = (TextView) view.findViewById(R.id.txtStatus);

        lvProjectList = (PullListView) view.findViewById(R.id.lvProjectList);
        rlSearchView = (RelativeLayout) view.findViewById(R.id.rlSearchView);
        edtSearch = (EditText) view.findViewById(R.id.edtSearch);
        imgSearch = (ImageView) view.findViewById(R.id.imgSearch);
        arrList = new ArrayList<>();
        arrSearchedProjectList = new ArrayList<>();
        mActivity = getActivity();

        sortingVal = "ProjectName";
        if (WebInterface.isOnline(mActivity)) {
            get_ProjectList(currentPageNo, searchText, sortingVal);
        } else {
            Toast.makeText(mActivity, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
        }

        SchindlerDrawerActivity.txtTitle.setText("My Project");
        SchindlerDrawerActivity.rlMail.setVisibility(View.GONE);
        SchindlerDrawerActivity.rlSearch.setVisibility(View.VISIBLE);
        SchindlerDrawerActivity.rlInfo.setVisibility(View.VISIBLE);
        SchindlerDrawerActivity.rlInfo.setBackgroundResource(R.drawable.ic_mail);
        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();

        deviceId = Pref.getValue(mActivity, ConstantData.Pref_Device_Id, "").toString();
        emailId = Pref.getValue(mActivity, ConstantData.Pref_Email, "").toString();
        userId = Pref.getValue(mActivity, ConstantData.Pref_UserId, "").toString();
        roleId = Pref.getValue(mActivity, ConstantData.Pref_RoleId, "").toString();
        compId = Pref.getValue(mActivity, ConstantData.Pref_CompanyId, "").toString();


        txtProjName.setBackground(getResources().getDrawable(R.drawable.bg_light_grey_rounded_view));
        txtProjName.setTextColor(Color.WHITE);

        txtProjNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtStatus.setBackground(getResources().getDrawable(R.drawable.bg_white_view));
                txtProjNum.setBackground(getResources().getDrawable(R.drawable.bg_light_grey_rounded_view));
                txtProjNum.setTextColor(Color.WHITE);
                txtStatus.setTextColor(Color.BLACK);
                txtProjName.setTextColor(Color.BLACK);
                txtProjName.setBackground(getResources().getDrawable(R.drawable.bg_white_view));
                sortingVal = "ProjectNo";
                if (WebInterface.isOnline(mActivity)) {
                    get_ProjectList(currentPageNo, searchText, sortingVal);
                } else {
                    Toast.makeText(mActivity, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                }
            }
        });

        txtProjName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtProjName.setBackground(getResources().getDrawable(R.drawable.bg_light_grey_rounded_view));
                txtStatus.setBackground(getResources().getDrawable(R.drawable.bg_white_view));
                txtProjNum.setBackground(getResources().getDrawable(R.drawable.bg_white_view));
                txtProjNum.setTextColor(Color.BLACK);
                txtStatus.setTextColor(Color.BLACK);
                txtProjName.setTextColor(Color.WHITE);
                sortingVal = "ProjectName";
                if (WebInterface.isOnline(mActivity)) {
                    get_ProjectList(currentPageNo, searchText, sortingVal);
                } else {
                    Toast.makeText(mActivity, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                }
            }
        });


        txtStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtProjNum.setBackground(getResources().getDrawable(R.drawable.bg_white_view));
                txtStatus.setBackground(getResources().getDrawable(R.drawable.bg_light_grey_rounded_view));
                txtProjName.setBackground(getResources().getDrawable(R.drawable.bg_white_view));
                txtProjNum.setTextColor(Color.BLACK);
                txtStatus.setTextColor(Color.WHITE);
                txtProjName.setTextColor(Color.BLACK);
                sortingVal = "StatusName";
                if (WebInterface.isOnline(mActivity)) {
                    get_ProjectList(currentPageNo, searchText, sortingVal);
                } else {
                    Toast.makeText(mActivity, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                }
            }
        });


        SchindlerDrawerActivity.rlSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("rlSearch", "rlSearch clicked");
                if (isSearch == false) {
                    isSearch = true;
                    rlSearchView.setVisibility(View.VISIBLE);
                } else {
                    isSearch = false;
                    rlSearchView.setVisibility(View.GONE);
                }
            }
        });

        edtSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    currentPageNo = 0;
                    searchText = edtSearch.getText().toString();
                    myProjectListArrayList.clear();
                    projectStatusBeanArrayList.clear();
                    arrList.clear();
                    get_ProjectList(currentPageNo, searchText, sortingVal);
                    return true;
                }
                return false;
            }
        });


        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchText = edtSearch.getText().toString();
                currentPageNo = 0;
                myProjectListArrayList.clear();
                projectStatusBeanArrayList.clear();
                arrList.clear();
                if (WebInterface.isOnline(mActivity)) {
                    get_ProjectList(currentPageNo, searchText, sortingVal);
                } else {
//                    ViewUtils.showSnackbar(v, getString(R.string.no_internet_connection));
                    Toast.makeText(mActivity, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                }
            }
        });

        lvProjectList.setDoMoreWhenBottom(true);
        lvProjectList.setOnMoreListener(ProjectList.this);
        lvProjectList.doneRefresh();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        SchindlerDrawerActivity.txtTitle.setText("My Project");
        SchindlerDrawerActivity.rlMail.setVisibility(View.GONE);
        SchindlerDrawerActivity.rlSearch.setVisibility(View.VISIBLE);
        SchindlerDrawerActivity.rlInfo.setVisibility(View.VISIBLE);
        SchindlerDrawerActivity.rlInfo.setBackgroundResource(R.drawable.ic_mail);
    }

    private void get_ProjectList(final int currentPageNo, final String searchText, final String sortText) {
        final ProgressDialog progressDialog = new ProgressDialog(mActivity);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        MySingleton.getInstance(mActivity).getRequestQueue().start();

        StringRequest sr = new StringRequest(Request.Method.POST, ConstantData.schindlerURL + MY_PROJECT_LIST, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jArray;
                JSONArray jsonArray = null;
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("Status");
                    Log.v("response : MY_PROJECT_LIST", "" + response);
                    Log.v("status : MY_PROJECT_LIST", "" + status);
                    if (status.equals("1")) {
                        MyProjectListBean myProjectListBean;
                        progressDialog.dismiss();
                        myProjectListArrayList = new ArrayList<MyProjectListBean>();
                        TOTAL_COUNT = jsonObject.optInt("Count");
                        Log.v("TOTAL_COUNT", "" + TOTAL_COUNT);
                        JSONArray jArrayStatus = jsonObject.getJSONArray("StatusList");

                        if (jArrayStatus != null && jArrayStatus.length() > 0) {
                            for (int i = 0; i < jArrayStatus.length(); i++) {
                                JSONObject jsonObjectStatus = jArrayStatus.getJSONObject(i);
                                ProjectStatusBean projectStatusBean = new ProjectStatusBean();
                                projectStatusBean.setStatus(jsonObjectStatus.optString("Status"));
                                projectStatusBean.setStatusName(jsonObjectStatus.optString("StatusName"));
                                projectStatusBeanArrayList.add(projectStatusBean);
                            }
                        }

                        jsonArray = jsonObject.getJSONArray("Data");

                        if (jsonArray != null && jsonArray.length() > 0) {
                            for (int j = 0; j < jsonArray.length(); j++) {
                                jsonObject = jsonArray.getJSONObject(j);
                                myProjectListBean = new MyProjectListBean();
                                myProjectListBean.setProjectID(jsonObject.optString("ProjectID"));
                                myProjectListBean.setProjectNo(jsonObject.optString("ProjectNo"));
                                myProjectListBean.setProjectName(jsonObject.optString("ProjectName"));
                                myProjectListBean.setBanks(jsonObject.optString("Banks"));
                                myProjectListBean.setCompanyId(jsonObject.optString("CompanyId"));
                                myProjectListBean.setCompanyName(jsonObject.optString("CompanyName"));
                                myProjectListBean.setProjectMgrPhone(jsonObject.optString("ProjectMgrPhone"));
                                myProjectListBean.setOriginalNODDate(jsonObject.optString("OriginalNODDate"));
                                myProjectListBean.setNOD(jsonObject.optString("NOD"));
                                myProjectListBean.setNODConfirm(jsonObject.optString("NODConfirm"));
                                myProjectListBean.setIsContractSigned(jsonObject.optString("IsContractSigned"));
                                myProjectListBean.setContractorSuper(jsonObject.optString("ContractorSuper"));
                                myProjectListBean.setSchindlerSuperintendent(jsonObject.optString("SchindlerSuperintendent"));
                                myProjectListBean.setSchindlerSalesExecutive(jsonObject.optString("SchindlerSalesExecutive"));
                                myProjectListBean.setTeamAssigned(jsonObject.optString("TeamAssigned"));
                                myProjectListBean.setJob_Address(jsonObject.optString("Job_Address"));
                                myProjectListBean.setJob_City(jsonObject.optString("Job_City"));
                                myProjectListBean.setJob_State(jsonObject.optString("Job_State"));
                                myProjectListBean.setJob_Zip(jsonObject.optString("Job_Zip"));
                                myProjectListBean.setFactoryMaterialEstimate(jsonObject.optString("FactoryMaterialEstimate"));
                                myProjectListBean.setFactoryMaterialActual(jsonObject.optString("FactoryMaterialActual"));
                                myProjectListBean.setContractorProjMgr(jsonObject.optString("ContractorProjMgr"));
                                myProjectListBean.setContractReceivedDate(jsonObject.optString("ContractReceivedDate"));
                                myProjectListBean.setAwardDate(jsonObject.optString("AwardDate"));
                                myProjectListBean.setCustomerNo(jsonObject.optString("CustomerNo"));
                                myProjectListBean.setNODConfirmDate(jsonObject.optString("NODConfirmDate"));
                                myProjectListBean.setUnitCount(jsonObject.optString("UnitCount"));
                                myProjectListBean.setBookingComplDate(jsonObject.optString("BookingComplDate"));
                                myProjectListBean.setProj_Mgr_Email(jsonObject.optString("Proj_Mgr_Email"));
                                myProjectListBean.setStatus(jsonObject.optString("Status"));
                                myProjectListBean.setStatusName(jsonObject.optString("StatusName"));
                                myProjectListBean.setCreatedDate(jsonObject.optString("CreatedDate"));
                                myProjectListBean.setCreatedBy(jsonObject.optString("CreatedBy"));
                                myProjectListBean.setUpdatedDate(jsonObject.optString("UpdatedDate"));
                                myProjectListBean.setUpdatedBy(jsonObject.optString("UpdatedBy"));
                                myProjectListBean.setDeletedDate(jsonObject.optString("DeletedDate"));
                                myProjectListBean.setDeletedBy(jsonObject.optString("DeletedBy"));
                                myProjectListBean.setIsDeleted(jsonObject.optString("IsDeleted"));
                                myProjectListBean.setInceptionDate(jsonObject.optString("InceptionDate"));
                                myProjectListBean.setTurnOverDate(jsonObject.optString("TurnOverDate"));
                                myProjectListBean.setAlternateProjectNo(jsonObject.optString("AlternateProjectNo"));
                                myProjectListBean.setContractorContactPerson(jsonObject.optString("ContractorContactPerson"));
                                myProjectListBean.setSchindlerProjMgr(jsonObject.optString("SchindlerProjMgr"));
                                myProjectListBean.setParentProjectId(jsonObject.optString("ParentProjectId"));
                                myProjectListBean.setAppAuditID(jsonObject.optString("appAuditID"));
                                myProjectListBean.setSessionID(jsonObject.optString("SessionID"));
                                myProjectListArrayList.add(myProjectListBean);
                            }
                            if (myProjectListArrayList != null) {
                                lvProjectList.setVisibility(View.VISIBLE);
//                                    txtEmptyView.setVisibility(View.GONE);
                                if (!FlagRefreshLoad) {
                                    lvProjectList.doneRefresh();
                                } else {
                                    lvProjectList.doneMore();
                                }

                                if (arrList != null) {
                                    @SuppressWarnings("unused")
                                    int no = arrList.size();

                                    if (currentPageNo == 0) {
                                        arrList = myProjectListArrayList;
                                    } else {
                                        arrList.addAll(myProjectListArrayList);
                                    }

                                    if (arrList != null && arrList.size() > 0) {
                                        // pageNo = pageNo + 1;

                                        if (currentPageNo == 0) {

                                            setAdaperToListview(arrList);
                                        } else {
                                            myProjectListAdapter.notifyDataSetChanged();
                                        }

                                        if (arrList.size() == TOTAL_COUNT) {
                                            lvProjectList.doneMore();
                                        } else {
                                            lvProjectList.doneMore();
                                        }
                                    }
                                }
                            }
                        } else {
                            progressDialog.dismiss();
                            lvProjectList.doneMore();
                            txtMessage.setVisibility(View.VISIBLE);
                            txtMessage.setText(R.string.no_project_found);
                        }
                    } else {
                        progressDialog.dismiss();
                        lvProjectList.doneMore();
                        txtMessage.setVisibility(View.VISIBLE);
                        txtMessage.setText(R.string.no_project_found);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(mActivity, "Errorrrrrrr", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                NetworkResponse response = volleyError.networkResponse;
                if (volleyError instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        Log.d("res,", res);
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
                params.put("CompanyID", compId);
                params.put("UserID", userId);
                params.put("RoleID", roleId);
                params.put("SortColumn", sortText);
                params.put("SortDirection", "asc");
                params.put("PageNumber", currentPageNo + "");
                Log.e("currentPageNo", "" + currentPageNo);
                params.put("PageSize", "35");
                params.put("SearchTerm", searchText);
                params.put("appAuditID", "");
                params.put("DeviceID", deviceId);
                params.put("Operation", "1");

                Log.v("CompanyID", "" + compId);
                Log.v("UserID", "" + userId);
                Log.v("RoleID", "" + roleId);
                Log.v("PageNumber", "" + currentPageNo);
                Log.v("searchText", "" + searchText);
                Log.v("deviceId", "" + deviceId);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(mActivity);
        sr.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mRequestQueue.add(sr);
    }

    public class MyProjectListAdapter extends BaseAdapter {
        private ArrayList<MyProjectListBean> arrayList;
        private MyProjectListBean myProjectListBean;
        private Activity context;

        public MyProjectListAdapter(Activity context, ArrayList<MyProjectListBean> arrayList) {
            this.arrayList = arrayList;
            this.context = context;
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.myproject_list_item, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.txtProjNum.setText(arrayList.get(position).getProjectNo());
            holder.txtProjName.setText(arrayList.get(position).getProjectName());
            holder.txtAddress.setText(arrayList.get(position).getJob_Address());
            holder.txtCompanyName.setText(arrayList.get(position).getCompanyName());
            holder.txtBank.setText(arrayList.get(position).getBanks());
            holder.txtUnits.setText(arrayList.get(position).getUnitCount());
            holder.txtGCSuper.setText(arrayList.get(position).getSchindlerSuperintendent());
            holder.txtGCProjManager.setText(arrayList.get(position).getSchindlerProjMgr());

            if (arrayList.get(position).getStatus().equals("13") && arrayList.get(position).getStatusName().equals("Booked")) {
                holder.txtStatus.setText("Booked");
//                holder.txtStatus.setBackgroundColor(Color.parseColor("#00F303"));
                holder.txtStatus.setBackground(getResources().getDrawable(R.drawable.bg_green_rounded_view));
            } else if (arrayList.get(position).getStatus().equals("14") && arrayList.get(position).getStatusName().equals("In Approval Stage")) {
                holder.txtStatus.setText("In Approval Stage");
                holder.txtStatus.setBackground(getResources().getDrawable(R.drawable.bg_red_rounded_view));
            } else if (arrayList.get(position).getStatus().equals("15") && arrayList.get(position).getStatusName().equals("Released to Manufacturing")) {
                holder.txtStatus.setText("Released to Manufacturing");
                holder.txtStatus.setBackground(getResources().getDrawable(R.drawable.bg_light_purple_rounded_view));
            } else if (arrayList.get(position).getStatus().equals("16") && arrayList.get(position).getStatusName().equals("Material Delivered")) {
                holder.txtStatus.setText("Material Delivered");
                holder.txtStatus.setBackground(getResources().getDrawable(R.drawable.bg_light_purple_rounded_view));
            } else if (arrayList.get(position).getStatus().equals("23") && arrayList.get(position).getStatusName().equals("Installation Complete")) {
                holder.txtStatus.setText("Installation Complete");
                holder.txtStatus.setBackground(getResources().getDrawable(R.drawable.bg_light_purple_rounded_view));
            } else if (arrayList.get(position).getStatus().equals("24") && arrayList.get(position).getStatusName().equals("Closed")) {
                holder.txtStatus.setText("Closed");
                holder.txtStatus.setBackground(getResources().getDrawable(R.drawable.bg_blue_rounded_view));
            } else if (arrayList.get(position).getStatus().equals("22") && arrayList.get(position).getStatusName().equals("Installation Started")) {
                holder.txtStatus.setText("Installation Started");
                holder.txtStatus.setBackground(getResources().getDrawable(R.drawable.bg_red_rounded_view));
            }

            holder.btnView.setOnClickListener(onListViewListener(position, holder));
            holder.btnContacts.setOnClickListener(onListContactListener(position, holder));
            holder.btnChat.setOnClickListener(onListChatListener(position, holder));

            return convertView;
        }

        private View.OnClickListener onListChatListener(final int position, final ViewHolder holder) {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tag = "projectListView";
                    ProjectListView projectListView = new ProjectListView(context);
                    Bundle args = new Bundle();
                    args.putSerializable("arrayList", arrayList.get(position));
                    args.putSerializable("arrayListStatus", projectStatusBeanArrayList);
                    args.putString("action", "chat");
                    projectListView.setArguments(args);
                    fragmentTransaction.replace(R.id.main_fragment_container, projectListView, tag);
                    fragmentTransaction.addToBackStack(tag);
                    fragmentTransaction.commit();
                }
            };
        }

        private View.OnClickListener onListContactListener(final int position, final ViewHolder holder) {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tag = "projectListView";
                    ProjectListView projectListView = new ProjectListView(context);
                    Bundle args = new Bundle();
                    args.putSerializable("arrayList", arrayList.get(position));
                    args.putSerializable("arrayListStatus", projectStatusBeanArrayList);
                    args.putString("action", "contact");
                    projectListView.setArguments(args);
                    fragmentTransaction.replace(R.id.main_fragment_container, projectListView, tag);
                    fragmentTransaction.addToBackStack(tag);
                    fragmentTransaction.commit();
                }
            };
        }

        private View.OnClickListener onListViewListener(final int position, final ViewHolder holder) {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tag = "projectListView";
                    ProjectListView projectListView = new ProjectListView(context);
                    Bundle args = new Bundle();
                    args.putSerializable("arrayList", arrayList.get(position));
                    args.putSerializable("arrayListStatus", projectStatusBeanArrayList);
                    args.putString("action", "view");
                    projectListView.setArguments(args);
                    fragmentTransaction.replace(R.id.main_fragment_container, projectListView, tag);
                    fragmentTransaction.addToBackStack(tag);
                    fragmentTransaction.commit();
                }
            };
        }

        public class ViewHolder {

            TextView txtProjNum, txtProjName, txtAddress, txtCompanyName, txtBank, txtUnits, txtGCSuper, txtStatus, txtGCProjManager;
            LinearLayout foregroundView;
            RelativeLayout rlItem;
            private View btnChat, btnView, btnContacts;
            private SwipeLayout swipeLayout;

            public ViewHolder(View v) {
                swipeLayout = (SwipeLayout) v.findViewById(R.id.swipe);
                btnChat = v.findViewById(R.id.chat);
                btnView = v.findViewById(R.id.view);
                btnContacts = v.findViewById(R.id.contacts);
                txtProjNum = (TextView) v.findViewById(R.id.txtProjNum);
                txtProjName = (TextView) v.findViewById(R.id.txtProjName);
                txtAddress = (TextView) v.findViewById(R.id.txtAddress);
                txtCompanyName = (TextView) v.findViewById(R.id.txtCompanyName);
                txtBank = (TextView) v.findViewById(R.id.txtBank);
                txtUnits = (TextView) v.findViewById(R.id.txtUnits);
                txtGCSuper = (TextView) v.findViewById(R.id.txtGCSuper);
                txtStatus = (TextView) v.findViewById(R.id.txtStatus);
                txtGCProjManager = (TextView) v.findViewById(R.id.txtGCProjManager);
                foregroundView = (LinearLayout) v.findViewById(R.id.foregroundView);
                rlItem = (RelativeLayout) v.findViewById(R.id.rlItem);
                swipeLayout.setShowMode(com.daimajia.swipe.SwipeLayout.ShowMode.LayDown);
            }
        }
    }

    @Override
    public boolean onRefreshOrMore(PullListView dynamicListView, boolean isRefresh) {
        if (isRefresh) {
        } else {

            if (arrList.size() == TOTAL_COUNT) {
                lvProjectList.doneMore();
            } else {
                FlagRefreshLoad = true;
                if (WebInterface.isOnline(mActivity)) {
                    currentPageNo = currentPageNo + 1;
                    get_ProjectList(currentPageNo, searchText, sortingVal);
                    Log.v("currentPageNo", "" + currentPageNo);
                } else {
                    Toast.makeText(mActivity, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                }
            }
        }
        return false;
    }

    private void setAdaperToListview(ArrayList<MyProjectListBean> arrList) {
        myProjectListAdapter = new MyProjectListAdapter(mActivity, arrList);
        lvProjectList.setAdapter(myProjectListAdapter);
        myProjectListAdapter.notifyDataSetChanged();
    }
}

