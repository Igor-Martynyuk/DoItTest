package com.doit.test.ui.task.prefs

import com.doit.test.TestApp
import com.doit.test.domain.layer.TaskInteractor
import com.doit.test.domain.layer.model.Task
import com.doit.test.ui.abstractions.Presenter
import com.doit.test.ui.task.prefs.abstractions.TaskPrefsView
import java.text.DateFormat
import java.util.*
import javax.inject.Inject

class TaskPrefsPresenter(view: TaskPrefsView) : Presenter<TaskPrefsView>(view) {
    var task: Task = Task(null, null, Calendar.getInstance(), Task.Priority.NONE)
    var apply: () -> Unit = {}

    @Suppress("ProtectedInFinal")
    @Inject
    protected lateinit var interactor: TaskInteractor

    init {
        TestApp.domainComponent.inject(this)
    }

    private fun validate() {
        if (!this.task.title.isNullOrEmpty()
            && this.task.date.isSet(Calendar.YEAR)
            && this.task.date.isSet(Calendar.MONTH)
            && this.task.date.isSet(Calendar.DAY_OF_MONTH)
            && this.task.date.isSet(Calendar.HOUR_OF_DAY)
            && this.task.date.isSet(Calendar.MINUTE)
        ) this.view.enableAction()
        else this.view.disableAction()
    }

    override fun onResume() {
        super.onResume()

        val selection = this.interactor.selectionSubject.firstOrError().blockingGet()
        if (selection.isPresent) {

            this.view.setMode(TaskPrefsView.Mode.UPDATE)
            this.task = selection.get().copy()
            this.view.setTitle(this.task.title!!)

            this.view.setDate(
                this.task.date.time, DateFormat.getDateInstance(DateFormat.SHORT).format(Date(task.date.timeInMillis))
            )

            this.view.setTime(
                this.task.date.time,
                DateFormat.getDateInstance(DateFormat.SHORT).format(Date(task.date.timeInMillis))
            )

            if (this.task.priority == Task.Priority.High) this.view.setHighPriority()
            if (this.task.priority == Task.Priority.Normal) this.view.setNormalPriority()
            if (this.task.priority == Task.Priority.Low) this.view.setLowPriority()

            this.apply = { this.interactor.update(this.task).subscribe() }

        } else {

            this.task.date.clear()
            this.view.setMode(TaskPrefsView.Mode.ADD)
            this.apply = { this.interactor.add(this.task).subscribe() }

        }

        this.view.disableAction()
    }

    fun onTitleEntered(value: String) {
        this.task.title = value
        this.validate()
    }

    fun onDateEntered(value: Calendar) {
        this.task.date.set(value.get(Calendar.YEAR), value.get(Calendar.MONTH), value.get(Calendar.DAY_OF_MONTH))
        this.validate()
    }

    fun onTimeEntered(value: Calendar) {
        this.task.date.add(Calendar.HOUR_OF_DAY, value.get(Calendar.HOUR_OF_DAY))
        this.task.date.add(Calendar.MINUTE, value.get(Calendar.MINUTE))
        this.validate()
    }

    fun onPriorityChecked(value: Task.Priority) {
        this.task.priority = value
        this.validate()
    }

    fun onAddTaskCommand() = this.apply.invoke()

}