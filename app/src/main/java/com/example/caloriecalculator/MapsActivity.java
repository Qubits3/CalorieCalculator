package com.example.caloriecalculator;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.turf.TurfMeasurement;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Objects;

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

public class MapsActivity extends FragmentActivity implements MapboxMap.OnMapLongClickListener {

    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ICON_LAYER_ID = "icon-layer-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";
    private static final String RED_PIN_ICON_ID = "red-pin-icon-id";

    private MapView mapView;
    private MapboxMap mapboxMap;
    private DirectionsRoute selectedRoute;
    GeoJsonSource routeLineSource;
    private Point destination = Point.fromLngLat(-99.167663574, 19.426984786987);  // Set predestined location
    private String selectedDirectionsProfile = DirectionsCriteria.PROFILE_CYCLING;  // Store last selected profile
    private final String[] profiles = new String[]{
            DirectionsCriteria.PROFILE_CYCLING,
            DirectionsCriteria.PROFILE_WALKING
    };

    LocationManager locationManager;
    LocationListener locationListener;
    LocationRequest locationRequest;
    LatLng userLocation;
    FloatingActionButton mapsFab;
    TextView toplamKaloriTextView, ortalamaHizTextView, kalanYolTextView;
    Button startRouteButton;
    private boolean isRouteStarted = false;
    private boolean isConnected = false;
    DecimalFormat df;
    SharedPreferences sharedPreferences;

    Handler handler;
    Runnable runnable;

    BigDecimal burnedCalorie = new BigDecimal(0), burnedCalorieOfAllDay = new BigDecimal(0);
    int MET = 0;

    String weight;

    private final int REQUEST_CHECK_SETTINGS = 9001;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        df = new DecimalFormat("####0.00");
        handler = new Handler();

        /*
         * Get an instance of the map, this has to be set before setContentView()
         */
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_maps);

        Bundle extras = getIntent().getExtras();
        selectedDirectionsProfile = extras.getString("profile");  // Get profile from Main Activity

        sharedPreferences = this.getSharedPreferences("com.example.caloriecalculator", Context.MODE_PRIVATE);

        weight = sharedPreferences.getString("kilo", "60");

        resetDailyCalorieOfAllDay();
        burnedCalorieOfAllDay = new BigDecimal(sharedPreferences.getString("dailyCalorieOfAllDay", "0.0"));

        storeDay();

        System.out.println("all calorie " + burnedCalorieOfAllDay);

        /*
         * Initialize components
         */
        mapsFab = findViewById(R.id.maps_fab);
        mapView = findViewById(R.id.mapView);
        toplamKaloriTextView = findViewById(R.id.toplamKaloriTextView);
        ortalamaHizTextView = findViewById(R.id.ortalamaHizTextView);
        startRouteButton = findViewById(R.id.startRouteButton);
        kalanYolTextView = findViewById(R.id.kalanYolTextView);

        /*
         * Ask to user to open his location as Google style
         */
        createLocationRequest();
        createLocationRequestPopup();

        /*
         * Initialize location manager and listener
         */
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            @Override
            public void onLocationChanged(@NonNull Location location) {
                userLocation = new LatLng(location.getLatitude(), location.getLongitude());  // Store current user location
                ortalamaHizTextView.setText(String.format("%.1f", location.getSpeed()) + " m/s");
                isConnected = true;
                if (isRouteStarted)
                    mapboxMap.animateCamera(CameraUpdateFactory.newLatLng(userLocation));  // Track current location
                if (!isRouteStarted) {
                    toplamKaloriTextView.setText("0 kalori");
                    kalanYolTextView.setText("0 metre");
                    ortalamaHizTextView.setText("0 m/s");
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                isConnected = true;
                mapsFab.setEnabled(true);  // Enable floating action button if GPS is on
                mapsFab.setImageResource(R.drawable.baseline_location_searching_24);
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                isConnected = false;
                mapsFab.setEnabled(false);  // Disable floating action button if GPS is off
                mapsFab.setImageResource(R.drawable.baseline_gps_off_24);
            }
        };

        /*
         * Load map asynchronously
         */
        mapView.getMapAsync(mapboxMap -> {
            /*
             * Set view style such as street view, satellite view or traffic view, and night mode can be set here as well
             */
            mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {

                MapsActivity.this.mapboxMap = mapboxMap;

                initSource(style);

                initLayers(style);

                mapboxMap.addOnMapLongClickListener(MapsActivity.this);

                /*
                 * Check location permission, ask to user if not granted
                 */
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

                    enableLocationComponent(style);

                    Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (lastLocation != null) {
                        LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                        mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15f));
                    }
                }

                /*
                 * Move the camera to Googleplex at first start
                 */

                float latitude = sharedPreferences.getFloat("lastLatitude", 37.4220656f);
                float longitude = sharedPreferences.getFloat("lastLongitude", -122.0862784f);
                mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 10f));
            });
        });

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
                    mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15f));
                }
            }
        }
    }

    /**
     * Draw dot to the current location
     *
     * @param loadedMapStyle gets the current style
     */
    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {

        // Get an instance of the component
        LocationComponent locationComponent = mapboxMap.getLocationComponent();

        // Activate with options
        locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(this, loadedMapStyle).build());

        // Enable to make component visible
        locationComponent.setLocationComponentEnabled(true);

        // Set the component's camera mode
        locationComponent.setCameraMode(CameraMode.TRACKING);

        // Set the component's render mode
        locationComponent.setRenderMode(RenderMode.NORMAL);
    }

    /**
     * Load route info for each Directions API profile.
     */
    private void getAllRoutes() {
        for (String profile : profiles) {
            getSingleRoute(profile);
        }
    }

    @SuppressLint("SetTextI18n")
    private void onRouteOngoing(Location location) {

        if (isRouteStarted) {
            runnable = () -> {
                int currentSpeed = getSpeed(location);
                if (selectedDirectionsProfile.equals(DirectionsCriteria.PROFILE_CYCLING)) {
                    if (currentSpeed < 20)
                        MET = 4;
                    else if (currentSpeed > 20 && currentSpeed <= 22)
                        MET = 8;
                    else if (currentSpeed > 22 && currentSpeed <= 25)
                        MET = 10;
                    else if (currentSpeed > 25 && currentSpeed <= 30)
                        MET = 12;
                    else if (currentSpeed > 30)
                        MET = 16;
                } else {
                    if (currentSpeed < 4)
                        MET = 2;
                    else if (currentSpeed > 4 && currentSpeed <= 8)
                        MET = 6;
                    else if (currentSpeed > 8 && currentSpeed <= 12)
                        MET = 8;
                    else if (currentSpeed > 12 && currentSpeed < 16)
                        MET = 13;
                    else if (currentSpeed > 16)
                        MET = 17;
                }

                burnedCalorie = burnedCalorie.add(BigDecimal.valueOf((MET * 3.5 * Integer.parseInt(weight)) / 12000));  // Calculate burned calorie every second
                toplamKaloriTextView.setText(String.valueOf(burnedCalorie)
                        .substring(0, String.valueOf(burnedCalorie).indexOf(".") + 3).replace(".", ",") + " kalori");
                kalanYolTextView.setText(getDistance(userLocation, destination));

                handler.postDelayed(runnable, 1000);  // Post runnable every second
            };
            handler.post(runnable);  // Post runnable for the first time
        }
    }

    @SuppressLint("SetTextI18n")
    public void startRoute(View view) {
        resetDailyCalorieOfAllDay();

        toplamKaloriTextView.setText("0  kalori");
        isRouteStarted = !isRouteStarted;  // Toggle between true and false

        mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f));  // Track current location

        onRouteOngoing(convertLatLngToLocation(userLocation));

        if (!isRouteStarted) {  // Route has finished
            burnedCalorieOfAllDay = burnedCalorieOfAllDay.add(burnedCalorie);  // Sum total calorie

            handler.removeCallbacks(runnable);
            startRouteButton.setText("Başla");
            startRouteButton.setEnabled(false);
            ortalamaHizTextView.setText("0 m/s");

            burnedCalorie = new BigDecimal(0);  // Reset calorie at end of the route
            storeDay();
            storeTotalCalorie();
            storeTotalCalorieToDay();

            removeRouteLine();
        } else {  // Route is ongoing
            startRouteButton.setText("Bitir");
        }
    }

    /**
     * Store calorie to device with shared preferences
     */
    private void storeTotalCalorie() {
        sharedPreferences.edit()
                .putString("dailyCalorieOfAllDay", burnedCalorieOfAllDay.toString())
                .apply();
    }

    /**
     * Stores total calorie day by day
     */
    private void storeTotalCalorieToDay() {
        sharedPreferences.edit()
                .putString(String.valueOf(getDay()), burnedCalorieOfAllDay.toString())
                .apply();
    }

    /**
     * Store current day to device with shared preferences
     */
    public void storeDay() {
        sharedPreferences.edit()
                .putInt("day", getDay())
                .apply();
    }

    /**
     * Resets total calorie
     */
    private void resetDailyCalorieOfAllDay() {
        if (sharedPreferences.getInt("day", 0) != getDay()) {  // Reset daily calorie if day is different
            sharedPreferences.edit()
                    .putString("dailyCalorieOfAllDay", "0.0")
                    .apply();
            System.out.println("Calorie reset!");
        }
    }

    /**
     * Gets current day value from {@link Calendar}
     *
     * @return day value in integer
     */
    public int getDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    }

    /**
     * Set route at long click
     *
     * @param point where the long click location is
     */
    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public boolean onMapLongClick(@NonNull LatLng point) {
        if (isConnected) {
            destination = Point.fromLngLat(point.getLongitude(), point.getLatitude());
            moveDestinationMarkerToNewLocation(point);
            getAllRoutes();

            // Set bounds for route zoom
            LatLngBounds latLngBounds = new LatLngBounds.Builder()
                    .include(userLocation)
                    .include(new LatLng(destination.latitude(), destination.longitude()))
                    .build();

            // Zoom camera to the route
            mapboxMap.easeCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 300), 5000);

            startRouteButton.setEnabled(true);
            return true;
        } else
            return false;
    }

    /**
     * Move the destination marker to wherever the map was tapped on.
     *
     * @param pointToMoveMarkerTo where the map was tapped on
     */
    private void moveDestinationMarkerToNewLocation(LatLng pointToMoveMarkerTo) {
        mapboxMap.getStyle(style -> {
            GeoJsonSource destinationIconGeoJsonSource = style.getSourceAs(ICON_SOURCE_ID);
            if (destinationIconGeoJsonSource != null) {
                destinationIconGeoJsonSource.setGeoJson(Feature.fromGeometry(Point.fromLngLat(
                        pointToMoveMarkerTo.getLongitude(), pointToMoveMarkerTo.getLatitude())));
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
     * Add the route and icon layers to the map
     */
    private void initLayers(@NonNull Style loadedMapStyle) {
        LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);

        /*
         * Add the LineLayer to the map. This layer will display the directions route.
         */
        routeLayer.setProperties(
                lineCap(Property.LINE_CAP_ROUND),  // Shape of the route line endings
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(5f),
                lineColor(Color.parseColor("#006eff"))  // Set route line color here
        );
        loadedMapStyle.addLayer(routeLayer);

        /*
         * Add the marker icon image to the map
         */
        loadedMapStyle.addImage(RED_PIN_ICON_ID, Objects.requireNonNull(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_room_24, null)));

        /*
         * Add the marker icon SymbolLayer to the map
         */
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
    private void getSingleRoute(String profile) {
        /*
         * Initialize Mapbox Directions
         */
        if (userLocation != null) {
            MapboxDirections mapboxDirections = MapboxDirections.builder()
                    .origin(Point.fromLngLat(userLocation.getLongitude(), userLocation.getLatitude()))
                    .destination(destination)
                    .overview(DirectionsCriteria.OVERVIEW_FULL)
                    .profile(profile)
                    .accessToken(getString(R.string.mapbox_access_token))
                    .build();

            /*
             * Ask to the server a route asynchronously
             */
            mapboxDirections.enqueueCall(new Callback<DirectionsResponse>() {
                @Override
                public void onResponse(@NonNull Call<DirectionsResponse> call, @NonNull Response<DirectionsResponse> response) {
                    // You can get the generic HTTP info about the response
                    Timber.d("Response code: " + response.code());
                    if (response.body() == null) {
                        Timber.e("No routes found, make sure you set the right user and access token.");
                        return;
                    } else if (response.body().routes().size() < 1) {
                        Timber.e("No routes found");
                        return;
                    }

                    selectedRoute = response.body().routes().get(0);
                    showRouteLine();
                }

                @Override
                public void onFailure(@NonNull Call<DirectionsResponse> call, @NonNull Throwable throwable) {
                    Timber.e("Error: " + throwable.getMessage());
                    Toast.makeText(MapsActivity.this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Display the Directions API route line depending on which profile was last
     * selected.
     */
    private void showRouteLine() {
        if (mapboxMap != null) {
            mapboxMap.getStyle(style -> {

                // Retrieve and update the source designated for showing the directions route
                routeLineSource = style.getSourceAs(ROUTE_SOURCE_ID);

                // Create a LineString with the directions route's geometry and
                // Reset the GeoJSON source for the route LineLayer source
                if (routeLineSource != null) {
                    routeLineSource.setGeoJson(LineString.fromPolyline(Objects.requireNonNull(selectedRoute.geometry()), PRECISION_6));
                }
            });
        }
    }

    /**
     * Remove the blue route line and destination marker from the map
     */
    private void removeRouteLine() {
        if (mapboxMap != null) {
            routeLineSource.setGeoJson(FeatureCollection.fromJson(""));
            mapboxMap.getStyle(style -> style.removeImage(RED_PIN_ICON_ID));
        }
    }

    /**
     * Creates a location request
     */
    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Creates a location request like Google do
     */
    protected void createLocationRequestPopup() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, locationSettingsResponse -> mapsFab.setImageResource(R.drawable.baseline_location_searching_24));

        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(MapsActivity.this,
                            REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                    sendEx.printStackTrace();
                }
            }
        });
    }

    /**
     * Floating action button function"
     */
    public void findLocation(View view) {
        createLocationRequestPopup();
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 18f), 2000);

        System.out.println(burnedCalorieOfAllDay);

        /*
         * Save last location
         */
        if (userLocation != null) {
            SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.caloriecalculator", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("lastLatitude", (float) userLocation.getLatitude());
            editor.putFloat("lastLongitude", (float) userLocation.getLongitude());
            editor.apply();
        }
    }

    /**
     * Converts LatLng to Location
     *
     * @param latLng is location in LatLng
     * @return location in Location type
     */
    public Location convertLatLngToLocation(@NonNull LatLng latLng) {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(latLng.getLatitude());
        location.setLongitude(latLng.getLongitude());
        return location;
    }

    /**
     * Calculates distance between two points
     *
     * @param point1 is the first location
     * @param point2 is the second location
     * @return the distance, automatically selects whether meter or kilometer
     */
    private String getDistance(@NonNull LatLng point1, @NonNull Point point2) {
        double distance = TurfMeasurement.distance(Point.fromLngLat(
                point1.getLongitude(),
                point1.getLatitude()),
                Point.fromLngLat(point2.longitude(), point2.latitude())) * 1000;

        if (distance >= 1000) {
            return String.format("%.1f", distance / 1000) + " kilometre";
        } else {
            return Math.round(distance) + " metre";
        }
    }

    private int getSpeed(Location location) {
        return Math.round(location.getSpeed() * 3.6f);  //Convert m/s to km/h
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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
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