package com.akitektuo.educationalaid.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.akitektuo.educationalaid.R
import kotlinx.android.synthetic.main.fragment_settings.*

/**
 * Created by Akitektuo on 31.12.2017.
 */
class SettingsFragment : Fragment() {

    companion object {
        val LANGUAGE_EN = "en"
        val LANGUAGE_RO = "ro"
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        textSound.setOnClickListener { }
    }

}