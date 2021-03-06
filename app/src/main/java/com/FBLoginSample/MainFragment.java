package com.FBLoginSample;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {

    String profileFname,profilelname,profileid,profilepic,imgurl,username;
    Button loginme,signmeUp;
    public static boolean signedIn = false;
    SharedPreferences sharedPref;

    boolean facebook_login = false;

    private ProgressDialog pDialog;
    private static String url = "http://gate-info.com/transportation/public/webservice/loginservice";
    public static String emailData;
    public static String passwordData;
    public TextView errormsg;
    private static final String TAG_LOGINCONTENTS = "logincontents";
    private static final String TAG_PASSWORD = "password";
    private static final String TAG_EMAIL = "email";
    JSONArray contents = null;



    private CallbackManager mCallbackManager;

    private AccessTokenTracker mTokenTracker;
    private ProfileTracker mProfileTracker;

    TextView forgetpass;


    private FacebookCallback<LoginResult> mCallback=new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {

            AccessToken accessToken=loginResult.getAccessToken();

            // App code
            final GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {

                            // Application co
                            Log.v("halima",object.toString());
                            Log.v("LoginActivity", response.toString());
                            //
                            String email= "";
                            try {
                                email = response.getJSONObject().getString("email");
                                username=response.getJSONObject().getString("name");
                                profileid = response.getJSONObject().getString("id");
                                imgurl=object.getJSONObject("picture").getJSONObject("data").getString("url");

                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            Log.d("FB Email:", email);
                            //
                            Intent i = new Intent(getActivity(), myprof.class);

                            i.putExtra("user_name",username);
                            i.putExtra("facebook_id", profileid);
                            i.putExtra("pic", imgurl);
                            i.putExtra("email",email);
                            facebook_login = true;
                            i.putExtra("facebook_login",facebook_login);

                            //

                            //Save user data to sharepreferences
                            signedIn = true;
                            SharedPreferences preferences = getActivity().getSharedPreferences("transportation", getActivity().getApplicationContext().MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("signedIn", signedIn);
                            editor.commit();

                            //

                            startActivity(i);

                        }
                    });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender,picture, birthday");

            request.setParameters(parameters);
            request.executeAsync();


        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException e) {

        }
    };
    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        mCallbackManager=CallbackManager.Factory.create();
        mTokenTracker=new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken old, AccessToken newToken) {

            }
        };

        mProfileTracker=new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {

                welcomemessage(newProfile);
            }
        };
        mTokenTracker.startTracking();
        mProfileTracker.startTracking();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }


    public void welcomemessage(Profile profile){
        if(profile != null)
        {
            profileFname=profile.getFirstName();
            profilelname=profile.getLastName();
            profileid=profile.getId();

            profilepic=profile.getProfilePictureUri(600,600).toString();
        }
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //test
        Button go= (Button) view.findViewById(R.id.gobtn);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity().getBaseContext(),test.class);
                startActivity(i);
            }
        });



        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        signmeUp= (Button) view.findViewById(R.id.signup_btn);
        forgetpass= (TextView) view.findViewById(R.id.forget_txt);
        LoginButton loginButton= (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday, user_friends"));
        loginButton.setFragment(this);
        Typeface face= Typeface.createFromAsset(getActivity().getAssets(), "fonts/BittersweetNF.otf");

        signmeUp.setTypeface(face);

        forgetpass.setTypeface(face);
        loginButton.registerCallback(mCallbackManager, mCallback);

        forgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),com.FBLoginSample.forgetPass.class);
                startActivity(intent);
            }
        });

        /* layout*/
//        loginButton.setBackgroundResource(R.drawable.fb);
//        loginButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
//        loginButton.setCompoundDrawablePadding(0);
//        loginButton.setPadding(0, 0, 0, 0);
//        loginButton.setText("fbbbbbbbbb");
//


        signmeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),com.FBLoginSample.signup.class);
                // intent.putExtra("user_id",id);
                startActivity(intent);

            }
        });

        final EditText email=(EditText)view.findViewById(R.id.emailtxt);
        final EditText password=(EditText)view.findViewById(R.id.passtxt);
        Button sign_btn=(Button)view.findViewById(R.id.email_sign_in_button);
        errormsg= (TextView) view.findViewById(R.id.errormsg);


        sign_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Log.d("bttttn","here");
                emailData=email.getText().toString().trim();
                passwordData=password.getText().toString();
                if(emailValid(emailData)&&!(passwordData.matches(""))){

                   // Toast.makeText(getBaseContext(), "successfully", Toast.LENGTH_SHORT).show();

                    new GetContacts().execute();

                }
                else
                {
                    if(emailValid(emailData)==false)
                        email.setError("it is not avalid email");
                    if(passwordData.matches(""))
                        password.setError("please insert your password");
                }



            }
        });


    }

    private class GetContacts extends AsyncTask<Void, Void, String> {

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
        protected String doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("email", emailData));
            nameValuePairs.add(new BasicNameValuePair("password", passwordData));
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.POST,nameValuePairs);//adding params

            Log.d("Response: ", "> " + jsonStr);

            return jsonStr;
        }

        @Override
        protected void onPostExecute(String jsonStr) {
            super.onPostExecute(jsonStr);
            // Dismiss the progress dialog
            Log.d("ana", "hna");
            /*if (pDialog.isShowing())
                pDialog.dismiss();*/

            //

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    String status = jsonObj.getJSONArray(TAG_LOGINCONTENTS).getJSONObject(0).getString("status");
                    if(status.equals("1")){     //correct

                        String id = jsonObj.getJSONArray(TAG_LOGINCONTENTS).getJSONObject(0).getString("user_id");
                        String username = jsonObj.getJSONArray(TAG_LOGINCONTENTS).getJSONObject(0).getString("user_name");


                        Intent intent=new Intent(getActivity(),myprof.class);
                        intent.putExtra("id",id);
                        intent.putExtra("user_name",username);
                        intent.putExtra("email",emailData);
                        intent.putExtra("password",passwordData);
                        intent.putExtra("facebook_login",facebook_login);

                        //       //save path to shared preference
//
                        SharedPreferences preferences = getActivity().getSharedPreferences("transportation", getActivity().getApplicationContext().MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("profile.jpg_path",  "");
                        editor.putString("user_name",username);
                        editor.putString("facebook_id","");
                        editor.putString("email",emailData);
                        editor.putString("user_id",id);
                        editor.commit();
                        //
                        //Save user data to sharepreferences
                        signedIn = true;
                        preferences = getActivity().getSharedPreferences("transportation", getActivity().getApplicationContext().MODE_PRIVATE);
                        editor = preferences.edit();
                        editor.putBoolean("signedIn", signedIn);
                        editor.commit();
                        //
                        startActivity(intent);
                    }
                    else if(status.equals("0")){//error email

                        errormsg.setText("please Enter valid email number");
                    }
                    else if(status.equals("-1")){//error password

                        errormsg.setText("please Enter valid password number");
                    }
                    else{
                        //connection error
                        errormsg.setText("Connection error");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            //

        }

    }

    public boolean emailValid(String evalid){
        String emailreg ="^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        if(evalid.matches(emailreg))

            return true;
        else
            return false;
    }
    @Override
    public void onResume() {
        super.onResume();

        Profile profile=Profile.getCurrentProfile();
        welcomemessage(profile);

    }



    @Override
    public void onStop() {
        super.onStop();
        mTokenTracker.stopTracking();
        mProfileTracker.stopTracking();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

    }
}
