package com.example.kzvdar42.deliveryoperatorapp.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class BottomBarAdapter(fragmentManager : FragmentManager) : SmartFragmentStatePagerAdapter(fragmentManager) {

    private val fragments : ArrayList<Fragment> = ArrayList()

    fun addFragment(fragment : Fragment) {
        fragments.add(fragment)
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

}