package com.doit.test.ui.task.list

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.doit.test.TestApp
import com.doit.test.domain.layer.TaskInteractor
import com.doit.test.domain.layer.model.Task
import com.doit.test.ui.abstractions.Presenter
import com.doit.test.ui.task.list.abstractions.TaskListView
import io.reactivex.CompletableSource
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class TaskListPresenter(view: TaskListView) : Presenter<TaskListView>(view) {
    private var alphabetComparator: Comparator<in Task> = Comparator { f, s -> f.title!!.compareTo(s.title!!) }
    private var dateComparator: Comparator<in Task> = Comparator { f, s -> f.date.compareTo(s.date) }
    private var priorityComparator: Comparator<in Task> =
        Comparator { f, s -> f.priority.equivalent - s.priority.equivalent }

    @Suppress("ProtectedInFinal")
    @Inject
    protected lateinit var interactor: TaskInteractor

    init {
        TestApp.domainComponent.inject(this)
        this.resumedDisposables?.add(this.interactor.observeTaskRemoved().subscribe { view.decrement() })
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() = this.interactor.get(true).subscribe(TasksObserver())

    override fun onResume() {
        super.onResume()
        this.view.setUpTasksListMenu(false)
        this.interactor.get(false).subscribe(TasksObserver())
        this.resumedDisposables?.addAll(
            this.interactor.observeError()
                .flatMapSingle { this.interactor.get(false) }
                .flatMapCompletable { t -> CompletableSource { view.update(t.size) } }
                .subscribe(),
            this.interactor.observeNewTask().subscribe { view.increment() },
            this.interactor.observeTaskRemoved().subscribe { view.decrement() }
        )
    }

    fun onCreateTaskCommand() {
        this.view.collapseMenu()
        this.interactor.prefsCommandSubject.onNext(Unit)
    }

    fun onSortCommand() = this.view.setUpSortingMenu(true)

    fun onSortByNameCommand() {
        this.view.setUpTasksListMenu(false)
        this.alphabetComparator = this.alphabetComparator.reversed()
        this.interactor.get(false, this.alphabetComparator).subscribe(TasksObserver())
    }

    fun onSortByPriorityCommand() {
        this.view.setUpTasksListMenu(false)
        this.priorityComparator = this.priorityComparator.reversed()
        this.interactor.get(false, this.priorityComparator).subscribe(TasksObserver())
    }

    fun onSortByDateCommand() {
        this.view.setUpTasksListMenu(false)
        this.dateComparator = this.dateComparator.reversed()
        this.interactor.get(false, this.dateComparator).subscribe(TasksObserver())
    }

    fun onUpdateCommand() {
        this.view.collapseMenu()
        this.interactor.get(true).subscribe(TasksObserver())
    }

    inner class TasksObserver : SingleObserver<MutableList<Task>> {
        override fun onSubscribe(d: Disposable) {}
        override fun onSuccess(t: MutableList<Task>) = view.update(t.size)
        override fun onError(e: Throwable) {}
    }

}
