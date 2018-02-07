package com.akitektuo.educationalaid.activity

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.akitektuo.educationalaid.R
import com.akitektuo.educationalaid.adapter.ChapterAdapter
import com.akitektuo.educationalaid.adapter.ChapterViewHolder
import com.akitektuo.educationalaid.adapter.ModuleAdapter
import com.akitektuo.educationalaid.fragment.SettingsFragment
import com.akitektuo.educationalaid.storage.database.Database
import com.firebase.ui.database.FirebaseRecyclerAdapter
import kotlinx.android.synthetic.main.activity_lesson.*

class LessonActivity : AppCompatActivity() {

    private lateinit var database: Database

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(SettingsFragment.Language(newBase!!).wrap())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson)
        buttonBack.setOnClickListener { finish() }

        listChapters.setHasFixedSize(true)
        listChapters.layoutManager = LinearLayoutManager(this)

        database.getLesson(intent.getStringExtra("key_id"), {
            textLessonName.text = it.name
        })

        val moduleModels = ArrayList<ModuleAdapter.ModuleModel>()
        moduleModels.add(ModuleAdapter.ModuleModel(1, 1, "Introduction to CSS3", 3, 2))
        moduleModels.add(ModuleAdapter.ModuleModel(2, 2, "Vendor Prefixes", 2, 2))
        moduleModels.add(ModuleAdapter.ModuleModel(3, 3, "Rounded Corners", 2, 2))
        moduleModels.add(ModuleAdapter.ModuleModel(4, 4, "box-shadow", 3, 2))
        moduleModels.add(ModuleAdapter.ModuleModel(5, 5, "Box Shadow Techniques", 2, 2))
        moduleModels.add(ModuleAdapter.ModuleModel(6, 6, "Transparency Effect", 1, 2))
        moduleModels.add(ModuleAdapter.ModuleModel(7, 7, "text-shadow", 2, 1))
        moduleModels.add(ModuleAdapter.ModuleModel(8, 8, "Pseudo Classes", 1, 0))
        moduleModels.add(ModuleAdapter.ModuleModel(9, 9, "Pseudo Elements", 2, 0))
        moduleModels.add(ModuleAdapter.ModuleModel(10, 10, "word-wrap", 1, 0))
        moduleModels.add(ModuleAdapter.ModuleModel(11, 11, "@font-face", 2, 0))
        moduleModels.add(ModuleAdapter.ModuleModel(12, 12, "Module 5 Quiz", 4, 0))
        val chapterModels = ArrayList<ChapterAdapter.ChapterModel>()
        chapterModels.add(ChapterAdapter.ChapterModel(1, "The Basics", "abc_white", "abc_silver", 2))
        chapterModels.add(ChapterAdapter.ChapterModel(2, "Working with Text", "text_white", "text_silver", 2))
        chapterModels.add(ChapterAdapter.ChapterModel(3, "Properties", "properties_white", "properties_silver", 2))
        chapterModels.add(ChapterAdapter.ChapterModel(4, "Positioning and Layout", "layout_white", "layout_silver", 2))
        chapterModels.add(ChapterAdapter.ChapterModel(5, "CSS3 Basics", "css3_white", "css3_silver", 1, moduleModels))
        val moduleModelsCopy = moduleModels.clone() as ArrayList<ModuleAdapter.ModuleModel>
        for (i in 1..5) {
            moduleModelsCopy.removeAt(7)
        }
        chapterModels.add(ChapterAdapter.ChapterModel(6, "Gradients & Backgrounds", "image_white", "image_silver", 0, moduleModelsCopy))
        chapterModels.add(ChapterAdapter.ChapterModel(6, "Transitions & Transforms", "transform_white", "transform_silver", 0, moduleModelsCopy))
        listChapters.adapter = ChapterAdapter(this, chapterModels)
    }

    override fun onStart() {
        super.onStart()

        database.getLesson(intent.getStringExtra("key_id"), {
            val firebaseAdapter = object : FirebaseRecyclerAdapter<Database.Chapter, ChapterViewHolder>(
                    Database.Chapter::class.java,
                    R.layout.item_chapter,
                    ChapterViewHolder::class.java,
                    database.databaseChapters
            ) {
                override fun populateViewHolder(viewHolder: ChapterViewHolder?, model: Database.Chapter?, position: Int) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            }
            listChapters.adapter = firebaseAdapter
        })

    }
}
