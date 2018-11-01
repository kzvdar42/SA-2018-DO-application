package com.example.kzvdar42.deliveryoperatorapp.fragment

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.kzvdar42.deliveryoperatorapp.R
import com.example.kzvdar42.deliveryoperatorapp.activity.NavigationActivity
import com.example.kzvdar42.deliveryoperatorapp.viewmodel.MapViewModel
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import kotlinx.android.synthetic.main.fragment_map.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class MapFragment : Fragment(), OnMapReadyCallback, PermissionsListener {

    private var mapView: MapView? = null

    // variables for adding location layer
    private lateinit var mapboxMap: MapboxMap
    private var originLocation: Location? = null

    // variables for adding a marker
    private var originCoord: LatLng? = null

    // variables for calculating and drawing a route
    private var originPosition: Point? = null
    private var navigationMapRoute: NavigationMapRoute? = null

    // View Model
    private val mViewModel
            by lazy { ViewModelProviders.of(this).get(MapViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)

        // Sets retain Instance to not recreate the fragment during rotation.
        // retainInstance = true TODO: Look at this.

        // Initiate map.
        Mapbox.getInstance(context!!, getString(R.string.access_token))
        mapView = rootView.findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)
        return rootView
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        // Initializing the map and location plugin.
        this.mapboxMap = mapboxMap


        if (PermissionsManager.areLocationPermissionsGranted(activity)) {
            // Get current position
            originLocation = mViewModel.getCurrentPosition()
            // Get order info
            mViewModel.getCurrentOrder().observe(this, Observer { order ->
                // Get the current location.
                originPosition = Point.fromLngLat(originLocation!!.longitude, originLocation!!.latitude)
                originCoord = LatLng(originLocation!!.latitude, originLocation!!.longitude)

                val latLngBounds = LatLngBounds.Builder().include(originCoord!!)

                if (order?.coords?.size ?: 0 > 0) {

                    // Lambda for adding right title for the marker.
                    val title = { ind: Int, maxindex: Int ->
                        // FIXME: move somewhere else, or change
                        getString(
                                if (maxindex < 2) R.string.order_title_end
                                else when (ind) {
                                    0 -> R.string.order_title_from
                                    maxindex - 1 -> R.string.order_title_end
                                    else -> R.string.order_title_transit
                                })
                    }

                    // Add bounds & markers
                    order.coords.forEachIndexed { index, coord ->
                        // Add bound.
                        latLngBounds.include(LatLng(coord.latitude, coord.longitude))
                        // Add marker to the map.
                        mapboxMap.addMarker(MarkerOptions()
                                .position(LatLng(order.coords[0].latitude, order.coords[0].longitude))
                                .title("${getString(R.string.order_num, order.orderNum)} [${title(0, order.coords.size)}]")
                                .snippet(getString(R.string.order_from_description_snippet,
                                        order.senderName, order.senderSurname, order.senderPhoneNumber, order.senderNotes, order.expectedTtd)))
                    }
                    // Animate the camera to the points
                    mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), 100))

                    // Get the route.
                    val orderFromPosition = Point.fromLngLat(order.coords[0].longitude, order.coords[0].latitude)
                    getRoute(originPosition!!, orderFromPosition!!)

                    // Set the On Click Listener for the `start navigation button`.
                    startButton.setOnClickListener {
                        startActivity(Intent(activity, NavigationActivity::class.java))
                    }

                } else {
                    // TODO: add screen "no order selected"
                    latLngBounds.include(originCoord!!)
                    mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), 2000))
                }
            })
        } else {
            PermissionsManager(this).requestLocationPermissions(activity) //TODO: reload map after getting permissions
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

                        // Draw the route on the map
                        if (navigationMapRoute != null) {
//                            navigationMapRoute?.removeRoute()
                        } else {
                            startButton?.isEnabled = true
                            navigationMapRoute = NavigationMapRoute(null, mapView!!, mapboxMap, R.style.NavigationMapRoute)
                        }
                        navigationMapRoute?.addRoute(response.body()!!.routes()[0])
                    }


                    override fun onFailure(call: Call<DirectionsResponse>, throwable: Throwable) {
                        Timber.e("Error: %s", throwable.message)
                    }
                })
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Toast.makeText(context, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            // TODO: refresh the view
        } else {
            Toast.makeText(context, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show()
            activity?.finish()
        }
    }


    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }
}