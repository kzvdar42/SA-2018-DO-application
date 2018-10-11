package com.example.kzvdar42.deliveryoperatorapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.kzvdar42.deliveryoperatorapp.R
import com.example.kzvdar42.deliveryoperatorapp.fragment.MapFragment
import com.example.kzvdar42.deliveryoperatorapp.fragment.OrdersFragment
import com.example.kzvdar42.deliveryoperatorapp.fragment.SettingsFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var mapsFragment: MapFragment
    private lateinit var ordersFragment: OrdersFragment
    private lateinit var settingsFragment: SettingsFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Add toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = resources.getString(R.string.map_label)
        setSupportActionBar(toolbar)

        // Creating fragments
        mapsFragment = MapFragment()
        ordersFragment = OrdersFragment()
        settingsFragment = SettingsFragment()

        // Attach the first fragment
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().add(R.id.fragmentContainer, mapsFragment).commit()

        bottom_navigation_menu.setOnNavigationItemSelectedListener { item ->
            var fragment: Fragment = MapFragment() //TODO: Manage to reuse old fragment
            when (item.itemId) {
                R.id.map_button -> {
                    fragment = MapFragment() //TODO: Manage to reuse old fragment
                    toolbar.title = resources.getString(R.string.map_label)
                }
                R.id.orders_button -> {
                    fragment = ordersFragment
                    toolbar.title = resources.getString(R.string.orders_label)
                }
                R.id.settings_button -> {
                    fragment = settingsFragment
                    toolbar.title = resources.getString(R.string.settings_label)
                }
            }

            // Insert the fragment by replacing any existing fragment
            fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit()

            // Return
            true
        }
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.order_list_item -> {
                val itemPosition = ordersFragment.mRecyclerView.getChildAdapterPosition(view)
                val order = ordersFragment.mViewModel.getOrders()?.value!![itemPosition]
                val i = Intent(this, OrderInfoActivity::class.java)
                i.putExtra("orderNum", resources.getString(R.string.order_num) + "${order.OrderNum}")
                i.putExtra("Username", order.Username)
                i.putExtra("orderDescription", order.OrderDescription)
                i.putExtra("coords", doubleArrayOf(order.FromLat, order.FromLng, order.ToLat, order.ToLng))
                startActivity(i)
            }
        }
    }
}

