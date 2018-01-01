package com.akitektuo.educationalaid.activity

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.InputMethodManager
import com.akitektuo.educationalaid.R
import com.akitektuo.educationalaid.fragment.LearnFragment
import com.akitektuo.educationalaid.fragment.ProfileFragment
import com.akitektuo.educationalaid.fragment.SettingsFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), TabLayout.OnTabSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupViewPager()
        tab.setupWithViewPager(pager)
        tab.addOnTabSelectedListener(this)
        setupTabIcons()
    }

    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        with(adapter) {
            addFragment(LearnFragment())
            addFragment(ProfileFragment())
            addFragment(SettingsFragment())
        }
        pager.adapter = adapter
    }

    private fun setupTabIcons() {
        with(tab) {
            getTabAt(0)?.setIcon(R.drawable.learn_selected)
            getTabAt(1)?.setIcon(R.drawable.profile)
            getTabAt(2)?.setIcon(R.drawable.settings)
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        (getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        when (tab?.position) {
            0 -> tab.setIcon(R.drawable.learn_selected)
            1 -> tab.setIcon(R.drawable.profile_selected)
            2 -> tab.setIcon(R.drawable.settings_selected)
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        when (tab?.position) {
            0 -> tab.setIcon(R.drawable.learn)
            1 -> tab.setIcon(R.drawable.profile)
            2 -> tab.setIcon(R.drawable.settings)
        }
    }

    class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {

        private val fragmentList = ArrayList<Fragment>()

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }

        fun addFragment(fragment: Fragment) {
            fragmentList.add(fragment)
        }

    }

}
