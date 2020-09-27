package com.example.mypowerfulandroidapp.di

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.example.mypowerfulandroidapp.BaseApplication
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector

object AppInjector {
    fun init(app: BaseApplication) {
        DaggerAppComponent.builder().application(app)
            .build().inject(app)
        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                handleActivity(activity)
            }

            override fun onActivityStarted(activity: Activity) {
                //ignore
            }

            override fun onActivityResumed(activity: Activity) {
                //ignore
            }

            override fun onActivityPaused(activity: Activity) {
                //ignore
            }

            override fun onActivityStopped(activity: Activity) {
                //ignore
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                //ignore
            }

            override fun onActivityDestroyed(activity: Activity) {
                //ignore
            }
        })

    }

    private fun handleActivity(activity: Activity) {
        if (activity is HasSupportFragmentInjector) {
            AndroidInjection.inject(activity)
        }
        if (activity is FragmentActivity) {
            activity.supportFragmentManager
                .registerFragmentLifecycleCallbacks(object :
                    FragmentManager.FragmentLifecycleCallbacks() {
                    override fun onFragmentCreated(
                        fm: FragmentManager,
                        f: Fragment,
                        savedInstanceState: Bundle?
                    ) {
                        if (f is Injectable) {
                            AndroidSupportInjection.inject(f)
                        }
                    }
                }, true)
        }
    }
}