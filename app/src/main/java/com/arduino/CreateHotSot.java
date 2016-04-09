package com.arduino;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by es29 on 3/1/2016.
 */
public class CreateHotSot extends Activity {

    private WifiManager wifiManager;
   @Override
   public void onCreate(Bundle saveInstanceState){
       super.onCreate(saveInstanceState);
       initialize();
   }

    private void initialize(){
         wifiManager= (WifiManager) getSystemService(Context.WIFI_SERVICE);

        if (wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(false);
            Toast.makeText(getApplicationContext(),"wifi is disabled",Toast.LENGTH_LONG).show();

        }
        Method[] wmMethods =wifiManager.getClass().getDeclaredMethods();
        boolean methodfound=false;
        for (Method method: wmMethods){
            if (method.getName().equals("setWifiApEnabled")){

                methodfound=true;
                WifiConfiguration wificonfig= new WifiConfiguration();
                wificonfig.SSID= "arduino";
                wificonfig.preSharedKey = "arduino123";
                wificonfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                wificonfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

                try {
                    boolean apstatus =(Boolean) method.invoke(wifiManager,wificonfig,true);
                    for (Method isWifiApEnabledmethod: wmMethods){
                        if (isWifiApEnabledmethod.getName().equals("isWifiApEnabled")){
                            while (!(Boolean) isWifiApEnabledmethod.invoke(wifiManager)){

                            }
                            for(Method method1: wmMethods){
                                if (method1.getName().equals("getWifiApState")){
                                    int apstate;
                                    apstate =(Integer) method1.invoke(wifiManager);
                                    Log.i(this.getClass().toString(),"Apstate::::" +apstate);
                                }
                            }
                        }
                    }
                    if (apstatus){
                        Toast.makeText(getApplicationContext(),"Hotspot is enabled",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(CreateHotSot.this,ImageDisplay.class);
                        startActivity(intent);
                    }else {
                        Toast.makeText(getApplicationContext(),"Hotspot has failed",Toast.LENGTH_LONG).show();
                    }
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }catch (IllegalArgumentException e){
                    e.printStackTrace();
                }
            }
        }
        if (!methodfound){
            Log.d("Splash Activity","cannot create access point");
        }


    }

    @Override
    protected void onResume(){
        super.onResume();
        if (wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(false);
            Toast.makeText(getApplicationContext(),"wifi is disabled",Toast.LENGTH_LONG).show();

        }


}
    @Override
    protected void onPause(){
        super.onPause();

        if (!wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
            Toast.makeText(getApplicationContext(),"wifi is enabled",Toast.LENGTH_LONG).show();

         }

    }
}
