package com.doit.test.data.di.abstractions

import com.doit.test.data.di.DataModule
import com.doit.test.data.di.anotations.DataScope
import com.doit.test.domain.di.DomainModule
import com.doit.test.domain.di.abstractions.DomainComponent
import com.doit.test.domain.layer.AuthInteractor
import com.doit.test.domain.layer.TaskInteractor
import dagger.Subcomponent

@DataScope
@Subcomponent(modules = [DataModule::class])
interface DataComponent{
    fun plusDomainComponent(authModule: DomainModule) : DomainComponent

    fun inject(interactor: AuthInteractor)
    fun inject(interactor: TaskInteractor)
}