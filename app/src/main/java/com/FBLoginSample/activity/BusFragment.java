package com.FBLoginSample.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.FBLoginSample.R;
import com.FBLoginSample.ServiceHandler;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class BusFragment extends Fragment  {

    Communicator communicator;
    private ProgressDialog pDialog;
    private static final String TAG_ID = "st_id";
    private static final String TAG_NAME = "st_name";
    private static final String TAG_LONG = "st_long";
    private static final String TAG_LATT = "st_latt";
    private static final String TAG_TYPE = "st_type";
    private   String database_version= "0000-00-00";
    RecyclerView recyclerView;
    private  static StorageDatabaseAdapter storageHelper;
    ItemData itemsData[] = new ItemData[]{};
    private static  String TAG_DATABASE_DATE_VERSION = "version";



    MyAdapter mAdapter;
    SharedPreferences sharedPref;




    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            communicator = (Communicator) activity;
        }catch (ClassCastException e)
        {
            e.printStackTrace();
        }
    }
    // on scroll

    private static int current_page = 0;


    private LinearLayoutManager linearLayoutManager;
    private List<ItemData> stationList;
    boolean isData_remotly = false;


    // JSON Node names
    private static final String TAG_metrostationmodel = "countdata";
    JSONArray metrostation = null;


    private static String url = "http://gate-info.com/transportation/public/webservice/countofbusinstation";


    public BusFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        // recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this));
        // this is data fro recycler view

        //paging
        recyclerView.setHasFixedSize(true);
        linearLayoutManager=new LinearLayoutManager(getActivity());
        storageHelper = new StorageDatabaseAdapter(getActivity());
        stationList = new ArrayList<ItemData>();


        sharedPref = getActivity().getSharedPreferences("transportation", getActivity().getApplicationContext().MODE_PRIVATE);
        final boolean isDataUpdated = sharedPref.getBoolean("db_updated_bus", false);

        String [] myStation_names =  storageHelper.getData("busstation");

        if (isDataUpdated && sharedPref.getString("last_inserted_date_bus", null)!=null)
        {
            database_version = sharedPref.getString("last_inserted_date_bus", null);
            loadData(current_page);
            isData_remotly = true;
        }
        else if (myStation_names.length == 0)
        {
            loadData(current_page);
            isData_remotly = true;
        }
        else
        {
            //Load from SQLight
            isData_remotly = false;

            if (myStation_names.length != 0) {
                itemsData = new ItemData[myStation_names.length];

                for (int i = 0; i < myStation_names.length; i++) {
                    itemsData[i] = new ItemData(myStation_names[i]);

                }

                mAdapter  = new MyAdapter(itemsData);
                // 4. set adapter
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }

        }

        recyclerView.setLayoutManager(linearLayoutManager);
        // 3. create an adapter
        // 4. set adapter
        // 5. set item animator to DefaultAnimator
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                // do somthing...

                if(isData_remotly)
                    loadMoreData(current_page);

            }

        });

    }


    private void loadData(int current_page) {

        // I have not used current page for showing demo, if u use a webservice
        // then it is useful for every call request

        new GetSignUp().execute();

    }

    private void loadMoreData(int current_page) {

        // I have not used current page for showing demo, if u use a webservice
        // then it is useful for every call request

        //    loadLimit = ival + 10;
        BusFragment.current_page = current_page;
        new GetSignUp().execute();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bus, container, false);
    }


    private class  GetSignUp extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog

            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String  doInBackground(Void... arg0) {
            // Creating service handler class instance
            com.FBLoginSample.ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("offset",""+current_page));
            nameValuePairs.add(new BasicNameValuePair("date",""+database_version));
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.POST , nameValuePairs);//adding params


            // String jsonStr = sh.makeServiceCall(url, ServiceHandler.POST);//adding params

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                return jsonStr;

            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return null;
            }


        }

        @Override
        protected void onPostExecute(String  jsonStr) {
            super.onPostExecute(jsonStr);

            // 2. set layoutManger

            // Dismiss the progress dialog
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    metrostation = jsonObj.getJSONArray(TAG_metrostationmodel);


                    for (int i = 0; i < metrostation.length(); i++) {
                        String counter="0"+i;
                        JSONObject c = metrostation.getJSONObject(i);
                        String st_id = c.getString(TAG_ID);
                        String st_name = c.getString(TAG_NAME);
                        String st_long = c.getString(TAG_LONG);
                        String st_latt = c.getString(TAG_LATT);

                        String BusLineNum=c.getString("countbusinstation");


                        stationList.add(new ItemData(st_name,counter,BusLineNum,st_id));
                        String st_type = c.getString(TAG_TYPE);
                        String st_version = c.getString(TAG_DATABASE_DATE_VERSION);
                        database_version = c.getString(TAG_DATABASE_DATE_VERSION);


                        // itemsData[i-1] = new ItemData(st_name);
                        try {
                            int deleteid= storageHelper.delete("busstation", Integer.parseInt(st_id));
                            long insertid = storageHelper.insertData(st_id, st_name, st_type, st_long, st_latt, "busstation", st_version);

                        } catch (SQLException e) {

                        }


                    }
                    // Getting JSON Array node

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */


            if (jsonStr != null)
            {
            String[] myStation_names = storageHelper.getData("busstation");

         /*    if (stationList != null && stationList.size() != 0) {
                itemsData = new ItemData[stationList.size()];
                for (int i = 0; i < stationList.size(); i++) {

                    itemsData[i] = stationList.get(i);
                }
            }*/

            recyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {

                            Fragment fragment = new BusLinesFragment();
//                            communicator.openBusLineFragment(Integer.parseInt(stationList.get(position).getItemposotion()), fragment);
                            Toast.makeText(getActivity().getBaseContext(),stationList.get(position).getItemposotion(),Toast.LENGTH_SHORT).show();
                            communicator.openBusLineFragment(Integer.parseInt(stationList.get(position).getItemposotion()), fragment);

                        }
                    }));
            if (myStation_names.length != 0) {
                itemsData = new ItemData[myStation_names.length];

                for (int i = 0; i < myStation_names.length; i++) {
                    itemsData[i] = new ItemData(myStation_names[i]);

                }

                mAdapter = new MyAdapter(itemsData);
                // 4. set adapter
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
                
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        current_page = 0;
        if (stationList != null && stationList.size() != 0)
            stationList.clear();
    }
}
