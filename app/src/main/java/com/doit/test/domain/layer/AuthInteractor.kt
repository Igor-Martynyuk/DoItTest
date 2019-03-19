package com.doit.test.domain.layer

import com.doit.test.TestApp
import com.doit.test.data.layer.auth.abstractions.AuthRepository
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@Suppress("ProtectedInFinal")
class AuthInteractor {

    @Inject
    protected lateinit var repository: AuthRepository

    init {
        TestApp.dataComponent.inject(this)
    }

    fun observe(): Observable<Boolean> {
        return repository.observeAuth().observeOn(AndroidSchedulers.mainThread())
    }

    fun getEmail(): Single<String> {
        return repository.getEmail().observeOn(AndroidSchedulers.mainThread())
    }

    fun signUp(email: String, pass: String) = repository.signUp(email, pass)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    fun signIn(email: String, pass: String) = repository.signIn(email, pass)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    fun signOut() = repository.signOut()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

}