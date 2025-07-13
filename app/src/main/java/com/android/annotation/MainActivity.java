package com.android.annotation;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import de.blinkt.openvpn.OpenVpnApi;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.OpenVPNThread;


public class MainActivity extends AppCompatActivity {


    OpenVPNThread vpnThread = new OpenVPNThread();
    OpenVPNService vpnService = new OpenVPNService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}
