package pl.roszkowska.geokr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback {
    private ShopDbHelper mDatabase;
    private static final int LOC_PERM_REQ_CODE = 1;
    private static final int GEOFENCE_RADIUS = 1000;

    private GoogleMap mMap;

    private GeofencingClient geofencingClient;
    private MarkerOptions options = new MarkerOptions();
    private ArrayList<Shop> allShops = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // instancja bazy
        mDatabase = new ShopDbHelper(this);

        //pobranie wszystkich zapisanych sklepów z bazy
        allShops = mDatabase.getAllShops();

        //inicjalizacja mapy
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.g_map);
        mapFragment.getMapAsync(this);

        //geofencing
        geofencingClient = LocationServices.getGeofencingClient(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //w pętli dodajemy wszytskie zapisane sklepy na mapę
        for (Shop shop : allShops) {
            options.position(new LatLng(shop.getLat(), shop.getLon()));
            options.title(shop.getName());
            options.snippet(shop.getDescription());
            mMap.addMarker(options);
        }

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMinZoomPreference(10);

        showCurrentLocationOnMap();

        //na event 'kliknięcie na mapę' pobieramy lokalizację i zapisujemy
        //ją do bazy a potem dodajemy do alertów
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                addTaskDialog(latLng.latitude, latLng.longitude);
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void showCurrentLocationOnMap() {
        if (isLocationAccessPermitted()) {
            requestLocationAccessPermission();
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
    }
    private boolean isLocationAccessPermitted(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return true;
        }else{
            return false;
        }
    }
    private void requestLocationAccessPermission(){
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOC_PERM_REQ_CODE);
    }

    //zapis sklepu do bazy
    private void addTaskDialog(final double lat, final double lng) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View subView = inflater.inflate(R.layout.add_shop, null);

        final EditText nameField = subView.findViewById(R.id.enter_name);
        final EditText descriptionField = subView.findViewById(R.id.enter_description);
        final EditText radiusField = subView.findViewById(R.id.enter_r);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("CREATE NEW SHOP'S LIST ELEMENT");
        builder.setView(subView);
        builder.create();

        builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    final String name = nameField.getText().toString();
                    final String description = descriptionField.getText().toString();
                    final Double radius = Double.parseDouble(radiusField.getText().toString());

                    Shop newShop = new Shop(name, description, radius, lat, lng);
                    mDatabase.add(newShop);

                    //dodanie alertu
                    addLocationAlert(lat, lng);

                finish();
                startActivity(getIntent());

            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Task cancelled", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }

    //alerty
    @SuppressLint("MissingPermission")
    private void addLocationAlert(double lat, double lng){
        if (isLocationAccessPermitted()) {
            requestLocationAccessPermission();
        } else  {
            String key = ""+lat+"-"+lng;

            Toast.makeText(MainActivity.this,
                    key,
                    Toast.LENGTH_SHORT).show();

            Geofence geofence = getGeofence(lat, lng, key);
            geofencingClient.addGeofences(getGeofencingRequest(geofence),
                    getGeofencePendingIntent())
            .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(MainActivity.this,
                            "Location alter has been added",
                            Toast.LENGTH_SHORT).show();
                }
            })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this,
                                    e.getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    private void removeLocationAlert(){
        if (isLocationAccessPermitted()) {
            requestLocationAccessPermission();
        } else {
            geofencingClient.removeGeofences(getGeofencePendingIntent())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this,
                                        "Location alters have been removed",
                                        Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(MainActivity.this,
                                        "Location alters could not be removed",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOC_PERM_REQ_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showCurrentLocationOnMap();
                    Toast.makeText(MainActivity.this,
                            "Location access permission granted, you try " +
                                    "add or remove location allerts",
                            Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }
    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(this, LocationAlertIntentService.class);
        return PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private GeofencingRequest getGeofencingRequest(Geofence geofence) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofence(geofence);
        return builder.build();
    }


    private Geofence getGeofence(double lat, double lang, String key) {
        return new Geofence.Builder()
                .setRequestId(key)
                .setCircularRegion(lat, lang, GEOFENCE_RADIUS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(10000)
                .build();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.remove_loc:
                removeLocationAlert();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mDatabase != null){
            mDatabase.close();
        }
    }
}
