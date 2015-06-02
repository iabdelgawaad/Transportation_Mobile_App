package com.FBLoginSample;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
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
        userName = extras.getString("Fname");
        userid = extras.getString("id");
        userpic=extras.getString("pic");
        name.setText(userName);
        Toast.makeText(getBaseContext(),"link"+userid,Toast.LENGTH_SHORT).show();
        Toast.makeText(getBaseContext(),"befor get",Toast.LENGTH_SHORT).show();
        GetUserData userData = new GetUserData();
        Toast.makeText(getBaseContext(),"after get",Toast.LENGTH_SHORT).show();
/*
        userData.execute("http://10.2.26.12/transportation/public/webservice/loginservice");
        Toast.makeText(getBaseContext(),"connect",Toast.LENGTH_SHORT).show();
*/
        new DownloadTask().execute(userpic);

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
            }
        }
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
////////////////////


    public class GetUserData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            return getDataFromBackend(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("result:",s);
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


}
