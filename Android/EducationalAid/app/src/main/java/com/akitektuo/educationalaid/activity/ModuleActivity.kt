package com.akitektuo.educationalaid.activity

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.akitektuo.educationalaid.R
import com.akitektuo.educationalaid.adapter.PagerAdapter
import com.akitektuo.educationalaid.component.TabbedPagerComponent
import com.akitektuo.educationalaid.fragment.InfoFragment
import com.akitektuo.educationalaid.fragment.InfoFragment.Companion.KEY_ID
import com.akitektuo.educationalaid.fragment.InfoFragment.Companion.KEY_ID_MIQ
import com.akitektuo.educationalaid.fragment.InfoFragment.Companion.KEY_LOCKED
import com.akitektuo.educationalaid.fragment.QuestionFragment
import com.akitektuo.educationalaid.fragment.QuestionFragment.Companion.KEY_TOTAL
import com.akitektuo.educationalaid.fragment.SettingsFragment
import com.akitektuo.educationalaid.storage.database.Database
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_module.*

class ModuleActivity : AppCompatActivity() {

    private lateinit var tabbedPager: TabbedPagerComponent
    private lateinit var auth: FirebaseAuth
    private lateinit var database: Database
    private lateinit var moduleId: String

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(SettingsFragment.Language(newBase!!).wrap())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_module)
        buttonBack.setOnClickListener { finish() }

        auth = FirebaseAuth.getInstance()
        database = Database()
        moduleId = intent.getStringExtra("key_id")

        database.getModule(moduleId, {
            textModuleName.text = it.name
        })

        val fragments = ArrayList<PagerAdapter.TabFragment>()

        database.getModuleIQAll(moduleId, {
            val modulesIsQs = it
            database.databaseUsersMsIsQs.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError?) {

                }

                override fun onDataChange(data: DataSnapshot?) {
                    val usersMsIsQs = ArrayList<Database.UserMIQ>()
                    data?.children?.mapNotNullTo(usersMsIsQs) { it.getValue(Database.UserMIQ::class.java) }
                    val questions = modulesIsQs.count { it.question }
                    modulesIsQs.forEach {
                        val arguments = Bundle()
                        val moduleIQ = it
                        var locked = true
                        usersMsIsQs.filter { it.moduleIQId == moduleIQ.id }.forEach {
                            locked = it.locked
                        }
                        if (it.question) {
                            val fragmentQuestion = QuestionFragment()
                            arguments.putString(KEY_ID, moduleIQ.questionId)
                            arguments.putBoolean(KEY_LOCKED, locked)
                            arguments.putString(KEY_ID_MIQ, moduleIQ.id)
                            arguments.putInt(KEY_TOTAL, questions)
                            fragmentQuestion.arguments = arguments
                            fragments.add(PagerAdapter.TabFragment(fragmentQuestion, R.drawable.question, R.drawable.question_selected, R.drawable.question_silver, locked))
                        } else {
                            val fragmentInfo = InfoFragment()
                            arguments.putString(KEY_ID, moduleIQ.infoId)
                            arguments.putBoolean(KEY_LOCKED, locked)
                            arguments.putString(KEY_ID_MIQ, moduleIQ.id)
                            fragmentInfo.arguments = arguments
                            fragments.add(PagerAdapter.TabFragment(fragmentInfo, R.drawable.book, R.drawable.book_selected, R.drawable.book_silver, locked))
                        }
                    }
                    tabbedPager = TabbedPagerComponent(this@ModuleActivity, pager, tab, fragments)
                }
            })
        })
//
//        for (i in 0..5) {
//            val fragmentInfo = InfoFragment()
//            val argumentsInfo = Bundle()
//            argumentsInfo.putInt(InfoFragment.KEY_ID, i + 1)
//            fragmentInfo.arguments = argumentsInfo
//            val fragmentQuestion = QuestionFragment()
//            val argumentsQuestion = Bundle()
//            argumentsQuestion.putInt(QuestionFragment.KEY_ID, i + 1)
//            fragmentQuestion.arguments = argumentsQuestion
//            when (i) {
//                0 -> fragments.add(PagerAdapter.TabFragment(fragmentInfo, R.drawable.book, R.drawable.book_selected))
//                1 -> fragments.add(PagerAdapter.TabFragment(fragmentQuestion, R.drawable.question, R.drawable.question_selected))
//                2 -> fragments.add(PagerAdapter.TabFragment(fragmentQuestion, R.drawable.question, R.drawable.question_selected))
//                3 -> fragments.add(PagerAdapter.TabFragment(fragmentQuestion, R.drawable.question, R.drawable.question_selected))
//                4 -> fragments.add(PagerAdapter.TabFragment(fragmentQuestion, R.drawable.question, R.drawable.question_selected))
//                5 -> fragments.add(PagerAdapter.TabFragment(fragmentQuestion, R.drawable.question, R.drawable.question_selected))
//            }
//        }
    }

    fun continueOnClick(idUMIQ: String) {
        tabbedPager.nextFragment(idUMIQ)
    }

}
