package com.ogentertainment.urbanknights;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float zoom = 12.185096f;

    //Namai 54.709761, 25.319916

    //vars
    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;

    List<Marker> markers = new ArrayList<>();
    List<Polygon> polygons = new ArrayList<>();
    List<Marker> players = new ArrayList<>();
    List<Marker> numbers = new ArrayList<>();
    List<Marker> dragons = new ArrayList<>();

    ArrayList<String> sectorIds = new ArrayList<>();
    ArrayList<String> sectorOwnerIds = new ArrayList<>();
    ArrayList<String> sectorLvls = new ArrayList<>();
    ArrayList<String> sectorCaptureTimes = new ArrayList<>();
    ArrayList<String> sectorColors = new ArrayList<>();
    ArrayList<String> sectorDragons = new ArrayList<>(); //1.2.0 Dragons
    ArrayList<String> sectorOwnerLocations = new ArrayList<>();

    private View fragmentMap; //Map view

    private LatLng bottomLeft; //Vilniaus kampai
    private LatLng bottomRight;
    private LatLng topRight;
    private LatLng topLeft;
    public LatLng locationLatLng;
    private LatLng markerPos;
    private double gap = 0.02;// tarpai tarp langeliu centru
    private int sectorColumnCount = 10;
    private int totalSectorsVilnius = 100;
    private double latt;
    private double longg;

    private Integer lastSectorId = 99999; //Random skaicius kuris nera realus sektoriaus id
    private Integer sectorId;
    private String sectorLevel;
    private String sectorOwner;
    private String sectorCaptureTime;
    private String sectorDragon; //v1.2.0 Dragons

    private TextView txUsername;
    private TextView txTeritorijos;
    private TextView txAuksas;

    private Button btnUpgrade;
    private Button btnStatistics;
    private Button btnAchievements;
    private Button btnCapture; //v1.2.0 Added capture btn
    private Button btnProfile; //v1.2.0 Added profile btn

    private AlertDialog dialog;
    private AlertDialog dialogUpdate;
    private boolean existsPermDialog = false;
    private boolean isOutOfBounds = true;
    private boolean isFirstLoad = true; //v1.0.1 Pan
    private boolean softBanned = false; //1.1.0
    private boolean isDisplayUpdateVisable = false; //1.2.0 Mapo update check

    //1.1.0 Coordinate change timer
    Stopwatch timer = new Stopwatch();

    String id;
    String username;
    String password;
    String sector;
    String gold;
    String color;
    String place1;
    String place2;
    String place3;
    String lastRefresh;
    String sectorCount;

    String ip;
    String onSectorChangedURL;
    String tryCaptureURL;
    String mainMapURL;
    String getPlayerLocationsURL;
    String updatePlayerInfoURL;
    String actionLogURL;
    String checkForUpdateURL; //1.2.0 Mapo update check
    String captureDragonURL; //v1.2.0 Dragons

    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ip = getString(R.string.ip);
        onSectorChangedURL = ip + "onSectorChange.php";
        tryCaptureURL = ip + "tryCapture.php";
        mainMapURL = ip + "mainMap.php";
        getPlayerLocationsURL = ip + "getPlayerLocations.php";
        updatePlayerInfoURL = ip + "updatePlayerInfo.php";
        actionLogURL = ip + "actionLog.php";
        checkForUpdateURL = ip + "checkversion.php"; //1.2.0 Mapo update check
        captureDragonURL = ip + "captureDragon.php"; //v1.2.0 Dragons

        fragmentMap = findViewById(R.id.map);
        fragmentMap.setVisibility(View.INVISIBLE);

        getLocationPermission();
        bottomLeft = new LatLng(54.56, 25.20);
        bottomRight = new LatLng(54.56, 25.40);
        topRight = new LatLng(54.76, 25.40);
        topLeft = new LatLng(54.76, 25.20);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        id = getIntent().getStringExtra("id");
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
        sector = getIntent().getStringExtra("sector");
        gold = getIntent().getStringExtra("gold");
        color = getIntent().getStringExtra("color");
        place1 = getIntent().getStringExtra("place1");
        place2 = getIntent().getStringExtra("place2");
        place3 = getIntent().getStringExtra("place3");
        lastRefresh = getIntent().getStringExtra("lastRefresh");
        sectorCount = getIntent().getStringExtra("sectorCount");

        txUsername = findViewById(R.id.txUsername);
        txUsername.setText(username);
        txTeritorijos = findViewById(R.id.txTeritorijos);
        txAuksas = findViewById(R.id.txAuksas);
        btnUpgrade = findViewById(R.id.btnUpgrade);
        btnStatistics = findViewById(R.id.btnStatistics);
        btnAchievements = findViewById(R.id.btnAchievements);
        btnCapture = findViewById(R.id.btnCapture); //v1.2.0 Added capture btn
        btnProfile = findViewById(R.id.btnProfile); //v1.2.0 Added profile btn
    }

    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map is ready");

        mMap = googleMap;
        txUsername.setText(username);
        txTeritorijos.setText("Teritorijos: " + sectorCount);
        txAuksas.setText("Auksas: " + gold);

        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                Integer clickedSectorId = (Integer) polygon.getTag();
                Log.d(TAG, "onPolygonClick: called | id: " + clickedSectorId);
                Intent intent = new Intent(MapsActivity.this, SectorClickActivity.class);
                intent.putExtra("id", clickedSectorId.toString());
                startActivity(intent);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d(TAG, "onMarkerClick: called");
                if (marker.getTitle().equals("player")){
                    markerPos = new LatLng(marker.getPosition().latitude-gap/2, marker.getPosition().longitude-gap/2);
                } else if (marker.getTitle().equals("number")){
                    markerPos = new LatLng(marker.getPosition().latitude-9*(gap/10), marker.getPosition().longitude-8.5*(gap/10));
                } else if (marker.getTitle().equals("dragon")){
                    markerPos = new LatLng(marker.getPosition().latitude-gap/2, marker.getPosition().longitude-gap/2);
                } else if (marker.getTitle().equals("little_dragon")){
                    markerPos = new LatLng(marker.getPosition().latitude - 9 * (gap / 10), marker.getPosition().longitude - 1.5*(gap/10));
                }

                for (int i = 0; i<polygons.size(); i++){
                    Polygon polygon = polygons.get(i);
                    LatLng points = polygon.getPoints().get(0);
                    Log.d(TAG, "onMarkerClickMarker: " + markerPos.latitude + " " + markerPos.longitude + " | " + points.latitude + " " + points.longitude);
                    if (points.latitude == markerPos.latitude && points.longitude == markerPos.longitude){
                        Intent intent = new Intent(MapsActivity.this, SectorClickActivity.class);
                        intent.putExtra("id", sectorIds.get(i));
                        Log.d(TAG, "onMarkerClick: " + sectorIds.get(i));
                        startActivity(intent);
                        return true;
                    }
                }

                return false;
            }
        });

        fillSectorLocations();
        Polyline line = mMap.addPolyline(new PolylineOptions().add(bottomLeft, bottomRight).width(5).color(Color.RED));
        Polyline line2 = mMap.addPolyline(new PolylineOptions().add(bottomRight, topRight).width(5).color(Color.RED));
        Polyline line3 = mMap.addPolyline(new PolylineOptions().add(topRight, topLeft).width(5).color(Color.RED));
        Polyline line4 = mMap.addPolyline(new PolylineOptions().add(topLeft, bottomLeft).width(5).color(Color.RED));
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        if (mLocationPermissionGranted) {
            getLastKnownLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            //1.0.1 Pan
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setTiltGesturesEnabled(false);
            mMap.getUiSettings().setRotateGesturesEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.getUiSettings().setZoomGesturesEnabled(false);
            mMap.getUiSettings().setScrollGesturesEnabled(true);
        } else {
            getLocationPermission();
        }

        //OnClicks
        btnStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, StatisticsActivity.class);
                startActivity(intent);
                if(sectorId != null){ //v1.2.0 only log if in bounds, otherwise crash will occur because sectorId will be null :)
                    actionLog(id, "Stats", sectorId.toString(), "0", "", ""); //1.0.1 actionLog
                }
                else
                {
                    actionLog(id, "Stats", "0", "0", "", "oob"); //1.2.0 Still logs, but puts outofbounds in secId
                }
            }
        });

        btnAchievements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, AchievementsActivity.class);
                startActivity(intent);
                if(sectorId != null){ //v1.2.0 only log if in bounds, otherwise crash will occur because sectorId will be null :)
                    actionLog(id, "Achvm", sectorId.toString(), "0", "", ""); //1.0.1 actionLog
                }
                else
                {
                    actionLog(id, "Achvm", "0", "0", "", "oob"); //1.2.0 Still logs, but puts outofbounds in secId
                }
            }
        });

        //v1.2.0 Added capture btn
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryCapture();
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() { //v1.2.0 Added profile btn
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, ProfileActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        //Scheduling stuff
        if (UrbanKnights.isActivityVisible()){

            //Gets user location and displays on map
            final Handler handlerLocation = new Handler();
            final int delay = 200; //milliseconds

            handlerLocation.postDelayed(new Runnable(){
                public void run(){
                    getLastKnownLocation();
                    handlerLocation.postDelayed(this, delay);
                }
            }, delay);


            //Checks for updates
            final Handler handlerUpdateCheck = new Handler();
            final int delayUpdateCheck = 5000; //milliseconds

            handlerUpdateCheck.postDelayed(new Runnable(){ //1.2.0 Mapo update check
                public void run(){
                    checkVersion();
                    handlerLocation.postDelayed(this, delayUpdateCheck);
                }
            }, delayUpdateCheck);

            //Refreshes the map, and all user info.
            final Handler handlerMainMap = new Handler();
            final int delayMainMap = 5000; //milliseconds

            handlerMainMap.postDelayed(new Runnable(){
                public void run(){
                    if(locationLatLng != null)
                    {
                        Log.d(TAG, "run: Well fuck");
                        mainMap();
                    }
                    else
                    {
                        handlerMainMap.postDelayed(this, 100);
                    }
                    handlerMainMap.postDelayed(this, delayMainMap);
                }
            }, delayMainMap);
        }
    }

    public void checkVersion(){ //1.2.0 Mapo update check
        StringRequest stringRequest = new StringRequest(Request.Method.POST, checkForUpdateURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d(TAG, "onResponse MapCheckVersion: " + response);
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String code = jsonObject.getString("code");

                    if (code.equals("fail")){
                        //Client versija neatitinka versijos serveryje arba nepavyko nuskaityti versijos is serverio.


                        if (dialogUpdate == null){
                            //Display a non-closable Pop-up that tells user about new version.
                            dialogUpdate = new AlertDialog.Builder(MapsActivity.this).create();
                            dialogUpdate.setTitle("Dėmesio");
                            dialogUpdate.setMessage("Yra naujesnė Urban Knights žaidimo versija.");
                            dialogUpdate.setCanceledOnTouchOutside(false);
                            dialogUpdate.setCancelable(false);
                            dialogUpdate.setButton(AlertDialog.BUTTON_NEUTRAL, "Naujinti",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogUpdate, int which)
                                        {
                                            final String appPackageName = getPackageName(); // v1.0.1 edit
                                            try
                                            {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                            }
                                            catch (android.content.ActivityNotFoundException anfe)
                                            {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                            }
                                        }
                                    });
                            dialogUpdate.show();
                        }
                        else if (!dialogUpdate.isShowing())
                        {
                            //Display a non-closable Pop-up that tells user about new version.
                            dialogUpdate = new AlertDialog.Builder(MapsActivity.this).create();
                            dialogUpdate.setTitle("Dėmesio");
                            dialogUpdate.setMessage("Yra naujesnė Urban Knights žaidimo versija.");
                            dialogUpdate.setCanceledOnTouchOutside(false);
                            dialogUpdate.setCancelable(false);
                            dialogUpdate.setButton(AlertDialog.BUTTON_NEUTRAL, "Naujinti",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogUpdate, int which)
                                        {
                                            final String appPackageName = getPackageName(); // v1.0.1 edit
                                            try
                                            {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                            }
                                            catch (android.content.ActivityNotFoundException anfe)
                                            {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                            }
                                        }
                                    });
                            dialogUpdate.show();
                        }
                    }
                } catch (JSONException e){
                    VolleyLog.d(TAG, "onResponse MapCheckVersion: response failed :( Error: " + e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse MapCheckVersion: " + error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("version", getResources().getString(R.string.app_version));
                return params;
            }
        };
        MySingleton.getInstance(MapsActivity.this).addToRequestQueue(stringRequest);
    }

    private void captureDragon()
    {
        //Start of server request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, captureDragonURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse captureDragon: " + response);
                    dialog = new AlertDialog.Builder(MapsActivity.this).create();
                    dialog.setIcon(R.drawable.dragon_50x50);

                    dialog.setTitle("Sveikiname");
                    dialog.setMessage("Jūs sėkmingai nugalėjote drakoną, Jums suteikta 10 000 aukso premija!");
                    dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    getLocationPermission();
                                }
                            });
                    dialog.show();

            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse captureDragon: " + error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("id", id);
                params.put("sectorId", sectorId.toString());
                return params;
            }
        };
        MySingleton.getInstance(MapsActivity.this).addToRequestQueue(stringRequest);
        //End of server request
    }

    private void mainMap(){
        //Start of server request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, mainMapURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d(TAG, "onResponse mainMap: " + response);
                    JSONArray jsonArray = new JSONArray(response);

                    sectorIds.clear();
                    sectorOwnerIds.clear();
                    sectorLvls.clear();
                    sectorCaptureTimes.clear();
                    sectorColors.clear();
                    sectorDragons.clear(); //1.2.0 Dragons

                    for (int i = 0; i<jsonArray.length(); i++){
                        JSONObject jOb = jsonArray.getJSONObject(i);
                        String id = jOb.getString("id");
                        String ownerId = jOb.getString("ownerId");
                        String level = jOb.getString("level");
                        String captureTime = jOb.getString("captureTime");
                        String dragon = jOb.getString("dragon");//1.2.0 Dragons
                        String color = jOb.getString("color");
                        sectorIds.add(id);
                        sectorOwnerIds.add(ownerId);
                        sectorLvls.add(level);
                        sectorCaptureTimes.add(captureTime);
                        sectorColors.add(color);
                        sectorDragons.add(dragon);//1.2.0 Dragons
                    }

                    getPlayerLocations();
                    updatePlayerInfo();
                } catch (JSONException e){
                    VolleyLog.d(TAG, "onResponse mainMap: response failed :( Error: " + e);
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse mainMap: " + error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("id", id);
                if (isInBounds()){ //1.1.0
                    params.put("isInBounds", "true");
                } else {
                    params.put("isInBounds", "false");
                }
                return params;
            }
        };
        MySingleton.getInstance(MapsActivity.this).addToRequestQueue(stringRequest);
        //End of server request
    }

    private void actionLog(String user1Id, String action, String sectorId, String user2Id, String time, String parameters){ //1.0.1 actionLog
        //Start of server request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, actionLogURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse actionLog: " + response);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse actionLog: " + error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("user1Id", user1Id);
                params.put("action", action);
                params.put("sectorId", sectorId);
                params.put("user2Id", user2Id);
                params.put("time", time);
                params.put("parameters", parameters);
                return params;
            }
        };
        MySingleton.getInstance(MapsActivity.this).addToRequestQueue(stringRequest);
        //End of server request
    }

    private void updatePlayerInfo(){
        //Start of server request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, updatePlayerInfoURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d(TAG, "onResponse updatePlayerInfo: " + response);
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jOb = jsonArray.getJSONObject(0);
                    String code = jOb.getString("code");
                    String message = jOb.getString("message");
                    if (code.equals("success")){
                        gold = jOb.getString("gold");
                        sectorCount = jOb.getString("count");
                        txAuksas.setText("Auksas: " + gold); //Paupdatina golda
                        txTeritorijos.setText("Teritorijos: " + sectorCount); //Paupdatina userio turimus sectors
                    } else {
                        Log.d(TAG, "onResponse: updatePlayerInfo error: " + message);
                    }
                } catch (JSONException e){
                    VolleyLog.d(TAG, "onResponse updatePlayerInfo: response failed :( Error: " + e);
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse updatePlayerInfo: " + error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("id", id);
                return params;
            }
        };
        MySingleton.getInstance(MapsActivity.this).addToRequestQueue(stringRequest);
        //End of server request
    }

    private void getPlayerLocations(){
        //Start of server request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getPlayerLocationsURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d(TAG, "onResponse playerLocations: " + response);
                    sectorOwnerLocations.clear();

                    JSONArray jsonArray = new JSONArray(response);
                    int playerCount = 0;
                    for (int i = 0; i<jsonArray.length(); i++){
                        JSONObject jOb = jsonArray.getJSONObject(i);
                        String sector = jOb.getString("sector");
                        sectorOwnerLocations.add(sector);
                        playerCount++;
                    }

                    drawOnMap(playerCount);

                } catch (JSONException e){
                    VolleyLog.d(TAG, "onResponse playerLocations: response failed :( Error: " + e);
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse playerLocations: " + error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("id", id);
                return params;
            }
        };
        MySingleton.getInstance(MapsActivity.this).addToRequestQueue(stringRequest);
        //End of server request
    }

    private void drawOnMap(int playerCount){
        markers.clear();
        polygons.clear();
        players.clear();
        numbers.clear();
        dragons.clear();
        if (mLocationPermissionGranted){
            mMap.clear();
        }


        fillSectorLocations();

        for (int i = 0; i<polygons.size(); i++){
            Integer color = Integer.parseInt(sectorColors.get(i));
            String level = sectorLvls.get(i);
            String dragon = sectorDragons.get(i); //v1.2.0 dragons
            Polygon polygon = polygons.get(i);

            //Settinam colors
            polygon.setFillColor(color);

            if (dragon.equals("1"))
            {
                List<LatLng> points = polygon.getPoints();
                LatLng middle = new LatLng(points.get(0).latitude + (gap / 2), points.get(0).longitude + (gap / 2));

                MarkerOptions markerOptions = new MarkerOptions().position(middle).title("dragon").icon(BitmapDescriptorFactory.fromResource(R.drawable.dragon_50x50)).anchor(0.5f, 0.5f);
                Marker dragonas;
                boolean yraPlayeris = false; // Ar yra playeris kur norima padet nupiest dragona
                for (int i1 = 0; i1<playerCount; i1++)
                {
                    if (Integer.valueOf(sectorOwnerLocations.get(i1)) == i-1)
                    {
                        yraPlayeris = true;
                    }
                }

                if (!yraPlayeris)
                {
                    dragonas = mMap.addMarker(markerOptions);
                    dragons.add(dragonas);
                }
                else //Jei yra zaidejas drakona piesiam virsui kairej sektoriaus kad neoverlappintu su zaidejo img
                {
                    Log.d(TAG, "drawOnMap: Yra gameris");
                    LatLng topLeft = new LatLng(points.get(0).latitude + 9 * (gap / 10), points.get(0).longitude + 1.5*(gap/10));
                    markerOptions = new MarkerOptions().position(topLeft).title("little_dragon").icon(BitmapDescriptorFactory.fromResource(R.drawable.dragon_15x15)).anchor(0.5f, 0.5f);
                    dragonas = mMap.addMarker(markerOptions);
                    dragons.add(dragonas);
                }

            }

            switch (level) {
                case "1": {
                    List<LatLng> points = polygon.getPoints();
                    LatLng topRight = new LatLng(points.get(0).latitude + 9 * (gap / 10), points.get(0).longitude + (gap / 10) * 8.5);

                    MarkerOptions markerOptions = new MarkerOptions().position(topRight).title("number").icon(BitmapDescriptorFactory.fromResource(R.drawable.number_one)).anchor(0.5f, 0.5f);
                    Marker number = mMap.addMarker(markerOptions);
                    numbers.add(number);
                    break;
                }
                case "2": {
                    List<LatLng> points = polygon.getPoints();
                    LatLng topRight = new LatLng(points.get(0).latitude + 9 * (gap / 10), points.get(0).longitude + (gap / 10) * 8.5);

                    MarkerOptions markerOptions = new MarkerOptions().position(topRight).title("number").icon(BitmapDescriptorFactory.fromResource(R.drawable.number_two)).anchor(0.5f, 0.5f);
                    Marker number = mMap.addMarker(markerOptions);
                    numbers.add(number);
                    break;
                }
                case "3": {
                    List<LatLng> points = polygon.getPoints();
                    LatLng topRight = new LatLng(points.get(0).latitude + 9 * (gap / 10), points.get(0).longitude + (gap / 10) * 8.5);

                    MarkerOptions markerOptions = new MarkerOptions().position(topRight).title("number").icon(BitmapDescriptorFactory.fromResource(R.drawable.number_three)).anchor(0.5f, 0.5f);
                    Marker number = mMap.addMarker(markerOptions);
                    numbers.add(number);
                    break;
                }
                case "4": {
                    List<LatLng> points = polygon.getPoints();
                    LatLng topRight = new LatLng(points.get(0).latitude + 9 * (gap / 10), points.get(0).longitude + (gap / 10) * 8.5);

                    MarkerOptions markerOptions = new MarkerOptions().position(topRight).title("number").icon(BitmapDescriptorFactory.fromResource(R.drawable.number_four)).anchor(0.5f, 0.5f);
                    Marker number = mMap.addMarker(markerOptions);
                    numbers.add(number);
                    break;
                }
                case "5": {
                    List<LatLng> points = polygon.getPoints();
                    LatLng topRight = new LatLng(points.get(0).latitude + 9 * (gap / 10), points.get(0).longitude + (gap / 10) * 8.5);

                    MarkerOptions markerOptions = new MarkerOptions().position(topRight).title("number").icon(BitmapDescriptorFactory.fromResource(R.drawable.number_five)).anchor(0.5f, 0.5f);
                    Marker number = mMap.addMarker(markerOptions);
                    numbers.add(number);
                    break;
                }
            }
        }

        for (int i = 0; i<playerCount; i++){
            Integer locationSector = Integer.parseInt(sectorOwnerLocations.get(i));
            Polygon polygon = polygons.get(locationSector-1);
            List<LatLng> points = polygon.getPoints();
            LatLng middle = new LatLng(points.get(0).latitude + gap/2, points.get(0).longitude + gap/2);

            MarkerOptions markerOptions = new MarkerOptions().position(middle).title("player").icon(BitmapDescriptorFactory.fromResource(R.drawable.player_helm_50_58)).anchor(0.5f, 0.5f);
            Marker player = mMap.addMarker(markerOptions);
            players.add(player);
        }
    }

    private void fillSectorLocations(){
        double x = bottomLeft.longitude;
        double y = bottomLeft.latitude;
        double endX = topRight.longitude;
        double endY = topRight.latitude;
        x = x + gap/2; //Pradedam piesti nuo vidurio
        y = y + gap/2; //Pradedam piesti nuo vidurio
        int i = 0;

        for ( ; y<=endY; y=y+gap){
            while (x <= endX){
                i++;
                //mMap.addMarker(new MarkerOptions().position(new LatLng(y,x)).title(id.toString()));
                Polygon polygon = mMap.addPolygon(new PolygonOptions().add(new LatLng(y-(gap/2),x-(gap/2)), new LatLng(y-(gap/2),x+(gap/2)), new LatLng(y+(gap/2),x+(gap/2)), new LatLng(y+(gap/2),x-(gap/2))).visible(true).clickable(true).strokeColor(Color.BLACK).strokeWidth(1.5f));
                Object object = i;
                polygon.setTag(i);
                polygons.add(polygon);
                x = x + gap;
            }
            x = bottomLeft.longitude + gap/2; //RESET THE X + GAP/2
        }
    }

    private void moveCamera(LatLng latLng){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
        fragmentMap.setVisibility(View.VISIBLE);
    }

    private void getSectorId(LatLng latlng){
        if (isInBounds()){
            latt = round(latlng.latitude, 4);
            longg = round(latlng.longitude, 4);
            int latas = (int) ((latt-bottomLeft.latitude)/((topRight.latitude-bottomLeft.latitude)/sectorColumnCount));
            int longas = (int) ((longg-bottomLeft.longitude)/((bottomRight.longitude-bottomLeft.longitude)/sectorColumnCount));
            int secID = latas*sectorColumnCount+longas+1;
            sectorId = secID;
            if (sectorId != lastSectorId && mLocationPermissionGranted){
                lastSectorId = sectorId;
                onSectorChanged(true);
            }
        }
    }

    private boolean isInBounds(){ //v1.1.0
        if (locationLatLng.latitude > topRight.latitude || locationLatLng.latitude < bottomLeft.latitude || locationLatLng.longitude > topRight.longitude || locationLatLng.longitude < bottomLeft.longitude){
            return false;
        } else {
            return true;
        }
    }

    private void tryDragonCapture() //1.2.0 dragons
    {
        if (sectorDragon.equals("1")) //There is a dragon inside this sector
        {
            if(!softBanned) //If not speeding
            {
                captureDragon();
            }
            else
            { //Wait 200ms and see if still speeding
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tryDragonCapture();
                    }
                }, 200);
            }
        }
    }

    private void onSectorChanged(boolean isDefault){ //1.0.1 removed double latit, double longit -> added boolean isDefault for actionLog
        Log.d(TAG, "onSectorChanged: called");

        //Send request to server
        StringRequest stringRequest = new StringRequest(Request.Method.POST, onSectorChangedURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d(TAG, "onResponse onSectorChange: " + response);
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String code = jsonObject.getString("code");
                    if (code.equals("success")){
                        sectorOwner = jsonObject.getString("ownerID");
                        sectorLevel = jsonObject.getString("level");
                        sectorCaptureTime = jsonObject.getString("captureTime");
                        sectorDragon = jsonObject.getString("dragon");

                        if (isDefault){ //1.0.1 actionLog
                            actionLog(id, "SChng", sectorId.toString(), "", "", "");
                        }

                        tryDragonCapture(); // 1.2.0 dragons

                        if (!id.equals(sectorOwner)){ //Jei ne tavo defencas, bandom uzimti
                            //tryCapture(); v1.2.0 Added capture btn, removed auto-capture
                            btnCapture.setEnabled(true);
                        }
                        else
                        {
                            btnCapture.setEnabled(false);
                        }

                        if (Integer.valueOf(sectorLevel) > 0 && Integer.valueOf(sectorLevel) < 5 && sectorOwner.equals(id)){
                            btnUpgrade.setEnabled(true);
                            btnUpgrade.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(MapsActivity.this, UpgradeActivity.class);
                                    intent.putExtra("gold", gold);
                                    intent.putExtra("level", sectorLevel);
                                    intent.putExtra("playerId", id);
                                    intent.putExtra("sectorId", sectorId.toString());
                                    startActivity(intent);
                                    btnUpgrade.setEnabled(false); //1.0.1 Upgrade fix
                                }
                            });
                        } else {
                            btnUpgrade.setEnabled(false);
                        }
                        //mainMap(); removed 1.0.1
                    } else {
                        Log.d(TAG, "onResponse onSectorChange: Failed to execute OnSectorChange query");
                    }
                } catch (JSONException e){
                    VolleyLog.d(TAG, "onResponse onSectorChange: response failed :( Error: " + e);
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse onSectorChange: " + error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("ID", id);
                params.put("currentSectorID", sectorId.toString());
                return params;
            }
        };
        MySingleton.getInstance(MapsActivity.this).addToRequestQueue(stringRequest);
    }

    private void tryCapture(){
        //Send request to server
        StringRequest stringRequest = new StringRequest(Request.Method.POST, tryCaptureURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d(TAG, "onResponse tryCapture: " + response);
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String code = jsonObject.getString("code");
                    String message = jsonObject.getString("message");
                    if (code.equals("success")){
                        if(!message.equals("")) //1.2.0 Checks if there is no message to not show empty alert
                        {
                            Toast.makeText(MapsActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                        mainMap();
                        onSectorChanged(false);
                    } else {
                        Log.d(TAG, "onResponse tryCapture: Failed to execute tryCapture query");
                        Toast.makeText(MapsActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e){
                    VolleyLog.d(TAG, "onResponse tryCapture: response failed :( Error: " + e);
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse tryCapture: " + error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("ownerID", sectorOwner);
                params.put("captureTime", sectorCaptureTime);
                params.put("userID", id);
                params.put("sectorLevel", sectorLevel);
                params.put("sectorID", sectorId.toString());
                return params;
            }
        };
        MySingleton.getInstance(MapsActivity.this).addToRequestQueue(stringRequest);
    }

    private void getLastKnownLocation(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (mLocationPermissionGranted && UrbanKnights.isActivityVisible()){
            try {
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {

                                    if (isFirstLoad){ //1.0.1 Pan
                                        locationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                        mainMap();
                                        timer.start();
                                        moveCamera(locationLatLng);
                                        isFirstLoad = false;
                                    }
                                    double timeSeconds = (double) timer.getElapsedTimeMili()/10;
                                    Log.d(TAG, "Greitis: " + calcDistance(location.getLatitude(), locationLatLng.latitude, location.getLongitude(), locationLatLng.longitude, 0.0, 0.0)/timeSeconds*3.6 + " | Time: " + timer.getElapsedTimeSecs() + " | Softbanned ?: " + softBanned + " | location: (" + location.getLatitude() + "," + location.getLongitude() + ") | oldLocation: " + locationLatLng);

                                    if (location.getLongitude() != locationLatLng.longitude || location.getLatitude() != locationLatLng.latitude){ //Pasikeite koordinate

                                        if (calcDistance(location.getLatitude(), locationLatLng.latitude, location.getLongitude(), locationLatLng.longitude, 0.0, 0.0)/timeSeconds*3.6>80 && !isFirstLoad && timeSeconds != 0.0) {
                                            softBanned = true;
                                            btnCapture.setEnabled(false);
                                            Toast.makeText(MapsActivity.this, "Judate per greitai. 5 sekundžių pauzė.", Toast.LENGTH_SHORT).show();

                                            final Handler softBan = new Handler(); //1.1.0 bano timeris
                                            final int softBanTimer = 5000; //milliseconds

                                            softBan.postDelayed(new Runnable(){
                                                public void run(){
                                                    Log.d(TAG, "onSpeedingOff: unbanned!");
                                                    softBanned = false;
                                                }
                                            }, softBanTimer);

                                        }
                                        timer.stop(); timer.start();
                                        locationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    }

                                    if (!softBanned){ //v1.1.0
                                        getSectorId(locationLatLng);
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: Failed to get user location. Exception: " + e);
                            }
                        });
            } catch (SecurityException e){
                Log.d(TAG, "Security location exception: " + e);
            }
        }
    }

    //1.1.0
    public static double calcDistance(double lat1, double lat2, double lon1,
                                      double lon2, double el1, double el2) {
        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: Getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true;
                initMap();
            }else {
                ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else {
            ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        mLocationPermissionGranted = false;
        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if (grantResults.length > 0){
                    for (int i =0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted = false;

                            displayPermissionPopup(); //Jei paspaudzia deny vel papraso

                            Log.d(TAG, "onRequestPermissionsResult: failed permission");
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionGranted = true;
                    //initialize map
                    initMap();
                }
            }
        }
    }

    private void displayPermissionPopup(){
        //Boolean kad initializinom sita popupa
        if (existsPermDialog){ //Kazkodel sukuria 2 instances popupo tai jei jau yra ismeta i sudu kruva finally ffs im saved from this aids ass shit :)
            dialog.dismiss();
        }
        existsPermDialog = true;

        dialog = new AlertDialog.Builder(MapsActivity.this).create();
        dialog.setTitle("Dėmėsio");
        dialog.setMessage("Prašome leisti nustatyti jūsų lokaciją. Jei to nepadarysite, žaidimas bus nefunkcialus!");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getLocationPermission();
                    }
                });
        dialog.show();
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed(); nieko nedarom
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: called");
        super.onResume();
        UrbanKnights.activityResumed();
        if (mLocationPermissionGranted){
            mainMap();
            onSectorChanged(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        UrbanKnights.activityPaused();
    }

    @Override
    protected void onStop() {
        super.onStop();
        UrbanKnights.activityPaused();
    }
}
