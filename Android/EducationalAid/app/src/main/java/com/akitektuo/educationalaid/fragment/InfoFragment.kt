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

    companion object {
        val KEY_ID = "key_id"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.fragment_info, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val bundle = arguments
        // get data from db for ID
        textTitle.text = "The text-shadow Property"
        textContent.text = "The text-shadow property defines one or more comma-separated shadow effects, to be applied to the text content of the current element.\n\nThe image below shows how the text-shadow property is applied:"
        if ("content".isNullOrEmpty()) {
                imageContent.visibility = View.GONE
            } else {
            imageContent.setImageResource(resources.getIdentifier("content", "drawable", context?.packageName))
            }
        if ("- The offset-x and offset-y values are required for the CSS text-shadow property.\n- The color value is not required, but since the default for the text-shadow is transparent, the text-shadow will not appear unless you specify a color value.".isNullOrEmpty()) {
                layoutImportant.visibility = View.GONE
            } else {
            textImportant.text = "- The offset-x and offset-y values are required for the CSS text-shadow property.\n- The color value is not required, but since the default for the text-shadow is transparent, the text-shadow will not appear unless you specify a color value."
            }
        if (bundle?.getInt(KEY_ID) == 3) {
            imageLocked.visibility = View.VISIBLE
        }

        buttonContinue.setOnClickListener {
            (activity as com.akitektuo.educationalaid.notifier.Fragment.OnClickContinue).continueOnClick()
        }
    }

    fun unlockFragment() {
        imageLocked.visibility = View.GONE
    }

}