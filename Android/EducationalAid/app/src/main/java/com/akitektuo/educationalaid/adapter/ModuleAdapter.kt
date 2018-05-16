package com.akitektuo.educationalaid.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.akitektuo.educationalaid.R

class ModuleAdapter : RecyclerView.Adapter<ModuleViewHolder>() {

    private val modules = ArrayList<ModuleViewHolder.Module>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder = ModuleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_module, parent, false))

    override fun getItemCount(): Int = modules.size

    override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) = holder.bind(modules[position])

    fun add(module: ModuleViewHolder.Module) {
        val position = modules.size
        modules.add(module)
        notifyItemInserted(position)
    }

}