package com.example.kzvdar42.deliveryoperatorapp.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.kzvdar42.deliveryoperatorapp.Adapter.BottomBarAdapter
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

        mapsFragment     = MapFragment()
        ordersFragment   = OrdersFragment()
        settingsFragment = SettingsFragment()

        setupViewPager(viewpager)
        viewpager.currentItem = 2


        bottom_navigation_menu.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.map_button -> {
                    Toast.makeText(application.baseContext, "Map", Toast.LENGTH_LONG).show()
                    viewpager.currentItem = 0
                }
                R.id.orders_button -> {
                    Toast.makeText(application.baseContext, "Orders", Toast.LENGTH_LONG).show()
                    viewpager.currentItem = 1
                }
                R.id.settings_button -> {
                    Toast.makeText(application.baseContext, "Settings", Toast.LENGTH_LONG).show()
                    viewpager.currentItem = 2
                }
            }
            true
        }
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = BottomBarAdapter(supportFragmentManager)
        adapter.addFragment(mapsFragment)
        adapter.addFragment(ordersFragment)
        adapter.addFragment(settingsFragment)
        viewPager.adapter = adapter
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

