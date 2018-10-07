package com.example.kzvdar42.deliveryoperatorapp.Activity


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.kzvdar42.deliveryoperatorapp.R


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Add the toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.login_label)
        setSupportActionBar(toolbar)
    }

    fun onClick(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.login_button -> {
                intent = Intent(this, ListOrdersActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
