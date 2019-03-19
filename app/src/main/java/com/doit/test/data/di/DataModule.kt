package com.doit.test.data.di

import com.doit.test.data.di.anotations.DataScope
import com.doit.test.data.layer.auth.DoItAuthRepository
import com.doit.test.data.layer.auth.abstractions.AuthRepository
import com.doit.test.data.layer.task.DoItTaskRepository
import com.doit.test.data.layer.task.abstractions.TaskRepository
import dagger.Module
import dagger.Provides

@Module
class DataModule {

    private val authRepository: AuthRepository by lazy { DoItAuthRepository() }
    private val taskRepository: TaskRepository by lazy { DoItTaskRepository() }

    @Provides
    @DataScope
    fun provideAuthRepository() = this.authRepository

    @Provides
    @DataScope
    fun provideTaskRepository() = this.taskRepository

}