package com.example.kzvdar42.deliveryoperatorapp.fragment

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
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
    private val mAdapter
            by lazy { OrdersListAdapter(LinkedList(), context!!) }
    private val mViewModel
            by lazy { ViewModelProviders.of(activity!!).get(OrderListViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_orders_list, container, false)
        setHasOptionsMenu(true)

        // Setting up the recycler view
        mRecyclerView = rootView.findViewById(R.id.order_list_recycler_view)

        // Use a linear layout manager
        mRecyclerView.layoutManager = LinearLayoutManager(activity)

        // Specify an adapter
        mRecyclerView.adapter = mAdapter

        // Read data from View Model
        mViewModel.updateOrders()
        mViewModel.getAcceptedOrders()?.observe(this,
                Observer<List<OrderEntity>> { t -> mAdapter.updateOrderList(t) }) // TODO: sort orders due ttd

        // Set the toolbar title
        activity?.findViewById<Toolbar>(R.id.toolbar)?.title = getString(R.string.accepted_orders_label)

        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.orders_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.accepted_orders -> {
                activity?.findViewById<Toolbar>(R.id.toolbar)?.title = getString(R.string.accepted_orders_label)
                mViewModel.getNewOrders()?.removeObservers(this)
                mViewModel.getAcceptedOrders()?.observe(this, Observer<List<OrderEntity>> { t -> mAdapter.updateOrderList(t) })
            }

            R.id.new_orders -> {
                activity?.findViewById<Toolbar>(R.id.toolbar)?.title = getString(R.string.new_orders_label)
                mViewModel.getAcceptedOrders()?.removeObservers(this)
                mViewModel.getNewOrders()?.observe(this, Observer<List<OrderEntity>> { t ->
                    mAdapter.updateOrderList(t)
                })
            }
        }
        return super.onOptionsItemSelected(item)
    }

}