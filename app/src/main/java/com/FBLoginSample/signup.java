package com.FBLoginSample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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


public class signup extends ActionBarActivity {

    public static String userEmail;
    public static String userPassword;
    public static String userConfirmPass;
    public static String userName;
    Button back;

    private ProgressDialog pDialog;

    // JSON Node names
    private static final String TAG_SignUp = "signupcontents";
    private static final String TAG_ID="user_id";
    private static final String TAG_Status="status";
    JSONArray sign_up = null;

    private static String url = "http://gate-info.com/transportation/public/webservice/signup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);


        final EditText username=(EditText)findViewById(R.id.username);
        final EditText email=(EditText)findViewById(R.id.email);
        final EditText password=(EditText)findViewById(R.id.password);
        final EditText confirmPassword=(EditText)findViewById(R.id.confirmPassword);
        Button signUp = (Button)findViewById(R.id.signUp);
        back=(Button)findViewById(R.id.signinbtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(signup.this, MainActivity.class);
                startActivity(intent);

            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                userEmail = email.getText().toString().trim();
                userName = username.getText().toString();
                userPassword = password.getText().toString();
                userConfirmPass= confirmPassword.getText().toString();

                if (emailValid(userEmail) && !(userPassword.matches("")) && !(userConfirmPass.matches("")) && !(userName.matches(""))) {
                    // Toast.makeText(getBaseContext(), "successfully", Toast.LENGTH_SHORT).show();

                    if(userPassword.equals(userConfirmPass))
                    {


                        new GetSignUp().execute();
                        Toast.makeText(getBaseContext(), "success", Toast.LENGTH_SHORT).show();
                    }

                    else
                    {
                        confirmPassword.setError("Password Doesn't Match Confirm Password! ");

                    }


                } else {
                    if (emailValid(userEmail) == false)
                        email.setError("It is not a valid Email");
                    if (userPassword.matches(""))
                        password.setError("Empty Field! ");
                    if(userConfirmPass.matches(""))
                        confirmPassword.setError("Empty Field!");
                    if(userName.matches(""))
                        username.setError("Empty Field!");
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
            // Showing progress dialog
            pDialog = new ProgressDialog(signup.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("email", userEmail));
            nameValuePairs.add(new BasicNameValuePair("password", userPassword));
            nameValuePairs.add(new BasicNameValuePair("userName",userName));
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.POST,nameValuePairs);//adding params

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    sign_up = jsonObj.getJSONArray(TAG_SignUp);

                    // looping through All Contacts


                    JSONObject c = sign_up.getJSONObject(0);

                    String status = c.getString(TAG_Status);
                    if(status .equals("1"))
                    {
                        String id = c.getString(TAG_ID);
                        Log.d("id",""+id);

                    }

                    else
                    {

                    }


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
