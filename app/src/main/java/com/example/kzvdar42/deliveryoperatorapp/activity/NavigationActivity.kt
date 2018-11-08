package com.example.kzvdar42.deliveryoperatorapp.activity

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.kzvdar42.deliveryoperatorapp.R
import com.example.kzvdar42.deliveryoperatorapp.db.CoordsEntity
import com.example.kzvdar42.deliveryoperatorapp.viewmodel.MapViewModel
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
import kotlinx.android.synthetic.main.alert_dialog.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class NavigationActivity : AppCompatActivity(), OnNavigationReadyCallback, NavigationListener, RouteListener, ProgressChangeListener {

    private var navigationView: NavigationView? = null
    private var dropoffDialogShown: Boolean = false
    private var lastKnownLocation: Location? = null

    private lateinit var points: ArrayList<CoordsEntity>

    //ViewModel
    private val mViewModel
            by lazy {ViewModelProviders.of(this).get(MapViewModel::class.java)}

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        // Get order info //TODO: Посмотри на меня
        mViewModel.getCurrentOrder().observe(this, Observer { order ->
            points = order.coords

            // Add current position at the start
            val currentPosition = mViewModel.getCurrentPosition()
            points.add(0, CoordsEntity(long=currentPosition!!.longitude, lat = currentPosition.latitude))

            navigationView = findViewById(R.id.navigationView)
            navigationView?.onCreate(savedInstanceState)
            navigationView?.initialize(this)
        })
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

    @SuppressLint("InflateParams")
    private fun showDropoffDialog() {
        // Creating alert dialog.
        val alertDialog = AlertDialog.Builder(this).create()
        // Inflating the view for the alert dialog.
        val dialogView = layoutInflater.inflate(R.layout.alert_dialog, null)
        dialogView.dialog_title.text = getString(R.string.order_title_transit)
        dialogView.dialog_description.text = getString(R.string.map_dialog_text)
        dialogView.dialog_positive_btn.text = getString(R.string.map_dialog_positive_text)
        dialogView.dialog_positive_btn.setOnClickListener {
            alertDialog.dismiss()
            val nextPoint = points.removeAt(0)
            fetchRoute(getLastKnownLocation(), Point.fromLngLat(nextPoint.long, nextPoint.lat))
        }
        dialogView.dialog_negative_btn.text = getString(R.string.map_dialog_negative_text)
        dialogView.dialog_negative_btn.setOnClickListener {
            //mViewModel.saveOrderStatus(points) // TODO: Update order status
            alertDialog.dismiss()
        }
        // Adding view to the alert dialog and show it.
        alertDialog.setView(dialogView)
        alertDialog.setCancelable(true)
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
                    override fun onFailure(call: Call<DirectionsResponse>, throwable: Throwable) {
                        Timber.e("Error: %s", throwable.message)
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

    public override fun onStart() {
        super.onStart()
        navigationView?.onStart()
    }

    public override fun onResume() {
        super.onResume()
        navigationView?.onResume()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        navigationView?.onLowMemory()
    }

    override fun onBackPressed() {
        // If the navigation view didn't need to do anything, call super
        if (!navigationView!!.onBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        navigationView?.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        navigationView?.onRestoreInstanceState(savedInstanceState)
    }

    public override fun onPause() {
        super.onPause()
        navigationView?.onPause()
    }

    public override fun onStop() {
        super.onStop()
        navigationView?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        navigationView?.onDestroy()
    }

    override fun onNavigationReady(isRunning: Boolean) {
        val currentPoint = points.removeAt(0)
        val nextPoint = points.removeAt(0)
        fetchRoute(Point.fromLngLat(currentPoint.long, currentPoint.lat),
                Point.fromLngLat(nextPoint.long, nextPoint.lat))
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

}
