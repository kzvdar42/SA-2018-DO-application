package com.example.kzvdar42.deliveryoperatorapp.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kzvdar42.deliveryoperatorapp.R
import timber.log.Timber


class SettingsFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.e("Я ТУТА ПАЦАНЫ!")
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

}