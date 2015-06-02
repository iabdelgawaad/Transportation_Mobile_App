package com.FBLoginSample;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HalimaHanafy on 5/20/2015.
 */
public class forgetPass extends Activity {

    private ProgressDialog pDialog;
    public static String userEmail;
    private static final String TAG_FORGETPASSWORD = "forgetpasswordcontents";
    private static final String TAG_Status="status";
    JSONArray sign_up = null;
    private static String url = "http://gate-info.com/transportation/public/webservice/forgetpassword";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget);
        final EditText email=(EditText)findViewById(R.id.send_email_txt);
        Button send_email = (Button)findViewById(R.id.send_email);
        send_email.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                userEmail = email.getText().toString().trim();
                if (emailValid(userEmail)) {
                    new GetSignUp().execute();
                    Toast.makeText(getBaseContext(), "sent", Toast.LENGTH_SHORT).show();
                } else {
                    email.setError("It can't be empty! ");

                }
            }
        });
    }

    public boolean emailValid(String evalid){
        String emailreg ="^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        if(evalid.matches(emailreg))

            return true;
        else
            return false;
    }



    private class GetSignUp extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("email", userEmail));

            String jsonStr = sh.makeServiceCall(url, ServiceHandler.POST,nameValuePairs);//adding params

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    sign_up = jsonObj.getJSONArray(TAG_FORGETPASSWORD);

                    // looping through All Contacts


                    JSONObject c = sign_up.getJSONObject(0);

                    String status = c.getString(TAG_Status);
                    if(status .equals("1"))
                    {
                    }
                    else
                    {
                    }

                    // Getting JSON Array node

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */

        }

    }
}




