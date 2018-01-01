package com.akitektuo.educationalaid.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.akitektuo.educationalaid.R
import com.akitektuo.educationalaid.adapter.ChapterAdapter
import kotlinx.android.synthetic.main.fragment_learn.*

/**
 * Created by Akitektuo on 31.12.2017.
 */
class LearnFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_learn, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listChapters.layoutManager = LinearLayoutManager(context)
        val chapterModels = ArrayList<ChapterAdapter.ChapterModel>()
        chapterModels.add(ChapterAdapter.ChapterModel(1, "The Basics", status = 2))
        chapterModels.add(ChapterAdapter.ChapterModel(2, "Working with Text", status = 2))
        chapterModels.add(ChapterAdapter.ChapterModel(3, "Properties", status = 2))
        chapterModels.add(ChapterAdapter.ChapterModel(4, "Positioning and Layout", status = 2))
        chapterModels.add(ChapterAdapter.ChapterModel(5, "CSS3 Basics", status = 1))
        chapterModels.add(ChapterAdapter.ChapterModel(6, "Gradients & Backgrounds", status = 0))
        listChapters.adapter = ChapterAdapter(context, chapterModels)
    }

}