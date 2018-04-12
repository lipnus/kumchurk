package com.lipnus.kumchurk.join;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lipnus.kumchurk.R;

public class Join2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join2);
    }

    public void onClick_junior(View v){
        Intent iT = new Intent(getApplicationContext(), Join3Activity.class);
        startActivity(iT);
    }
}
