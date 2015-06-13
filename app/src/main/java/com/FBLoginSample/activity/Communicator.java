package com.FBLoginSample.activity;

/**
 * Created by HalimaHanafy on 5/20/2015.
 */
public interface Communicator {
    public void respond(String[] stations,String[] stationType);
    public void openMetroLineFragment (int stationId , android.support.v4.app.Fragment fragment);
    public void openBusLineFragment (int stationId , android.support.v4.app.Fragment fragment);
}
