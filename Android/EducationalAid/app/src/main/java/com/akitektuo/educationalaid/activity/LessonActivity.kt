package com.akitektuo.educationalaid.activity

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.akitektuo.educationalaid.R
import com.akitektuo.educationalaid.adapter.ChapterAdapter
import com.akitektuo.educationalaid.adapter.ModuleAdapter
import com.akitektuo.educationalaid.fragment.SettingsFragment
import kotlinx.android.synthetic.main.activity_lesson.*

class LessonActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(SettingsFragment.Language(newBase!!).wrap())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson)
        buttonBack.setOnClickListener { finish() }

        Toast.makeText(this, "Intent with id ${intent.getIntExtra("key_id", 0)}", Toast.LENGTH_SHORT).show()
        textLessonName.text = "CSS"
        listChapters.layoutManager = LinearLayoutManager(this)
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
}
