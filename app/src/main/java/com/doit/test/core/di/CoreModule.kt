package com.doit.test.core.di

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.doit.test.core.di.anotations.CoreScope
import com.doit.test.core.layer.utils.navigation.NavigationUtil
import com.doit.test.ui.auth.AuthActivity
import com.doit.test.ui.auth.abstractions.AuthView
import com.doit.test.ui.navigator.NavigatorActivity
import com.doit.test.ui.navigator.abstractions.NavigatorView
import com.doit.test.ui.task.details.TaskDetailsFragment
import com.doit.test.ui.task.details.abstractions.TaskDetailsView
import com.doit.test.ui.task.list.TaskListFragment
import com.doit.test.ui.task.list.abstractions.TaskListView
import com.doit.test.ui.task.prefs.TaskPrefsFragment
import com.doit.test.ui.task.prefs.abstractions.TaskPrefsView
import dagger.Module
import dagger.Provides

@Module
class CoreModule(private val context: Context) {

    private val prefs: SharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(this.context)!! }
    private val navigationUtil: NavigationUtil by lazy {
        NavigationUtil(
            this.context,
            mapOf(
                AuthView::class.java to AuthActivity::class.java,
                NavigatorView::class.java to NavigatorActivity::class.java
            ),
            mapOf(
                TaskListView::class.java to TaskListFragment::class.java,
                TaskPrefsView::class.java to TaskPrefsFragment::class.java,
                TaskDetailsView::class.java to TaskDetailsFragment::class.java
            )
        )
    }

    @CoreScope
    @Provides
    fun providePrefs() = this.prefs

    @CoreScope
    @Provides
    fun provideNavigationUtil() = this.navigationUtil

}