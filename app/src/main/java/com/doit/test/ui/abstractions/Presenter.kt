package com.doit.test.ui.abstractions

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.CompositeDisposable

@Suppress("MemberVisibilityCanBePrivate")
abstract class Presenter<T>(view: T) : LifecycleObserver where T : LifecycleOwner {
    protected var resumedDisposables: CompositeDisposable? = null

    protected var view: T = view
        private set

    init {
        this.view.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    protected open fun onResume() {
        this.resumedDisposables = CompositeDisposable()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    protected open fun onPause() {
        this.resumedDisposables?.dispose()
    }

}