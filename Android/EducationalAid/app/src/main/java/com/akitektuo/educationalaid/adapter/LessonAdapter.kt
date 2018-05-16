package com.akitektuo.educationalaid.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.akitektuo.educationalaid.R

class LessonAdapter : RecyclerView.Adapter<LessonViewHolder>() {

    private val lessons = ArrayList<LessonViewHolder.Lesson>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder = LessonViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_lesson, parent, false))

    override fun getItemCount(): Int = lessons.size

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) = holder.bind(lessons[position])

    fun add(lesson: LessonViewHolder.Lesson) {
        val position = lessons.size
        lessons.add(lesson)
        notifyItemInserted(position)
    }

}