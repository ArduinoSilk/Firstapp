package com.arduino;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


/**
 * Created by ES29 on 3/5/2016.
 */
public class BluetoothData extends Activity {

    private static final String TAG = "BluetoothData";
    private static final boolean D = true;
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    private BluetoothAdapter mBtadapter;
    private ArrayAdapter<String> mPairedDeviceArrayAdapter;
    private ListView pairedlistview;
    Set<BluetoothDevice> pairedDevice;
    private final UUID my_UUID = UUID.fromString("00001802-0000-1000-8000-00805f9b34fb");
    TextView textView1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_list);
        checkBtState();
        bluetoothlist();

    }

    @Override
    public void onResume() {

        super.onResume();
    }

        private void bluetoothlist(){

            textView1 = (TextView) findViewById(R.id.bttext1);
            textView1.setTextSize(40);
            textView1.setText(" ");
            mPairedDeviceArrayAdapter = new ArrayAdapter<String>(this, R.layout.bluetooth_list,0);
            pairedlistview = (ListView) findViewById(R.id.btlist);
            pairedlistview.setOnItemClickListener(mDeviceClickListener);

            IntentFilter filter =new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(Receiver,filter);
            filter =new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(Receiver,filter);
            mBtadapter=BluetoothAdapter.getDefaultAdapter();

            String lastusedremotedevice = getlastusedremotedevice();
            if (lastusedremotedevice != null) {
                Set<BluetoothDevice> paireddevices = mBtadapter.getBondedDevices();


                if (paireddevices.size() > 0) {
                    findViewById(R.id.bttext1).setVisibility(View.VISIBLE);
                    for (BluetoothDevice device : paireddevices) {
                        mPairedDeviceArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    }
                } else {
                    String nodevices = getResources().getText(R.string.bluetooth_none_paired).toString();
                    mPairedDeviceArrayAdapter.add(nodevices);
                }
            }
            dodiscovery();
            pairedlistview.setAdapter(mPairedDeviceArrayAdapter);
        }
    private String getlastusedremotedevice(){
        SharedPreferences prefs=getPreferences(MODE_PRIVATE);
        String result=prefs.getString("LAST_REMOTE_DEVICE_ADDRESS",null);
        return result;
    }
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
            textView1.setText("connecting .....");
                    try {
                        mBtadapter.cancelDiscovery();
                        final sddata senddata =new sddata(view);
                        setContentView(R.layout.pickup_image);
                        Button sendbt =(Button) findViewById(R.id.btnsend);
                        sendbt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                senddata.sendmessage();

                            }

                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
        }
    };

    final BroadcastReceiver Receiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action= intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)){
                Toast.makeText(getBaseContext(),"New device found",Toast.LENGTH_LONG).show();
                BluetoothDevice device =intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() !=BluetoothDevice.BOND_BONDED) {
                    mPairedDeviceArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    mPairedDeviceArrayAdapter.notifyDataSetChanged();
                }

            }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                setTitle(R.string.select_device);
                if (mPairedDeviceArrayAdapter.getCount() ==0){
                    String nodevices =getResources().getText(R.string.bluetooth_none_paired).toString();
                    mPairedDeviceArrayAdapter.add(nodevices);
                }
            }

        }
    };
    private void checkBtState(){
        BluetoothAdapter mBtadapter =BluetoothAdapter.getDefaultAdapter();
        if (mBtadapter == null){
            Toast.makeText(getBaseContext(),"Device doesn't support bluetooth", Toast.LENGTH_LONG).show();
        }else {
            if (mBtadapter.isEnabled()){
                Toast.makeText(getBaseContext(),"Bluetooth is already ON", Toast.LENGTH_LONG).show();
            }else {
                Intent enableBTintent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBTintent, 1);
                Toast.makeText(getBaseContext(),"Turning on Bluetooth", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(mBtadapter != null){
            mBtadapter.cancelDiscovery();
            mBtadapter.disable();
        }
        unregisterReceiver(Receiver);
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    private void dodiscovery(){
        setTitle(R.string.scanning);

        if (mBtadapter.isDiscovering()){
            mBtadapter.cancelDiscovery();
        }
        mBtadapter.startDiscovery();

    }

 class sddata extends Thread {
     private BluetoothDevice connect_device = null;
     private OutputStream mOutputStream = null;
     private InputStream mInputStream = null;
     private BluetoothSocket socket = null;


     public sddata(View view) throws IOException {
         String info = ((TextView) view).getText().toString();
         String address = info.substring(info.length() - 17);
         connect_device = mBtadapter.getRemoteDevice(address);

         try {
             socket = connect_device.createRfcommSocketToServiceRecord(my_UUID);
             socket.connect();


         } catch (IOException e) {
             try {
                 socket.close();
             } catch (IOException e1) {
                 e1.printStackTrace();
             }
             e.printStackTrace();
         }
         mBtadapter.cancelDiscovery();
         try {
             mOutputStream = socket.getOutputStream();
             mInputStream = socket.getInputStream();
         } catch (IOException e) {

         }
     }

     public void sendmessage(){
         mBtadapter =BluetoothAdapter.getDefaultAdapter();
         Bitmap bm= BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher);
         ByteArrayOutputStream boas =new ByteArrayOutputStream();
         bm.compress(Bitmap.CompressFormat.JPEG,100,boas);
         byte[] b=boas.toByteArray();
         try {
             mOutputStream.write(b);
             mOutputStream.flush();
         } catch (IOException e) {
             e.printStackTrace();
         }
     }


 }
}
