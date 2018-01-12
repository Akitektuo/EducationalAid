package com.akitektuo.educationalaid.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.akitektuo.educationalaid.R
import com.akitektuo.educationalaid.adapter.ModuleAdapter
import kotlinx.android.synthetic.main.activity_chapter.*

class ChapterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chapter)
        buttonBack.setOnClickListener { finish() }

        Toast.makeText(this, "Intent with id ${intent.getIntExtra("key_id", 0)}", Toast.LENGTH_SHORT).show()
        textChapterName.text = "CSS3 Basics"
        listModules.layoutManager = LinearLayoutManager(this)
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
        listModules.adapter = ModuleAdapter(this, moduleModels)
    }
}
