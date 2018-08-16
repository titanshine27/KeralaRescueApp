package in.co.iodev.keralarescue.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import in.co.iodev.keralarescue.R;


public class MainActivity extends Activity {
    private static final int LOCATION_PERMISSIONS_REQUEST = 10;
    Button Malayalam, English, Help_English, Help_malayalam;
    LinearLayout Malayal_layout,status_malayalam;
    LinearLayout English_layout,status_english;
    TextView location_place_english, location_place_malayalam;
    public static final int LOCATION_UPDATE_INTERVAL = 10;  //mins


    String batteryPercentage;

    private View.OnClickListener edit_location_listener = new View.OnClickListener() {
        public void onClick(View view) {
            getLocation();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Malayalam = findViewById(R.id.malayalam);
        English = findViewById(R.id.english);
        Malayal_layout = findViewById(R.id.malayalam_layout);
        English_layout = findViewById(R.id.english_layout);
        Help_English = findViewById(R.id.get_help_english);
        Help_malayalam = findViewById(R.id.get_help_malayalam);

        status_english = findViewById(R.id.help_status_english);
        status_malayalam = findViewById(R.id.help_status_malayalam);
        status_malayalam.setVisibility(View.GONE);
        status_english.setVisibility(View.GONE);

        findViewById(R.id.edit_location_english).setOnClickListener(edit_location_listener);
        findViewById(R.id.edit_location_malayalam).setOnClickListener(edit_location_listener);


        Help_English.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                batteryPercentage = getBatteryPercentage();
                getLocation();

                Help_English.setVisibility(View.GONE);
                status_english.setVisibility(View.VISIBLE);

            }
        });
        Help_malayalam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                batteryPercentage = getBatteryPercentage();
                getLocation();

                Help_malayalam.setVisibility(View.GONE);
                status_malayalam.setVisibility(View.VISIBLE);
            }
        });

        Malayalam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Malayalam.setBackgroundResource(R.drawable.cornered_edges_malayalam_selected);
                English.setBackgroundResource(R.drawable.cornered_edges_english_deselected);
                Malayalam.setTextColor(Color.parseColor("#ffffff"));
                English.setTextColor(Color.parseColor("#ff000000"));
                Malayal_layout.setVisibility(View.VISIBLE);
                English_layout.setVisibility(View.INVISIBLE);
            }
        });

        English.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Malayalam.setBackgroundResource(R.drawable.cornered_edges_malayalam_deselected);
                English.setBackgroundResource(R.drawable.cornered_edges_english_selected);
                Malayalam.setTextColor(Color.parseColor("#ff000000"));
                English.setTextColor(Color.parseColor("#ffffff"));
                Malayal_layout.setVisibility(View.INVISIBLE);
                English_layout.setVisibility(View.VISIBLE);
            }
        });


        location_place_english = findViewById(R.id.location_text_english);
        location_place_malayalam = findViewById(R.id.location_text_malayalam);
    }

    String getBatteryPercentage() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level / (float) scale;
        float p = batteryPct * 100;
        Log.d("Battery percentage", String.valueOf(p));
        return String.valueOf(Math.round(p));
    }

    void getLocation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    new AlertDialog.Builder(this)
                            .setTitle("Give location permission")
                            .setMessage("Location permission is needed for this app")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                                }
                            })
                            .create()
                            .show();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            }
        }
        getPosition();
    }

    public void getPosition() {
        LocationRequest mLocationRequest;
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    Log.d("location", location.getLatitude() + " " + location.getLongitude());

                    Geocoder gcd = new Geocoder(MainActivity.this, Locale.getDefault());
                    List<Address> addresses = null;
                    try {
                        addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (addresses.size() > 0) {
                        location_place_english.setText(addresses.get(0).getLocality());
                        location_place_malayalam.setText(addresses.get(0).getLocality());
                    }

                }


            }
        };
        mLocationRequest = new LocationRequest();
        int inteval_ms = LOCATION_UPDATE_INTERVAL * 60 * 1000;
        mLocationRequest.setInterval(inteval_ms);
        mLocationRequest.setFastestInterval(inteval_ms);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSIONS_REQUEST);
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        getLocation();
                    }
                }else{
                    Toast.makeText(this, "Please provide the permission", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}