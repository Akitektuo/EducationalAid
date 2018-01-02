package com.akitektuo.educationalaid.activity

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import com.akitektuo.educationalaid.R
import kotlinx.android.synthetic.main.activity_module.*

class ModuleActivity : AppCompatActivity(), TabLayout.OnTabSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_module)
        setupViewPager()
        tab.setupWithViewPager(pager)
        tab.addOnTabSelectedListener(this)
        setupTabIcons()
    }

    private fun setupViewPager() {
//        val adapter = MainActivity.ViewPagerAdapter(supportFragmentManager)
//        with(adapter) {
//            addFragment()
//        }
//        pager.adapter = adapter
    }

    private fun setupTabIcons() {

    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
