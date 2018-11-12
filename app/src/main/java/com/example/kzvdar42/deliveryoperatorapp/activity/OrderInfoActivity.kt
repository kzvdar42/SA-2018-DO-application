package com.example.kzvdar42.deliveryoperatorapp.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.kzvdar42.deliveryoperatorapp.R
import com.example.kzvdar42.deliveryoperatorapp.db.CoordsEntity
import com.example.kzvdar42.deliveryoperatorapp.db.OrderEntity
import com.example.kzvdar42.deliveryoperatorapp.viewmodel.OrderInfoViewModel
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
    private val mViewModel
            by lazy { ViewModelProviders.of(this).get(OrderInfoViewModel::class.java) }

    // OrderEntity info
    private lateinit var order: OrderEntity
    private lateinit var coords: ArrayList<CoordsEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_info)

        // Get data about the order
        val intent = intent
        val orderNum = intent.getIntExtra("orderNum", 0)


        // Initializing the mapView
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)


        // Get order information
        mViewModel.getOrder(orderNum).observe(this, Observer<OrderEntity> { it ->
            //FIXME: find more elegant implementation.
            order = it
            coords = order.coords

            // Add toolbar
            val toolbar = findViewById<Toolbar>(R.id.toolbar)
            toolbar.title = getString(R.string.order_num, order.orderNum)
            setSupportActionBar(toolbar)

            // Set back button on toolbar
            toolbar.setNavigationOnClickListener { finish() }
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            // Add info
            customer_name_text.text = "${order.receiverName} ${order.receiverSurname}"
            time_left_text.text = getString(R.string.time_left_label, order.expectedTtd)
            dimensions_text.text = getString(R.string.dimensions_text, order.length.toLong(), order.width.toLong(), order.height.toLong())
            weight_text.text = getString(R.string.weight_label, order.weight.toLong())
            if (order.senderNotes != null) sender_notes_label.text = getString(R.string.sender_notes_label, order.senderNotes)

            // Change the layout due to order status.
            when {
                order.orderStatus == "Approved" -> order_actions_bar.visibility = View.GONE
                order.orderStatus == "Accepted" -> select_order_button.visibility = View.GONE
                order.orderStatus == "Delivered" -> {
                    order_actions_bar.visibility = View.GONE
                    select_order_button.visibility = View.GONE
                }
                else -> select_order_button.visibility = View.GONE
            }


            // If the driver is near the end of route add the `sign for parcel` button
            if (order.lastTransitPoint < order.coords.size-2) assignment_button.visibility = View.GONE

            // Initiate map
            Mapbox.getInstance(this, getString(R.string.access_token))
            mapView.getMapAsync(this)
        })
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
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
        val latLngBounds = LatLngBounds.Builder()
        // Add bounds & markers.
        order.coords.forEachIndexed { index, coord ->
            // Add bound.
            latLngBounds.include(LatLng(coord.lat, coord.long))

            // Add marker to the map.
            mapboxMap.addMarker(MarkerOptions()
                    .position(LatLng(coord.lat, coord.long))
                    .title("${getString(R.string.order_num, order.orderNum)} [${title(index, order.coords.size)}]")
                    .snippet(getString(R.string.order_to_description_snippet,
                            order.receiverName, order.receiverSurname, order.receiverPhoneNumber, order.senderNotes, order.expectedTtd))) // TODO: Handle null values
        }

        if (order.coords.size > 1) {
            // Get the route.
            getRoute(Point.fromLngLat(order.coords[0].long, order.coords[0].lat),
                    Point.fromLngLat(coords[1].long, coords[1].lat))
            // Animate the camera to the points.
            mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), 200))
        } else {
            mViewModel.getCurrentPosition().observe(this, Observer { lastLocation ->
                latLngBounds.include(LatLng(lastLocation))
                getRoute(Point.fromLngLat(lastLocation.longitude, lastLocation.latitude),
                        Point.fromLngLat(coords[0].long, coords[0].lat))
                // Animate the camera to the points.
                mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), 200))
            })
        }

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
            R.id.navigation_button -> {
                val sharedPref = getSharedPreferences("currentOrder", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putInt("orderNum", order.orderNum)
                    apply()
                }
                finish()
            }
            R.id.call_button -> { // TODO: Implement taking the number from the server
                intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:0123456789")
                startActivity(intent)
            }
            R.id.assignment_button -> {
                val intent = Intent(this, ReceiverInfoActivity::class.java)
                intent.putExtra("orderNum", order.orderNum)
                startActivity(intent)
            }
            R.id.select_order_button -> {
                mViewModel.updateOrder(order.orderNum, "Accepted", order.lastTransitPoint, null)
                finish()
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
