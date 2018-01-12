package com.akitektuo.educationalaid.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.akitektuo.educationalaid.R
import com.akitektuo.educationalaid.activity.LessonActivity
import kotlinx.android.synthetic.main.item_lesson.view.*

/**
 * Created by Akitektuo on 31.12.2017.
 */
class LessonAdapter(private val context: Context, private val lessons: ArrayList<LessonModel>) : RecyclerView.Adapter<LessonAdapter.ViewHolder>() {

    data class LessonModel(val id: Int, val name: String, val image: String, val chapters: ArrayList<ChapterAdapter.ChapterModel> = ArrayList())

    override fun onBindViewHolder(holder: LessonAdapter.ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.bind(lessons[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): LessonAdapter.ViewHolder = ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_lesson, parent, false))

    override fun getItemCount(): Int = lessons.size

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        fun bind(lesson: LessonModel) = with(itemView) {
            cardLesson.setOnClickListener {
                val intent = Intent(context, LessonActivity::class.java)
                intent.putExtra("key_id", lesson.id)
                context.startActivity(intent)
            }
            textTitle.text = lesson.name
            imageLesson.setImageResource(resources.getIdentifier(lesson.image, "drawable", context.packageName))
            val total = lesson.chapters.sumBy { it.modules.size }
            val count = lesson.chapters.sumBy { it.modules.count { it.status == 2 } }
            progressCount.progress = count
            progressCount.max = total
            textProgress.text = context.getString(R.string.percent, count * 100 / total)
        }

    }

}