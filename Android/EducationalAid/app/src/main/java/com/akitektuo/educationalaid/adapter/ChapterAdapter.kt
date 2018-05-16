package com.akitektuo.educationalaid.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.akitektuo.educationalaid.R

class ChapterAdapter : RecyclerView.Adapter<ChapterViewHolder>() {

    private val chapters = ArrayList<ChapterViewHolder.Chapter>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder = ChapterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_chapter, parent, false))

    override fun getItemCount(): Int = chapters.size

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) = holder.bind(chapters[position])

    fun add(chapter: ChapterViewHolder.Chapter) {
        val position = chapters.size
        chapters.add(chapter)
        notifyItemInserted(position)
    }

}