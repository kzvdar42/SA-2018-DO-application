package com.example.kzvdar42.deliveryoperatorapp.adapter


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.kzvdar42.deliveryoperatorapp.R
import com.example.kzvdar42.deliveryoperatorapp.activity.OrderInfoActivity
import com.example.kzvdar42.deliveryoperatorapp.db.OrderEntity
import com.example.kzvdar42.deliveryoperatorapp.util.Constants
import kotlinx.android.synthetic.main.orders_list_recycle_view_item.view.*


class OrdersListAdapter(private var orderEntityList: List<OrderEntity>, val context: Context)
    : RecyclerView.Adapter<OrdersListAdapter.MyViewHolder>() {


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(orderEntityList[position], context)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val v = LayoutInflater.from(context)
                .inflate(R.layout.orders_list_recycle_view_item,
                        parent, false) as CardView
        v.setOnClickListener {
            val intent = Intent(context, OrderInfoActivity::class.java)
            val orderNum =
                    String(StringBuilder(
                            it.order_name.text.removePrefix(
                                    context.resources.getString(
                                            R.string.order_num).removeSuffix(
                                            "%1\$d")))) // FIXME: Find a more elegant way.
            intent.putExtra("orderNum", orderNum.toInt())
            context.startActivity(intent)
        }
        return MyViewHolder(v)

    }


    override fun getItemCount(): Int {
        return orderEntityList.size
    }


    class MyViewHolder(itemView: CardView) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(orderEntity: OrderEntity, context: Context) {
            itemView.order_name.text = context.resources.getString(R.string.order_num, orderEntity.orderNum)
            itemView.order_description.text =
                    "%s %s\n%s\n%s\n%s".format(
                            orderEntity.receiverName, orderEntity.receiverSurname,
                             leftTime(Constants.leftTime(orderEntity.expectedTtd), context),
                            orderEntity.senderAddress, orderEntity.receiverAddress)
        }
        private fun leftTime(time: Pair<Int, String>, context: Context):String {
            return when (time.first) {
                0 -> context.getString(R.string.ttl_days, time.second)
                1 -> context.getString(R.string.ttl_hours, time.second)
                2 -> context.getString(R.string.ttl_minutes, time.second)
                else -> context.getString(R.string.ttl_ended)
            }
        }
    }



    fun updateOrderList(orderEntityList: List<OrderEntity>) {
        this.orderEntityList = orderEntityList
        notifyDataSetChanged()
    }

}