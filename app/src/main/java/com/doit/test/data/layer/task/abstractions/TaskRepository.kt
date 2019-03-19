package com.doit.test.data.layer.task.abstractions

import com.doit.test.data.layer.task.model.response.TaskResponse
import com.doit.test.data.layer.task.model.response.TaskSingleResponse
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface TaskRepository {

    fun get(shouldUpdate: Boolean): Observable<TaskResponse>
    fun add(title: String, dueBy: Int, priority: String?): Single<TaskResponse>
    fun update(id: Int, title: String, dueBy: Int, priority: String?): Completable
    fun remove(id: Int): Completable

}