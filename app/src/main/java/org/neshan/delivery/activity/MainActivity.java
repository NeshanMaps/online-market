package org.neshan.delivery.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.carto.core.ScreenBounds;
import com.carto.core.ScreenPos;
import com.carto.styles.AnimationStyle;
import com.carto.styles.AnimationStyleBuilder;
import com.carto.styles.AnimationType;
import com.carto.styles.MarkerStyle;
import com.carto.styles.MarkerStyleBuilder;
import com.carto.styles.TextStyleBuilder;
import com.carto.utils.BitmapUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.neshan.common.model.LatLng;
import org.neshan.common.model.LatLngBounds;
import org.neshan.delivery.R;
import org.neshan.delivery.database_helper.AssetDatabaseHelper;
import org.neshan.delivery.model.Market;
import org.neshan.mapsdk.MapView;
import org.neshan.mapsdk.model.Marker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private static final float NEAR_MARKETS_DISTANCE_KILOMETERS = 2f;

    private MapView map;

    private SQLiteDatabase db;

    private FusedLocationProviderClient fusedLocationClient;
    private SettingsClient settingsClient;
    private LocationCallback locationCallback;
    private Location userLocation;
    private LocationRequest locationRequest;
    private LocationSettingsRequest locationSettingsRequest;
    private Marker userMarker;
    private BottomSheetDialog travelDetailBottomSheetDialog;
    private List<Market> markets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        initLayoutReferences();

        getMyFakeLocation();

        markets = getMarkets();

        List<LatLng> marketsLatLng = new ArrayList<>();
        for (Market market : markets) {
            Location marketLocation = new Location("");
            marketLocation.setLatitude(market.getLat());
            marketLocation.setLongitude(market.getLng());

            if (marketLocation.distanceTo(userLocation) / 1000 <= NEAR_MARKETS_DISTANCE_KILOMETERS) {
                LatLng latLng = new LatLng(market.getLat(), market.getLng());
                addMarker(market.getId(), latLng);
                marketsLatLng.add(latLng);
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showLatLngsInCamera(marketsLatLng);
            }
        }, 200);

        map.setOnMarkerClickListener(new MapView.OnMarkerClickListener() {
            @Override
            public void OnMarkerClicked(Marker marker) {
                Intent intent = new Intent(MainActivity.this, StoreActivity.class);

                startActivity(intent);
            }
        });

        TextStyleBuilder textStyleBuilder = new TextStyleBuilder();
        textStyleBuilder.setHideIfOverlapped(false);
    }

    private void showLatLngsInCamera(List<LatLng> latLngs) {
        if (latLngs != null && !latLngs.isEmpty()) {
            double minLat = Double.MAX_VALUE;
            double minLng = Double.MAX_VALUE;
            double maxLat = Double.MIN_VALUE;
            double maxLng = Double.MIN_VALUE;
            LatLng northEast;
            LatLng southWest;

            for (LatLng latLng : latLngs) {
                minLat = Math.min(latLng.getLatitude(), minLat);
                minLng = Math.min(latLng.getLongitude(), minLng);
                maxLat = Math.max(latLng.getLatitude(), maxLat);
                maxLng = Math.max(latLng.getLongitude(), maxLng);
            }

            northEast = new LatLng(maxLat, maxLng);
            southWest = new LatLng(minLat, minLng);

            map.moveToCameraBounds(
                    new LatLngBounds(northEast, southWest),
                    new ScreenBounds(
                            new ScreenPos(0, 0),
                            new ScreenPos(map.getWidth(), map.getHeight())
                    ),
                    true, 0.25f);
        }
    }

    private void initLayoutReferences() {
        initViews();

        initMap();
    }

    private void initMap() {
        map.getSettings().setZoomGesturesEnabled(true);
        //temp
        map.getSettings().setZoomControlsEnabled(true);
        //
    }

    private void initViews() {
        map = findViewById(R.id.mapview);
    }

    private void getMyFakeLocation() {
        userLocation = new Location("");
        userLocation.setLatitude(35.701433073);
        userLocation.setLongitude(51.337892468);
        LatLng userLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
        addUserMarker(userLatLng);
        map.moveCamera(userLatLng, .5f);
    }

    private void getMyLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        settingsClient = LocationServices.getSettingsClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                userLocation = locationResult.getLastLocation();

                onLocationChange();
                stopLocationUpdates();
            }
        };

        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();

    }

    public void stopLocationUpdates() {
        fusedLocationClient
                .removeLocationUpdates(locationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }

    private void onLocationChange() {
        if (userLocation != null) {
            addUserMarker(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()));
        }
    }

    private void addUserMarker(LatLng loc) {
        if (userMarker != null) {
            map.removeMarker(userMarker);
        }
        MarkerStyleBuilder markStCr = new MarkerStyleBuilder();
        markStCr.setSize(30f);
        markStCr.setBitmap(com.carto.utils.BitmapUtils.createBitmapFromAndroidBitmap(BitmapFactory.decodeResource(getResources(), org.neshan.mapsdk.R.drawable.ic_marker)));
        MarkerStyle markSt = markStCr.buildStyle();

        userMarker = new Marker(loc, markSt);

        map.addMarker(userMarker);
    }

    private Marker addMarker(String id, LatLng loc) {
        AnimationStyleBuilder animStBl = new AnimationStyleBuilder();
        animStBl.setFadeAnimationType(AnimationType.ANIMATION_TYPE_SMOOTHSTEP);
        animStBl.setSizeAnimationType(AnimationType.ANIMATION_TYPE_SPRING);
        animStBl.setPhaseInDuration(0.5f);
        animStBl.setPhaseOutDuration(0.5f);
        AnimationStyle animSt = animStBl.buildStyle();

        MarkerStyleBuilder markStCr = new MarkerStyleBuilder();
        markStCr.setSize(30f);
        markStCr.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.market)));
        markStCr.setAnimationStyle(animSt);
        MarkerStyle markSt = markStCr.buildStyle();

        Marker marker = new Marker(loc, markSt);
        marker.putMetadata("id", id);

        map.addMarker(marker);
        return marker;
    }

    private List<Market> getMarkets() {
        AssetDatabaseHelper myDbHelper = new AssetDatabaseHelper(this);

        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        try {
            db = myDbHelper.openDataBase();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        Cursor cursor = db.rawQuery("select * from market", null);

        List<Market> markets = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Market market = new Market();
                market.setLat(cursor.getDouble(cursor.getColumnIndex("lat")))
                        .setLng(cursor.getDouble(cursor.getColumnIndex("lng")))
                        .setId(cursor.getString(cursor.getColumnIndex("id")))
                        .setName(cursor.getString(cursor.getColumnIndex("name")));
                markets.add(market);

                cursor.moveToNext();
            }

        }
        cursor.close();

        return markets;
    }

    private void showTravelDetailBottomSheetDialog() {
        travelDetailBottomSheetDialog = new BottomSheetDialog(map.getContext());
        travelDetailBottomSheetDialog.setContentView(R.layout.travel_detail_bottom_sheet);

        AppCompatTextView lblPrice = travelDetailBottomSheetDialog.findViewById(R.id.lbl_price);

        travelDetailBottomSheetDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkLocationPermission() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getLocationPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
    }
}