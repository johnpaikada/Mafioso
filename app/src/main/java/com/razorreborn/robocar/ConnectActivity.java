package com.razorreborn.robocar;

/**
 * Created by Kiran Anto aka RazorSharp on 26/3/2016.
 * For more Info Contact
 * Kirananto@gmail.com
 * 9495333724
 * All Copyrights Reserved 2016
 */
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ConnectActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;

    private Button btnScanDevice;
    private TextView stateBluetooth;
    private BluetoothAdapter bluetoothAdapter;
    private RecyclerView mRecyclerView;
    private maincardAdapter mAdapter;
    private final List<maincontent> devices = new ArrayList<>();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_connect);

        Toolbar toolbar = (Toolbar) findViewById(R.id.bluetoothtoolbar);
        if (toolbar != null) {
            toolbar.setLogo(R.mipmap.ic_launcher);
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            setSupportActionBar(toolbar);
        }
        btnScanDevice = (Button)findViewById(R.id.scandevice);
        stateBluetooth = (TextView)findViewById(R.id.bluetoothstate);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        StaggeredGridLayoutManager mSGLM;
        mSGLM = new StaggeredGridLayoutManager(1,1);
        mRecyclerView = (RecyclerView) findViewById(R.id.devicesfound);
        mSGLM.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mSGLM);
        mAdapter = new maincardAdapter(devices,getApplicationContext());
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        CheckBlueToothState();
        btnScanDevice.setOnClickListener(btnScanDeviceOnClickListener);

        registerReceiver(ActionFoundReceiver,
                new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(mPairReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
    }

    public void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
            Global.deviceName= Global.device.getName();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(ActionFoundReceiver);
    }

    private void CheckBlueToothState(){
        if (bluetoothAdapter == null){
            stateBluetooth.setText("Bluetooth NOT support");
        }else{
            if (bluetoothAdapter.isEnabled()){
                if(bluetoothAdapter.isDiscovering()){
                    stateBluetooth.setText("Bluetooth is currently in device discovery process.");
                }else{
                    stateBluetooth.setText("Bluetooth is Enabled.");
                    btnScanDevice.setEnabled(true);
                }
            }else{
                stateBluetooth.setText("Bluetooth is NOT Enabled!");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    private final Button.OnClickListener btnScanDeviceOnClickListener
            = new Button.OnClickListener(){

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            mAdapter.clearData();
            bluetoothAdapter.startDiscovery();
        }};

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if(requestCode == REQUEST_ENABLE_BT){
            CheckBlueToothState();
        }
    }

    private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                Global.device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                maincontent maincontent = new maincontent();
                maincontent.setAddress(Global.device.getAddress());
                maincontent.setName(Global.device.getName());
                devices.add(maincontent);
                //Toast.makeText(ConnectActivity.this, maincontent.getName(), Toast.LENGTH_SHORT).show();
                mAdapter = new maincardAdapter(devices, getApplicationContext());
                mRecyclerView.setAdapter(mAdapter);
            }
        }};
    private void unpairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        private final BroadcastReceiver mPairReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state        = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState    = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                    Toast.makeText(ConnectActivity.this, "Paired", Toast.LENGTH_SHORT).show();
                    Intent maps = new Intent(getApplicationContext(), MapsActivity.class);
                    startActivity(maps);
                } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED){
                    Toast.makeText(ConnectActivity.this, "UnPaired", Toast.LENGTH_SHORT).show();
                }

            }
        }
    };
}