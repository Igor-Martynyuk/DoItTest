package com.doit.test.ui.navigator.abstractions

import androidx.lifecycle.LifecycleOwner
import com.doit.test.core.layer.utils.navigation.abstractions.Container

interface NavigatorView : Container, LifecycleOwner{
    fun showProgress()
    fun hideProgress()
    fun notifyError(res: Int)
}