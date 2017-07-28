package com.dps924.mithilan.workshop8;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    ArrayList<Object> locations = new ArrayList<>();

    TextView timer;
    Button start, pause, reset, viewMap;

    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L;

    Handler handler;
    //TrackLocationService trackLocationService = new TrackLocationService();

    int Seconds, Minutes, Milliseconds;
    //Intent serviceIntent = new Intent(this,TrackLocationService.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get and Attach Buttons
        timer = (TextView) findViewById(R.id.textView);
        start = (Button) findViewById(R.id.button);
        pause = (Button) findViewById(R.id.button2);
        reset = (Button) findViewById(R.id.button3);
        viewMap = (Button) findViewById(R.id.button4);

        //Get Location Services
        locationManager =(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

        handler = new Handler();

        start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                StartTime = SystemClock.uptimeMillis();
                handler.postDelayed(runnable, 0);
                reset.setEnabled(false);
                //trackLocationService.startService(serviceIntent);
                if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    Log.i("Alert", "No Location Permission");
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }

                else {

                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if(lastKnownLocation != null) {
                        LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                        locations.add(userLocation);
                        Log.i("Location", lastKnownLocation.toString());
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }

            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimeBuff += MillisecondTime;

                handler.removeCallbacks(runnable);

                reset.setEnabled(true);
                //trackLocationService.stopService(serviceIntent);

            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MillisecondTime = 0L;
                StartTime = 0L;
                TimeBuff = 0L;
                UpdateTime = 0L;
                Seconds = 0;
                Minutes = 0;
                Milliseconds = 0;
                timer.setText("00:00:00");
                locations.clear();
            }
        });

        viewMap.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("locations",locations);
                intent.putExtra("Bundle", args);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode ==1){
            if(grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                }
            }
        }
    }

    public Runnable runnable = new Runnable() {

        public void run() {

            MillisecondTime = SystemClock.uptimeMillis() - StartTime;

            UpdateTime = TimeBuff + MillisecondTime;

            Seconds = (int) (UpdateTime / 1000);

            Minutes = Seconds / 60;

            Seconds = Seconds % 60;

            Milliseconds = (int) (UpdateTime % 1000);

            timer.setText("" + Minutes + ":"
                    + String.format("%02d", Seconds) + ":"
                    + String.format("%03d", Milliseconds));

            handler.postDelayed(this, 0);
    }

    };


    final LocationListener locationListener= new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Toast.makeText(MainActivity.this, location.toString(), Toast.LENGTH_SHORT).show();
            // Add a marker in Sydney and move the camera
            LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());
            locations.add(userLocation);
            Log.i("Location",userLocation.toString());

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
}

