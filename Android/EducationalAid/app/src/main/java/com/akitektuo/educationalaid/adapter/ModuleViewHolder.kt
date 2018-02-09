package com.akitektuo.educationalaid.adapter

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import com.akitektuo.educationalaid.R

/**
 * Created by Akitektuo on 07.02.2018.
 */
class ModuleViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    private val textCount = view.findViewById<TextView>(R.id.textCount)
    private val textTitle = view.findViewById<TextView>(R.id.textTitle)
    private val textQuestions = view.findViewById<TextView>(R.id.textQuestions)
    private val imageStatus = view.findViewById<ImageView>(R.id.imageStatus)
    private val cardModule = view.findViewById<CardView>(R.id.cardModule)

    data class Module(val context: Context, val position: Int, val total: Int, val name: String, val questions: Int, val status: Int, val onClick: () -> Unit)

    fun makeGone() {
        view.visibility = GONE
    }

    fun bind(module: Module) = with(module) {
        textCount.text = context.getString(R.string.out_of, position, total)
        textTitle.text = name
        textQuestions.text = context.getString(R.string.question)
        if (questions > 1) {
            textQuestions.text = context.getString(R.string.questions, questions)
        }
        when (module.status) {
            0 -> {
                cardModule.setCardBackgroundColor(context.resources.getColor(com.akitektuo.educationalaid.R.color.colorSilverLight))
                textQuestions.setBackgroundColor(context.resources.getColor(com.akitektuo.educationalaid.R.color.colorSilverLight))
                textQuestions.setTextColor(context.resources.getColor(com.akitektuo.educationalaid.R.color.colorSilver))
                textQuestions.setTypeface(textQuestions.typeface, Typeface.BOLD)
                imageStatus.setImageDrawable(context.resources.getDrawable(com.akitektuo.educationalaid.R.drawable.lock))
            }
            1 -> {
                cardModule.setCardBackgroundColor(context.resources.getColor(com.akitektuo.educationalaid.R.color.white))
                textQuestions.setBackgroundColor(context.resources.getColor(com.akitektuo.educationalaid.R.color.colorSilverDark))
                textQuestions.setTextColor(context.resources.getColor(com.akitektuo.educationalaid.R.color.white))
                textQuestions.setTypeface(textQuestions.typeface, Typeface.NORMAL)
                imageStatus.setImageDrawable(context.resources.getDrawable(com.akitektuo.educationalaid.R.drawable.play))
            }
            2 -> {
                cardModule.setCardBackgroundColor(context.resources.getColor(com.akitektuo.educationalaid.R.color.white))
                textQuestions.setBackgroundColor(context.resources.getColor(com.akitektuo.educationalaid.R.color.colorAccent))
                textQuestions.setTextColor(context.resources.getColor(com.akitektuo.educationalaid.R.color.white))
                textQuestions.setTypeface(textQuestions.typeface, Typeface.NORMAL)
                imageStatus.setImageDrawable(context.resources.getDrawable(com.akitektuo.educationalaid.R.drawable.check))
            }
        }
        if (status != 0) {
            view.setOnClickListener {
                onClick()
            }
        }
        view.visibility = VISIBLE
    }

}