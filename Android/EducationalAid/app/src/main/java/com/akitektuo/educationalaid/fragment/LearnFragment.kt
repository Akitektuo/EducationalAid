package com.akitektuo.educationalaid.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.akitektuo.educationalaid.R
import com.akitektuo.educationalaid.adapter.ChapterAdapter
import com.akitektuo.educationalaid.adapter.LessonAdapter
import com.akitektuo.educationalaid.adapter.ModuleAdapter
import kotlinx.android.synthetic.main.fragment_learn.*

/**
 * Created by Akitektuo on 31.12.2017.
 */

class LearnFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_learn, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        listLessons.layoutManager = LinearLayoutManager(context)
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
        val lessonModels = ArrayList<LessonAdapter.LessonModel>()
        lessonModels.add(LessonAdapter.LessonModel(1, "CSS", "css", chapterModels))
        listLessons.adapter = LessonAdapter(context, lessonModels)
    }

}