package com.akitektuo.educationalaid.component

import android.app.Activity
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.InputMethodManager
import com.akitektuo.educationalaid.adapter.PagerAdapter
import com.akitektuo.educationalaid.fragment.InfoFragment
import com.akitektuo.educationalaid.fragment.QuestionFragment
import com.akitektuo.educationalaid.storage.database.Database
import com.google.firebase.auth.FirebaseAuth

/**
 * Created by Akitektuo on 03.01.2018.
 */

class TabbedPagerComponent(
        private val activity: AppCompatActivity,
        private val pager: ViewPager,
        private val tab: TabLayout,
        private val fragments: ArrayList<PagerAdapter.TabFragment>,
        private val startingPage: Int = 0) : TabLayout.OnTabSelectedListener, ViewPager.OnPageChangeListener {

    init {
        pager.adapter = PagerAdapter(activity.supportFragmentManager, fragments)
        pager.addOnPageChangeListener(this)
        tab.setupWithViewPager(pager)
        tab.addOnTabSelectedListener(this)
        with(tab) {
            for (i in 0 until fragments.size) {
                if (fragments[i].locked) {
                    getTabAt(i)?.setIcon(fragments[i].imageLocked)
                } else {
                    getTabAt(i)?.setIcon(fragments[i].image)
                }
            }
            getTabAt(startingPage)?.setIcon(fragments[startingPage].imageSelected)
            getTabAt(startingPage)?.select()
        }
    }

    private val database = Database()
    private val auth = FirebaseAuth.getInstance()

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        (activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
        with(tab) {
            if (!fragments[position].locked) {
                setIcon(fragments[position].imageSelected)
            }
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {
        with(tab) {
            if (!fragments[position].locked) {
                setIcon(fragments[position].image)
            }
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (fragments[position].locked) {
            tab.getTabAt(position - 1)?.select()
        }
    }

    override fun onPageSelected(position: Int) {
    }

    fun nextFragment(idUMIQ: String) {
        val position = tab.selectedTabPosition + 1
        if (position == fragments.size) {
            //TODO unlock next part
            database.unlockNext(auth.currentUser?.uid!!, idUMIQ, {
                activity.finish()
            })
        } else {
            with(fragments[position]) {
                locked = false
                when (fragment) {
                    is InfoFragment -> fragment.unlockFragment()
                    is QuestionFragment -> fragment.unlockFragment()
                }
            }
            tab.getTabAt(position)?.select()
        }
    }

}