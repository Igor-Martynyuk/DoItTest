package com.doit.test.domain.di.abstractions

import com.doit.test.domain.di.DomainModule
import com.doit.test.domain.di.annotations.DomainScope
import com.doit.test.ui.auth.AuthPresenter
import com.doit.test.ui.navigator.NavigatorPresenter
import com.doit.test.ui.task.cell.TaskCellPresenter
import com.doit.test.ui.task.details.TaskDetailsPresenter
import com.doit.test.ui.task.list.TaskListPresenter
import com.doit.test.ui.task.prefs.TaskPrefsPresenter
import dagger.Subcomponent

@DomainScope
@Subcomponent(modules = [DomainModule::class])
interface DomainComponent {
    fun inject(presenter: NavigatorPresenter)
    fun inject(presenter: AuthPresenter)
    fun inject(presenter: TaskListPresenter)
    fun inject(presenter: TaskCellPresenter)
    fun inject(presenter: TaskPrefsPresenter)
    fun inject(presenter: TaskDetailsPresenter)
}