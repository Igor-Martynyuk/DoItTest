package com.doit.test.data.layer.auth.abstractions

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface AuthRepository {
    fun observeAuth() : Observable<Boolean>
    fun getEmail() : Single<String>

    fun signUp(email: String, password: String) : Completable
    fun signIn(email: String, password: String) : Completable
    fun signOut() : Completable
}