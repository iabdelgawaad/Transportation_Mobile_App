package com.FBLoginSample.activity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

/**
 * Created by rokia on 6/1/2015.
 */
public class ContactUs extends ActionBarActivity {

    private EditText name;
    private EditText email;
    private EditText edit_message;
    private Button send;

    GetUserData userData;

    private static String user_id;
    private static String user_email;
    private static String message;
    SharedPreferences sharedPref;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contactus);

        name= (EditText) findViewById(R.id.editTextName);
        email= (EditText) findViewById(R.id.editTextEmail);
        edit_message= (EditText) findViewById(R.id.editTextMessage);
        send= (Button) findViewById(R.id.buttonSend);


        sharedPref = getSharedPreferences("transportation", getApplicationContext().MODE_PRIVATE);
        String user_name = sharedPref.getString("user_name", null);
        user_id = sharedPref.getString("user_id", null);
        user_email = sharedPref.getString("email", null);

        name.setText(user_name);
        email.setText(user_email);
        message=edit_message.getText().toString();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userData = new GetUserData();
                userData.execute("http://gate-info.com/transportation/public/webservice/contactus");
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
                JSONArray jsonResult = jsonObj.getJSONArray("status");

            } catch (JSONException e) {

                e.printStackTrace();
            }
            super.onPostExecute(s);
        }
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
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("user_id", user_id+""));
                nameValuePairs.add(new BasicNameValuePair("email", user_email));
                nameValuePairs.add(new BasicNameValuePair("message", message));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));



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
}
