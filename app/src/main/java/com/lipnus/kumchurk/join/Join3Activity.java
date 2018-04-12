package com.lipnus.kumchurk.join;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.lipnus.kumchurk.MainActivity;
import com.lipnus.kumchurk.R;

public class Join3Activity extends AppCompatActivity {

    EditText idEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join3);

        idEt = (EditText) findViewById(R.id.id_Et);
    }

    public void onClick_enter_id(View v){
        Toast.makeText(getApplicationContext(), idEt.getText() + "님 환영합니다", Toast.LENGTH_LONG).show();
        Intent iT = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(iT);
    }
}
