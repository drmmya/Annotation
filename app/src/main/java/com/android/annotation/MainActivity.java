package com.android.annotation;

import android.os.Bundle;
import android.provider.SyncStateContract;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.material.Data;
import com.android.material.Get;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Get.get(this,getPackageName());
        Data.getServers(this, (free, premium) -> {
            // Convert List<JSONObject> to JSON string
            Constants.FREE_SERVERS = new org.json.JSONArray(free).toString();
            Constants.PREMIUM_SERVERS = new org.json.JSONArray(premium).toString();
            Toast.makeText(this, "Data Get Success!", Toast.LENGTH_SHORT).show();
        });


    }
}