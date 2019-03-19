package com.doit.test

import android.app.Application
import com.doit.test.core.di.CoreModule
import com.doit.test.core.di.HttpModule
import com.doit.test.core.di.abstractions.CoreComponent
import com.doit.test.core.di.abstractions.DaggerCoreComponent
import com.doit.test.data.di.DataModule
import com.doit.test.data.di.abstractions.DataComponent
import com.doit.test.domain.di.DomainModule
import com.doit.test.domain.di.abstractions.DomainComponent

class TestApp : Application() {

    companion object DI {
        lateinit var coreComponent: CoreComponent
            private set

        lateinit var dataComponent: DataComponent
            private set

        lateinit var domainComponent: DomainComponent
            private set
    }

    override fun onCreate() {
        super.onCreate()

        coreComponent = DaggerCoreComponent
            .builder()
            .coreModule(CoreModule(this))
            .httpModule(HttpModule())
            .build()

        dataComponent = coreComponent.plusDataComponent(DataModule())
        domainComponent = dataComponent.plusDomainComponent(DomainModule())

    }

}