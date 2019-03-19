package com.doit.test.ui.abstractions

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView

abstract class MvpRecyclerHolder(view: View) : RecyclerView.ViewHolder(view),
    RecyclerView.OnChildAttachStateChangeListener,
    LifecycleOwner {

    private var lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
    protected val context = view.context!!

    init {
        this.lifecycleRegistry.markState(Lifecycle.State.CREATED)
    }

    abstract fun onBind(index: Int)

    override fun onChildViewAttachedToWindow(view: View) {
        when (adapterPosition) {
            RecyclerView.NO_POSITION -> this.lifecycleRegistry.markState(Lifecycle.State.CREATED)
            else -> this.lifecycleRegistry.markState(Lifecycle.State.RESUMED)
        }
    }

    override fun onChildViewDetachedFromWindow(view: View) {
        this.lifecycleRegistry.markState(Lifecycle.State.CREATED)
    }

    override fun getLifecycle(): Lifecycle {
        return this.lifecycleRegistry
    }

}