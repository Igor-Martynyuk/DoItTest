package com.doit.test.ui.task.details.abstractions

import androidx.lifecycle.LifecycleOwner

interface TaskDetailsView : LifecycleOwner{
    fun setDay(value: String)
    fun setMonth(value: String)
    fun setTime(value: String)
    fun setTitle(value: String)
    fun setPriority(res: Int)
    fun setNotificationEnabled(res: Int)
}