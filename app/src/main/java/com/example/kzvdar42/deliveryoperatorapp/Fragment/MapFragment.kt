package com.example.kzvdar42.deliveryoperatorapp.Fragment

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kzvdar42.deliveryoperatorapp.R
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import kotlinx.android.synthetic.main.fragment_map.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class MapFragment : Fragment(), OnMapReadyCallback, PermissionsListener, LocationEngineListener {


    private lateinit var mapView: MapView
    // variables for adding location layer
    private lateinit var mapboxMap: MapboxMap
    private var permissionsManager: PermissionsManager? = null
    private var locationLayerPlugin: LocationLayerPlugin? = null
    private var locationEngine: LocationEngine? = null
    private var originLocation: Location? = null
    // variables for adding a marker
    private var originCoord: LatLng? = null
    // variables for calculating and drawing a route
    private var originPosition: Point? = null
    private var currentRoute: DirectionsRoute? = null
    private var navigationMapRoute: NavigationMapRoute? = null

    // variables for order info
    private lateinit var orderName: String
    private lateinit var orderDescription: String
    private lateinit var orderFrom: LatLng
    private lateinit var orderTo: LatLng
    private var orderFromPosition: Point? = null
    private var orderToPosition: Point? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {



        val rootView = inflater.inflate(R.layout.fragment_map, container, false)
        orderName = "Order #-1"
        orderDescription = "Lorem ipsum"
        val coords = doubleArrayOf(55.7476907, 48.7433593, 55.7867635, 49.1216088)
        orderFrom = LatLng(coords[0], coords[1])
        orderTo = LatLng(coords[2], coords[3])

        // Initiate map
        Mapbox.getInstance(context!!, getString(R.string.access_token))
        mapView = rootView.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        return rootView
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        enableLocationPlugin()

        originPosition = Point.fromLngLat(originLocation!!.longitude, originLocation!!.latitude)
        orderFromPosition = Point.fromLngLat(orderFrom.longitude, orderFrom.latitude)
        orderToPosition = Point.fromLngLat(orderTo.longitude, orderTo.latitude)
        originCoord = LatLng(originLocation!!.latitude, originLocation!!.longitude)

        val latLngBounds = LatLngBounds.Builder()
                .include(originCoord!!)
                .include(orderFrom)
                .include(orderTo)
                .build()
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100))

        getRoute(originPosition!!, orderFromPosition!!)

        mapboxMap.addMarker(MarkerOptions()
                .position(orderFrom)
                .title(orderName)
                .snippet(orderDescription))
        mapboxMap.addMarker(MarkerOptions()
                .position(orderTo)
                .title(orderName)
                .snippet(orderDescription))

        startButton.setOnClickListener { _: View? ->
            run {
                val simulateRoute = true
                val options = NavigationLauncherOptions.builder()
                        .directionsRoute(currentRoute)
                        .shouldSimulateRoute(simulateRoute)
                        .build()
                // Call this method with Context from within an Activity
                NavigationLauncher.startNavigation(activity, options)
            }
        }
    }

    private fun getRoute(origin: Point, destination: Point) {
        NavigationRoute.builder(context)
                .accessToken(Mapbox.getAccessToken()!!)
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(object : Callback<DirectionsResponse> {
                    override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                        // You can get the generic HTTP info about the response
                        Timber.d("Response code: %s", response.code())
                        if (response.body() == null) {
                            Timber.e("No routes found, make sure you set the right user and access token.")
                            return
                        } else if (response.body()?.routes()!!.size < 1) {
                            Timber.e("No routes found")
                            return
                        }

                        currentRoute = response.body()!!.routes()[0]

                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute?.removeRoute()
                        } else {
                            startButton.isEnabled = true
                            navigationMapRoute = NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute)
                        }
                        navigationMapRoute?.addRoute(currentRoute)
                    }


                    override fun onFailure(call: Call<DirectionsResponse>, throwable: Throwable) {
                        Timber.e("Error: %s", throwable.message)
                    }
                })
    }

    private fun enableLocationPlugin() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(activity)) {
            initializeLocationEngine()
            // Create an instance of the plugin. Adding in LocationLayerOptions is also an optional
            // parameter
            val locationLayerPlugin = LocationLayerPlugin(mapView, mapboxMap)

            // Set the plugin's camera mode
            locationLayerPlugin.cameraMode = CameraMode.TRACKING
            addObserver()
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager!!.requestLocationPermissions(activity)
        }
    }

    @SuppressLint("MissingPermission")
    private fun initializeLocationEngine() {
        val locationEngineProvider = LocationEngineProvider(activity)
        locationEngine = locationEngineProvider.obtainBestLocationEngineAvailable()
        locationEngine!!.priority = LocationEnginePriority.BALANCED_POWER_ACCURACY
        locationEngine!!.activate()

        val lastLocation = locationEngine!!.lastLocation
        if (lastLocation != null) {
            originLocation = lastLocation
        } else {
            locationEngine!!.addLocationEngineListener(this)
        }
    }


    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Toast.makeText(context, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationPlugin()
        } else {
            Toast.makeText(context, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show()
            activity?.finish()
        }
    }

    override fun onLocationChanged(location: Location?) {
    }

    override fun onConnected() {
    }

    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()
        mapView.onStart()
        locationLayerPlugin?.onStart()
    }


    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }


    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }


    override fun onStop() {
        super.onStop()
        mapView.onStop()
        locationLayerPlugin?.onStart()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }


    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }


    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}

private fun addObserver() {

}