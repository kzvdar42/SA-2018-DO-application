package com.example.kzvdar42.deliveryoperatorapp.fragment

import android.os.Bundle
import android.view.*
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


    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: OrdersListAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mViewModel: OrderListViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        val rootView = inflater.inflate(R.layout.fragment_orders_list, container, false)
        setHasOptionsMenu(true)

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
        mViewModel.getAcceptedOrders()?.observe(this, Observer<List<OrderEntity>> { t -> mAdapter.updateOrderList(t) })

        return rootView

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.orders_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.accepted_orders -> mViewModel.getAcceptedOrders()?.observe(this, Observer<List<OrderEntity>> { t -> mAdapter.updateOrderList(t) })

            R.id.new_orders -> mViewModel.getNewOrders()?.observe(this, Observer<List<OrderEntity>> { t -> mAdapter.updateOrderList(t) })
        }
        return super.onOptionsItemSelected(item)
    }

}