package com.doit.test.ui.abstractions

import androidx.recyclerview.widget.RecyclerView

abstract class MvpRecyclerAdapter<V> : RecyclerView.Adapter<V>() where V : MvpRecyclerHolder {

    private lateinit var view: RecyclerView
    var count = 0
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onAttachedToRecyclerView(view: RecyclerView) {
        super.onAttachedToRecyclerView(view)
        this.view = view
    }

    override fun onBindViewHolder(holder: V, index: Int) {
        holder.onBind(index)
        this.view.addOnChildAttachStateChangeListener(holder)
    }

    override fun getItemCount(): Int = this.count

}