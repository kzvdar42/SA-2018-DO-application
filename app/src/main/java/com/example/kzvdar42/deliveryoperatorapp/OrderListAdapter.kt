package com.example.kzvdar42.deliveryoperatorapp


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView


class OrdersListAdapter : RecyclerView.Adapter<OrdersListAdapter.ViewHolder>() {

    private var mDataset: List<Order>? = null

    class ViewHolder internal constructor(v: CardView) : RecyclerView.ViewHolder(v) {
        val mName: TextView
        val mDescription: TextView

        init {
            val rv = v.getChildAt(0) as RelativeLayout
            mName = rv.getChildAt(0) as TextView
            mDescription = rv.getChildAt(1) as TextView

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersListAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.orders_list_recycle_view_item, parent, false) as CardView
        return OrdersListAdapter.ViewHolder(v)
    }

    override fun onBindViewHolder(holder: OrdersListAdapter.ViewHolder, position: Int) {
        if (mDataset != null) {
            val currentOrder = mDataset!![position]
            holder.mName.text = currentOrder.name
            holder.mDescription.text = currentOrder.description
        }

    }

    override fun getItemCount(): Int {
        return if (mDataset == null) 0 else mDataset!!.size
    }

    fun setProductList(menuList: List<Order>) {
        mDataset = menuList
        notifyDataSetChanged()
    }

}