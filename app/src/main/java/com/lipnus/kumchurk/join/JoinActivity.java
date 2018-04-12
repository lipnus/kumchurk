package com.lipnus.kumchurk.join;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lipnus.kumchurk.R;

public class JoinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
    }

    public void onClick_boy(View v){
        Intent iT = new Intent(getApplicationContext(), Join2Activity.class);
        startActivity(iT);
    }

    public void onClick_girl(View v){
        Intent iT = new Intent(getApplicationContext(), Join2Activity.class);
        startActivity(iT);
    }
}
