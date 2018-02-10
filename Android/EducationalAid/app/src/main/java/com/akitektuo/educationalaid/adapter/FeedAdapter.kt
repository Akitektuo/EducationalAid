package com.akitektuo.educationalaid.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.akitektuo.educationalaid.R

/**
 * Created by AoD Akitektuo on 10-Feb-18 at 20:52.
 */

class FeedAdapter(private val actions: ArrayList<Action>) : RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_feed, parent, false))

    override fun getItemCount(): Int = actions.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(actions[position])

    data class Action(val type: Int, var message: String, val date: String)

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val imageType = view.findViewById<ImageView>(R.id.imageType)
        private val textMessage = view.findViewById<TextView>(R.id.textMessage)
        private val textDate = view.findViewById<TextView>(R.id.textDate)

        fun bind(action: Action) = with(action) {
            when (type) {
                0 -> imageType.setImageResource(R.drawable.join)
                1 -> imageType.setImageResource(R.drawable.follow_feed)
                2 -> imageType.setImageResource(R.drawable.unfollow_feed)
                3 -> imageType.setImageResource(R.drawable.lesson_created)
                4 -> imageType.setImageResource(R.drawable.lesson_started)
                5 -> imageType.setImageResource(R.drawable.lesson_completed)
                6 -> imageType.setImageResource(R.drawable.level_up)
            }
            textMessage.text = message
            textDate.text = date
        }

    }

}