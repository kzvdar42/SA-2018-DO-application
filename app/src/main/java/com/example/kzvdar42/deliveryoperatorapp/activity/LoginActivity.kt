package com.example.kzvdar42.deliveryoperatorapp.activity


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.kzvdar42.deliveryoperatorapp.R


class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Add the toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.login_label)
        setSupportActionBar(toolbar)

        // Get the user data
        sharedPref = this.getSharedPreferences("user", Context.MODE_PRIVATE)

        // Go to main page if already logged in.
        if (sharedPref.getBoolean("isLogged", false)) goToMainPage()
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.login_button -> {
                login()
            }
        }
    }

    private fun goToMainPage() {
        intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION
        startActivity(intent)
    }

    private fun login() {
        // TODO: Implement the Login process
        sharedPref.edit().putBoolean("isLogged", true).apply()
        goToMainPage()
    }
}
