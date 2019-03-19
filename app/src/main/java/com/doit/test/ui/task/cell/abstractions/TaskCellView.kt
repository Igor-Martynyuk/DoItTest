package com.doit.test.ui.task.cell.abstractions

import androidx.lifecycle.LifecycleOwner

interface TaskCellView : LifecycleOwner {
    fun updateTitle(value: String)
    fun updateDue(value: String)
    fun updatePriority(resText: Int, resColor: Int)
}