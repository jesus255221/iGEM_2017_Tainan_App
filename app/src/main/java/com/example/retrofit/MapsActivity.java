package com.example.retrofit;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private ArrayList<Double> longitude = new ArrayList<>();
    private ArrayList<Double> latituude = new ArrayList<>();
    private ArrayList<LatLng> latLngs = new ArrayList<>();
    private Runnable runnable;
    private Handler handler = new Handler();
    private GoogleMap mMap;
    private final int MY_LOCATION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Button Update_Button = (Button) findViewById(R.id.update_button);
        Update_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetData();
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(getApplicationContext(), "Ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST_CODE);
        }
        /*LatLng sydney = new LatLng(22.995571, 120.221539);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker of my home"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
        runnable = new Runnable() {
            @Override
            public void run() {
                if (isNetworkConnected()) {
                    Retrofit retrofit = new Retrofit
                            .Builder()
                            .baseUrl("http://jia.ee.ncku.edu.tw")
                            .addConverterFactory(GsonConverterFactory.create()).build();
                    MainActivity.Service service = retrofit.create(MainActivity.Service.class);
                    Call<locationsResponse> get = service.GetLocations();
                    get.enqueue(new Callback<locationsResponse>() {
                        @Override
                        public void onResponse(Call<locationsResponse> call, Response<locationsResponse> response) {
                            for (int i = 0; i < response.body().getLocations().size(); i++) {
                                latituude.add(i, response.body().getLocations().get(i).getLatitude() / 100);
                                longitude.add(i, response.body().getLocations().get(i).getLongitude() / 100);
                                latLngs.add(i, new LatLng(latituude.get(i), longitude.get(i)));
                                if (i == (response.body().getLocations().size() - 1)) {
                                    mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(latituude.get(i),longitude.get(i)))
                                    );
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<locationsResponse> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), t.getMessage().toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                    PolylineOptions rectOptions = new PolylineOptions();
                    rectOptions.addAll(latLngs);
                    mMap.addPolyline(rectOptions);
                    latituude.clear();
                    longitude.clear();
                    latLngs.clear();
                } else {
                    Toast.makeText(getApplicationContext(), "NetWorkERROR", Toast.LENGTH_SHORT).show();
                }
                handler.postDelayed(this, 10000);
            }
        };
        handler.post(runnable);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_LOCATION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Location permissions ERROR", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    public void GetData() {
        //Toast.makeText(getApplicationContext(), "Get", Toast.LENGTH_SHORT).show();
        if (isNetworkConnected()) {
            Retrofit retrofit = new Retrofit
                    .Builder()
                    .baseUrl("http://jia.ee.ncku.edu.tw")
                    .addConverterFactory(GsonConverterFactory.create()).build();
            MainActivity.Service service = retrofit.create(MainActivity.Service.class);
            Call<locationsResponse> get = service.GetLocations();
            get.enqueue(new Callback<locationsResponse>() {
                @Override
                public void onResponse(Call<locationsResponse> call, Response<locationsResponse> response) {
                    for (int i = 0; i < response.body().getLocations().size(); i++) {
                        latituude.add(i, response.body().getLocations().get(i).getLatitude() / 100);
                        longitude.add(i, response.body().getLocations().get(i).getLongitude() / 100);
                        latLngs.add(i, new LatLng(latituude.get(i), longitude.get(i)));
                    }
                    //Toast.makeText(getApplicationContext(), "Response", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<locationsResponse> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), t.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
            });
            PolylineOptions rectOptions = new PolylineOptions();
            rectOptions.addAll(latLngs);
            mMap.addPolyline(rectOptions);
            latituude.clear();
            longitude.clear();
        } else {
            Toast.makeText(this, "NetWorkERROR", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }
}
