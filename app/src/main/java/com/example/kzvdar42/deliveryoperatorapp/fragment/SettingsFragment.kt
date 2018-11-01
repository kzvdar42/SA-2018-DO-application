package com.example.kzvdar42.deliveryoperatorapp.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.kzvdar42.deliveryoperatorapp.R
import com.example.kzvdar42.deliveryoperatorapp.activity.EmptyActivity
import com.example.kzvdar42.deliveryoperatorapp.activity.LoginActivity
import com.example.kzvdar42.deliveryoperatorapp.viewmodel.SettingsViewModel
import kotlinx.android.synthetic.main.fragment_settings.view.*


class SettingsFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_settings, container, false)

        // Get the View Model.
        val mViewModel = ViewModelProviders.of(activity!!).get(SettingsViewModel::class.java)

        // Setting ocClick listeners to the buttons.
        rootView.settings_localization_text.setOnClickListener {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.setClassName("com.android.settings", "com.android.settings.LanguageSettings")
            startActivity(intent)
        }

        rootView.settings_contact_CO_text.setOnClickListener {
            startActivity(Intent(context, EmptyActivity::class.java))
        }

        rootView.settings_log_out_text.setOnClickListener {
            // Delete all user data.
            mViewModel.logout()
            // Go to the login activity.
            val i = Intent(context, LoginActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
        }

        return rootView
    }

}