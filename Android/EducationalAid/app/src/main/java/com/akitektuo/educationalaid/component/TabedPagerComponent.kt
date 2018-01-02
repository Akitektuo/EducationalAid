package com.akitektuo.educationalaid.component

import android.app.Activity
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.InputMethodManager
import com.akitektuo.educationalaid.adapter.PagerAdapter

/**
 * Created by Akitektuo on 03.01.2018.
 */

class TabedPagerComponent(
        private val activity: AppCompatActivity,
        private val pager: ViewPager,
        private val tab: TabLayout,
        private val fragments: ArrayList<PagerAdapter.TabFragment>,
        private val startingPage: Int = 0) : TabLayout.OnTabSelectedListener {

    init {
        pager.adapter = PagerAdapter(activity.supportFragmentManager, fragments)
        tab.setupWithViewPager(pager)
        tab.addOnTabSelectedListener(this)
        with(tab) {
            for (i in 0 until fragments.size) {
                getTabAt(i)?.setIcon(fragments[i].image)
            }
            getTabAt(startingPage)?.setIcon(fragments[startingPage].imageSelected)
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        (activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
        with(tab) {
            setIcon(fragments[position].imageSelected)
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {
        with(tab) {
            setIcon(fragments[position].image)
        }
    }

}