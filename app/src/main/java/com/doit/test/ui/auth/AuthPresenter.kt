package com.doit.test.ui.auth

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.doit.test.R
import com.doit.test.TestApp
import com.doit.test.core.layer.utils.navigation.NavigationUtil
import com.doit.test.domain.layer.AuthInteractor
import com.doit.test.ui.abstractions.Presenter
import com.doit.test.ui.auth.abstractions.AuthView
import com.doit.test.ui.navigator.abstractions.NavigatorView
import io.reactivex.CompletableObserver
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Inject

@Suppress("ProtectedInFinal")
class AuthPresenter(view: AuthView) : Presenter<AuthView>(view) {
    private var isInProgress = false

    @Inject
    protected lateinit var navigationUtil: NavigationUtil
    @Inject
    protected lateinit var interactor: AuthInteractor

    var email: String? = ""
        set(value) {
            field = value
            if (this.view.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
                super.view.disposeInvalidEmailNotification()
        }

    var password: String? = ""
        set(value) {
            field = value
            if (this.view.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
                super.view.disposeInvalidPassNotification()
        }

    var isInSignUpMode = false
        set(value) {
            if (value != field && this.view.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                if (value) super.view.notifySignUpMode()
                else super.view.notifySignInMode()
            }
            field = value
        }

    init {
        TestApp.domainComponent.inject(this)
    }

    private fun validateEmail() = !email.isNullOrEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    private fun validatePass() = !password.isNullOrEmpty()

    private fun updateView() {
        if (this.isInProgress) {

            super.view.notifyInProgress()
            super.view.notifyLoginDisabled()

        } else {

            super.view.notifyFree()
            when {
                !this.validateEmail() -> {
                    if (!this.email.isNullOrEmpty())
                        super.view.notifyInvalidEmail(R.string.invalid_email)
                    super.view.notifyLoginDisabled()
                }
                !this.validatePass() -> {
                    if (!this.password.isNullOrEmpty())
                        super.view.notifyInvalidPass(R.string.invalid_email)
                    super.view.notifyLoginDisabled()
                }
                else -> super.view.notifyLoginEnabled()
            }

        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() = this.interactor.getEmail().subscribe(
        object : SingleObserver<String> {
            override fun onSubscribe(d: Disposable) {}
            override fun onError(e: Throwable) {}
            override fun onSuccess(t: String) {
                view.setEmail(t)
            }
        }
    )

    override fun onResume() {
        super.onResume()
        updateView()
    }

    fun onEmailEntered() = updateView()
    fun onPassEntered() = updateView()

    private fun onAuthFailed(t: Throwable) {
        this.isInProgress = false

        var msgId = R.string.unexpected_login_error
        if (t is UnknownHostException) msgId = R.string.unexpected_login_error
        if (t is HttpException) {
            when (t.code()) {
                401, 403 -> msgId = R.string.invalid_credentials
                409 -> msgId = R.string.email_exists
            }
        }

        super.view.notifyLoginFailed(msgId)
        this.updateView()
    }

    fun onAccepted() {

        this.isInProgress = true
        this.view.notifyInProgress()

        if (isInSignUpMode) this.interactor.signUp(this.email!!, this.password!!)
        else this.interactor.signIn(this.email!!, this.password!!)
            .subscribe(
                object : CompletableObserver {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onComplete() {
                        isInProgress = false
                        navigationUtil.activity.launch(NavigatorView::class.java, Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    }

                    override fun onError(e: Throwable) {
                        isInProgress = false
                        onAuthFailed(e)
                    }
                }
            )


    }

}