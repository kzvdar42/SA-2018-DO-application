package com.example.kzvdar42.deliveryoperatorapp.activity

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.kzvdar42.deliveryoperatorapp.R
import com.example.kzvdar42.deliveryoperatorapp.fragment.MapFragment
import com.example.kzvdar42.deliveryoperatorapp.fragment.OrdersFragment
import com.example.kzvdar42.deliveryoperatorapp.fragment.SettingsFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var mapsFragment: MapFragment? = null
    private var ordersFragment: OrdersFragment? = null
    private var settingsFragment: SettingsFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Add toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = resources.getString(R.string.map_label)
        setSupportActionBar(toolbar)

        // Attach the first fragment
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().add(R.id.fragmentContainer, MapFragment()).commit()

        bottom_navigation_menu.setOnNavigationItemSelectedListener { item ->
            var fragment: Fragment = mapsFragment ?: MapFragment()
            when (item.itemId) {
                R.id.map_button -> {
                    // mapsFragment = mapsFragment ?: MapFragment()
                    fragment = MapFragment() //TODO: Manage to reuse old fragment
                    toolbar.title = resources.getString(R.string.map_label)
                }
                R.id.orders_button -> {
                    ordersFragment = ordersFragment ?: OrdersFragment()
                    fragment = ordersFragment!!
                    toolbar.title = resources.getString(R.string.orders_label)  //FIXME: I'm bad
                }
                R.id.settings_button -> {
                    settingsFragment = settingsFragment ?: SettingsFragment()   //FIXME: I'm bad
                    fragment = settingsFragment!!
                    toolbar.title = resources.getString(R.string.settings_label)
                }
            }
            // Insert the fragment by replacing any existing fragment
            fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit()

            // Return
            true
        }
    }
}

