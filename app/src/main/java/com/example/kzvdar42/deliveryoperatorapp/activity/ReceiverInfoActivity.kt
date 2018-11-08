package com.example.kzvdar42.deliveryoperatorapp.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.divyanshu.draw.activity.DrawingActivity
import com.example.kzvdar42.deliveryoperatorapp.R
import com.example.kzvdar42.deliveryoperatorapp.db.OrderEntity
import com.example.kzvdar42.deliveryoperatorapp.viewmodel.OrderInfoViewModel
import kotlinx.android.synthetic.main.activity_receiver_info.*
import kotlinx.android.synthetic.main.alert_dialog.view.*

class ReceiverInfoActivity : AppCompatActivity() {

    //View Model
    private val mViewModel
            by lazy { ViewModelProviders.of(this).get(OrderInfoViewModel::class.java) }

    private var order: OrderEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receiver_info)

        // Get data about the order
        val intent = intent
        val orderNum = intent.getIntExtra("orderNum", 0)

        mViewModel.getOrder(orderNum).observe(this, Observer { order ->
            this.order = order

            // Add the toolbar
            val toolbar = findViewById<Toolbar>(R.id.toolbar)
            toolbar.title = getString(R.string.order_num, order.orderNum)
            setSupportActionBar(toolbar)

            // Add info
            customer_name_text.text = "${order.receiverName} ${order.receiverSurname}"
            time_left_text.text = getString(R.string.time_left_label, order.expectedTtd) // TODO: rewrite to actual data
            dimensions_text.text = getString(R.string.dimensions_text, order.length, order.width, order.height)
            weight_text.text = getString(R.string.weight_label, order.weight)
            if (order.senderNotes != null) sender_notes_label.text = getString(R.string.sender_notes_label, order.senderNotes)
        })
    }

    @SuppressLint("InflateParams")
    fun onClick(view: View) {
        when (view.id) {
            R.id.decline -> {
                // TODO
                Toast.makeText(this, "Decline", Toast.LENGTH_LONG).show()
            }
            R.id.accept -> {
                // Creating alert dialog.
                val alertDialog = AlertDialog.Builder(this).create()
                // Inflating the view for the alert dialog.
                val dialogView = layoutInflater.inflate(R.layout.alert_dialog, null)
                dialogView.dialog_title.text = getString(R.string.dialog_title)
                dialogView.dialog_description.visibility = View.GONE
                dialogView.dialog_positive_btn.setOnClickListener {
                    val intent = Intent(this, DrawingActivity::class.java)
                    startActivityForResult(intent, REQUEST_CODE_DRAW)
                }
                dialogView.dialog_negative_btn.setOnClickListener {
                    alertDialog.dismiss()
                }
                // Adding view to the alert dialog and show it.
                alertDialog.setView(dialogView)
                alertDialog.setCancelable(true)
                alertDialog.show()
            }
        }
    }

    // Get bitmap in onActivityResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null && resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_DRAW -> {
                    val result = data.getByteArrayExtra("bitmap")
                    val bitmap = BitmapFactory.decodeByteArray(result, 0, result.size)
                    saveImage(bitmap)
                    finish()
                }
            }
        }
    }

    private fun saveImage(signature: Bitmap) {
        Toast.makeText(this, "Got the image", Toast.LENGTH_LONG).show()
        mViewModel.updateOrder(order!!.orderNum, "Delivered",
                order!!.coords.size - 1, signature)
    }

    companion object {
        // TODO: Check hot to create code in a right way.
        private const val REQUEST_CODE_DRAW = 42
    }
}
