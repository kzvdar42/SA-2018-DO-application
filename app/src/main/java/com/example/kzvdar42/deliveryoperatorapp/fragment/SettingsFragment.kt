package com.example.kzvdar42.deliveryoperatorapp.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kzvdar42.deliveryoperatorapp.R
import com.example.kzvdar42.deliveryoperatorapp.activity.EmptyActivity
import com.example.kzvdar42.deliveryoperatorapp.activity.LoginActivity
import kotlinx.android.synthetic.main.fragment_settings.view.*


class SettingsFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_settings, container, false)

        // Setting ocClick listeners to the buttons.
        rootView.settings_localization_text.setOnClickListener {
            startActivity(Intent(context, EmptyActivity::class.java))
        }

        rootView.settings_contact_CO_text.setOnClickListener {
            startActivity(Intent(context, EmptyActivity::class.java))
        }

        rootView.settings_log_out_text.setOnClickListener {
            val i = Intent(context, LoginActivity::class.java)

            // Get the user data
            val sharedPref = context?.getSharedPreferences("user", Context.MODE_PRIVATE)
            sharedPref?.edit()?.putBoolean("isLogged", false)?.apply()
            startActivity(i)
        }

        return rootView
    }

}