package com.FBLoginSample.activity;

import android.app.ProgressDialog;
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

import com.FBLoginSample.R;
import com.FBLoginSample.ServiceHandler;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class BusFragment extends Fragment {

    private ProgressDialog pDialog;
    private static final String TAG_ID = "st_id";
    private static final String TAG_NAME = "st_name";
    private static final String TAG_LONG = "st_long";
    private static final String TAG_LATT = "st_latt";
    RecyclerView recyclerView;

    ItemData itemsData[] = new ItemData[]{};

    MyAdapter mAdapter;

    // on scroll

    private static int current_page = 0;


    private LinearLayoutManager linearLayoutManager;
    private List<ItemData> stationList;



    // JSON Node names
    private static final String TAG_metrostationmodel = "listallbussstation";
    JSONArray metrostation = null;


    private static String url = "http://gate-info.com/transportation/public/webservice/listbusstation";

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


        stationList = new ArrayList<ItemData>();

        loadData(current_page);



        recyclerView.setLayoutManager(linearLayoutManager);
        // 3. create an adapter
        // 4. set adapter
        // 5. set item animator to DefaultAnimator
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                // do somthing...

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
        BusFragment.current_page=current_page;
        new GetSignUp().execute();
        mAdapter.notifyDataSetChanged();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    private class GetSignUp extends AsyncTask<Void, Void, List<ItemData>> {

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
        protected List<ItemData>  doInBackground(Void... arg0) {
            // Creating service handler class instance
            com.FBLoginSample.ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("offset",""+current_page));
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.POST , nameValuePairs);//adding params




            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    metrostation = jsonObj.getJSONArray(TAG_metrostationmodel);


                    for (int i = 0; i < metrostation.length(); i++) {
                        JSONObject c = metrostation.getJSONObject(i);
                        String st_id = c.getString(TAG_ID);
                        String st_name = c.getString(TAG_NAME);
                        String st_long = c.getString(TAG_LONG);
                        String st_latt = c.getString(TAG_LATT);


                        stationList.add(new ItemData(st_name));


                    }
                    // Getting JSON Array node

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return stationList;
        }

        @Override
        protected void onPostExecute(List<ItemData>  result) {
            super.onPostExecute(result);

            // 2. set layoutManger

            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            if (result != null && result.size() != 0) {
                itemsData = new ItemData[result.size()];
                for (int i = 0; i < result.size(); i++) {

                    itemsData[i] = result.get(i);
                }
            }
            mAdapter  = new MyAdapter(itemsData);
            // 4. set adapter
            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();


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
