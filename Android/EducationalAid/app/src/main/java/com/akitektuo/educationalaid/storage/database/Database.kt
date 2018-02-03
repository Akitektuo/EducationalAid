package com.akitektuo.educationalaid.storage.database

import android.graphics.Bitmap

/**
 * Created by Akitektuo on 01.02.2018.
 */
class Database {

    data class User(val id: String, val image: Bitmap, val name: String, val level: Int, val currentXp: Int, val email: String, val password: String, val isAdmin: Boolean = false)

}