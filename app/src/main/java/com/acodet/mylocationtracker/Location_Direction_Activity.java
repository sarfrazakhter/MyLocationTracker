package com.acodet.mylocationtracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.acodet.mylocationtracker.calc.testClass;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


/**
 * Created by sarfraz on 11/12/2018.
 */
public class Location_Direction_Activity extends FragmentActivity implements
        LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapLongClickListener, OnMapReadyCallback {

    ProgressBar progressBar;

    TextView tv_loc_pos, tv_destination, tv_Totaldistance;
    ImageButton cancelButton;

    GoogleMap googleMap;
    String lat, longi;
    String address, destAddress;
    ArrayList<LatLng> markerPoints;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location loc;
    Geocoder geocoder;
    MarkerOptions mark_origin, mark_destination;
    MarkerOptions markerOptions;
    LatLng latLng;
    String foramattedAddress, locationid;
    HashMap<String, String> map, temp_HashMap;

    private static final int GPS_ERRORDIALOG_REQUEST = 9001;
    String parsedDistance = "";
    String response;
    LatLng origin, destination;
    Location mLastLocation;
    Button btn_next_to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // show error dialog if GoolglePlayServices not available
            setContentView(R.layout.direction_location);
        btn_next_to = (Button)findViewById(R.id.btn_next_to);
        ((MapFragment) getFragmentManager().findFragmentById(
                R.id.googleMap)).getMapAsync(this);



        lat = "28.6213";

        longi = "77.0613";

        destAddress = getIntent().getStringExtra("ADDRESS");

        views();

        progressBar.setVisibility(View.VISIBLE);

        markerPoints = new ArrayList<LatLng>();

        /*buildApiClient();*/

        events();
    }

    private void events() {

        btn_next_to.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Location_Direction_Activity.this,testClass.class));
            }
        });

        cancelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                onBackPressed();
            }
        });
    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
    }


    private void getAddress() {
        // TODO Auto-generated method stub
        Geocoder geocoder = new Geocoder(Location_Direction_Activity.this,
                Locale.getDefault());
        address = "not available";
        String Address = null;
        String state = null;
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
            if (addressList != null && addressList.size() > 0) {
                Address = addressList.get( 0 ).getAddressLine( 0 ) + "," + addressList.get( 0 ).getSubLocality();
                String subLocality = addressList.get(0).getSubLocality();
                StringBuilder strReturnedAddress = new StringBuilder( "Address:\n" );
                address = Address +  "," + " " + subLocality  ;
            }
        } catch (IOException e) {
            // Log.e(TAG, "Unable connect to Geocoder", e);
        }catch (NullPointerException npe){

        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + ","
                + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        /*String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + parameters;*/

        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters + "&key=" +"AIzaSyAN9hE_8qVZyDFtkCsGpxUdMY4TRdiqYJA";

        return url;
    }

    @Override
    public void onMapReady(GoogleMap gMap) {

        googleMap = gMap;

       // setUpMap();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildApiClient();
                googleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            buildApiClient();
            googleMap.setMyLocationEnabled(true);
        }

    }

    private void setUpMap() {

        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMarkerDragListener(this);
        // myLocation = new MyLocation(Direction_Location.this);
        // LatLng origin = new
        // LatLng(myLocation.getCurrentLocation().getLatitude(),myLocation.getCurrentLocation().getLongitude());
        if(loc!=null) {
            origin = new LatLng(loc.getLatitude(), loc.getLongitude());
        }else{
            origin = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }

        destination = new LatLng(Double.parseDouble(lat), Double.parseDouble(longi));

        addMarkers(mLastLocation.getLatitude(), mLastLocation.getLongitude());

        getDistance(origin.latitude, origin.longitude, destination.latitude,
                destination.longitude);


        mark_destination = new MarkerOptions().position(destination);
        mark_destination.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mark_destination.title(destAddress);
        googleMap.addMarker(mark_destination);

        String url = getDirectionsUrl(destination, origin);

        DownloadTask downloadTask = new DownloadTask();

        // /
        // settingTextsintextviews
        getAddress();
        tv_loc_pos.setText("you're At:  " + address);
        // tv_dest_distance.setText("Total Distance: " + parsedDistance);
        tv_destination.setText("Destination: " + destAddress);

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }


    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub

            super.onPreExecute();
        }

        // Downloading data in non-ui thread

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);

            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            //Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            if (result != null && result.size() > 0) {
                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(5);
                    lineOptions.color(Color.GREEN);
                }

                // Drawing polyline in the Google Map for the i-th route
                googleMap.addPolyline(lineOptions);
                progressBar.setVisibility(View.GONE);
            } else {
                /*Toast.makeText(Location_Direction_Activity.this,
                        "can not draw!No route Found!!", Toast.LENGTH_SHORT)
                        .show();*/
                progressBar.setVisibility(View.GONE);
            }
        }

    }

    private void addMarkers(double lat, double lng) {
        if (googleMap != null) {
            LatLng latLng = new LatLng(lat, lng);

            mark_origin = new MarkerOptions().position(latLng);
            mark_origin.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mark_origin.title(address);

            googleMap.addMarker(mark_origin);

            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(
                    latLng, 10);

            googleMap.animateCamera(yourLocation);
            googleMap.moveCamera(yourLocation);

            googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));


        }
    }

    private void views() {
        // TODO Auto-generated method stub
        /*************** text views *******************/
        tv_destination = (TextView) findViewById(R.id.tv_destination);
        tv_Totaldistance = (TextView) findViewById(R.id.tv_totalDistance);
        tv_loc_pos = (TextView) findViewById(R.id.tv_loc_pos);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_map);
        progressBar.setVisibility(View.GONE);
        cancelButton = (ImageButton) findViewById(R.id.imageButton_cancel);
    }



    protected synchronized void buildApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        // TODO Auto-generated method stub
        Toast.makeText(Location_Direction_Activity.this, "Connection failed!!",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(Bundle arg0) {
        // TODO Auto-generated method stub

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);


    }

    @Override
    public void onConnectionSuspended(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        mLastLocation = location;
        setUpMap();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMapLongClick(LatLng arg0) {
        // TODO Auto-generated method stub
        googleMap.addMarker(markerOptions.position(arg0).draggable(true));
    }

    @Override
    public void onMarkerDrag(Marker arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        // TODO Auto-generated method stub
        LatLng dragPosition = marker.getPosition();
        if (foramattedAddress != null)
            marker.setTitle(foramattedAddress);
        /*
         * double dragLat = dragPosition.latitude; double dragLong =
         * dragPosition.longitude;
         */

        Toast.makeText(getApplicationContext(), "Marker Dragged..!",
                Toast.LENGTH_LONG).show();

    }

    @Override
    public void onMarkerDragStart(Marker arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onMarkerClick(Marker arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    // /////////////
    /***************** location route distance ***************/
    public String getDistance(final double lat1, final double lon1,
                              final double lat2, final double lon2) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    String url = "https://maps.googleapis.com/maps/api/directions/json?origin="
                            + lat1
                            + ","
                            + lon1
                            + "&destination="
                            + lat2
                            + ","
                            + lon2 + "&sensor=false&units=metric&mode=driving&key=AIzaSyAN9hE_8qVZyDFtkCsGpxUdMY4TRdiqYJA";

                    response = new GetResponse().getData(url);

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray array = jsonObject.getJSONArray("routes");
                    JSONObject routes = array.getJSONObject(0);
                    JSONArray legs = routes.getJSONArray("legs");
                    JSONObject steps = legs.getJSONObject(0);
                    JSONObject distance = steps.getJSONObject("distance");
                    parsedDistance = distance.getString("text");

                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tv_Totaldistance.setText("Total Distance: " + parsedDistance);

        return parsedDistance;
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(Location_Direction_Activity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildApiClient();
                        }
                        googleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }


        }
    }

}
