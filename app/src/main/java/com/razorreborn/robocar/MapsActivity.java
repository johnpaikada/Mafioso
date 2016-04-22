package com.razorreborn.robocar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.razorreborn.robocar.MapWrapperLayout.OnDragListener;
import com.google.android.gms.location.LocationListener;

import org.json.JSONObject;

/**
 * Created by Kiran Anto aka RazorSharp on 26/3/2016.
 * For more Info Contact
 * Kirananto@gmail.com
 * 9495333724
 * All Copyrights Reserved 2016
 */

public class MapsActivity extends AppCompatActivity implements OnDragListener, OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener {

    //+++++++++++++++++++++++++++++++++++++++++++++++++
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private LocationRequest mLocationRequest;
    // Google Map
    private GoogleMap googleMap;
    private TextView text;
    private LatLng currentPosition;
    private LatLng centerLatLng;
    private View mMarkerParentView;
    private ImageView mMarkerImageView;
    private GoogleApiClient mGoogleApiClient;
    private int centerX = -1;
    private int centerY = -1;
    private TextView mLocationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.mapstoolbar);
        assert toolbar != null;
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        text = (TextView) findViewById(R.id.navigate_text_view);
        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(AppIndex.API).build();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(3000).setFastestInterval(3000);
//				.setSmallestDisplacement(3);
        // InitializeUI
        initializeUI();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Accepted
                    getLocation();
                } else {
                    // Denied
                    Toast.makeText(MapsActivity.this, "LOCATION Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onLocationChanged(Location location)
    {

    }

    private void handlePermissionsAndGetLocation() {
        int hasWriteContactsPermission = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasWriteContactsPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
            return;
        }
        getLocation();//if already has permission
    }

    private void getLocation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                Log.v("WEAVER_", "Has permission");
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                        mLocationRequest, this);
                Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
                if (mLastLocation != null) {
                    showCurrentLocation(mLastLocation);
                }
            } else {
                Log.v("WEAVER_", "Does not have permission");
            }
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                showCurrentLocation(mLastLocation);
            }
        }

    }
    private void showCurrentLocation(Location location){

        googleMap.clear();


        currentPosition = new LatLng(location.getLatitude(),location.getLongitude());

        googleMap.addMarker(new MarkerOptions()
                .position(currentPosition)
                .snippet("Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin))
                .flat(true)
                .title("I'm here!"));

        // Zoom in, animating the camera.
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 18));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    private void initializeUI() {
        try {
            // Loading map
            initializeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }
        mLocationTextView = (TextView) findViewById(R.id.location_text_view);
        mMarkerParentView = findViewById(R.id.marker_view_incl);
        mMarkerImageView = (ImageView) findViewById(R.id.marker_icon_view);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        int imageParentWidth = mMarkerParentView.getWidth();
        int imageParentHeight = mMarkerParentView.getHeight();
        int imageHeight = mMarkerImageView.getHeight();

        centerX = imageParentWidth / 2;
        centerY = (imageParentHeight / 2) + (imageHeight / 2);
    }

    private void initializeMap() {
        if (googleMap == null) {
            CustomMapFragment mCustomMapFragment = ((CustomMapFragment) getFragmentManager()
                    .findFragmentById(R.id.map));
            mCustomMapFragment.setOnDragListener(MapsActivity.this);
            googleMap = mCustomMapFragment.getMap();
            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.getGpsLocation);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlePermissionsAndGetLocation();
                text.setText(" Choose Path");

            }
        });
        CardView cardView = (CardView) findViewById(R.id.cardz);
        assert cardView != null;
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(text.getText().equals(" Choose Path")) {
                LatLng origin = currentPosition;
                LatLng dest = centerLatLng;
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions()
                        .position(dest)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin))
                        .flat(true)
                        .title("My Destination"));
                String url = getDirectionsUrl(origin, dest);
                DownloadTask downloadTask = new DownloadTask();
                downloadTask.execute(url);
                text.setText("Navigate to This Location");

                }
                else {
                    //TODO add stuff that happens when navigate is clicked...
                }
            }
        });
0
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest)
    {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + ","
                + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Travelling Mode
        String mode = "mode=driving";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&"
                + mode;

        // Output format
        String output = "json";

        // Building the url to the web service

        return "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + parameters;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException
    {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try
        {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    iStream));

            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null)
            {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e)
        {
            Log.d("Exception ing url", e.toString());
        } finally
        {
            assert iStream != null;
            iStream.close();
            assert urlConnection != null;
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>
    {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url)
        {

            // For storing data from web service
            String data = "";

            try
            {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e)
            {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;
            Log.e("results", result + "");
            if (result.size() < 1) {
                Toast.makeText(getApplicationContext(), "No Points", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = result.get(i);
                Log.e("points", path + "");
                int count=0;
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    if (j == 0) {
                        //tvDistance.setText("Distance : "
                              //  + point.get("distance"));
                    } else if (j == 1) {
                        //tvTime.setText("Duration : " + point.get("duration"));
                    } else if (j > 1) {
                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        points.add(position);
                        if(count==0)
                        {
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 18));
                            count++;
                        }
                    }
                }
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(getResources().getColor(R.color.PathColor));
            }
            googleMap.addPolyline(lineOptions);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onDrag(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            Projection projection = (googleMap != null) ? googleMap.getProjection()
                    : null;
            //
            if (projection != null) {
                centerLatLng = projection.fromScreenLocation(new Point(
                        centerX, centerY));
                updateLocation(centerLatLng);
            }
        }
    }

    private void updateLocation(LatLng centerLatLng) {
        if (centerLatLng != null) {
            Geocoder geocoder = new Geocoder(MapsActivity.this,
                    Locale.getDefault());

            List<Address> addresses = new ArrayList<>();
            try {
                addresses = geocoder.getFromLocation(centerLatLng.latitude,
                        centerLatLng.longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addresses != null && addresses.size() > 0) {

                String addressIndex0 = (addresses.get(0).getAddressLine(0) != null) ? addresses
                        .get(0).getAddressLine(0) : null;
                String addressIndex1 = (addresses.get(0).getAddressLine(1) != null) ? addresses
                        .get(0).getAddressLine(1) : null;
                String addressIndex2 = (addresses.get(0).getAddressLine(2) != null) ? addresses
                        .get(0).getAddressLine(2) : null;
                String addressIndex3 = (addresses.get(0).getAddressLine(3) != null) ? addresses
                        .get(0).getAddressLine(3) : null;

                String completeAddress = addressIndex0 + "," + addressIndex1;

                if (addressIndex2 != null) {
                    completeAddress += "," + addressIndex2;
                }
                if (addressIndex3 != null) {
                    completeAddress += "," + addressIndex3;
                }
                mLocationTextView.setText(completeAddress);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);
        map.setTrafficEnabled(true);
        map.setIndoorEnabled(true);
        map.setBuildingsEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
    }

    private boolean isGPSEnabled() {
        LocationManager cm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return cm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onConnected(Bundle bundle) {
        //if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        //    return;
        //}
        //Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
       // if (mLastLocation != null) {
        //    lat.setText(String.valueOf(mLastLocation.getLatitude()));
       //     lon.setText(String.valueOf(mLastLocation.getLongitude()));
       // }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.razorreborn.robocar/http/host/path")
        );
        AppIndex.AppIndexApi.start(mGoogleApiClient, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.razorreborn.robocar/http/host/path")
        );
        AppIndex.AppIndexApi.end(mGoogleApiClient, viewAction);
        mGoogleApiClient.disconnect();
    }
}
