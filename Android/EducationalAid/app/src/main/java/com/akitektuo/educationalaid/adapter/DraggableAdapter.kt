package com.akitektuo.educationalaid.adapter

import android.content.Context
import android.support.v4.view.MotionEventCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.akitektuo.educationalaid.R
import com.akitektuo.educationalaid.fragment.QuestionFragment
import com.akitektuo.educationalaid.util.ItemTouchHelperCallback
import kotlinx.android.synthetic.main.item_draggable.view.*
import java.util.*

/**
 * Created by Akitektuo on 05.01.2018.
 */
class DraggableAdapter(private val context: Context, private val dragTexts: ArrayList<QuestionFragment.Draggable>, private val dragStartListener: OnStartDragListener) : RecyclerView.Adapter<DraggableAdapter.ViewHolder>(), ItemTouchHelperCallback.ItemTouchHelperAdapter {

    override fun onBindViewHolder(holder: DraggableAdapter.ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.bind(dragTexts[position].text, dragStartListener, holder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DraggableAdapter.ViewHolder = ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_draggable, parent, false))

    override fun getItemCount(): Int = dragTexts.size

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        fun bind(text: String, listener: OnStartDragListener, viewHolder: DraggableAdapter.ViewHolder) {
            with(itemView) {
                textItemDraggable.text = text
                cardDraggable.setOnTouchListener(View.OnTouchListener({ _, event ->
                    if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                        listener.onStartDrag(viewHolder)
                    }
                    return@OnTouchListener false
                }))
            }
        }

    }

    interface OnStartDragListener {

        fun onStartDrag(viewHolder: RecyclerView.ViewHolder)

    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(dragTexts, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onItemDismiss(position: Int) {
    }

}