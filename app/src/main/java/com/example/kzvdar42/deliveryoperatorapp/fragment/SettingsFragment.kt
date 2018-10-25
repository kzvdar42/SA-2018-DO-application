package com.example.kzvdar42.deliveryoperatorapp.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
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
            val intent = Intent(Intent.ACTION_MAIN)
            intent.setClassName("com.android.settings", "com.android.settings.LanguageSettings")
            startActivity(intent)
        }

        rootView.settings_contact_CO_text.setOnClickListener {
            startActivity(Intent(context, EmptyActivity::class.java))
        }

        rootView.settings_log_out_text.setOnClickListener {//TODO: Delete all data from the database before log out
            val i = Intent(context, LoginActivity::class.java) // TODO: log out from the server
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Delete the user data
            val sharedPref = context?.getSharedPreferences("user", Context.MODE_PRIVATE)
            sharedPref?.edit()?.putString("token", "")?.apply()
            startActivity(i)
        }

        return rootView
    }

}