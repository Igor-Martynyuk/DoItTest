package com.doit.test.ui.task.details

import android.annotation.SuppressLint
import com.doit.test.TestApp
import com.doit.test.domain.layer.TaskInteractor
import com.doit.test.ui.abstractions.Presenter
import com.doit.test.ui.task.details.abstractions.TaskDetailsView
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class TaskDetailsPresenter(view: TaskDetailsView) : Presenter<TaskDetailsView>(view) {

    @Suppress("ProtectedInFinal")
    @Inject
    protected lateinit var interactor: TaskInteractor

    init {
        TestApp.domainComponent.inject(this)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onResume() {
        super.onResume()

        val task = this.interactor.selectionSubject.firstOrError().blockingGet().get()

        this.view.setTitle(task.title!!)
        this.view.setDay(SimpleDateFormat("MMMM").format(task.date.time))
        this.view.setMonth(DateFormat.getDateInstance(DateFormat.SHORT).format(Date(task.date.timeInMillis)))
        this.view.setTime(DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(task.date.timeInMillis)))
        this.view.setPriority(task.priority.nameId)

    }

    fun onEnableNotificationCommand() {
        this.interactor.prefsCommandSubject.onNext(Unit)
    }

}