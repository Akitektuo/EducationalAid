package com.akitektuo.educationalaid.storage.database

import android.app.Application
import android.content.Context
import android.os.StatFs
import com.firebase.client.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.squareup.okhttp.Cache
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Protocol
import com.squareup.picasso.OkHttpDownloader
import com.squareup.picasso.Picasso
import java.io.File
import java.util.*


/**
 * Created by Akitektuo on 01.02.2018.
 */
class EducationalAid : Application() {

    override fun onCreate() {
        super.onCreate()
        Firebase.setAndroidContext(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        val client = OkHttpClient()
        client.protocols = Arrays.asList(Protocol.HTTP_1_1)
        val file = createDefaultCacheDir(this)
        client.cache = Cache(file, calculateDiskCacheSize(file))

        val builder = Picasso.Builder(this)
//        builder.downloader(OkHttpDownloader(OkHttpClient().setProtocols(Arrays.asList(Protocol.HTTP_1_1))))
//        builder.downloader(OkHttpDownloader(this, Long.MAX_VALUE))
        builder.downloader(OkHttpDownloader(client))
        val picasso = builder.build()
        picasso.setIndicatorsEnabled(false)
        picasso.isLoggingEnabled = true
        Picasso.setSingletonInstance(picasso)
    }

    private fun createDefaultCacheDir(context: Context): File {
        val cache = File(context.applicationContext.cacheDir, "picasso-cache")
        if (!cache.exists()) {
            cache.mkdirs()
        }
        return cache
    }

    private fun calculateDiskCacheSize(dir: File): Long {
        val long = 5242880L
        var size = long
        try {
            val statFs = StatFs(dir.absolutePath)
            val available = statFs.blockCount.toLong() * statFs.blockSize
            // Target 2% of the total space.
            size = available / 50
        } catch (ignored: IllegalArgumentException) {
        }

        // Bound inside min/max size for disk cache.
        return Math.max(Math.min(size, long), long)
    }

}