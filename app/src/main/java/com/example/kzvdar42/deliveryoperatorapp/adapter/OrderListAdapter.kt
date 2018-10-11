package com.example.kzvdar42.deliveryoperatorapp.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.kzvdar42.deliveryoperatorapp.R
import com.example.kzvdar42.deliveryoperatorapp.db.OrderEntity
import kotlinx.android.synthetic.main.orders_list_recycle_view_item.view.*


class OrdersListAdapter(private var orderEntityList: List<OrderEntity>, val context: Context) : RecyclerView.Adapter<OrdersListAdapter.MyViewHolder>() {


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(orderEntityList[position], context)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val v = LayoutInflater.from(context).inflate(R.layout.orders_list_recycle_view_item, parent, false) as CardView

        return MyViewHolder(v)

    }


    override fun getItemCount(): Int {
        return orderEntityList.size
    }


    class MyViewHolder(itemView: CardView) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(orderEntity: OrderEntity, context: Context) {
            itemView.order_name.text = context.getResources().getString(R.string.order_num) + "${orderEntity.OrderNum}"
            itemView.order_description.text = orderEntity.Username + '\n' + orderEntity.OrderDescription
        }
    }

    fun updateOrderList(orderEntityList: List<OrderEntity>) {
        this.orderEntityList = orderEntityList
        notifyDataSetChanged()
    }

}