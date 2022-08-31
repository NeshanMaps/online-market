package org.neshan.delivery;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.carto.styles.AnimationStyle;
import com.carto.styles.AnimationStyleBuilder;
import com.carto.styles.AnimationType;
import com.carto.styles.MarkerStyle;
import com.carto.styles.MarkerStyleBuilder;
import com.carto.utils.BitmapUtils;

import org.neshan.common.model.LatLng;
import org.neshan.mapsdk.MapView;
import org.neshan.mapsdk.model.Marker;

public class MapActivity extends AppCompatActivity {

    public static final String LAT_LNG = "LAT_LNG";

    private MapView mapView;
    private AppCompatButton btnSubmit;

    private LatLng selectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
    }

    @Override
    protected void onStart() {
        super.onStart();

        initLayoutReferences();

        mapView.setOnMapClickListener(new MapView.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                selectedLocation = latLng;
                addMarker(latLng);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedLocation != null) {
                    Intent intent = new Intent();
                    intent.putExtra(LAT_LNG, String.valueOf(selectedLocation.getLatitude() + "," + selectedLocation.getLongitude()));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    private void initLayoutReferences() {
        initViews();
    }

    private void initViews() {
        mapView = findViewById(R.id.mapview);
        btnSubmit = findViewById(R.id.btn_submit_location);
    }

    private Marker addMarker(LatLng loc) {
        AnimationStyleBuilder animStBl = new AnimationStyleBuilder();
        animStBl.setFadeAnimationType(AnimationType.ANIMATION_TYPE_SMOOTHSTEP);
        animStBl.setSizeAnimationType(AnimationType.ANIMATION_TYPE_SPRING);
        animStBl.setPhaseInDuration(0.5f);
        animStBl.setPhaseOutDuration(0.5f);
        AnimationStyle animSt = animStBl.buildStyle();

        MarkerStyleBuilder markStCr = new MarkerStyleBuilder();
        markStCr.setSize(30f);
        markStCr.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(BitmapFactory.decodeResource(getResources(), org.neshan.mapsdk.R.drawable.ic_cluster_marker_blue)));
        markStCr.setAnimationStyle(animSt);
        MarkerStyle markSt = markStCr.buildStyle();

        Marker marker = new Marker(loc, markSt);

        mapView.addMarker(marker);
        return marker;
    }
}