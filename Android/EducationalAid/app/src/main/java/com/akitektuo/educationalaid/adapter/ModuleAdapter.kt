package com.akitektuo.educationalaid.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.akitektuo.educationalaid.R
import com.akitektuo.educationalaid.activity.ModuleActivity
import kotlinx.android.synthetic.main.item_module.view.*

/**
 * Created by Akitektuo on 31.12.2017.
 */
class ModuleAdapter(private val context: Context, private val modules: ArrayList<ModuleModel>) : RecyclerView.Adapter<ModuleAdapter.ViewHolder>() {

    data class ModuleModel(val id: Int, val number: Int, val name: String, val questions: Int, val status: Int)

    override fun onBindViewHolder(holder: ModuleAdapter.ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.bind(modules, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ModuleAdapter.ViewHolder = ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_module, parent, false))

    override fun getItemCount(): Int = modules.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(modules: ArrayList<ModuleModel>, pos: Int) = with(itemView) {
            val module = modules[pos]
            textCount.text = context.getString(R.string.out_of, module.number, modules.size)
            textTitle.text = module.name
            textQuestions.text = context.getString(R.string.question)
            if (module.questions > 1) {
                textQuestions.text = context.getString(R.string.questions, module.questions)
            }
            if (module.status != 0) {
                cardModule.setOnClickListener {
                    val intent = Intent(context, ModuleActivity::class.java)
                    intent.putExtra("key_id", module.id)
                    context.startActivity(intent)
                }
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
        }

    }

}