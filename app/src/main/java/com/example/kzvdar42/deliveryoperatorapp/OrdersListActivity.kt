package com.example.kzvdar42.deliveryoperatorapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*


class ListOrdersActivity : AppCompatActivity() {

    private var mAdapter: OrdersListAdapter? = null
    private var mRecyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_orders)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.orders_list)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { _ -> finish() }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mRecyclerView = findViewById(R.id.order_list_recycler_view)
        mRecyclerView?.setHasFixedSize(true)

        val mLayoutManager = LinearLayoutManager(this)
        mRecyclerView?.layoutManager = mLayoutManager

        mAdapter = OrdersListAdapter()
        mRecyclerView?.adapter = mAdapter
        mAdapter?.setProductList(foo())
    }

    private fun foo(): List<Order> {
        val result = LinkedList<Order>()
        result.add(Order(0, "First order", "First order description", doubleArrayOf(55.7476907, 48.7433593)))
        result.add(Order(1, "Second order", "Second order description.\n Impossible route", doubleArrayOf(38.9098, -77.0295)))
        result.add(Order(2, "Third order", "Third order description", doubleArrayOf(55.7867635, 49.1216088)))
        result.add(Order(3, "Forth order", "Forth order description", doubleArrayOf(49.125392, 55.784798)))
        result.add(Order(4, "Fifth order", "Fifth order description", doubleArrayOf(49.107202, 55.794498)))
        return result
    }

    fun onClick(v: View) {
        val id = v.id

        if (id == R.id.order_list_item) {
            val itemPosition = mRecyclerView!!.getChildAdapterPosition(v)
            val order = foo()[itemPosition]
            val i = Intent(this, MapActivity::class.java)
            i.putExtra("orderName", order.name)
            i.putExtra("orderDescription", order.description)
            i.putExtra("coords", order.coords)
            startActivity(i)
        }
    }

}
