package com.FBLoginSample.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.FBLoginSample.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MetroLinesFragment extends Fragment {

    Communicator communicator;
    private ProgressDialog pDialog;
    private static final String TAG_ID = "st_id";
    private static final String TAG_NAME = "st_name";
    private static final String TAG_LONG = "st_long";
    private static final String TAG_LATT = "st_latt";

    // JSON Node names
    private static final String TAG_metrostationmodel = "pass stations";
    JSONArray metrostation = null;

    private static String url = "http://gate-info.com/transportation/public/webservice/getpasstransportation";

    TextView lblid;
    static int returnedID;

    RecyclerView recyclerView;

    ItemData itemsData[] = new ItemData[]{};

    MyAdapter mAdapter;


    public MetroLinesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bus_lines, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        new GetSignUp().execute();

        // 2. set layoutManger
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // 3. create an adapter
        mAdapter  = new MyAdapter(itemsData);
        // 4. set adapter
        recyclerView.setAdapter(mAdapter);
        // 5. set item animator to DefaultAnimator
        recyclerView.setItemAnimator(new DefaultItemAnimator());





    }


    private class GetSignUp extends AsyncTask<Void, Void, ItemData[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
//            pDialog = new ProgressDialog(getActivity());
//            pDialog.setMessage("Please wait...");
//            pDialog.setCancelable(false);
//            pDialog.show();

        }

        @Override
        protected ItemData[] doInBackground(Void... arg0) {
            // Creating service handler class instance
            com.FBLoginSample.ServiceHandler sh = new com.FBLoginSample.ServiceHandler();

            // Making a request to url and getting response

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("stationid",""+returnedID));
            String jsonStr = sh.makeServiceCall(url, com.FBLoginSample.ServiceHandler.POST , nameValuePairs);//adding params


            // String jsonStr = sh.makeServiceCall(url, ServiceHandler.POST);//adding params

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    metrostation = jsonObj.getJSONArray("pass stations");

                    if(metrostation.length()!=0) {

                        itemsData = new ItemData[metrostation.length()];
                        for (int i = 0; i < metrostation.length(); i++) {
                            JSONObject c = metrostation.getJSONObject(i);
                            String st_name = c.getString(TAG_NAME);
                            String st_long = c.getString(TAG_LONG);
                            String st_latt = c.getString(TAG_LATT);

                            String st_stauts = c.getString("st_stauts");
                            String st_stations_number = c.getString("st_stations_number");
                            String st_transportation_number = c.getString("st_transportation_number");
                            String st_transportationmean = c.getString("st_transportationmean");


                            itemsData[i] = new ItemData("Metro num:"+st_transportation_number, st_transportationmean, "", st_stauts,"");
                        }
                        // Getting JSON Array node

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {

                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return itemsData;
        }

        @Override
        protected void onPostExecute(ItemData[] result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
//            if (pDialog.isShowing())
//                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */


            mAdapter  = new MyAdapter(result);
            // 4. set adapter
            recyclerView.setAdapter(mAdapter);

        }

    }


    public void getStationID (int id)
    {
        Log.d("stationID" ,""+id);
        returnedID=id;
    }
}
