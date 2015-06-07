package com.FBLoginSample.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.FBLoginSample.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

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


public class NearbyFragment extends Fragment {


    public static String flat = "1.396810375642184";
    public static String flong = "103.8185612431347";

    private ProgressDialog pDialog;

    // JSON Node names
    private static final String TAG_STATIONS = "metrostationmodel";
    private static final String TAG_ID="user_id";
    private static final String TAG_STATION_NAME="st_name";
    private static final String TAG_STATION_LATT="st_latt";
    private static final String TAG_STATION_LONG="st_long";
    JSONArray sign_up = null;

    private static String url = "http://gate-info.com/transportation/public/webservice/nearby";


    private static LatLng StaticMe = new LatLng(1.396810375642184 ,103.8185612431347);
    public static GoogleMap map;
    public static TextView latlangtxt;
    GetUserData userData;

    public NearbyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        latlangtxt=(TextView)view.findViewById(R.id.latlang_txt);

        map = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map)).getMap();
        PutPlacesMarker(1.396810375642184 ,103.8185612431347);




        /***********************  IMPORTANT TO GET LOCATION   ************************/
        /*
        LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        MyCurrentLoctionListener locationListener = new MyCurrentLoctionListener();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        */



        //PutPlacesMarker(1.396810375642184, 103.8185612431347);



        //connect Backend
        userData = new GetUserData();
        userData.execute("http://gate-info.com/transportation/public/webservice/nearby");



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
                // Getting JSON Array node
                sign_up = jsonObj.getJSONArray(TAG_STATIONS);
                // looping through All Contacts

                for (int i = 0; i < sign_up.length(); i++) {
                    JSONObject c = sign_up.getJSONObject(i);
                    String st_name = c.getString(TAG_STATION_NAME);
                    String st_latt = c.getString(TAG_STATION_LATT);
                    String st_long = c.getString(TAG_STATION_LONG);

                    LatLng StaticMinee = new LatLng(Double.parseDouble(st_latt),Double.parseDouble(st_long));
                    Marker meHere = map.addMarker(new MarkerOptions().position(StaticMinee).title(st_name).snippet("Near Station"));
                    //PutPlacesMarker(Double.parseDouble(st_latt),Double.parseDouble(st_long));
                    // zoom in the camera to My place
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(StaticMinee, 15));
                    // animate the zoom process
                    map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

                    PolylineOptions rectOptions = new PolylineOptions()
                            .add(StaticMe)
                            .add(StaticMinee); // Closes the polyline.

// Get back the mutable Polyline
                    Polyline polyline = map.addPolyline(rectOptions);


                }


            } catch (JSONException e) {

                e.printStackTrace();
            }

          /*  //send data to tab1
            MainActivity mainActivity = new MainActivity();
            mainActivity.respond(areas);*/
            super.onPostExecute(s);


        }
        //

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
                // Making a request to url and getting response
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("latt", flat));
                nameValuePairs.add(new BasicNameValuePair("long", flong));
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


    public void PutPlacesMarker(double h,double l)
    {

        LatLng StaticMine = new LatLng(h ,l);
            /*     My location Static One   */
        Marker meHere = map.addMarker(new MarkerOptions().position(StaticMine).title("ME").snippet("I am here "));
        // zoom in the camera to My place
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(StaticMine, 15));
        // animate the zoom process
        map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

    }


        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_nearby, container, false);

        }
}


