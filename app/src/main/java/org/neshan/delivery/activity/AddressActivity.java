package org.neshan.delivery.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import com.carto.styles.AnimationStyle;
import com.carto.styles.AnimationStyleBuilder;
import com.carto.styles.AnimationType;
import com.carto.styles.MarkerStyle;
import com.carto.styles.MarkerStyleBuilder;
import com.carto.utils.BitmapUtils;

import org.neshan.common.model.LatLng;
import org.neshan.delivery.CompletedOrderActivity;
import org.neshan.delivery.MapActivity;
import org.neshan.delivery.R;
import org.neshan.mapsdk.MapView;
import org.neshan.mapsdk.model.Marker;

public class AddressActivity extends AppCompatActivity {

    private static final int SELECT_LOCATION_REQUEST_CODE = 1001;

    private MapView mapView;
    private AppCompatEditText txtName;
    private AppCompatEditText txtLastname;
    private AppCompatEditText txtAddress;
    private AppCompatEditText txtPostalCode;
    private AppCompatButton btnCompleteOrder;

    private String firstName;
    private String lastName;
    private String address;
    private String postalCode;
    private LatLng selectedLatLng;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String[] arr = result.getData().getStringExtra(MapActivity.LAT_LNG).split(",");
                    selectedLatLng = new LatLng(Double.parseDouble(arr[0]), Double.parseDouble(arr[1]));
                    addMarker(selectedLatLng);
                    mapView.moveCamera(selectedLatLng,0);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        initLayoutReferences();

        mapView.setOnMapClickListener(new MapView.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent intent = new Intent(AddressActivity.this, MapActivity.class);
                activityResultLauncher.launch(intent);
            }
        });

        btnCompleteOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtName.getText().toString() != null && !txtName.getText().toString().isEmpty()) {
                    firstName = txtName.getText().toString();
                }
                if (txtLastname.getText().toString() != null && !txtLastname.getText().toString().isEmpty()) {
                    lastName = txtLastname.getText().toString();
                }
                if (txtAddress.getText().toString() != null && !txtAddress.getText().toString().isEmpty()) {
                    address = txtAddress.getText().toString();
                }
                if (txtPostalCode.getText().toString() != null && !txtPostalCode.getText().toString().isEmpty()) {
                    postalCode = txtPostalCode.getText().toString();
                }
                if (checkDataValidity()) {
                    Intent intent = new Intent(AddressActivity.this, CompletedOrderActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(AddressActivity.this, getString(R.string.please_fill_data_correctly), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void initLayoutReferences() {
        initViews();

    }

    private void initViews() {
        mapView = findViewById(R.id.mapview);
        txtName = findViewById(R.id.txt_receiver_name);
        txtLastname = findViewById(R.id.txt_receiver_lastname);
        txtAddress = findViewById(R.id.txt_address);
        txtPostalCode = findViewById(R.id.txt_postal_code);
        btnCompleteOrder = findViewById(R.id.btn_complete_order);
    }

    private boolean checkDataValidity() {
        if (firstName != null && lastName != null && address != null && postalCode != null && selectedLatLng != null) {
            return true;
        }
        return false;
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