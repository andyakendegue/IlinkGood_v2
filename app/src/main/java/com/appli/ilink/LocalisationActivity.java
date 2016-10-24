package com.appli.ilink;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocalisationActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    private String TAG;
    private String latitude;
    private TextView latitudeText;
    private ImageView locatedImage;
    private TextView locatedText;
    private LocationManager locationManager;
    private String longitude;
    private TextView longitudeText;
    private GoogleMap mMap;
    private Marker marker;
    private MaterialDialog pDialog;

    public LocalisationActivity() {
        this.TAG = TamponGeolocatedActivity.KEY_TAG;
        this.latitude = null;
        this.longitude = null;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localisation);
        getSupportActionBar().hide();
        this.locatedText = (TextView) findViewById(R.id.textLocated);
        this.latitudeText = (TextView) findViewById(R.id.textLatitude);
        this.longitudeText = (TextView) findViewById(R.id.textLongitude);
        this.locatedImage = (ImageView) findViewById(R.id.imageLocated);
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapLocalisation)).getMapAsync(this);
    }

    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        LatLng latLng = new LatLng(0.4112103d, 9.4346296d);
        this.marker = this.mMap.addMarker(new MarkerOptions().title("Vous \u00eates ici").position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.main_marker)));
        this.mMap.getUiSettings().setMyLocationButtonEnabled(true);
        this.mMap.getUiSettings().setCompassEnabled(true);
        this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
    }

    public void onResume() {
        super.onResume();
        this.locationManager = (LocationManager) getSystemService("location");
        if (this.locationManager.isProviderEnabled("gps")) {
            abonnementGPS();
        }
    }

    public void onPause() {
        super.onPause();
        desabonnementGPS();
    }

    public void abonnementGPS() {
        this.locationManager.requestLocationUpdates("gps", 5000, 10.0f, this);
    }

    public void desabonnementGPS() {
        this.locationManager.removeUpdates(this);
    }

    public void onLocationChanged(Location location) {
        StringBuilder msg = new StringBuilder("latitude : ");
        msg.append(location.getLatitude());
        msg.append("; longitude : ");
        msg.append(location.getLongitude());
        Toast.makeText(this, msg.toString(), 0).show();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
        this.marker.setPosition(latLng);
        this.latitude = String.valueOf(location.getLatitude());
        this.longitude = String.valueOf(location.getLongitude());
        Toast.makeText(this, msg.toString(), 0).show();
        if (this.latitude != "0" && this.longitude != "0") {
            this.locatedText.setText("Vous avez \u00e9t\u00e9 localis\u00e9");
            this.locatedImage.setImageResource(R.drawable.gps_located);
            this.latitudeText.setText(this.latitude);
            this.longitudeText.setText(this.longitude);
        }
    }

    public void onProviderDisabled(String provider) {
        if ("gps".equals(provider)) {
            desabonnementGPS();
        }
    }

    public void onProviderEnabled(String provider) {
        if ("gps".equals(provider)) {
            abonnementGPS();
        }
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public void onBackPressed() {
        super.onBackPressed();
    }
}
