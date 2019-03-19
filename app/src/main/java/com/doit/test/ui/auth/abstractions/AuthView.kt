package com.doit.test.ui.auth.abstractions

import androidx.lifecycle.LifecycleOwner

interface AuthView : LifecycleOwner{
    fun setEmail(value: String)
    fun notifySignUpMode()
    fun notifySignInMode()
    fun notifyInvalidEmail(res: Int)
    fun disposeInvalidEmailNotification()
    fun notifyInvalidPass(res: Int)
    fun disposeInvalidPassNotification()
    fun notifyLoginEnabled()
    fun notifyLoginDisabled()
    fun notifyInProgress()
    fun notifyFree()
    fun notifyLoginFailed(res: Int)
}