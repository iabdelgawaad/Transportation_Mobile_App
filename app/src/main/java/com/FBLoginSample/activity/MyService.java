package com.FBLoginSample.activity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MyService extends Service {
    BroadcastReceiver mReceiver;
    private static final String TAG_listservices = "'contents";
    private  static JSONArray listservice = null;
    private static String url = "http://gate-info.com/transportation/public/webservice/checkdate";
    private  static StorageDatabaseAdapter storageHelper;
    private static String last_inserted_data;
    private static String  last_inserted_data_bus;


    public  MyService()
    {

    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static class NetworkChangeReciver extends BroadcastReceiver {
        Context context ;
        public boolean db_updated = false;
        public  boolean last_inserted=false;
        String url = "http://gate-info.com/transportation/public/webservice/checkdate";


        public NetworkChangeReciver(){

            super();
        }

        @Override
        public void onReceive(final Context mcontext, final Intent intent) {
               context = mcontext ;

                String status = NetworkUtil.getConnectivityStatusString(mcontext);
                Toast.makeText(mcontext,""+status,Toast.LENGTH_SHORT).show();
                storageHelper = new StorageDatabaseAdapter(mcontext);
                if(!(status.equals("Not connected to Internet")))
                {
                    Toast.makeText(mcontext,"in if",Toast.LENGTH_SHORT).show();

                    if(storageHelper.getDataVersion("metrostation")!=null){
                        last_inserted_data=storageHelper.getDataVersion("metrostation");


                       Toast.makeText(context,""+last_inserted_data,Toast.LENGTH_SHORT).show();
                        new GetSignUp().execute(last_inserted_data,"metro");


                    }
                    else
                    {

                        SharedPreferences preferences = context.getSharedPreferences("transportation", context.getApplicationContext().MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("db_updated", true);
                        editor.commit();
                    }

                    if(storageHelper.getDataVersion("bustation")!=null){
                        last_inserted_data_bus=storageHelper.getDataVersion("bustation");


                        Toast.makeText(context,""+ last_inserted_data_bus,Toast.LENGTH_SHORT).show();
                        new GetSignUp().execute(last_inserted_data_bus,"bus");


                    }
                    else
                    {

                        SharedPreferences preferences = context.getSharedPreferences("transportation", context.getApplicationContext().MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("db_updated_bus", true);
                        editor.commit();
                    }




                }
                else
                {

                    SharedPreferences preferences = context.getSharedPreferences("transportation", context.getApplicationContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("db_updated", false);
                    editor.putBoolean("db_updated_bus", false);
                    editor.commit();

                }

                //1- //http request to backend to retrive boolean value
                //1- -1//copy asynck task of bus station



                //2//save to share perefernce
                //Save user data to sharepreferences


                //

            }
            private class GetSignUp extends AsyncTask<String, Void, String[]> {

                String page_type = "";


                @Override
                protected void onPreExecute() {
                    super.onPreExecute();


                }

                @Override
                protected String[]  doInBackground(String... arg0) {
                    page_type = arg0[1];
                    // Creating service handler class instance
                    com.FBLoginSample.ServiceHandler sh = new com.FBLoginSample.ServiceHandler();

                    // Making a request to url and getting response

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("date",""+arg0[0]));
                String jsonStr = sh.makeServiceCall(url, com.FBLoginSample.ServiceHandler.POST , nameValuePairs);//adding params


                // String jsonStr = sh.makeServiceCall(url, ServiceHandler.POST);//adding params

                Log.d("Response: ", "> " + jsonStr);
                String[] status = new String[3];

                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        listservice = jsonObj.getJSONArray("contents");
                        JSONObject c = listservice.getJSONObject(0);
                        status[0] = c.getString("status");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("ServiceHandler", "Couldn't get any data from the url");
                }
                 status[1] = page_type;
                 status[2] = arg0[0];

                return status;
            }

            @Override
            protected void onPostExecute(String[] result) {
                super.onPostExecute(result);

                // 2. set layoutManger

                if(result[0].equals("True")){


                   SharedPreferences preferences = context.getSharedPreferences("transportation", context.getApplicationContext().MODE_PRIVATE);
                   SharedPreferences.Editor editor = preferences.edit();
                    if (result[1].equals("metro")) {
                        editor.putBoolean("db_updated", true);
                        editor.putString("last_inserted_date", result[2]);
                    }
                    else
                    {
                        editor.putBoolean("db_updated_bus", true);
                        editor.putString("last_inserted_date_bus", result[2]);
                    }
                   editor.commit();

                }
                else{

                   SharedPreferences preferences = context.getSharedPreferences("transportation", context.getApplicationContext().MODE_PRIVATE);
                   SharedPreferences.Editor editor = preferences.edit();
                   editor.putBoolean("db_updated", false);
                   editor.putBoolean("db_updated_bus", false);

                    editor.commit();

                }

            }

        }


    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction("action");
        filter.addAction("anotherAction");
        mReceiver = new NetworkChangeReciver();
        registerReceiver(mReceiver, filter);
    }

}
