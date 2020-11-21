package com.example.caloriecalculator;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;


public class MapsActivity extends FragmentActivity implements MapboxMap.OnMapClickListener {
/*
    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    LocationRequest locationRequest;
    LatLng userLocation;
    private LatLng mOrigin;
    private LatLng mDestination;
    FloatingActionButton mapsFab;

    private final int REQUEST_CHECK_SETTINGS = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mapsFab = findViewById(R.id.maps_fab);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        //
        mOrigin = new LatLng(41.3949, 2.0086);
        mDestination = new LatLng(41.1258, 1.2035);
        //
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        createLocationRequest();

        createLocationRequestPopup();

        mMap.setOnMapLongClickListener(this);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                mapsFab.setImageResource(R.drawable.baseline_location_searching_24);
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                mapsFab.setImageResource(R.drawable.baseline_gps_off_24);
            }
        };

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15));
            }
        }

        //
        mMap.addMarker(new MarkerOptions().position(mOrigin).title("Origin"));
        mMap.addMarker(new MarkerOptions().position(mDestination).title("Destination"));
        new TaskDirectionRequest().execute(getRequestedUrl(mOrigin,mDestination));
        //
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && requestCode == 1) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastLocation != null) {
                    LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15));
                }
            }
        }
    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void createLocationRequestPopup() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, locationSettingsResponse -> {
            mapsFab.setImageResource(R.drawable.baseline_location_searching_24);
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
        });

        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(MapsActivity.this,
                            REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error.
                }
            }
        });
    }

    private String getRequestedUrl(LatLng origin, LatLng destination) {
        String strOrigin = "origin=" + origin.latitude + "," + origin.longitude;
        String strDestination = "destination=" + destination.latitude + "," + destination.longitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";

        String param = strOrigin + "&" + strDestination + "&" + sensor + "&" + mode;
        String output = "json";
        String APIKEY = getResources().getString(R.string.google_maps_key);

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param + APIKEY;
        return url;
    }

    private String requestDirection(String requestedUrl) {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(requestedUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            responseString = stringBuffer.toString();
            bufferedReader.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        httpURLConnection.disconnect();
        return responseString;
    }


    //Get JSON data from Google Direction
    public class TaskDirectionRequest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String responseString) {
            super.onPostExecute(responseString);
            //Json object parsing
            TaskParseDirection parseResult = new TaskParseDirection();
            parseResult.execute(responseString);
        }
    }

    //Parse JSON Object from Google Direction API & display it on Map
    public class TaskParseDirection extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonString) {
            List<List<HashMap<String, String>>> routes = null;
            JSONObject jsonObject = null;

            try {
                jsonObject = new JSONObject(jsonString[0]);
                DirectionParser parser = new DirectionParser();
                routes = parser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            super.onPostExecute(lists);
            ArrayList points = null;
            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lng"));

                    points.add(new LatLng(lat, lon));
                }
                polylineOptions.addAll(points);
                polylineOptions.width(15f);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }
            if (polylineOptions != null) {
                mMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getApplicationContext(), "Direction not found", Toast.LENGTH_LONG).show();
            }
        }


    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    public void findLocation(View view) {
        createLocationRequestPopup();

        LatLng barcelona = new LatLng(41.3949, 2.0086);

        if (userLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(barcelona, 8f));
        }
    }
    */

    private static final String TAG = "MapsActivity";
    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ICON_LAYER_ID = "icon-layer-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";
    private static final String RED_PIN_ICON_ID = "red-pin-icon-id";
    private MapView mapView;
    private MapboxMap mapboxMap;
    private DirectionsRoute drivingRoute;
    private DirectionsRoute walkingRoute;
    private DirectionsRoute cyclingRoute;
    private MapboxDirections client;
    private Point origin = Point.fromLngLat(-99.13037323366, 19.40488375253);
    private Point destination = Point.fromLngLat(-99.167663574, 19.426984786987);
    private String lastSelectedDirectionsProfile = DirectionsCriteria.PROFILE_DRIVING;
    private Button drivingButton;
    private Button walkingButton;
    private Button cyclingButton;
    private boolean firstRouteDrawn = false;
    private String[] profiles = new String[]{
            DirectionsCriteria.PROFILE_DRIVING,
            DirectionsCriteria.PROFILE_CYCLING,
            DirectionsCriteria.PROFILE_WALKING
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_maps);

        drivingButton = findViewById(R.id.driving_profile_button);
        drivingButton.setTextColor(Color.WHITE);
        walkingButton = findViewById(R.id.walking_profile_button);
        cyclingButton = findViewById(R.id.cycling_profile_button);

        mapView = findViewById(R.id.mapView);
        //mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        MapsActivity.this.mapboxMap = mapboxMap;

                        initSource(style);

                        initLayers(style);

                        getAllRoutes(false);

                        initButtonClickListeners();

                        mapboxMap.addOnMapClickListener(MapsActivity.this);

                        Toast.makeText(MapsActivity.this,
                                R.string.instruction, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    /**
     * Load route info for each Directions API profile.
     *
     * @param fromMapClick whether the route loading is being triggered from tapping
     *                     on the map
     */
    private void getAllRoutes(boolean fromMapClick) {
        for (String profile : profiles) {
            getSingleRoute(profile, fromMapClick);
        }
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        destination = Point.fromLngLat(point.getLongitude(), point.getLatitude());
        moveDestinationMarkerToNewLocation(point);
        getAllRoutes(true);
        return true;
    }

    /**
     * Move the destination marker to wherever the map was tapped on.
     *
     * @param pointToMoveMarkerTo where the map was tapped on
     */
    private void moveDestinationMarkerToNewLocation(LatLng pointToMoveMarkerTo) {
        mapboxMap.getStyle(new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                GeoJsonSource destinationIconGeoJsonSource = style.getSourceAs(ICON_SOURCE_ID);
                if (destinationIconGeoJsonSource != null) {
                    destinationIconGeoJsonSource.setGeoJson(Feature.fromGeometry(Point.fromLngLat(
                            pointToMoveMarkerTo.getLongitude(), pointToMoveMarkerTo.getLatitude())));
                }
            }
        });
    }

    /**
     * Add the source for the Directions API route line LineLayer.
     */
    private void initSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));
        GeoJsonSource iconGeoJsonSource = new GeoJsonSource(ICON_SOURCE_ID,
                Feature.fromGeometry(Point.fromLngLat(destination.longitude(),
                        destination.latitude())));
        loadedMapStyle.addSource(iconGeoJsonSource);
    }

    /**
     * Set up the click listeners on the buttons for each Directions API profile.
     */
    private void initButtonClickListeners() {
        drivingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drivingButton.setTextColor(Color.WHITE);
                walkingButton.setTextColor(Color.BLACK);
                cyclingButton.setTextColor(Color.BLACK);
                lastSelectedDirectionsProfile = DirectionsCriteria.PROFILE_DRIVING;
                showRouteLine();
            }
        });
        walkingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drivingButton.setTextColor(Color.BLACK);
                walkingButton.setTextColor(Color.WHITE);
                cyclingButton.setTextColor(Color.BLACK);
                lastSelectedDirectionsProfile = DirectionsCriteria.PROFILE_WALKING;
                showRouteLine();
            }
        });
        cyclingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drivingButton.setTextColor(Color.BLACK);
                walkingButton.setTextColor(Color.BLACK);
                cyclingButton.setTextColor(Color.WHITE);
                lastSelectedDirectionsProfile = DirectionsCriteria.PROFILE_CYCLING;
                showRouteLine();
            }
        });
    }

    /**
     * Display the Directions API route line depending on which profile was last
     * selected.
     */
    private void showRouteLine() {
        if (mapboxMap != null) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {

                    // Retrieve and update the source designated for showing the directions route
                    GeoJsonSource routeLineSource = style.getSourceAs(ROUTE_SOURCE_ID);

                    // Create a LineString with the directions route's geometry and
                    // reset the GeoJSON source for the route LineLayer source
                    if (routeLineSource != null) {
                        switch (lastSelectedDirectionsProfile) {
                            case DirectionsCriteria.PROFILE_DRIVING:
                                routeLineSource.setGeoJson(LineString.fromPolyline(drivingRoute.geometry(),
                                        PRECISION_6));
                                break;
                            case DirectionsCriteria.PROFILE_WALKING:
                                routeLineSource.setGeoJson(LineString.fromPolyline(walkingRoute.geometry(),
                                        PRECISION_6));
                                break;
                            case DirectionsCriteria.PROFILE_CYCLING:
                                routeLineSource.setGeoJson(LineString.fromPolyline(cyclingRoute.geometry(),
                                        PRECISION_6));
                                break;
                            default:
                                break;
                        }
                    }
                }
            });
        }
    }

    /**
     * Add the route and icon layers to the map
     */
    private void initLayers(@NonNull Style loadedMapStyle) {
        LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);

        // Add the LineLayer to the map. This layer will display the directions route.
        routeLayer.setProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(5f),
                lineColor(Color.parseColor("#006eff"))
        );
        loadedMapStyle.addLayer(routeLayer);

//// Add the red marker icon image to the map
//        loadedMapStyle.addImage(RED_PIN_ICON_ID, Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
//                getResources().getDrawable(R.drawable.baseline_gps_fixed_24))));

        // Add the red marker icon SymbolLayer to the map
        loadedMapStyle.addLayer(new SymbolLayer(ICON_LAYER_ID, ICON_SOURCE_ID).withProperties(
                iconImage(RED_PIN_ICON_ID),
                iconIgnorePlacement(true),
                iconAllowOverlap(true),
                iconOffset(new Float[]{0f, -9f})));
    }

    /**
     * Make a request to the Mapbox Directions API. Once successful, pass the route to the
     * route layer.
     *
     * @param profile the directions profile to use in the Directions API request
     */
    private void getSingleRoute(String profile, boolean fromMapClick) {
        client = MapboxDirections.builder()
                .origin(origin)
                .destination(destination)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(profile)
                .accessToken(getString(R.string.mapbox_access_token))
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                // You can get the generic HTTP info about the response
                Timber.d("Response code: " + response.code());
                if (response.body() == null) {
                    Timber.e("No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().routes().size() < 1) {
                    Timber.e("No routes found");
                    return;
                }

                switch (profile) {
                    case DirectionsCriteria.PROFILE_DRIVING:
                        drivingRoute = response.body().routes().get(0);
                        drivingButton.setText(String.format(getString(R.string.driving_profile),
                                String.valueOf(TimeUnit.SECONDS.toMinutes(drivingRoute.duration().longValue()))));
                        if (!firstRouteDrawn) {
                            showRouteLine();
                            firstRouteDrawn = true;
                        }
                        break;
                    case DirectionsCriteria.PROFILE_WALKING:
                        walkingRoute = response.body().routes().get(0);
                        walkingButton.setText(String.format(getString(R.string.walking_profile),
                                String.valueOf(TimeUnit.SECONDS
                                        .toMinutes(walkingRoute.duration().longValue()))));
                        break;
                    case DirectionsCriteria.PROFILE_CYCLING:
                        cyclingRoute = response.body().routes().get(0);
                        cyclingButton.setText(String.format(getString(R.string.cycling_profile),
                                String.valueOf(TimeUnit.SECONDS
                                        .toMinutes(cyclingRoute.duration().longValue()))));
                        break;
                    default:
                        break;
                }
                if (fromMapClick) {
                    showRouteLine();
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Timber.e("Error: " + throwable.getMessage());
                Toast.makeText(MapsActivity.this,
                        "Error: " + throwable.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

}