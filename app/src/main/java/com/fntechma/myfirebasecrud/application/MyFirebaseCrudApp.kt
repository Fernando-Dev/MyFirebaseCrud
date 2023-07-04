package com.fntechma.myfirebasecrud.application

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class MyFirebaseCrudApp: Application() {

    var firebaseAnalytics: FirebaseAnalytics? = null
    lateinit var db: FirebaseFirestore

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
        db = FirebaseFirestore.getInstance()
        db.firestoreSettings = FirebaseFirestoreSettings.Builder().build()

    }


}