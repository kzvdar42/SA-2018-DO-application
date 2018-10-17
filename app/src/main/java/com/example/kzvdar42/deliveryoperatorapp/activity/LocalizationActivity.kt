package com.example.kzvdar42.deliveryoperatorapp.activity

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.kzvdar42.deliveryoperatorapp.R
import kotlinx.android.synthetic.main.activity_localization.*
import android.widget.Toast
import java.util.*
import android.util.DisplayMetrics




class LocalizationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_localization)

        // Add the toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.login_label)
        setSupportActionBar(toolbar)

        // Set back button on toolbar
        toolbar.setNavigationOnClickListener { finish() }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun onClick(view : View) {
        var lang = "eng"
        when (view.id) {
            R.id.english_layout -> lang = "en"
            R.id.russian_layout -> lang = "ru"
        }
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }
}
