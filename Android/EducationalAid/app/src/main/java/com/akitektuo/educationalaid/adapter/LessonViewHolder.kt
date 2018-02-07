package com.akitektuo.educationalaid.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.akitektuo.educationalaid.R
import com.akitektuo.educationalaid.util.Tool.Companion.load
import com.squareup.picasso.Picasso

/**
 * Created by Akitektuo on 06.02.2018.
 */

class LessonViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    data class Lesson(val context: Context, val title: String, val image: String, val progress: Int, val total: Int, val onClick: () -> Unit)

    private val textTitle = view.findViewById<TextView>(R.id.textTitle)
    private val imageLesson = view.findViewById<ImageView>(R.id.imageLesson)
    private val progressCount = view.findViewById<ProgressBar>(R.id.progressCount)
    private val textProgress = view.findViewById<TextView>(R.id.textProgress)

    fun bind(lesson: Lesson) = with(lesson) {
        textTitle.text = title
        Picasso.with(context).load(image).into(imageLesson)
        load(context, image, imageLesson)
        progressCount.progress = progress
        progressCount.max = total
        textProgress.text = context.getString(R.string.percent, progress * 100 / total)
        view.setOnClickListener {
            onClick()
        }

    }

}