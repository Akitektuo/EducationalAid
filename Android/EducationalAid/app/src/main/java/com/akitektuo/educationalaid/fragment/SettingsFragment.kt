package com.akitektuo.educationalaid.fragment

import android.annotation.TargetApi
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.akitektuo.educationalaid.R
import com.akitektuo.educationalaid.storage.preference.SettingsPreference
import com.akitektuo.educationalaid.storage.preference.SettingsPreference.Companion.KEY_LANGUAGE
import com.akitektuo.educationalaid.storage.preference.SettingsPreference.Companion.KEY_SOUND
import kotlinx.android.synthetic.main.fragment_settings.*
import java.util.*

/**
 * Created by Akitektuo on 31.12.2017.
 */
class SettingsFragment : Fragment() {

    companion object {
        val LANGUAGE_EN = "en"
        val LANGUAGE_RO = "ro"
    }

    private var preference: SettingsPreference? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        preference = SettingsPreference(context)

        textSound.setOnClickListener {
            switchSound.isChecked = !switchSound.isChecked
        }
        switchSound.isChecked = preference?.getBoolean(KEY_SOUND)!!
        switchSound.setOnCheckedChangeListener({ _, bool ->
            preference?.set(KEY_SOUND, bool)
        })

        val builderLanguage = AlertDialog.Builder(context)
        builderLanguage.setTitle(R.string.dialog_language_title)
        builderLanguage.setMessage(R.string.dialog_language_body)
        builderLanguage.setPositiveButton(R.string.yes, { _, _ ->
            when (preference?.getString(KEY_LANGUAGE)) {
                LANGUAGE_EN -> {
                    preference?.set(KEY_LANGUAGE, LANGUAGE_RO)
                }
                LANGUAGE_RO -> {
                    preference?.set(KEY_LANGUAGE, LANGUAGE_EN)
                }
            }
            activity.recreate()
        })
        builderLanguage.setNegativeButton(R.string.no, null)
        textLanguage.setOnClickListener {
            builderLanguage.show()
        }
    }

    class Language(private val context: Context) : ContextWrapper(context) {

        @TargetApi(Build.VERSION_CODES.N)
        fun wrap(): ContextWrapper {
            val configuration = context.resources.configuration
            val locale = Locale(SettingsPreference(context).getString(KEY_LANGUAGE))
            configuration.setLocale(locale)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val localeList = LocaleList(locale)
                LocaleList.setDefault(localeList)
                configuration.locales = localeList
            }
            return ContextWrapper(context.createConfigurationContext(configuration))
        }

    }

}