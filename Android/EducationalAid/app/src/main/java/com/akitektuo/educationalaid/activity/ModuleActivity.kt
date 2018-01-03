package com.akitektuo.educationalaid.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.akitektuo.educationalaid.R
import com.akitektuo.educationalaid.adapter.PagerAdapter
import com.akitektuo.educationalaid.component.TabbedPagerComponent
import com.akitektuo.educationalaid.fragment.InfoFragment
import kotlinx.android.synthetic.main.activity_module.*

class ModuleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_module)

        buttonBack.setOnClickListener { finish() }
        Toast.makeText(this, "Intent with id ${intent.getIntExtra("key_id", 0)}", Toast.LENGTH_SHORT).show()
        textModuleName.text = "text-shadow"

        val fragments = ArrayList<PagerAdapter.TabFragment>()
        val fragmentInfo = InfoFragment()
        val arguments = Bundle()
        arguments.putInt(fragmentInfo.KEY_ID, 1)
        arguments.putString(fragmentInfo.KEY_TITLE, "The text-shadow Property")
        arguments.putString(fragmentInfo.KEY_CONTENT, "The text-shadow property defines one or more comma-separated shadow effects, to be applied to the text content of the current element.\n\nThe image below shows how the text-shadow property is applied:")
        arguments.putString(fragmentInfo.KEY_IMAGE, "content")
        arguments.putString(fragmentInfo.KEY_IMPORTANT, "- The offset-x and offset-y values are required for the CSS text-shadow property.\n- The color value is not required, but since the default for the text-shadow is transparent, the text-shadow will not appear unless you specify a color value.")
        fragmentInfo.arguments = arguments
        fragments.add(PagerAdapter.TabFragment(fragmentInfo, R.drawable.book, R.drawable.book_selected))
        TabbedPagerComponent(this, pager, tab, fragments)
    }

}
