package com.doit.test.ui.task.list.abstractions

import androidx.lifecycle.LifecycleOwner

interface TaskListView : LifecycleOwner {
    fun update(count: Int)
    fun increment()
    fun decrement()
    fun collapseMenu()
    fun setUpTasksListMenu(openOnComplete: Boolean)
    fun setUpSortingMenu(openOnComplete: Boolean)
    fun updateMenu(res: Array<Int>)
}