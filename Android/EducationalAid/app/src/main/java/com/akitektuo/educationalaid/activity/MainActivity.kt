package com.akitektuo.educationalaid.activity

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.InputMethodManager
import com.akitektuo.educationalaid.R
import com.akitektuo.educationalaid.adapter.PagerAdapter
import com.akitektuo.educationalaid.fragment.LearnFragment
import com.akitektuo.educationalaid.fragment.ProfileFragment
import com.akitektuo.educationalaid.fragment.SettingsFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), TabLayout.OnTabSelectedListener {

    private val fragments = ArrayList<PagerAdapter.TabFragment>()
    private val startingPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupViewPager()
        tab.setupWithViewPager(pager)
        tab.addOnTabSelectedListener(this)
        setupTabIcons()
    }

    private fun setupViewPager() {
        fragments.add(PagerAdapter.TabFragment(LearnFragment(), R.drawable.learn, R.drawable.learn_selected))
        fragments.add(PagerAdapter.TabFragment(ProfileFragment(), R.drawable.profile, R.drawable.profile_selected))
        fragments.add(PagerAdapter.TabFragment(SettingsFragment(), R.drawable.settings, R.drawable.settings_selected))
        pager.adapter = PagerAdapter(this.supportFragmentManager, fragments)
    }

    private fun setupTabIcons() {
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
        (getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(currentFocus?.windowToken, 0)
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
