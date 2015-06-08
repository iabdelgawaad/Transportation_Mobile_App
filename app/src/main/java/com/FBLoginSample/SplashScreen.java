package com.FBLoginSample;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Created by HalimaHanafy on 5/12/2015.
 */
public class SplashScreen extends Activity {

    // splash screen timer
    private static int SPLASH_TIME_OUT =1000;
    private ViewGroup sceneRoot;
    public static boolean signedIn = false;
    SharedPreferences sharedPref;

    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.splash);
        img= (ImageView) findViewById(R.id.imgLogo);
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity


                //checking sharedPrefs to see if we had signed in already - if we have, skip this activity
                sharedPref = getSharedPreferences("transportation", getApplicationContext().MODE_PRIVATE);
                signedIn = sharedPref.getBoolean("signedIn", false);

                if (signedIn) {
                    Intent intent = new Intent(SplashScreen.this, com.FBLoginSample.activity.MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Intent i = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(i);

                    // close this activity
                    finish();


                }


            }
        }, SPLASH_TIME_OUT);


    }

}

