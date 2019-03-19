package com.doit.test.core.di.abstractions

import com.doit.test.core.di.CoreModule
import com.doit.test.core.di.HttpModule
import com.doit.test.core.di.anotations.CoreScope
import com.doit.test.data.di.DataModule
import com.doit.test.data.di.abstractions.DataComponent
import com.doit.test.data.layer.auth.DoItAuthRepository
import com.doit.test.data.layer.task.DoItTaskRepository
import dagger.Component

@CoreScope
@Component(modules = [CoreModule::class, HttpModule::class])
interface CoreComponent {
    fun plusDataComponent(dataModule: DataModule): DataComponent

    fun inject(repository: DoItAuthRepository)
    fun inject(repository: DoItTaskRepository)
}