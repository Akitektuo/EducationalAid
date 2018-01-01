package com.akitektuo.educationalaid.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.akitektuo.educationalaid.R
import com.akitektuo.educationalaid.activity.ChapterActivity
import kotlinx.android.synthetic.main.item_chapter.view.*

/**
 * Created by Akitektuo on 31.12.2017.
 */
class ChapterAdapter(private val context: Context, private val chapters: ArrayList<ChapterModel>) : RecyclerView.Adapter<ChapterAdapter.ViewHolder>() {

    data class ChapterModel(val id: Int, val name: String, val image: String = "", val imageLocked: String = "", val status: Int)

    override fun onBindViewHolder(holder: ChapterAdapter.ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.bind(chapters[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ChapterAdapter.ViewHolder = ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_chapter, parent, false))

    override fun getItemCount(): Int = chapters.size

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        fun bind(chapter: ChapterModel) = with(itemView) {
            imageChapter.setImageDrawable(context.resources.getDrawable(R.drawable.learn))
            textChapter.text = chapter.name
            layoutChapter.setOnClickListener {
                val intent = Intent(context, ChapterActivity::class.java)
                intent.putExtra("key_id", chapter.id)
                context.startActivity(intent)
            }
        }

    }

}