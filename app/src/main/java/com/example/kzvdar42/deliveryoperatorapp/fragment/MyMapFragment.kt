package com.example.kzvdar42.deliveryoperatorapp.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.kzvdar42.deliveryoperatorapp.BuildConfig
import com.example.kzvdar42.deliveryoperatorapp.R
import com.example.kzvdar42.deliveryoperatorapp.util.Constants
import com.example.kzvdar42.deliveryoperatorapp.util.MyNavigationLauncher
import com.example.kzvdar42.deliveryoperatorapp.util.SendLocation
import com.example.kzvdar42.deliveryoperatorapp.viewmodel.MapViewModel
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.constants.Style
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.*
import com.mapbox.mapboxsdk.utils.MapFragmentUtils
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import kotlinx.android.synthetic.main.fragment_map.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*


class MyMapFragment : Fragment(), OnMapReadyCallback {

    private val mapReadyCallbackList = ArrayList<OnMapReadyCallback>()
    private var mapViewReadyCallback: MapFragment.OnMapViewReadyCallback? = null
    private var mapboxMap: MapboxMap? = null
    private var map: MapView? = null

    // variables for calculating and drawing a route
    private var originPosition: Point? = null
    private var navigationMapRoute: NavigationMapRoute? = null
    private var currentRoute: DirectionsRoute? = null

    // View Model
    private val mViewModel by lazy {
        ViewModelProviders.of(this).get(MapViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        Timber.e("onAttach")
        super.onAttach(context)
        if (context is MapFragment.OnMapViewReadyCallback) {
            mapViewReadyCallback = context
        }
    }

    override fun onInflate(context: Context, attrs: AttributeSet, savedInstanceState: Bundle) {
        Timber.e("onInflate")
        super.onInflate(context, attrs, savedInstanceState)
        arguments = MapFragmentUtils.createFragmentArgs(MapboxMapOptions.createFromAttributes(context, attrs))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.e("onCreateView")
        Mapbox.getInstance(context!!, getString(R.string.access_token))
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)
        map = rootView.findViewById(R.id.map_view)
        map?.apply {
            onCreate(savedInstanceState)
            getMapAsync(this@MyMapFragment)
            setStyleUrl(Style.TRAFFIC_DAY)
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.e("onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        map?.apply {
            onCreate(savedInstanceState)
            getMapAsync(this@MyMapFragment)
        }
        mapViewReadyCallback?.onMapViewReady(map)
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        Timber.e("onMapReady")
        this.mapboxMap = mapboxMap
        for (onMapReadyCallback in mapReadyCallbackList) {
            onMapReadyCallback.onMapReady(mapboxMap)
        }
        // Check if the app has the location permission.
        if (ContextCompat.checkSelfPermission(activity!!,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            Toast.makeText(activity, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show()
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION)
        } else {
            inflateMap()
        }
    }

    private fun inflateMap() {
        Timber.e("inflateMap")
        // Start sending current location.
        Timber.i("Starting the SendLocation service.")
        activity?.startService(Intent(activity, SendLocation::class.java))
        // Get current position
        mViewModel.getCurrentLocation().observe(this, Observer { originLocation ->
            Timber.i("Got position ${originLocation.longitude} ${originLocation.latitude}")
            // Get order info
            mViewModel.getCurrentOrder().observe(this, Observer { order ->
                // Get the current location.
                originPosition = Point.fromLngLat(originLocation.longitude, originLocation.latitude)

                val latLngBounds = LatLngBounds.Builder().include(LatLng(originLocation))

                if (order != null) {
                    // Add bounds & markers
                    order.coords.forEach { coord ->
                        // Add bound.
                        latLngBounds.include(LatLng(coord.lat, coord.long))
                        // Add marker to the map.
                        mapboxMap?.addMarker(MarkerOptions()
                                .position(LatLng(order.coords[0].lat, order.coords[0].long))
                                .title("${getString(R.string.order_num, order.orderNum)} [${getString(Constants.getTitle(0, order.coords.size))}]")
                                .snippet(getString(R.string.order_to_description_snippet,
                                        order.receiverName, order.receiverSurname, order.receiverPhoneNumber,
                                        leftTime(Constants.leftTime(order.expectedTtd)))))
                    }
                    // Animate the camera to the points
                    mapboxMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), 100))

                    // Get the route.
                    val orderFromPosition = Point.fromLngLat(order.coords[0].long, order.coords[0].lat)
                    getRoute(originPosition!!, orderFromPosition!!)

                    // Set the On Click Listener for the `start navigation button`.
                    start_button.setOnClickListener {
//                        startActivity(Intent(activity, MyNavigationActivity::class.java))
                        val options = NavigationLauncherOptions.builder()
//                                .lightThemeResId(R.style.MyNavigationView)
                                .directionsRoute(currentRoute)
                                .shouldSimulateRoute(BuildConfig.DEBUG)
                                .build()
                            // Call this method with Context from within an Activity
                        MyNavigationLauncher.startNavigation(activity!!, options)
                    }
                } else {
                    // TODO: add screen "no order selected"
                    latLngBounds.include(LatLng(originLocation.latitude + 0.01, originLocation.longitude + 0.01))
                    latLngBounds.include(LatLng(originLocation.latitude - 0.01, originLocation.longitude - 0.01))
                    Toast.makeText(context, R.string.no_order_selected, Toast.LENGTH_LONG).show()

                    // Animate the camera to the points
                    mapboxMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), 200))
                }
            })
        })
    }

    private fun leftTime(time: Pair<Int, String>):String {
        return when (time.first) {
            0 -> getString(R.string.ttl_days, time.second)
            1 -> getString(R.string.ttl_hours, time.second)
            2 -> getString(R.string.ttl_minutes, time.second)
            else -> getString(R.string.ttl_ended)
        }
    }

    private fun getRoute(origin: Point, destination: Point) {
        Timber.e("getRoute")
        // Deactivate the button till the route is fetched.
        start_button?.isEnabled = false
        NavigationRoute.builder(activity)
                .accessToken(Mapbox.getAccessToken()!!)
                .origin(origin)
                .voiceUnits(DirectionsCriteria.METRIC)
                .destination(destination)
                .build()
                .getRoute(object : Callback<DirectionsResponse> {
                    override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                        // You can get the generic HTTP info about the response
                        Timber.d("GetRoute response code: %s", response.code())
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
                            start_button?.isEnabled = true
                            navigationMapRoute = NavigationMapRoute(null, map!!, mapboxMap!!, R.style.MyNavigationMapRoute)
                        }
                        navigationMapRoute?.addRoute(currentRoute)
                    }


                    override fun onFailure(call: Call<DirectionsResponse>, throwable: Throwable) {
                        Timber.e("Error: %s", throwable.message)
                    }
                })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Timber.e("onRequestPermissionsResult")
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Timber.i("Location permissions granted!")
                    inflateMap()
                } else {
                    Timber.e("Location permission are not granted closing the app.")
                    Toast.makeText(activity, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show()
                    // activity?.finish() //TODO: Handle it in a more elegant way.
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onStart() {
        Timber.e("onStart")
        super.onStart()
        map?.onStart()
    }

    override fun onResume() {
        Timber.e("onResume")
        super.onResume()
        map?.onResume()
    }

    override fun onPause() {
        Timber.e("onPause")
        super.onPause()
        map?.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Timber.e("onSaveInstanceState")
        super.onSaveInstanceState(outState)
        if (map?.isDestroyed == false) {
            map?.onSaveInstanceState(outState)
        }
    }

    override fun onStop() {
        Timber.e("onStop")
        super.onStop()
        map?.onStop()
    }

    override fun onLowMemory() {
        Timber.e("onLowMemory")
        super.onLowMemory()
        if (map?.isDestroyed == false) {
            map?.onLowMemory()
        }
    }

    override fun onDestroyView() {
        Timber.e("onDestroyView")
        super.onDestroyView()
        map?.onDestroy()
        mapReadyCallbackList.clear()
    }

    companion object {

        fun newInstance(): MyMapFragment {
            Timber.e("newInstance")
            return MyMapFragment()
        }

        private const val MY_PERMISSIONS_REQUEST_FINE_LOCATION = 42
    }
}
