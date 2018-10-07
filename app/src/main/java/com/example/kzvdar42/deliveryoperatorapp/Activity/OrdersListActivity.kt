package com.example.kzvdar42.deliveryoperatorapp.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kzvdar42.deliveryoperatorapp.Adapter.OrdersListAdapter
import com.example.kzvdar42.deliveryoperatorapp.DB.OrderEntity
import com.example.kzvdar42.deliveryoperatorapp.R
import java.util.*
import androidx.lifecycle.ViewModelProviders
import com.example.kzvdar42.deliveryoperatorapp.ViewModel.OrderListViewModel


class ListOrdersActivity : AppCompatActivity() {

    private lateinit var mAdapter: OrdersListAdapter
    private lateinit var mRecyclerView: RecyclerView

    private lateinit var mViewModel: OrderListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_orders)

        // Add toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.orders_list)
        setSupportActionBar(toolbar)

        // Connecting to the ViewModel
        mViewModel = ViewModelProviders.of(this).get(OrderListViewModel::class.java)

        // Connecting to the Recycler View
        mRecyclerView = findViewById(R.id.order_list_recycler_view)
        mRecyclerView.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(this)
        mRecyclerView.layoutManager = mLayoutManager
        mAdapter = OrdersListAdapter(LinkedList(), this)

        // Updating the Recycling view
        mViewModel.getOrders()?.observe(this, Observer<List<OrderEntity>> { t -> mAdapter.updateOrderList(t!!) })

        mRecyclerView.adapter = mAdapter
    }


    fun onClick(v: View) {
        val id = v.id

        if (id == R.id.order_list_item) {
            val itemPosition = mRecyclerView.getChildAdapterPosition(v)
            val order = mViewModel.getOrders()?.value!![itemPosition]
            val i = Intent(this, OrderInfoActivity::class.java)
            i.putExtra("orderNum", resources.getString(R.string.order_num) + "${order.OrderNum}")
            i.putExtra("Username", order.Username)
            i.putExtra("orderDescription", order.OrderDescription)
            i.putExtra("coords", doubleArrayOf(order.FromLat, order.FromLng, order.ToLat, order.ToLng))
            startActivity(i)
        }
    }

}
