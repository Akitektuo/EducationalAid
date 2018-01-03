package com.akitektuo.educationalaid.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.akitektuo.educationalaid.R
import com.akitektuo.educationalaid.adapter.PagerAdapter
import com.akitektuo.educationalaid.component.TabbedPagerComponent
import com.akitektuo.educationalaid.fragment.InfoFragment
import com.akitektuo.educationalaid.fragment.InfoFragment.Companion.KEY_CONTENT
import com.akitektuo.educationalaid.fragment.InfoFragment.Companion.KEY_ID
import com.akitektuo.educationalaid.fragment.InfoFragment.Companion.KEY_IMAGE
import com.akitektuo.educationalaid.fragment.InfoFragment.Companion.KEY_IMPORTANT
import com.akitektuo.educationalaid.fragment.InfoFragment.Companion.KEY_TITLE
import kotlinx.android.synthetic.main.activity_module.*

class ModuleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_module)

        buttonBack.setOnClickListener { finish() }
        Toast.makeText(this, "Intent with id ${intent.getIntExtra("key_id", 0)}", Toast.LENGTH_SHORT).show()
        textModuleName.text = "text-shadow"

        val fragments = ArrayList<PagerAdapter.TabFragment>()
        for (i in 0..9) {
            val fragmentInfo = InfoFragment()
            val arguments = Bundle()
            arguments.putInt(KEY_ID, 1)
            arguments.putString(KEY_TITLE, "The text-shadow Property")
            arguments.putString(KEY_CONTENT, "The text-shadow property defines one or more comma-separated shadow effects, to be applied to the text content of the current element.\n\nThe image below shows how the text-shadow property is applied:")
            arguments.putString(KEY_IMAGE, "content")
            arguments.putString(KEY_IMPORTANT, "- The offset-x and offset-y values are required for the CSS text-shadow property.\n- The color value is not required, but since the default for the text-shadow is transparent, the text-shadow will not appear unless you specify a color value.")
            fragmentInfo.arguments = arguments
            if (i < 4) {
                fragments.add(PagerAdapter.TabFragment(fragmentInfo, R.drawable.book, R.drawable.book_selected))
            } else {
                fragments.add(PagerAdapter.TabFragment(fragmentInfo, R.drawable.book, R.drawable.book_selected, locked = true))
            }
        }
        TabbedPagerComponent(this, pager, tab, fragments)
    }

}
