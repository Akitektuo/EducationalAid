package com.akitektuo.educationalaid.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * Created by Akitektuo on 02.01.2018.
 */
class PagerAdapter(manager: FragmentManager, private val fragmentList: ArrayList<TabFragment>) : FragmentPagerAdapter(manager) {

    data class TabFragment(val fragment: Fragment, val image: Int, val imageSelected: Int, val imageLocked: Int = 0, var locked: Boolean = false)

    override fun getItem(position: Int): Fragment {
        return fragmentList[position].fragment
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

}