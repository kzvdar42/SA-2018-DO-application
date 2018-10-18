package com.example.kzvdar42.deliveryoperatorapp.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kzvdar42.deliveryoperatorapp.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // убедитесь, что вызываете до super.onCreate()
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        val user = getSharedPreferences("user", Context.MODE_PRIVATE)
        intent = if (user.getBoolean("isLogged", false)) {
            Intent(this, MainActivity::class.java)

        } else {
            Intent(this, LoginActivity::class.java)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION
        startActivity(intent)
        finish()
    }

}