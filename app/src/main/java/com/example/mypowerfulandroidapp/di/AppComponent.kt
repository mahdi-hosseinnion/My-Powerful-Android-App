package com.example.mypowerfulandroidapp.di

import android.app.Application
import com.example.mypowerfulandroidapp.di.auth.AuthComponent
import com.example.mypowerfulandroidapp.di.main.MainComponent
import com.example.mypowerfulandroidapp.session.SessionManager
import com.example.mypowerfulandroidapp.ui.BaseActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        SubComponentsModule::class
    ]
)
interface AppComponent {

    val sessionManager: SessionManager // must add here b/c injecting into abstract class


    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
    fun inject(baseActivity: BaseActivity)

    fun authComponent():AuthComponent.Factory
    fun mainComponent():MainComponent.Factory

}
