package com.akitektuo.educationalaid.storage.database

import android.app.Application
import com.firebase.client.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Protocol
import com.squareup.picasso.OkHttpDownloader
import com.squareup.picasso.Picasso
import java.util.*

/**
 * Created by Akitektuo on 01.02.2018.
 */
class EducationalAid : Application() {

    override fun onCreate() {
        super.onCreate()
        Firebase.setAndroidContext(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        val builder = Picasso.Builder(this)
        builder.downloader(OkHttpDownloader(OkHttpClient().setProtocols(Arrays.asList(Protocol.HTTP_1_1))))
        val picasso = builder.build()
        picasso.setIndicatorsEnabled(false)
        picasso.isLoggingEnabled = true
        Picasso.setSingletonInstance(picasso)
    }

}