package com.example.kzvdar42.deliveryoperatorapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kzvdar42.deliveryoperatorapp.R
import com.example.kzvdar42.deliveryoperatorapp.adapter.OrdersListAdapter
import com.example.kzvdar42.deliveryoperatorapp.db.OrderEntity
import com.example.kzvdar42.deliveryoperatorapp.viewmodel.OrderListViewModel
import java.util.*


class OrdersFragment : Fragment() {


    lateinit var mRecyclerView: RecyclerView
    lateinit var mAdapter: OrdersListAdapter
    lateinit var mLayoutManager: LinearLayoutManager
    lateinit var mViewModel: OrderListViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        val rootView = inflater.inflate(R.layout.fragment_orders_list, container, false)

        // Setting up the recycler view
        mRecyclerView = rootView.findViewById(R.id.order_list_recycler_view)

        // Use a linear layout manager
        mLayoutManager = LinearLayoutManager(activity)
        mRecyclerView.layoutManager = mLayoutManager

        // Specify an adapter
        mAdapter = OrdersListAdapter(LinkedList(), context!!)
        mRecyclerView.adapter = mAdapter

        // Read data from View Model
        mViewModel = ViewModelProviders.of(activity!!).get(OrderListViewModel::class.java)
        mViewModel.getOrders()?.observe(this, Observer<List<OrderEntity>> { t -> mAdapter.updateOrderList(t!!) })

        return rootView

    }

}