package com.example.kzvdar42.deliveryoperatorapp.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.divyanshu.draw.activity.DrawingActivity
import com.example.kzvdar42.deliveryoperatorapp.R
import com.example.kzvdar42.deliveryoperatorapp.db.CoordsEntity
import com.example.kzvdar42.deliveryoperatorapp.db.OrderEntity
import com.example.kzvdar42.deliveryoperatorapp.util.Constants
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
import kotlinx.android.synthetic.main.alert_dialog.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*

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

    @SuppressLint("SetTextI18n")
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
            time_left_text.text = getString(R.string.time_left_label,
                    leftTime(Constants.leftTime(order.expectedTtd)))
            dimensions_text.text = getString(R.string.dimensions_text, order.length.toLong(), order.width.toLong(), order.height.toLong())
            weight_text.text = getString(R.string.weight_label, order.weight.toLong())
            if (order.senderNotes != null) sender_notes_label.text = getString(R.string.sender_notes_label, order.senderNotes)

            // Change the layout due to order status.
            when {
                order.orderStatus == "Created" -> order_actions_bar.visibility = View.GONE
                order.orderStatus == "Accepted" -> select_order_button.visibility = View.GONE
                order.orderStatus == "In Transit" -> select_order_button.visibility = View.GONE
                order.orderStatus == "Delivered" -> {
                    order_actions_bar.visibility = View.GONE
                    select_order_button.visibility = View.GONE
                }
                else -> select_order_button.visibility = View.GONE
            }

            // Initiate map
            Mapbox.getInstance(this, getString(R.string.access_token))
            mapView.getMapAsync(this)
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

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        val latLngBounds = LatLngBounds.Builder()
        // Add bounds & markers.
        order.coords.forEachIndexed { index, coord ->
            // Add bound.
            latLngBounds.include(LatLng(coord.lat, coord.long))

            val snippet = if (index == 0) {
                getString(R.string.order_from_description_snippet,
                        order.senderName, order.senderSurname, order.senderPhoneNumber, order.expectedTtd)
            } else {
                getString(R.string.order_to_description_snippet,
                        order.receiverName, order.receiverSurname, order.receiverPhoneNumber, order.expectedTtd)
            }
            // Add marker to the map.
            mapboxMap.addMarker(MarkerOptions()
                    .position(LatLng(coord.lat, coord.long))
                    .title("${getString(R.string.order_num, order.orderNum)} " +
                            "[${getString(Constants.getTitle(index, order.coords.size))}]")
                    .snippet(snippet))
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
            R.id.call_button -> {
                intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:${order.receiverPhoneNumber}")
                startActivity(intent)
            }
            R.id.assignment_button -> {
                // Creating alert dialog.
                val alertDialog = AlertDialog.Builder(this).create()
                // Inflating the view for the alert dialog.
                val dialogView = layoutInflater.inflate(R.layout.alert_dialog, null)
                dialogView.dialog_title.text = getString(R.string.dialog_title)
                dialogView.dialog_description.visibility = View.GONE
                dialogView.dialog_positive_btn.setOnClickListener {
                    alertDialog.dismiss()
                    val intent = Intent(this, DrawingActivity::class.java)
                    startActivityForResult(intent, REQUEST_CODE_DRAW)
                }
                dialogView.dialog_negative_btn.setOnClickListener {
                    alertDialog.dismiss()
                }
                // Adding view to the alert dialog and show it.
                alertDialog.window?.decorView?.setBackgroundResource(android.R.color.transparent)
                alertDialog.setView(dialogView)
                alertDialog.setCancelable(true)
                alertDialog.show()
            }
            R.id.select_order_button -> {
                mViewModel.updateOrder(order.orderNum, "Accepted", null)
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

    // Get bitmap in onActivityResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null && resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_DRAW -> {
                    val result = data.getByteArrayExtra("bitmap")
                    val bitmap = BitmapFactory.decodeByteArray(result, 0, result.size)
                    saveImage(bitmap)
                    finish()
                }
            }
        }
    }

    private fun saveImage(signature: Bitmap) {
        mViewModel.updateOrder(order.orderNum, "Delivered", signature)
    }

    companion object {
        // TODO: Check how to create code in a right way.
        private const val REQUEST_CODE_DRAW = 42
    }
}
