package com.coffee.king.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class ViewPagerAdapter(private val context: Context, fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private var mFragmentList: ArrayList<Fragment> = ArrayList()

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    fun addFrag(fragment: Fragment) {
        mFragmentList.add(fragment)
    }
}
