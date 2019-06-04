package com.example.lektoraty;

import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private LatLng camera = new LatLng(50.062299, 19.923949);
    private String serverKey = "";
    private LatLng origin = new LatLng(50.062299, 19.923949);
    private LatLng destination = new LatLng(50.064696, 19.926052);
    private LocationManager mLocationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) throws SecurityException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_fragment);

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 100f, mLocationListener);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        provideDirections();
    }


    private void provideDirections() {
        this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera, 15));
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        System.out.println(">>Direction: " + direction);
                        System.out.println(">>Body: " + rawBody);
                        markDirections(direction);
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        System.out.println(">>Error");
                    }
                });
    }

    private void markDirections(Direction direction) {
        if (direction.isOK()) {
            googleMap.addMarker(new MarkerOptions().position(origin));
            googleMap.addMarker(new MarkerOptions().position(destination));
            ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
            googleMap.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 5, Color.RED));
        } else {
            System.out.println(">>Direction not Ok");
        }
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            System.out.println("Location>> " + location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
}
