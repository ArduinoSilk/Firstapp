package com.arduino.DataServer;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;

import com.arduino.R;

/**
 * Created by ES29 on 3/8/2016.
 */
public class BluetoothFileSend extends Activity {

    private GridView gridView;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.pickup_image);

        gridView =(GridView)findViewById(R.id.grdimgview);


    }
}
