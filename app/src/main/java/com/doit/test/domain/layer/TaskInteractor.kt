package com.doit.test.domain.layer

import com.doit.test.TestApp
import com.doit.test.data.layer.task.abstractions.TaskRepository
import com.doit.test.data.layer.task.model.response.TaskResponse
import com.doit.test.domain.layer.model.Task
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.Comparator

class TaskInteractor {
    private val inProgressSubject = BehaviorSubject.createDefault<Boolean>(false)
    private val errorSubject = PublishSubject.create<Throwable>()
    private val newTaskSubject = PublishSubject.create<Task>()
    private val taskRemovedSubject = PublishSubject.create<Task>()
    private var comparator: Comparator<in Task> = Comparator { _, _ -> 0 }

    @Suppress("ProtectedInFinal")
    @Inject
    protected lateinit var repository: TaskRepository

    val prefsCommandSubject = PublishSubject.create<Unit>()
    val selectionSubject: BehaviorSubject<Optional<Task>> = BehaviorSubject.createDefault(Optional.empty())

    init {
        TestApp.dataComponent.inject(this)
    }

    private fun convert(response: TaskResponse): Task {
        val due = Calendar.getInstance()
        due.timeInMillis = TimeUnit.MILLISECONDS.convert(response.dueBy.toLong(), TimeUnit.SECONDS)

        return Task(
            response.id.toString(),
            response.title,
            due,
            if (response.priority == null) Task.Priority.NONE else Task.Priority.valueOf(response.priority!!)
        )
    }

    fun get(shouldReload: Boolean) = this.get(shouldReload, this.comparator)

    fun get(shouldReload: Boolean, comparator: Comparator<in Task>) = this.repository
        .get(shouldReload)
        .map { convert(it) }
        .sorted(comparator)
        .toList()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe {
            this.comparator = comparator
            this.inProgressSubject.onNext(shouldReload)
        }
        .doOnSuccess { this.inProgressSubject.onNext(false) }
        .doOnError { this.errorSubject.onNext(it) }

    fun add(task: Task) = this.repository.add(
        task.title!!,
        TimeUnit.SECONDS.convert(task.date.timeInMillis, TimeUnit.MILLISECONDS).toInt(),
        if (task.priority == Task.Priority.NONE) null else task.priority.name
    )
        .map { convert(it) }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe { this.inProgressSubject.onNext(true) }
        .doOnError { this.errorSubject.onNext(it) }
        .doOnSuccess {
            this.inProgressSubject.onNext(false)
            this.newTaskSubject.onNext(task)
        }

    fun update(task: Task) = this.repository.update(
        task.id!!.toInt(),
        task.title!!,
        TimeUnit.SECONDS.convert(task.date.timeInMillis, TimeUnit.MILLISECONDS).toInt(),
        if (task.priority == Task.Priority.NONE) null else task.priority.name
    )
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe { this.inProgressSubject.onNext(true) }
        .doOnError { this.errorSubject.onNext(it) }
        .doOnComplete() {
            this.inProgressSubject.onNext(false)
            this.newTaskSubject.onNext(task)
        }!!

    fun remove(task: Task) = this.repository.remove(task.id!!.toInt())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe { this.inProgressSubject.onNext(true) }
        .doOnComplete {
            this.inProgressSubject.onNext(false)
            this.taskRemovedSubject.onNext(task)
        }
        .doOnError {
            this.inProgressSubject.onNext(true)
            this.errorSubject.onNext(it)
        }!!

    fun observeProgress(): Observable<Boolean> = inProgressSubject.observeOn(AndroidSchedulers.mainThread())
    fun observeError(): Observable<Throwable> = errorSubject.observeOn(AndroidSchedulers.mainThread())
    fun observeNewTask(): Observable<Task> = newTaskSubject.observeOn(AndroidSchedulers.mainThread())
    fun observeTaskRemoved(): Observable<Task> = taskRemovedSubject.observeOn(AndroidSchedulers.mainThread())

}