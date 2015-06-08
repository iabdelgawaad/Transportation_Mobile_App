package com.FBLoginSample.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.FBLoginSample.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
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

    Circle mCircle;

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
    JSONArray get_stations = null;

    private static String url = "http://gate-info.com/transportation/public/webservice/nearby";


    private static LatLng StaticMe;
    public static GoogleMap map;
    public static TextView latlangtxt;
    GetUserData userData;
    GPSTracker gps;
    private AutoCompleteTextView source;
    private AutoCompleteTextView destination;
    private Button Searchbtn;
    GetAreas AreaData;

    Marker mMarker;

    private int sourceState=0;
    private int destinationState=0;
    private String JsonArrayName="areas";
    private String TAG_STATION="metrostationmodel";
    private static String tvSource;
    private static String tvdestination;
    private JSONArray jsonResult;
    private String[] areas;
    public static String[] stations =new String[] {};
    public static String[] stationsType= new String[] {};
    private static int SearchbtnAction=0;


    ArrayAdapter<String> adapterStations;




    public NearbyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        AreaData = new GetAreas();
        if (AreaData != null)
            AreaData.execute("http://gate-info.com/transportation/public/webservice/listallarea");

    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //connect Backend
        AreaData = new GetAreas();
        AreaData.execute("http://gate-info.com/transportati…/public/webservice/listmap");

        //declare views
        source = (AutoCompleteTextView) view.findViewById(R.id.autotxt_src);
        destination = (AutoCompleteTextView) view.findViewById(R.id.autotxt_dest);
        Searchbtn=(Button)view.findViewById(R.id.go);

        //Searchbtn control
        Searchbtn.setEnabled(false);
        source.setThreshold(1);
        destination.setThreshold(1);

        //add listener
        source.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sourceState = 1;
                if (destinationState == 1) {
                    Searchbtn.setEnabled(true);
                }
            }
        });

        destination.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                destinationState = 1;
                if (sourceState == 1) {
                    Searchbtn.setEnabled(true);
                }
            }
        });

        Searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvSource = source.getText().toString();
                tvdestination = destination.getText().toString();
                SearchbtnAction = 1;
                AreaData = new GetAreas();
                AreaData.execute("http://gate-info.com/transportation/public/webservice/listmap");
            }
        });



        latlangtxt=(TextView)view.findViewById(R.id.latlang_txt);
        map = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map)).getMap();
        //drawMarkerWithCircle(30.043650, 31.236545);

        map.setMyLocationEnabled(true);

        /////////////////////////////////////////
        gps = new GPSTracker(getActivity());

        // check if GPS enabled
        if(gps.canGetLocation()){

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            StaticMe=new LatLng(latitude,longitude);
            // \n is for new line
            Toast.makeText(getActivity().getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            if(mCircle == null || mMarker == null){
                drawMarkerWithCircle(latitude, longitude,"Me","","","");
            }else{
                updateMarkerWithCircle(latitude, longitude);
            }


        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
        //connect Backend

        //userData = new GetUserData();
        //userData.execute("http://gate-info.com/transportation/public/webservice/nearby");
    }




    public class GetUserData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return getDataFromBackend(params[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();

            try {

                map.clear();
                JSONObject jsonObj = new JSONObject(s);
                // Getting JSON Array node
                sign_up = jsonObj.getJSONArray(TAG_STATIONS);
                // looping through All Contacts

                for (int i = 0; i < sign_up.length(); i++) {
                    JSONObject c = sign_up.getJSONObject(i);
                    String st_name = c.getString(TAG_STATION_NAME);
                    String st_latt = c.getString(TAG_STATION_LATT);
                    String st_long = c.getString(TAG_STATION_LONG);

                    Double la=Double.parseDouble(st_latt);
                    Double lo=Double.parseDouble(st_latt);

                    /*
                    LatLng StaticMinee = new LatLng(Double.parseDouble(st_latt),Double.parseDouble(st_long));
                    Marker meHere = map.addMarker(new MarkerOptions().position(StaticMinee).title(st_name).snippet("Near Station"));
                    //PutPlacesMarker(Double.parseDouble(st_latt),Double.parseDouble(st_long));
                    // zoom in the camera to My place
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(StaticMinee, 15));
                    // animate the zoom process
                    map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

                    */

                    if(mCircle == null || mMarker == null){
                        drawMarkerWithCircle(la, lo,"","","","");
                    }else{
                        updateMarkerWithCircle(la, lo);
                    }




                    /*
                    PolylineOptions rectOptions = new PolylineOptions()
                            .add(StaticMe)
                            .add(StaticMinee); // Closes the polyline.

// Get back the mutable Polyline
                    Polyline polyline = map.addPolyline(rectOptions);
                    */

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
        Marker meHere = map.addMarker(new MarkerOptions().position(StaticMine).title("ME").snippet("I am here in Mac "));
        // zoom in the camera to My place
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(StaticMine, 15));
        // animate the zoom process
        map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

    }


    private void updateMarkerWithCircle(double latt,double lang) {
        LatLng position = new LatLng(latt ,lang);
        mCircle.setCenter(position);
        mMarker = null;
        mMarker.setPosition(position);
    }

    private void drawMarkerWithCircle(double latt,double lang, final String name,String status,String mean,String MorB) {

        LatLng StaticMine = new LatLng(latt ,lang);
        double radiusInMeters = 100.0;
        int strokeColor = 0xffff0000; //red outline
        int shadeColor = 0x44ff0000; //opaque red fill

        CircleOptions circleOptions = new CircleOptions().center(StaticMine).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
        mCircle = map.addCircle(circleOptions);

        MarkerOptions markerOptions = new MarkerOptions().position(StaticMine).title(name).snippet("Mean: "+MorB+" Num "+status+"Type: "+ mean+"this station");
        mMarker = map.addMarker(markerOptions);
        // zoom in the camera to My place
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(StaticMine, 15));
        // animate the zoom process
        map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

        PolylineOptions rectOptions = new PolylineOptions()
                .add(StaticMe)
                .add(StaticMine); // Closes the polyline.

// Get back the mutable Polyline
        Polyline polyline = map.addPolyline(rectOptions);


//        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//
//            @Override
//            public boolean onMarkerClick(Marker arg0) {
//                //if (arg0.getTitle().equals(myMarker_name[arg0.getTitle().])) // if marker source is clicked
//                Toast.makeText(getActivity(), arg0.getTitle(), Toast.LENGTH_SHORT).show();// display toast
//
//                return true;
//            }
//
//        });



    }

        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_nearby, container, false);

        }


    public class GetAreas extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return getDataFromBackendArea(params[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();

            try {

                JSONObject jsonObj = new JSONObject(s);

                if(SearchbtnAction==1){
                    get_stations = jsonObj.getJSONArray("map data");

                    //stations=new String[jsonResult.length()];
                    //stationsType=new String[jsonResult.length()];

                    final String[] myMarker_name = new String[get_stations.length()];
                    for(int i=0;i<get_stations.length();i++){

                        JSONObject g = get_stations.getJSONObject(i);
                        String id=g.getString("st_id");
                        String name=g.getString("st_name");
                        String type=g.getString("st_type");
                        String latt=g.getString("st_latt");
                        String longg=g.getString("st_long");
                        String t_mean=g.getString("st_transportationmean");
                        String t_status=g.getString("st_stauts");



                        myMarker_name[i] = name;

                        /* type: metro or bus */

                        double la=Double.parseDouble(latt);
                        double lo=Double.parseDouble(longg);

                        drawMarkerWithCircle(la, lo, name,t_mean,t_status,type);




                    }



                    SearchbtnAction = 0;


                }else {
                    jsonResult = jsonObj.getJSONArray(JsonArrayName);
                    areas = new String[jsonResult.length()];

                    for(int i=0;i<jsonResult.length();i++){
                        areas[i]=jsonResult.getJSONObject(i).getString("area_name");
                    }
                    //fill&settingg Adapter
                    adapterStations = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,areas);
                    source.setAdapter(adapterStations);
                    destination.setAdapter(adapterStations);

                }


            } catch (JSONException e) {

                e.printStackTrace();
            }

          /*  //send data to tab1
            MainActivity mainActivity = new MainActivity();
            mainActivity.respond(areas);*/

        }
        //

    }
    public static String getDataFromBackendArea(String url) {

        // To Handle USER & PASSAWARD OF ALPHA
        // Create a new HttpClient and Post Header
        // String finalURL = convertURL(url);
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        InputStream inputStream = null;
        StringBuilder result = new StringBuilder();

        try {
            // Add your data
            if(SearchbtnAction==1){
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("areanamefrom", tvSource));
                nameValuePairs.add(new BasicNameValuePair("areanameto", tvdestination));
                nameValuePairs.add(new BasicNameValuePair("offset", "0"));


                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            }


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


