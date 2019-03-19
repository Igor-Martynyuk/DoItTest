package com.doit.test.core.layer.utils.navigation.abstractions

import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner

interface Container {
    fun getActualFragmentManager() : FragmentManager
    fun getParentFor(target : Class<out LifecycleOwner>) : ViewGroup
}