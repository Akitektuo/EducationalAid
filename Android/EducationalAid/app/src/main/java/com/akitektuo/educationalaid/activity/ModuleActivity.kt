package com.akitektuo.educationalaid.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.akitektuo.educationalaid.R
import com.akitektuo.educationalaid.adapter.PagerAdapter
import com.akitektuo.educationalaid.component.TabbedPagerComponent
import com.akitektuo.educationalaid.fragment.InfoFragment
import com.akitektuo.educationalaid.fragment.QuestionFragment
import com.akitektuo.educationalaid.notifier.Fragment
import kotlinx.android.synthetic.main.activity_module.*

class ModuleActivity : AppCompatActivity(), Fragment.OnClickContinue {

    private var tabbedPager: TabbedPagerComponent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_module)

        buttonBack.setOnClickListener { finish() }
        Toast.makeText(this, "Intent with id ${intent.getIntExtra("key_id", 0)}", Toast.LENGTH_SHORT).show()
        textModuleName.text = "text-shadow"

        val fragments = ArrayList<PagerAdapter.TabFragment>()
        for (i in 0..5) {
            val fragmentInfo = InfoFragment()
            val argumentsInfo = Bundle()
            argumentsInfo.putInt(InfoFragment.KEY_ID, i + 1)
            fragmentInfo.arguments = argumentsInfo
            val fragmentQuestion = QuestionFragment()
            val argumentsQuestion = Bundle()
            argumentsQuestion.putInt(QuestionFragment.KEY_ID, i + 1)
            fragmentQuestion.arguments = argumentsQuestion
            when (i) {
                0 -> fragments.add(PagerAdapter.TabFragment(fragmentInfo, R.drawable.book, R.drawable.book_selected))
                1 -> fragments.add(PagerAdapter.TabFragment(fragmentQuestion, R.drawable.question, R.drawable.question_selected))
                2 -> fragments.add(PagerAdapter.TabFragment(fragmentQuestion, R.drawable.question, R.drawable.question_selected))
                3 -> fragments.add(PagerAdapter.TabFragment(fragmentQuestion, R.drawable.question, R.drawable.question_selected))
                4 -> fragments.add(PagerAdapter.TabFragment(fragmentQuestion, R.drawable.question, R.drawable.question_selected))
                5 -> fragments.add(PagerAdapter.TabFragment(fragmentQuestion, R.drawable.question, R.drawable.question_selected))
            }
        }
        tabbedPager = TabbedPagerComponent(this, pager, tab, fragments)
    }

    override fun continueOnClick() {
        tabbedPager?.nextFragment()
    }

}
