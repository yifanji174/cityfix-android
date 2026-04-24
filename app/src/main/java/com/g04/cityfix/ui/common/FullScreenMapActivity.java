package com.g04.cityfix.ui.common;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.g04.cityfix.R;
import com.g04.cityfix.data.model.RepairReport;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;
/**
 * The page where user can view map and location
 * @author Jerry Yang
 */
public class FullScreenMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private double lat;
    private double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_select);
        RepairReport report = getIntent().getParcelableExtra("report", RepairReport.class);
        lat = Double.parseDouble(report.getLocation().split(",")[0]);
        lng = Double.parseDouble(report.getLocation().split(",")[1]);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        findViewById(R.id.confirmButton).setOnClickListener(view -> {
            finish();
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set location
        LatLng location = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(location).title("Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14));
    }
}
