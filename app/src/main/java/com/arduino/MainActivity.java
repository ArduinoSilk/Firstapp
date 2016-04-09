package com.arduino;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;



public class MainActivity extends Activity {

    final Context context = this;
    Button btnConnect, btnload_picture;
    private static int RESULT_LOAD_IMAGE = 1;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       btnConnect = (Button) findViewById(R.id.btnconnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              final String list[] = {"Bluetooth", "WiFi"};
                                              AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                              builder.setTitle(R.string.choose_popup);
                                              builder.setIcon(R.drawable.ic_list_black_24dp);
                                              builder.setItems(list, new DialogInterface.OnClickListener() {

                                                  @Override
                                                  public void onClick(DialogInterface dialog, int which) {
                                                      if (which == 0) {
                                                          dialog.dismiss();
                                                          Intent myintent = new Intent(MainActivity.this, BluetoothData.class);
                                                          startActivity(myintent);
                                                      }
                                                      if (which == 1) {
                                                          dialog.dismiss();
                                                          Intent myintent = new Intent(MainActivity.this, CreateHotSot.class);
                                                          startActivity(myintent);
                                                      }
                                                  }
                                              });

                                              builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                                  @Override
                                                  public void onClick(DialogInterface dialog, int which) {
                                                      dialog.dismiss();
                                                  }
                                              });
                                              AlertDialog alertDialog = builder.create();
                                              alertDialog.show();

                                          }

                                      }
        );



    }



    @Override
    protected void onResume(){
        super.onResume();

    }
    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
