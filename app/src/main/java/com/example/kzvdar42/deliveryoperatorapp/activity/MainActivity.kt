package com.example.kzvdar42.deliveryoperatorapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.kzvdar42.deliveryoperatorapp.BuildConfig
import com.example.kzvdar42.deliveryoperatorapp.R
import com.example.kzvdar42.deliveryoperatorapp.fragment.MyMapFragment
import com.example.kzvdar42.deliveryoperatorapp.fragment.OrdersFragment
import com.example.kzvdar42.deliveryoperatorapp.fragment.SettingsFragment
import com.mapbox.mapboxsdk.Mapbox
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private var currFragment = 0
    private var mapsFragment = MyMapFragment.newInstance()
    private var ordersFragment = OrdersFragment()
    private var settingsFragment = SettingsFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Mapbox.getInstance(this, getString(R.string.access_token))

        // Initialize the Timber if it is a debug build.
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Add toolbar.
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = resources.getString(R.string.map_label)
        setSupportActionBar(toolbar)

        // Attach the current fragment to the view.
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().add(R.id.fragmentContainer, getFragment(currFragment)).commit()
        bottom_navigation_menu.selectedItemId = currFragment

        bottom_navigation_menu.setOnNavigationItemSelectedListener { item ->
            var nextFragment = 0
            when (item.itemId) {
                R.id.map_button -> {
                    nextFragment = 0
                    toolbar.title = resources.getString(R.string.map_label)
                }
                R.id.orders_button -> {
                    nextFragment = 1
                    // Title is managed by the fragment.
                }
                R.id.settings_button -> {
                    nextFragment = 2
                    toolbar.title = resources.getString(R.string.settings_label)

                }
            }
            if (currFragment == nextFragment) {
                createNewFragment(currFragment)
            }
            currFragment = nextFragment
            val fragment = getFragment(currFragment)
            // Insert the fragment by replacing any existing fragment
            fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit()

            // Return
            true
        }
    }

    private fun getFragment(num: Int): Fragment {
        return when (num) {
            0 -> MyMapFragment() // TODO: Manage to use the old instance.
            1 -> ordersFragment
            2 -> settingsFragment
            else -> {
                Timber.e("Unexpected fragment id in `getFragment`, returned mapsFragment.")
                MyMapFragment()
            }
        }
    }

    private fun createNewFragment(num: Int) {
        return when (num) {
            0 -> mapsFragment = MyMapFragment()
            1 -> ordersFragment = OrdersFragment()
            2 -> settingsFragment = SettingsFragment()
            else -> Timber.e("Unexpected fragment id in `createNewFragment`.")
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

