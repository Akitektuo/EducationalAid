package com.akitektuo.educationalaid.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.akitektuo.educationalaid.R
import kotlinx.android.synthetic.main.fragment_info.*

/**
 * Created by Akitektuo on 03.01.2018.
 */

class InfoFragment : Fragment() {

    val KEY_ID = "key_id"
    val KEY_TITLE = "key_title"
    val KEY_CONTENT = "key_content"
    val KEY_IMAGE = "key_image"
    val KEY_IMPORTANT = "key_important"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.fragment_info, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (savedInstanceState != null) {
            textTitle.text = savedInstanceState.getString(KEY_TITLE)
            textContent.text = savedInstanceState.getString(KEY_CONTENT)
            if (savedInstanceState.getString(KEY_IMAGE).isNullOrEmpty()) {
                imageContent.visibility = View.GONE
            } else {
                imageContent.setImageResource(resources.getIdentifier(savedInstanceState.getString(KEY_IMAGE), "drawable", context.packageName))
            }
            if (savedInstanceState.getString(KEY_IMPORTANT).isNullOrEmpty()) {
                layoutImportant.visibility = View.GONE
            } else {
                textImportant.text = savedInstanceState.getString(KEY_IMPORTANT)
            }
        }
    }

}