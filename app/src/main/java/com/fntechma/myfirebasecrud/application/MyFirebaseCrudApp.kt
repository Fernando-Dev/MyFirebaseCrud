package com.fntechma.myfirebasecrud.application

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics

class MyFirebaseCrudApp: Application() {

    var firebaseAnalytics: FirebaseAnalytics? = null

    init {
        instance = this
    }

    companion object {
        private var instance: MyFirebaseCrudApp? = null

        fun getInstance(): MyFirebaseCrudApp {
            return instance as MyFirebaseCrudApp
        }

    }


    override fun onCreate() {
        super.onCreate()

        firebaseAnalytics = FirebaseAnalytics.getInstance(applicationContext)

    }


}