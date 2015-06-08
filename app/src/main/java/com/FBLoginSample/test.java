package com.FBLoginSample;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by HalimaHanafy on 6/6/2015.
 */
public class test extends Activity {

    TextView t1,t2;
    EditText e1,e2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen2);
        Typeface face= Typeface.createFromAsset(getAssets(), "fonts/BittersweetNF.otf");

//        t1= (TextView) findViewById(R.id.txt1);
//        t2= (TextView) findViewById(R.id.txt2);
//        e1= (EditText) findViewById(R.id.etxt1);
//        e2= (EditText) findViewById(R.id.etxt2);
//
//        t1.setTypeface(face);
//        t2.setTypeface(face);
//        e1.setTypeface(face);
//        e2.setTypeface(face);

    }
}
