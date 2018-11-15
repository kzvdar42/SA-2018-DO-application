package com.example.kzvdar42.deliveryoperatorapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.kzvdar42.deliveryoperatorapp.BuildConfig
import com.example.kzvdar42.deliveryoperatorapp.R
import com.example.kzvdar42.deliveryoperatorapp.db.CoordsEntity
import com.example.kzvdar42.deliveryoperatorapp.db.OrderEntity
import com.example.kzvdar42.deliveryoperatorapp.viewmodel.MapViewModel
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
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


class MyNavigationActivity : AppCompatActivity(), OnNavigationReadyCallback, NavigationListener, RouteListener, ProgressChangeListener {

    companion object {
        private const val INITIAL_ZOOM = 20.0
    }

    private var navigationView: NavigationView? = null
    private var dropoffDialogShown: Boolean = false
    private var lastKnownLocation: Location? = null

    private lateinit var order: OrderEntity
    private lateinit var points: ArrayList<CoordsEntity>
    private var currPoint = -1

    //ViewModel
    private val mViewModel
            by lazy { ViewModelProviders.of(this).get(MapViewModel::class.java) }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {

        setTheme(com.mapbox.services.android.navigation.ui.v5.R.style.Theme_AppCompat_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_activity_navigation)
        // Get order info
        // Initiate navigation view.
        navigationView = findViewById(R.id.navigationView)
        navigationView?.onCreate(savedInstanceState)
        navigationView?.initialize(this)
        mViewModel.getCurrentOrder().observe(this, Observer { order ->
            points = order.coords
            this.order = order
            currPoint = 0

            val initialPosition = CameraPosition.Builder().zoom(INITIAL_ZOOM)

            // Add latest position at the start.
            mViewModel.getLatestLocation().also {
                if (it != null) {
                    points.add(0, CoordsEntity(long = it.longitude, lat = it.latitude))
                    initialPosition.target(LatLng(it.latitude, it.longitude))
                }
            }
        })
    }

    private fun updateOrder() {
        if (currPoint > 0 && currPoint != order.coords.size) {
            mViewModel.saveOrderStatus(order.orderNum, "In Transit", null)
        }
    }

    override fun onArrival() {
        if (!dropoffDialogShown && !points.isEmpty()) {
            showDropoffDialog()
            dropoffDialogShown = true // Accounts for multiple arrival events
        } else if (!dropoffDialogShown && points.isEmpty()) {
            showEndDialog()
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
        dialogView.dialog_description.text = getString(R.string.map_dialog_transit_text)
        dialogView.dialog_positive_btn.text = getString(R.string.map_dialog_positive_text)
        dialogView.dialog_positive_btn.setOnClickListener {
            alertDialog.dismiss()
            currPoint++
            val nextPoint = points.removeAt(0)
            fetchRoute(getLastKnownLocation(), Point.fromLngLat(nextPoint.long, nextPoint.lat))
        }
        dialogView.dialog_negative_btn.text = getString(R.string.map_dialog_negative_text)
        dialogView.dialog_negative_btn.setOnClickListener {
            updateOrder()
            alertDialog.dismiss()
        }
        // Adding view to the alert dialog and show it.
        alertDialog.window?.decorView?.setBackgroundResource(android.R.color.transparent)
        alertDialog.setView(dialogView)
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    @SuppressLint("InflateParams")
    private fun showEndDialog() {
        // Creating alert dialog.
        val alertDialog = AlertDialog.Builder(this).create()
        // Inflating the view for the alert dialog.
        val dialogView = layoutInflater.inflate(R.layout.alert_dialog, null)
        dialogView.dialog_title.text = getString(R.string.order_title_end)
        dialogView.dialog_description.text = getString(R.string.map_dialog_end_text)
        dialogView.dialog_positive_btn.text = getString(R.string.yes)
        dialogView.dialog_positive_btn.setOnClickListener {
            val intent = Intent(this, OrderInfoActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION
            startActivity(intent)
        }
        dialogView.dialog_negative_btn.text = getString(R.string.no)
        dialogView.dialog_negative_btn.setOnClickListener {
            alertDialog.dismiss()
            finish()
        }
        // Adding view to the alert dialog and show it.
        alertDialog.window?.decorView?.setBackgroundResource(android.R.color.transparent)
        alertDialog.setView(dialogView)
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    private fun fetchRoute(origin: Point, destination: Point) {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken()!!)
                .origin(origin)
                .destination(destination)
                .voiceUnits(DirectionsCriteria.METRIC)
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

        return NavigationViewOptions.builder()
                .directionsRoute(directionsRoute)
                .navigationListener(this)
                .progressChangeListener(this)
                .routeListener(this)
                .shouldSimulateRoute(BuildConfig.DEBUG)
                .build()
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
        updateOrder()
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
        updateOrder()
        super.onPause()
        navigationView?.onPause()
    }

    public override fun onStop() {
        updateOrder()
        super.onStop()
        navigationView?.onStop()
    }

    override fun onDestroy() {
        updateOrder()
        super.onDestroy()
        // End the navigation session
        navigationView?.stopNavigation()
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
        Toast.makeText(this, getString(R.string.on_failed_reroute), Toast.LENGTH_LONG).show()
    }

}
