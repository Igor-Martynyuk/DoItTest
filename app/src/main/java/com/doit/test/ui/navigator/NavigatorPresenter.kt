package com.doit.test.ui.navigator

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.doit.test.R
import com.doit.test.TestApp
import com.doit.test.core.layer.utils.navigation.NavigationUtil
import com.doit.test.domain.layer.AuthInteractor
import com.doit.test.domain.layer.TaskInteractor
import com.doit.test.ui.abstractions.Presenter
import com.doit.test.ui.auth.abstractions.AuthView
import com.doit.test.ui.navigator.abstractions.NavigatorView
import com.doit.test.ui.task.details.abstractions.TaskDetailsView
import com.doit.test.ui.task.list.abstractions.TaskListView
import com.doit.test.ui.task.prefs.abstractions.TaskPrefsView
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import java.util.*
import javax.inject.Inject

@Suppress("ProtectedInFinal")
class NavigatorPresenter(view: NavigatorView) : Presenter<NavigatorView>(view) {
    @Inject
    protected lateinit var navigationUtil: NavigationUtil
    @Inject
    protected lateinit var authInteractor: AuthInteractor
    @Inject
    protected lateinit var taskInteractor: TaskInteractor

    init {
        TestApp.domainComponent.inject(this)
    }

    fun currentContent() = view.getActualFragmentManager()
        .fragments.last() as LifecycleOwner

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        this.navigationUtil.fragment.forward(view, TaskListView::class.java)
    }

    override fun onResume() {
        super.onResume()

        this.resumedDisposables?.addAll(

            this.authInteractor.observe().subscribe {
                if (!it) this.navigationUtil.activity.launch(AuthView::class.java, Intent.FLAG_ACTIVITY_CLEAR_TASK)
            },

            this.taskInteractor.observeNewTask().subscribe {
                this.navigationUtil.fragment.back(view)
            },
            this.taskInteractor.selectionSubject.subscribe {
                when {
                    !it.isPresent -> return@subscribe
                    currentContent() is TaskListView -> {
                        this.navigationUtil.fragment.forward(
                            view,
                            TaskDetailsView::class.java,
                            android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right
                        )
                    }
                }
            },

            this.taskInteractor.observeProgress().subscribe { if (it) view.showProgress() else view.hideProgress() },
            this.taskInteractor.prefsCommandSubject.subscribe {
                if (this.currentContent() is TaskListView) {
                    this.navigationUtil.fragment.forward(
                        view,
                        TaskPrefsView::class.java,
                        android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right
                    )
                } else {
                    this.navigationUtil.fragment.replace(
                        view,
                        TaskPrefsView::class.java,
                        android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right
                    )
                }
            },

            this.taskInteractor.observeError().subscribe {
                view.notifyError(
                    when (it) {
                        is HttpException -> R.string.sync_error
                        else -> R.string.unexpected_error
                    }
                )
                view.hideProgress()
            }

        )

    }

    fun onBackCommand() {
        if (currentContent() is TaskDetailsView || currentContent() is TaskPrefsView)
            this.taskInteractor.selectionSubject.onNext(Optional.empty())
    }

    fun onLogoutCommand() = this.authInteractor.signOut()
        .subscribe(
            object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {}
                override fun onError(e: Throwable) {
                    view.notifyError(R.string.unexpected_error)
                }
            }
        )

}