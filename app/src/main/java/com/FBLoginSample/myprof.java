package com.FBLoginSample;

import android.app.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.FBLoginSample.mainscreenss.MainMenu;

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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HalimaHanafy on 5/14/2015.
 */


public class myprof extends Activity {

    public static String userName,userid,userpic;
    TextView name,id;
    ImageView imageView;
    Button skip;

    public static String userEmail;
    public static String userPassword;
    public static String userConfirmPass;
    private ProgressDialog pDialog;
    private static String url_login = "http://gate-info.com/transportation/public/webservice/loginservice";
    private static String url_sign_up = "http://gate-info.com/transportation/public/webservice/signup";

    JSONArray sign_up = null;
    // JSON Node names
    private static final String TAG_SignUp = "signupcontents";
    private static final String TAG_ID="user_id";
    private static final String TAG_Status="status";
    public TextView errormsg;
    private static final String TAG_LOGINCONTENTS = "logincontents";
    private static final String TAG_PASSWORD = "password";
    private static final String TAG_EMAIL = "email";
    JSONArray contents = null;



    private static final int SELECT_PICTURE = 1;

    private String selectedImagePath;
    private ImageView browseimg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        name= (TextView) findViewById(R.id.name_txtview);
        imageView = (ImageView) findViewById(R.id.user_imgview);
        browseimg = (ImageView)findViewById(R.id.browseimage);
        skip= (Button) findViewById(R.id.skip);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getBaseContext(),com.FBLoginSample.activity.MainActivity.class);
                startActivity(i);
            }
        });

        browseimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);

            }
        });


        /*GET DATA FROM FACEBOOK LOGIN*/
        Bundle extras = getIntent().getExtras();
        userName = extras.getString("user_name");
        boolean facebook_login = extras.getBoolean("facebook_login",false);

        if (facebook_login)
            userpic=extras.getString("pic");

        userEmail = extras.getString("email");

        if (!facebook_login)
        userPassword = extras.getString("password");

        if (facebook_login)
            userPassword = extras.getString("facebook_id");

        name.setText(userName);

        Toast.makeText(getBaseContext(),"link"+userid,Toast.LENGTH_SHORT).show();
        Toast.makeText(getBaseContext(),"befor get",Toast.LENGTH_SHORT).show();

        if (facebook_login)
            new GetContacts().execute();

        Toast.makeText(getBaseContext(),"after get",Toast.LENGTH_SHORT).show();
/*
        userData.execute("http://10.2.26.12/transportation/public/webservice/loginservice");
        Toast.makeText(getBaseContext(),"connect",Toast.LENGTH_SHORT).show();
*/


    }


    /* browse image*/
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
                System.out.println("Image Path : " + selectedImagePath);
                imageView.setImageURI(selectedImageUri);
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    /////////////////////////////////
    public class DownloadTask extends AsyncTask<String, Void, Bitmap> {
        Bitmap bitmap = null;


        @Override
        protected Bitmap doInBackground(String... params)
        {
            String url = params[0];
            bitmap = downloadImage(url);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);

            if (result != null) {
                imageView.setImageBitmap(result);

                //save to internal memory
                String profile_img_path = saveToInternalSorage(result);
                //save path to shared preference
//
                SharedPreferences preferences = getSharedPreferences("transportation", getApplicationContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("profile.jpg_path",  profile_img_path);
                editor.putString("user_name",userName);
                editor.putString("facebook_id",userid);

                editor.putString("email",userEmail);
                editor.putString("user_id",userid);
                editor.commit();
                //


            }
        }
    }

    private String saveToInternalSorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(mypath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }

    // Method to download using URL class

    public Bitmap downloadImage(String url) {
        Bitmap bitmap = null;

        try {

            InputStream ips = (InputStream) new URL(url).getContent();
            bitmap = BitmapFactory.decodeStream(ips);
            ips.close();
        } catch (Exception e) {
            return null;
        }

        return bitmap;

    }

    public class GetUserData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            return getDataFromBackend(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("result:", s);
            JSONObject returnedObject = null;
            JSONArray resultArray;
            try {
                resultArray = new JSONArray(s);

                for (int i = 0; i < resultArray.length(); i++) {
                    returnedObject = resultArray.getJSONObject(i);
                    String user_id = returnedObject.getString("user_id");
                    Toast.makeText(getApplicationContext(), user_id,
                            Toast.LENGTH_SHORT).show();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(s);
        }
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
            nameValuePairs.add(new BasicNameValuePair("email", userEmail));
            nameValuePairs.add(new BasicNameValuePair("password", userPassword));
            String jsonStr = sh.makeServiceCall(url_login, ServiceHandler.POST,nameValuePairs);//adding params

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

                        userid=jsonObj.getJSONArray(TAG_LOGINCONTENTS).getJSONObject(0).getString("user_id");
                        new DownloadTask().execute(userpic);

                    }
                    else if(status.equals("0")){//error email

                        //  errormsg.setText("please Enter valid email number");

                        //SignUp
                        GetSignUp getSignUp = new GetSignUp();
                        getSignUp.execute();

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
            nameValuePairs.add(new BasicNameValuePair("phone",userid));
            nameValuePairs.add(new BasicNameValuePair("password", userid));
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
            // Log.d("HTTP Exception", "HTTP ERROR GUIDE_1");
        } catch (IOException e) {
            // Log.d("HTTP Exception", "HTTP ERROR GUIDE_2");
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


    //save user data to database
    private class GetSignUp extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(myprof.this);
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
            String jsonStr = sh.makeServiceCall(url_sign_up, ServiceHandler.POST,nameValuePairs);//adding params

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

            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute();

        }

    }
    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
