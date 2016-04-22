package com.razorreborn.robocar;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Kiran Anto aka RazorSharp on 21/4/2016.
 * For more Info Contact
 * Kirananto@gmail.com
 * 9495333724
 * All Copyrights Reserved 2016
 */

public class Global extends AppCompatActivity {
    public static String deviceName;
    public static BluetoothDevice device;
    private static BluetoothSocket btSocket = null;
    private final String address = null;
    private ProgressDialog progress;
    private boolean isBtConnected = false;
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    public class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(getApplicationContext(), "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    BluetoothAdapter myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                Toast.makeText(getApplicationContext(), "Connection Failed. Is it a SPP Bluetooth? Try again.", Toast.LENGTH_SHORT).show();
                finish();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Connected.", Toast.LENGTH_SHORT).show();
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }

    public void sentDirection(String DIRECTION)
    {
        if (btSocket!=null)
        {
            try
            {
                switch(DIRECTION)
                {
                    case "left" : btSocket.getOutputStream().write("L".getBytes());
                                    break;
                    case "forward" : btSocket.getOutputStream().write("F".getBytes());
                        break;
                    case "forwardright" : btSocket.getOutputStream().write("I".getBytes());
                        break;
                    case "forwardleft" : btSocket.getOutputStream().write("G".getBytes());
                        break;
                    case "backward" : btSocket.getOutputStream().write("B".getBytes());
                        break;
                    case "backwardleft" : btSocket.getOutputStream().write("H".getBytes());
                        break;
                    case "backwardright" : btSocket.getOutputStream().write("J".getBytes());
                        break;
                    case "right" : btSocket.getOutputStream().write("R".getBytes());
                        break;
                }
            }
            catch (IOException e)
            {
               e.printStackTrace();
            }
        }
    }

}
