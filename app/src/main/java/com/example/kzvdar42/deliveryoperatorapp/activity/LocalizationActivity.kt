package com.example.kzvdar42.deliveryoperatorapp.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.kzvdar42.deliveryoperatorapp.R
import java.util.*


class LocalizationActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_localization)

        // Add the toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.localization)
        setSupportActionBar(toolbar)

        // Set back button on toolbar
        toolbar.setNavigationOnClickListener { finish() }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun onClick(view: View) {
        var locale = Locale.ENGLISH
        // Change language on tap.
        when (view.id) {
            R.id.english_layout_text -> {
                locale = Locale.ENGLISH
                Toast.makeText(this, "English", Toast.LENGTH_LONG).show()
            }
            R.id.russian_layout_text -> {
                locale = Locale("ru")
                Toast.makeText(this, "Russian", Toast.LENGTH_LONG).show()
            }
            else -> {
                Toast.makeText(this, "Error!", Toast.LENGTH_LONG).show()
            }
        }
        changeLaunguage(locale)
    }

    // TODO: Make it work!
    private fun changeLaunguage(language: Locale) {
        Locale.setDefault(language)
        val resources = this.resources
        val configuration = Configuration(resources.configuration)

        if (Build.VERSION.SDK_INT >= 17) {
            configuration.setLocale(language)
            this.createConfigurationContext(configuration)
        } else {
            configuration.locale = language
            resources.updateConfiguration(configuration, resources.displayMetrics)
        }

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        val i = baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)
        i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
    }

}
