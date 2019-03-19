package com.doit.test.data.layer.task

import com.doit.test.TestApp
import com.doit.test.data.layer.task.abstractions.TaskRepository
import com.doit.test.data.layer.task.model.request.TaskRequest
import com.doit.test.data.layer.task.model.response.TaskListResponse
import com.doit.test.data.layer.task.model.response.TaskResponse
import com.doit.test.data.layer.task.model.response.TaskSingleResponse
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.*
import javax.inject.Inject

private const val routeAll = "tasks"
private const val routeSingle = "tasks/{task}"

@Suppress("ProtectedInFinal")
class DoItTaskRepository : TaskRepository {

    private val api: Api
    private var data: MutableList<TaskResponse> = mutableListOf()

    @Inject
    protected lateinit var clientBuilder: OkHttpClient.Builder
    @Inject
    protected lateinit var retrofitBuilder: Retrofit.Builder

    private interface Api {
        @GET(routeAll)
        fun load(): Single<TaskListResponse>

        @POST(routeAll)
        fun create(@Body body: TaskRequest): Single<TaskSingleResponse>

        @PUT(routeSingle)
        fun update(@Path(value = "task") task: Int, @Body body: TaskRequest): Completable

        @DELETE(routeSingle)
        fun remove(@Path(value = "task") task: Int): Completable
    }

    init {
        TestApp.coreComponent.inject(this)
        this.api = retrofitBuilder.client(this.clientBuilder.build()).build().create(Api::class.java)
    }

    override fun get(shouldUpdate: Boolean): Observable<TaskResponse> {
        return if (shouldUpdate)
            this.api.load()
                .doOnSuccess { this.data = it.tasks }
                .flatMapObservable { Observable.fromIterable(it.tasks) }
        else
            Observable.fromIterable(this.data)
    }

    override fun add(title: String, dueBy: Int, priority: String?) =
        this.api.create(TaskRequest(title, dueBy, priority)).map { it.task }
            .doOnSuccess { this.data.add(it) }

    override fun update(id: Int, title: String, dueBy: Int, priority: String?) =
        this.api.update(id, TaskRequest(title, dueBy, priority))
            .doOnComplete {
                val target = this.data.find { it.id == id }!!
                target.title = title
                target.dueBy = dueBy
                target.priority = priority
            }!!

    override fun remove(id: Int) = api.remove(id)
        .doOnComplete { this.data.remove(this.data.find { it.id == id }) }!!

}