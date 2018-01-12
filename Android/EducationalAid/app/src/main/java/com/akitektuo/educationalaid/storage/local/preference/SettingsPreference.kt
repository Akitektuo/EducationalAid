package com.akitektuo.educationalaid.storage.local.preference

import android.content.Context
import android.content.SharedPreferences
import com.akitektuo.educationalaid.fragment.SettingsFragment.Companion.LANGUAGE_EN

/**
 * Created by Akitektuo on 11.01.2018.
 */
class SettingsPreference(private val context: Context) {

    companion object {
        val KEY_INITIALIZE = "key_initialize"
        val KEY_CREATED = "key_created"
        val KEY_SOUND = "key_sound"
        val KEY_LANGUAGE = "key_language"
    }

    private var sharedPreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    init {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(KEY_INITIALIZE, Context.MODE_PRIVATE)
            editor = sharedPreferences?.edit()
        }
    }

    fun set(key: String, bool: Boolean) {
        editor?.putBoolean(key, bool)
        editor?.commit()
    }

    fun set(key: String, str: String) {
        editor?.putString(key, str)
        editor?.commit()
    }

    fun getBoolean(key: String): Boolean? {
        return sharedPreferences?.getBoolean(key, false)
    }

    fun getString(key: String): String? {
        return sharedPreferences?.getString(key, "")
    }

    fun setDefault() {
        set(KEY_CREATED, true)
        set(KEY_SOUND, true)
        set(KEY_LANGUAGE, LANGUAGE_EN)
    }

}