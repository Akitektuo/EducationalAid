package com.akitektuo.educationalaid.adapter

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.akitektuo.educationalaid.R
import com.squareup.picasso.Picasso

/**
 * Created by Akitektuo on 06.02.2018.
 */
class ChapterViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    private val textChapter = view.findViewById<TextView>(R.id.textChapter)
    private val textCount = view.findViewById<TextView>(R.id.textCount)
    private val progressCount = view.findViewById<ProgressBar>(R.id.progressCount)
    private val imageChapter = view.findViewById<ImageView>(R.id.imageChapter)

    data class Chapter(val context: Context, val name: String, val status: Int, val count: Int, val total: Int, val image: String, val imageLocked: String, val onClick: () -> Unit)

    fun bind(chapter: Chapter) = with(chapter) {
        textChapter.text = name
        if (status != 0) {
            view.setOnClickListener { onClick }
        }
        val drawableBackground = context.resources.getDrawable(R.drawable.circle) as GradientDrawable
        when (status) {
            0 -> {
                drawableBackground.setColor(context.resources.getColor(R.color.colorSilverLight))
                textCount.text = context.getString(R.string.out_of, 0, total)
                progressCount.visibility = View.GONE
                Picasso.with(context).load(imageLocked).into(imageChapter)
            }
            1 -> {
                drawableBackground.setColor(context.resources.getColor(R.color.colorSilverDark))
                textCount.text = context.getString(R.string.out_of, count, total)
                progressCount.progress = count
                progressCount.max = total
                Picasso.with(context).load(image).into(imageChapter)
            }
            2 -> {
                drawableBackground.setColor(context.resources.getColor(R.color.colorAccent))
                textCount.visibility = View.GONE
                progressCount.visibility = View.GONE
                Picasso.with(context).load(image).into(imageChapter)
            }
        }
    }

}