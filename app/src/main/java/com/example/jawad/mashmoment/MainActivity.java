package com.example.jawad.mashmoment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void camera_go(View view)
    {
        {

            Intent camera_go=new Intent(this,AndroidCamera2API.class);
            startActivity(camera_go);
        }
    }
}
