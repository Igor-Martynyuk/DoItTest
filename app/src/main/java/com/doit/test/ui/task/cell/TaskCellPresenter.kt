package com.doit.test.ui.task.cell

import androidx.recyclerview.widget.RecyclerView
import com.doit.test.R
import com.doit.test.TestApp
import com.doit.test.domain.layer.TaskInteractor
import com.doit.test.domain.layer.model.Task
import com.doit.test.ui.abstractions.Presenter
import com.doit.test.ui.task.cell.abstractions.TaskCellView
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import java.text.DateFormat
import java.util.*
import javax.inject.Inject

@Suppress("ProtectedInFinal")
class TaskCellPresenter(view: TaskCellView) : Presenter<TaskCellView>(view) {
    private lateinit var task: Task

    @Inject
    protected lateinit var interactor: TaskInteractor
    var index = RecyclerView.NO_POSITION

    init {
        TestApp.domainComponent.inject(this)
    }

    override fun onResume() {
        if (index == RecyclerView.NO_POSITION)
            return

        this.interactor.get(false).subscribe(
            object : SingleObserver<MutableList<Task>> {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
                override fun onSuccess(t: MutableList<Task>) {
                    task = t[index]

                    view.updateTitle(task.title!!)
                    view.updateDue(DateFormat.getDateInstance(DateFormat.SHORT).format(Date(task.date.timeInMillis)))

                    if (task.priority == Task.Priority.High) view.updatePriority(R.string.high, android.R.color.holo_red_light)
                    if (task.priority == Task.Priority.Normal) view.updatePriority(R.string.normal, R.color.colorAccent)
                    if (task.priority == Task.Priority.Low) view.updatePriority(R.string.low, R.color.colorPrimary)
                }
            }
        )
    }

    fun onRemoveCommand() {
        this.interactor.remove(this.task).subscribeBy({}, { })
    }

    fun onSelectCommand() {
        this.interactor.selectionSubject.onNext(Optional.of(this.task))
    }

}