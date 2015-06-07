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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MessagesFragment extends Fragment {

    private ProgressDialog pDialog;
    private static final String TAG_ID = "st_id";
    private static final String TAG_NAME = "st_name";
    private static final String TAG_LONG = "st_long";
    private static final String TAG_LATT = "st_latt";
    RecyclerView recyclerView;

    ItemData itemsData[] = new ItemData[]{};

    MyAdapter mAdapter;


    // JSON Node names
    private static final String TAG_metrostationmodel = "metrostationmodel";
    JSONArray metrostation = null;

    private static String url = "http://gate-info.com/transportation/public/webservice/listbusstation";

    public MessagesFragment() {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);
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
            com.FBLoginSample.ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response

            String jsonStr = sh.makeServiceCall(url, ServiceHandler.POST);//adding params

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    metrostation = jsonObj.getJSONArray(TAG_metrostationmodel);

                    itemsData = new ItemData[metrostation.length()];
                    for (int i = 0; i < metrostation.length(); i++) {
                        JSONObject c = metrostation.getJSONObject(i);
                        String st_id = c.getString(TAG_ID);
                        String st_name = c.getString(TAG_NAME);
                        String st_long = c.getString(TAG_LONG);
                        String st_latt = c.getString(TAG_LATT);

                        itemsData[i] = new ItemData(st_name);
                    }

                    // Getting JSON Array node





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


}
