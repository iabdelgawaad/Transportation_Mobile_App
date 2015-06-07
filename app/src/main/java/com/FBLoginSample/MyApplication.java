package com.FBLoginSample;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by HalimaHanafy on 5/11/2015.
 * My facebook application Class
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        findHashKey();

    }

    public void findHashKey(){
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.FBLoginSample",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }
}
