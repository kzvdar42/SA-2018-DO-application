package com.example.kzvdar42.deliveryoperatorapp.activity

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.kzvdar42.deliveryoperatorapp.R
import com.example.kzvdar42.deliveryoperatorapp.db.OrderEntity
import com.example.kzvdar42.deliveryoperatorapp.viewmodel.OrderInfoViewModel
import com.google.gson.Gson
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
import kotlinx.android.synthetic.main.activity_order_info.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class OrderInfoActivity : AppCompatActivity(), OnMapReadyCallback {

    // Map info
    private lateinit var mapView: MapView
    private lateinit var mapboxMap: MapboxMap

    //View Model
    private lateinit var mViewModel: OrderInfoViewModel

    // OrderEntity info
    private lateinit var title: String
    private lateinit var order: OrderEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_info)

        // Get data about the order
        val intent = intent
        val orderNum = intent.getIntExtra("orderNum", 0)

        // Get View Model
        mViewModel = ViewModelProviders.of(this).get(OrderInfoViewModel::class.java)

        // Initializing the mapView
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)

        // Get order information
        mViewModel.getOrder(orderNum).observe(this, Observer<OrderEntity> { it -> //FIXME: find more elegant implementation.
            order = it
            title = getString(R.string.order_num) + order.orderNum

            // Add toolbar
            val toolbar = findViewById<Toolbar>(R.id.toolbar)
            toolbar.title = title
            setSupportActionBar(toolbar)

            // Set back button on toolbar
            toolbar.setNavigationOnClickListener { finish() }
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            // Add info
            customer_name_text.text = intent.getStringExtra("username")
            order_info_text.text = order.orderDescription

            // Initiate map
            Mapbox.getInstance(this, getString(R.string.access_token))
            mapView.getMapAsync(this)
        })
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap

        val orderFrom = LatLng(order.fromLat, order.fromLng)
        val orderTo = LatLng(order.toLat, order.toLng)
        val orderFromPosition = Point.fromLngLat(order.fromLng, order.fromLat)
        val orderToPosition = Point.fromLngLat(order.toLng, order.toLat)

        val latLngBounds = LatLngBounds.Builder()
                .include(orderFrom)
                .include(orderTo)
                .build()
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 200))

        getRoute(orderFromPosition!!, orderToPosition!!)

        mapboxMap.addMarker(MarkerOptions()
                .position(orderFrom)
                .title("$ [From]")
                .snippet(order.orderDescription))
        mapboxMap.addMarker(MarkerOptions()
                .position(orderTo)
                .title("$title [To]")
                .snippet(order.orderDescription))
    }

    private fun getRoute(origin: Point, destination: Point) {
        NavigationRoute.builder(this)
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
                            Toast.makeText(application.baseContext, "No routes found", Toast.LENGTH_LONG).show()
                            Timber.e("No routes found")
                            return
                        }

                        val navigationMapRoute = NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute)

                        navigationMapRoute.addRoute(response.body()!!.routes()[0])
                    }

                    override fun onFailure(call: Call<DirectionsResponse>, throwable: Throwable) {
                        Timber.e("Error: %s", throwable.message)
                    }
                })
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.select_order -> {
                val sharedPref = getSharedPreferences("currentOrder", Context.MODE_PRIVATE)
                        ?: return
                val gsonCoords = Gson().toJson(arrayOf(order.fromLat, order.fromLng, order.toLat, order.toLng))
                with(sharedPref.edit()) {
                    putString("orderName", title)
                    putString("orderDescription", order.orderDescription)
                    putString("coords", gsonCoords)
                    apply()
                    onBackPressed()
                }
            }
        }
    }


    override fun onStart() {
        super.onStart()
        mapView.onStart()
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
