package vivekvaidya.com.lookingfor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

//google map apis
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //google map related
    private GoogleMap map;

    //location service related
    private GoogleApiClient mGoogleApiClient;
    // retrieved in location service, used in google map
    private Location mCurrentLocation ;
    private EventLocation mLocation;
    private String eventTitle = "";
    private List<Event> events;
    public static final String EVENT_LOCATION = "event location";
    public static final String EVENT_TITLE = "event title";
    public static final String EVENT_ALL = "all events";
    //for snackbar in requesting permissions
    private View rootLayout;
    private static final String TAG = "Main Activity";

    //requesting permissions related
    private static final int REQUEST_LOCATION_PERMISSIONS = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        rootLayout = findViewById(R.id.root_layout);
        mLocation = getIntent().getParcelableExtra(EVENT_LOCATION);
        eventTitle = getIntent().getStringExtra(EVENT_TITLE);
        events = getIntent().getParcelableArrayListExtra(EVENT_ALL);
        mCurrentLocation = newLocationProvider(mLocation);
        //google map starter
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .enableAutoManage( this, 0, this )
                    .addApi( Places.GEO_DATA_API )
                    .addApi( Places.PLACE_DETECTION_API )
                    .build();
        }

    }
    //Google map
    public void setUpGoogleMap() {
        LatLng curLocation = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        map.addMarker(new MarkerOptions().position(curLocation)
                .title(eventTitle));
//        This one moves the camera to the specific location
        map.moveCamera(CameraUpdateFactory.newLatLng(curLocation));
//        This one moves the camera to the specific location and sets the zoom
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curLocation, 15));

    }
    //Google map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (mCurrentLocation != null) {
            setUpGoogleMap();
        }
    }
    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }
//    protected void createLocationRequest() {
//        //remove location updates so that it resets
//        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this); //Import should not be **android.Location.LocationListener**
//        //import should be **import com.google.android.gms.location.LocationListener**;
//
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(10000);
//        mLocationRequest.setFastestInterval(5000);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        //restart location updates with the new interval
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//
//    }
    //Location service
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //check for permission
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.i(TAG,
                        "Displaying camera permission rationale to provide additional context.");
                Snackbar.make(rootLayout, "Give Location Permission for app to work",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("Ok!", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(MapActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_LOCATION_PERMISSIONS);
                            }
                        })
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_PERMISSIONS);
            }
            return;
        }
        locationReceived();
    }

//    @Override
//    public void onLocationChanged(Location location) {
//        Log.i(TAG, "Location Changed!");
//        Log.i(TAG, " Location: " + location); //I guarantee,I get the changed location here
//
//    }
    // called when permission is granted
    // discard the errors
    public void locationReceived() {//TODO: find current location
//        final LocationManager locationManager = (LocationManager)  this.getSystemService(Context.LOCATION_SERVICE);
//        Criteria criteria = new Criteria();
//        String bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();
//        locationManager.requestLocationUpdates(bestProvider, 1000, 0, );
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, new LocationRequest(), new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//                mCurrentLocation = location;
//                if (map != null) {
//                    setUpGoogleMap();
//                    //           locateEvents();
//                }
//            }
//        });

//         mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
//                 mGoogleApiClient);
        //createLocationRequest();
//        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
//                mGoogleApiClient);

        if (map!=null){
            setUpGoogleMap();
            locateEvents();
        }


    }
    // handle user granting permission results
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Permission is granted.");

                    locationReceived();
                } else {
                    Log.i(TAG, "Permission is not granted.");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public void locateEvents() {
        if (events != null) {
            for(final Event event: events) {
                EventLocation location = event.getEventLocation();
                if (location != null) {
                    if (location.getId() != null) {
                        Places.GeoDataApi.getPlaceById( mGoogleApiClient, location.getId() ) .setResultCallback( new ResultCallback<PlaceBuffer>() {
                            @Override
                            public void onResult(PlaceBuffer places) {
                                if( places.getStatus().isSuccess() ) {
                                    for(Place place:places) {
                                        map.addMarker(new MarkerOptions().position(place.getLatLng()).title(event.getTitle()));
                                    }
                                }
                                //Release the PlaceBuffer to prevent a memory leak
                                places.release();
                            }
                        } );
                    }
                    double a = location.getLatitude();
                    double b = location.getLongitude();
                    String title = event.getTitle();
                    LatLng input = new LatLng(a, b);
                    map.addMarker((new MarkerOptions().position(input).title(title)));
                }
            }
        }
    }



    private Location newLocationProvider(EventLocation mLocation) {
        Location loc = new Location("dummy");
        if (mLocation != null) {
            loc.setLongitude(mLocation.getLongitude());
            loc.setLatitude(mLocation.getLatitude());
            Log.e("longitude", String.valueOf(loc.getLongitude()));
            Log.e("latitude", String.valueOf(loc.getLatitude()));
        } else {
            loc.setLatitude(40.103093);
            loc.setLongitude(-88.227244);
        }
        return loc;
    }
}
