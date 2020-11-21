package com.example.caloriecalculator;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
    private DirectionsRoute walkingRoute;
    private DirectionsRoute cyclingRoute;
    private Point destination = Point.fromLngLat(-99.167663574, 19.426984786987);  //set predestined location
    private String lastSelectedDirectionsProfile = DirectionsCriteria.PROFILE_CYCLING;  //store last selected profile
    private final String[] profiles = new String[]{
            DirectionsCriteria.PROFILE_CYCLING,
            DirectionsCriteria.PROFILE_WALKING
    };

    LocationManager locationManager;
    LocationListener locationListener;
    LocationRequest locationRequest;
    LatLng userLocation;
    FloatingActionButton mapsFab;

    private final int REQUEST_CHECK_SETTINGS = 9001;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         * Get an instance of the map, this has to be set before setContentView()
         */
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_maps);

        Bundle extras = getIntent().getExtras();
        lastSelectedDirectionsProfile = extras.getString("profile");

        /*
         * Initialize floating action button
         */
        mapsFab = findViewById(R.id.maps_fab);

        /*
         * Ask to user to open his location as google style
         */
        createLocationRequest();
        createLocationRequestPopup();

        /*
         * Initialize location manager and listener
         */
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

        mapView = findViewById(R.id.mapView);
        /*
         * Load map asynchronously
         */
        mapView.getMapAsync(mapboxMap -> {
            /*
             * Set view style such as street view, satellite view or traffic view, and night mode can be set here as well
             */
            mapboxMap.setStyle(Style.OUTDOORS, style -> {

                MapsActivity.this.mapboxMap = mapboxMap;

                initSource(style);

                initLayers(style);

                //getAllRoutes(false);

                mapboxMap.addOnMapLongClickListener(MapsActivity.this);

                /*
                 * Check location permission, if not granted ask to user
                 */
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

                    Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (lastLocation != null) {
                        LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                        mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15));
                    }
                }

                Toast.makeText(MapsActivity.this,
                        R.string.instruction, Toast.LENGTH_SHORT).show();
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
                    mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15));
                }
            }
        }
    }

    /**
     * Load route info for each Directions API profile.
     *
     */
    private void getAllRoutes() {
        for (String profile : profiles) {
            getSingleRoute(profile);
        }
    }

    @Override
    public boolean onMapLongClick(@NonNull LatLng point) {
        destination = Point.fromLngLat(point.getLongitude(), point.getLatitude());
        moveDestinationMarkerToNewLocation(point);
        getAllRoutes();
        return true;
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
     * Display the Directions API route line depending on which profile was last
     * selected.
     */
    private void showRouteLine() {
        if (mapboxMap != null) {
            mapboxMap.getStyle(style -> {

                // Retrieve and update the source designated for showing the directions route
                GeoJsonSource routeLineSource = style.getSourceAs(ROUTE_SOURCE_ID);

                // Create a LineString with the directions route's geometry and
                // reset the GeoJSON source for the route LineLayer source
                if (routeLineSource != null) {
                    switch (lastSelectedDirectionsProfile) {
                        case DirectionsCriteria.PROFILE_WALKING:
                            if (walkingRoute != null)
                                routeLineSource.setGeoJson(LineString.fromPolyline(Objects.requireNonNull(walkingRoute.geometry()), PRECISION_6));
                            break;
                        case DirectionsCriteria.PROFILE_CYCLING:
                            if (cyclingRoute != null)
                            routeLineSource.setGeoJson(LineString.fromPolyline(Objects.requireNonNull(cyclingRoute.geometry()), PRECISION_6));
                            break;
                        default:
                            break;
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

        /*
         * Add the LineLayer to the map. This layer will display the directions route.
         */
        routeLayer.setProperties(
                lineCap(Property.LINE_CAP_ROUND),  //shape of the route line endings
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(5f),
                lineColor(Color.parseColor("#006eff"))  //set route line color here
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
             * Ask from the server for a route asynchronously
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

                    switch (profile) {
                        case DirectionsCriteria.PROFILE_WALKING:
                            walkingRoute = response.body().routes().get(0);
                            break;
                        case DirectionsCriteria.PROFILE_CYCLING:
                            cyclingRoute = response.body().routes().get(0);
                            break;
                        default:
                            break;
                    }
                    showRouteLine();
                }

                @Override
                public void onFailure(@NonNull Call<DirectionsResponse> call, @NonNull Throwable throwable) {
                    Timber.e("Error: " + throwable.getMessage());
                    Toast.makeText(MapsActivity.this,
                            "Error: " + throwable.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
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
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f), 2000);
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