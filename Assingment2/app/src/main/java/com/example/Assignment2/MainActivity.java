package com.example.hack2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;

import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, Style.OnStyleLoaded {
    private MapView mapView;
    private MapboxMap map;
    double lat, lng;
    Button btnSearch;
    TrainWebServiceClient client = new TrainWebServiceClient();
    TextView txtStations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      // insertmapbox
        Mapbox.getInstance(this, getString(R.string.apiKey));

        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        txtStations = findViewById(R.id.txtStations);

        // request permissions
        String[] requiredPermissions = {
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        boolean ok = true;

        for (String requiredPermission : requiredPermissions) {
            int result = ActivityCompat.checkSelfPermission(this, requiredPermission);
            if (result != PackageManager.PERMISSION_GRANTED) ok = false;
        }
        if (!ok) {
            ActivityCompat.requestPermissions(this, requiredPermissions, 1);
            System.exit(0);
        } else {
            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                }
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) { }
                @Override
                public void onProviderEnabled(String provider) { }
                @Override
                public void onProviderDisabled(String provider) { }
            });
        }
    }
    //show results in text view
    public void onClick(View v) {
        btnSearch = findViewById(R.id.Search);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnected()) {
            new task().execute(client.buildURLfromLocation(lat, lng));
        }
    }
    public void displayResults(List<Station> list) {
        txtStations.setText("");
        for (Station s : list) {
            txtStations.append(s.toString());
        }
        addStations(list, lat, lng);
    }
    public void addStations(List<Station> list, Double lat, Double lng) {
        for(Station s : list){
            SymbolManager sm = new SymbolManager(mapView, map, map.getStyle());
            SymbolOptions so = new SymbolOptions()
                    .withLatLng(new LatLng(s.getLat(), s.getLong()))
                    .withIconImage("suitcase-15")
                    .withIconColor("black")
                    .withIconSize(2.5f);
            Symbol symbol = sm.create(so);
        }
    }
    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        map = mapboxMap;
        mapboxMap.setStyle(Style.OUTDOORS, this);

        mapboxMap.setCameraPosition(
                new CameraPosition.Builder()
                        .target(new LatLng(lat, lng))
                        .zoom(10.0)
                        .build()
        );
    }
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }
    @Override
    public void onStyleLoaded(@NonNull Style style) {
        SymbolManager sm = new SymbolManager(mapView, map, style);

        SymbolOptions so = new SymbolOptions()
                .withLatLng(new LatLng(lat, lng))
                .withIconImage("suitcase-15")
                .withIconColor("black")
                .withIconSize(2.5f);

        Symbol symbol = sm.create(so);
    }
    //threading
    class task extends AsyncTask<URL, Void, List<Station>> {
        @Override
        protected List<Station> doInBackground(URL... urls) {
            return new TrainWebServiceClient().getStationByURL(urls[0]);
        }
        @Override
        protected void onPostExecute(List<Station> list) {
            displayResults(list);
        }
    }
}
