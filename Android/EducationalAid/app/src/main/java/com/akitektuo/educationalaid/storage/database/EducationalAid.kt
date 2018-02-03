package com.akitektuo.educationalaid.storage.database

import android.app.Application
import com.firebase.client.Firebase

/**
 * Created by Akitektuo on 01.02.2018.
 */
class EducationalAid : Application() {

    override fun onCreate() {
        super.onCreate()
        Firebase.setAndroidContext(this)
    }

}