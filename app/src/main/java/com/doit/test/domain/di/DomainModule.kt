package com.doit.test.domain.di

import com.doit.test.domain.di.annotations.DomainScope
import com.doit.test.domain.layer.AuthInteractor
import com.doit.test.domain.layer.TaskInteractor
import dagger.Module
import dagger.Provides

@Module
class DomainModule {

    private val authInteractor: AuthInteractor by lazy { AuthInteractor() }
    private val taskInteractor: TaskInteractor by lazy { TaskInteractor() }

    @DomainScope
    @Provides
    fun provideAuthInteractor() = this.authInteractor

    @DomainScope
    @Provides
    fun provideTaskInteractor() = this.taskInteractor

}