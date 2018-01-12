package com.akitektuo.educationalaid.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.akitektuo.educationalaid.R
import com.akitektuo.educationalaid.adapter.PagerAdapter
import com.akitektuo.educationalaid.component.TabbedPagerComponent
import com.akitektuo.educationalaid.fragment.LearnFragment
import com.akitektuo.educationalaid.fragment.ProfileFragment
import com.akitektuo.educationalaid.fragment.SettingsFragment
import com.akitektuo.educationalaid.storage.local.preference.SettingsPreference
import com.akitektuo.educationalaid.storage.local.preference.SettingsPreference.Companion.KEY_CREATED
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val preference = SettingsPreference(this)
        if (!preference.getBoolean(KEY_CREATED)!!) {
            preference.setDefault()
        }

        val fragments = ArrayList<PagerAdapter.TabFragment>()
        fragments.add(PagerAdapter.TabFragment(LearnFragment(), R.drawable.learn, R.drawable.learn_selected))
        fragments.add(PagerAdapter.TabFragment(ProfileFragment(), R.drawable.profile, R.drawable.profile_selected))
        fragments.add(PagerAdapter.TabFragment(SettingsFragment(), R.drawable.settings, R.drawable.settings_selected))
        TabbedPagerComponent(this, pager, tab, fragments, 1)
    }

}
