package com.example.kzvdar42.deliveryoperatorapp.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.kzvdar42.deliveryoperatorapp.Fragment.MapFragment
import com.example.kzvdar42.deliveryoperatorapp.Fragment.OrdersFragment
import com.example.kzvdar42.deliveryoperatorapp.Fragment.SettingsFragment
import com.example.kzvdar42.deliveryoperatorapp.R
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var mapsFragment: MapFragment
    private lateinit var ordersFragment: OrdersFragment
    private lateinit var settingsFragment: SettingsFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                    Toast.makeText(application.baseContext, "Map", Toast.LENGTH_LONG).show()
                    fragment = MapFragment() //TODO: Manage to reuse old fragment
                }
                R.id.orders_button -> {
                    Toast.makeText(application.baseContext, "Orders", Toast.LENGTH_LONG).show()
                    fragment = ordersFragment
                }
                R.id.settings_button -> {
                    Toast.makeText(application.baseContext, "Settings", Toast.LENGTH_LONG).show()
                    fragment = settingsFragment
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
            R.id.settings_contact_CO -> {
                Toast.makeText(application.baseContext, "Contact CO", Toast.LENGTH_LONG).show()
            }
            R.id.settings_log_out -> {
                Toast.makeText(application.baseContext, "Log out", Toast.LENGTH_LONG).show()
                val i = Intent(this, LoginActivity::class.java)

                // Get the user data
                val sharedPref = this.getSharedPreferences("user", Context.MODE_PRIVATE)
                sharedPref.edit().putBoolean("IsLogged", false).apply()
                startActivity(i)
            }
        }
    }
}

