package com.doit.test.ui.task.prefs.abstractions

import androidx.lifecycle.LifecycleOwner
import java.util.*

interface TaskPrefsView : LifecycleOwner {
    fun setMode(mode: Mode)
    fun enableAction()
    fun disableAction()

    fun setTitle(value: String)
    fun setDate(date: Date, dateStr: String)
    fun setTime(time: Date, dateStr: String)
    fun setHighPriority()
    fun setNormalPriority()
    fun setLowPriority()

    fun notifyInvalidTitle(res: Int)
    fun notifyInvalidDate(res: Int)
    fun notifyInvalidTime(res: Int)

    enum class Mode {
        ADD,
        UPDATE
    }

}