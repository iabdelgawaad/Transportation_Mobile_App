package com.FBLoginSample.activity;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.FBLoginSample.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    Communicator comm;
    private AutoCompleteTextView source;
    private AutoCompleteTextView destination;
    private Button search;
    private int sourceState=0;
    private int destinationState=0;
    private String JsonArrayName="areas";
    private String TAG_STATION="listareastation";
    private static String tvSource;
    private static String tvdestination;
    private JSONArray jsonResult;
    private String[] areas;
    public static String[] stations =new String[] {};
    public static String[] stationsType= new String[] {};
    public static int searchAction=0;


    // Declaring Your View and Variables
    Toolbar toolbar;
    ViewPager pager;
    public static ViewPagerAdapter adapter;


    ArrayAdapter<String> adapterStations;
    SlidingTabLayout tabs;
    CharSequence Titles[]={"All","Metro","Bus"};
    int Numboftabs =3;
    GetUserData userData;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //connect Backend
        userData = new GetUserData();
        userData.execute("http://gate-info.com/transportation/public/webservice/listallarea");

        //declare views
        source = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextViewFrom);
        destination = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextViewTo);
        search=(Button)view.findViewById(R.id.search);

        //search control
        search.setEnabled(false);
        source.setThreshold(1);
        destination.setThreshold(1);

        /// FloatingActionButton.Builder(this)
//
//        FloatingActionButton fabButton = new FloatingActionButton.Builder(getActivity())
//                .withDrawable(getResources().getDrawable(R.drawable.ic_action))
//                .withButtonColor(Color.WHITE)
//                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
//                .withMargins(0, 0, 16, 16)
//                .create();

        ////


        //add listener
        source.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sourceState=1;
                if(destinationState==1){
                    search.setEnabled(true);
                }
            }
        });

        destination.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                destinationState=1;
                if(sourceState==1){
                    search.setEnabled(true);
                }
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvSource= source.getText().toString();
                tvdestination= destination.getText().toString();
                searchAction=1;
                userData = new GetUserData();
                userData.execute("http://gate-info.com/transportation/public/webservice/listareastation");


            }
        });

        //tabs

        // Creating The Toolbar and setting it as the Toolbar for the activity

        toolbar = (Toolbar) view.findViewById(R.id.tool_bar);
        //setSupportActionBar(toolbar);

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdapter(getActivity().getSupportFragmentManager(),Titles,Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) view.findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) view.findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Communicator communicator =  (Communicator) adapter.instantiateItem(pager,position);
                //tab1 = (Tab1) manager.findFragmentById(R.id.tab1);
                communicator.respond(HomeFragment.stations , HomeFragment.stationsType);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public class GetUserData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return getDataFromBackend(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            try {

                JSONObject jsonObj = new JSONObject(s);

                if(searchAction==1){
                    jsonResult = jsonObj.getJSONArray(TAG_STATION);
                    stations=new String[jsonResult.length()];
                    stationsType=new String[jsonResult.length()];
                    for(int i=0;i<stations.length;i++){
                        stations[i]=jsonResult.getJSONObject(i).getString("st_name");
                        /* type: metro or bus */
                        stationsType[i]=jsonResult.getJSONObject(i).getString("st_type");
                    }
                    searchAction = 0;

                    comm.respond(stations,stationsType);

                }else {
                    jsonResult = jsonObj.getJSONArray(JsonArrayName);
                    areas = new String[jsonResult.length()];

                    for(int i=0;i<jsonResult.length();i++){
                        areas[i]=jsonResult.getJSONObject(i).getString("area_name");
                    }
                    //fill&settingg Adapter
                    adapterStations = new ArrayAdapter<String> (getActivity(),android.R.layout.simple_list_item_1,areas);
                    source.setAdapter(adapterStations);
                    destination.setAdapter(adapterStations);

                }


            } catch (JSONException e) {

                e.printStackTrace();
            }

          /*  //send data to tab1
            MainActivity mainActivity = new MainActivity();
            mainActivity.respond(areas);*/
            super.onPostExecute(s);

        }
        //

    }
    public static String getDataFromBackend(String url) {

        // To Handle USER & PASSAWARD OF ALPHA
        // Create a new HttpClient and Post Header
        // String finalURL = convertURL(url);
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        InputStream inputStream = null;
        StringBuilder result = new StringBuilder();

        try {
            // Add your data
            if(searchAction==1){
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("source", tvSource));
                nameValuePairs.add(new BasicNameValuePair("destination", tvdestination));
                nameValuePairs.add(new BasicNameValuePair("offset", "0"));


                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            }


            // Execute HTTP Get Request
            HttpResponse response_alpha = httpclient.execute(httppost);

            inputStream = response_alpha.getEntity().getContent();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(
                    inputStream));

            String tmpString = "";
            while ((tmpString = buffer.readLine()) != null) {
                result.append(tmpString);
            }

        } catch (ClientProtocolException e) {
            Log.d("HTTP Exception", "HTTP ERROR GUIDE_1");
        } catch (IOException e) {
            Log.d("HTTP Exception", "HTTP ERROR GUIDE_2");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result.toString();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);


        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            comm = (Communicator) activity;
        }catch (ClassCastException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        //connect Backend
       userData = new GetUserData();
        if (userData != null)
        userData.execute("http://gate-info.com/transportation/public/webservice/listallarea");

    }
}
