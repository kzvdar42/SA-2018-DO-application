package com.example.kzvdar42.deliveryoperatorapp.activity

import android.content.DialogInterface
import android.location.Location
import android.os.Bundle
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.kzvdar42.deliveryoperatorapp.R
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.services.android.navigation.ui.v5.NavigationView
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener
import com.mapbox.services.android.navigation.ui.v5.listeners.RouteListener
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TestMapActivity : AppCompatActivity(), OnNavigationReadyCallback, NavigationListener, RouteListener, ProgressChangeListener {

    private var navigationView: NavigationView? = null
    private var dropoffDialogShown: Boolean = false
    private var lastKnownLocation: Location? = null

    private val points = ArrayList<Point>()

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_AppCompat_NoActionBar)
        super.onCreate(savedInstanceState)

        val coords = intent.getDoubleArrayExtra("coords")
        var i = 0
        while (i <= coords.size) {
            points.add(Point.fromLngLat(coords[i], coords[i + 1]))
            i += 2
        }
        setContentView(R.layout.activity_navigation)
        navigationView = findViewById(R.id.navigationView)
        navigationView!!.onCreate(savedInstanceState)
        navigationView!!.initialize(this)
    }

    public override fun onStart() {
        super.onStart()
        navigationView!!.onStart()
    }

    public override fun onResume() {
        super.onResume()
        navigationView!!.onResume()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        navigationView!!.onLowMemory()
    }

    override fun onBackPressed() {
        // If the navigation view didn't need to do anything, call super
        if (!navigationView!!.onBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        navigationView!!.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        navigationView!!.onRestoreInstanceState(savedInstanceState)
    }

    public override fun onPause() {
        super.onPause()
        navigationView!!.onPause()
    }

    public override fun onStop() {
        super.onStop()
        navigationView!!.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        navigationView!!.onDestroy()
    }

    override fun onNavigationReady(isRunning: Boolean) {
        fetchRoute(points.removeAt(0), points.removeAt(0))
    }

    override fun onCancelNavigation() {
        // Navigation canceled, finish the activity
        finish()
    }

    override fun onNavigationFinished() {
        // Intentionally empty
    }

    override fun onNavigationRunning() {
        // Intentionally empty
    }

    override fun allowRerouteFrom(offRoutePoint: Point): Boolean {
        return true
    }

    override fun onOffRoute(offRoutePoint: Point) {

    }

    override fun onRerouteAlong(directionsRoute: DirectionsRoute) {

    }

    override fun onFailedReroute(errorMessage: String) {

    }

    override fun onArrival() {
        if (!dropoffDialogShown && !points.isEmpty()) {
            showDropoffDialog()
            dropoffDialogShown = true // Accounts for multiple arrival events
        }
    }

    override fun onProgressChange(location: Location, routeProgress: RouteProgress) {
        lastKnownLocation = location
    }

    private fun startNavigation(directionsRoute: DirectionsRoute) {
        val navigationViewOptions = setupOptions(directionsRoute)
        navigationView!!.startNavigation(navigationViewOptions)
    }

    private fun showDropoffDialog() {
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setMessage(getString(R.string.dropoff_dialog_text))
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.dropoff_dialog_positive_text)
        ) { _, _ -> fetchRoute(getLastKnownLocation(), points.removeAt(0)) }
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.dropoff_dialog_negative_text)
        ) { _, _ ->
            // Do nothing
        }

        alertDialog.show()
    }

    private fun fetchRoute(origin: Point, destination: Point) {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken()!!)
                .origin(origin)
                .destination(destination)
                .alternatives(true)
                .build()
                .getRoute(object : Callback<DirectionsResponse> {
                    override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                        startNavigation(response.body()!!.routes()[0])
                    }
                })
    }

    private fun setupOptions(directionsRoute: DirectionsRoute): NavigationViewOptions {
        dropoffDialogShown = false

        val options = NavigationViewOptions.builder()
        options.directionsRoute(directionsRoute)
                .navigationListener(this)
                .progressChangeListener(this)
                .routeListener(this)
                .shouldSimulateRoute(true)
        return options.build()
    }

    private fun getLastKnownLocation(): Point {
        return Point.fromLngLat(lastKnownLocation!!.longitude, lastKnownLocation!!.latitude)
    }
}
