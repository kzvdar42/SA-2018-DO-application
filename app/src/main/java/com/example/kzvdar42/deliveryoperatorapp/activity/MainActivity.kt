package com.example.kzvdar42.deliveryoperatorapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.kzvdar42.deliveryoperatorapp.R
import com.example.kzvdar42.deliveryoperatorapp.fragment.MapFragment
import com.example.kzvdar42.deliveryoperatorapp.fragment.OrdersFragment
import com.example.kzvdar42.deliveryoperatorapp.fragment.SettingsFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var currFragment = 0
    private var mapsFragment = MapFragment()
    private var ordersFragment = OrdersFragment()
    private var settingsFragment = SettingsFragment()


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
        fragmentManager.beginTransaction().add(R.id.fragmentContainer, getFragment(currFragment)).commit()

        bottom_navigation_menu.setOnNavigationItemSelectedListener { item ->
            var fragment: Fragment = getFragment(currFragment)
            when (item.itemId) {
                R.id.map_button -> {
                    fragment = MapFragment() //TODO: Manage to reuse old fragment
                    toolbar.title = resources.getString(R.string.map_label)
                    currFragment = 0
                }
                R.id.orders_button -> {
                    fragment = ordersFragment
                    currFragment = 1
                }
                R.id.settings_button -> {
                    fragment = settingsFragment
                    toolbar.title = resources.getString(R.string.settings_label)
                    currFragment = 2
                }
            }
            // Insert the fragment by replacing any existing fragment
            fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit()

            // Return
            true
        }
    }

    private fun getFragment(num: Int): Fragment {
        return when (num) {
            0 -> MapFragment()
            1 -> ordersFragment
            2 -> settingsFragment
            else -> MapFragment()
        }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentFragment", currFragment)
    }

    public override fun onRestoreInstanceState(inState: Bundle) {
        super.onRestoreInstanceState(inState)
        currFragment = inState.getInt("currentFragment", 0)
    }


    override fun onBackPressed() {
        // Left blank intentinally. TODO: Add handling of navigating between fragments.
    }
}

