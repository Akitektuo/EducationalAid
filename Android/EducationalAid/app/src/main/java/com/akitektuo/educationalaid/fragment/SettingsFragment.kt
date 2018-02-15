package com.akitektuo.educationalaid.fragment

import android.annotation.TargetApi
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Toast
import com.akitektuo.educationalaid.R
import com.akitektuo.educationalaid.activity.EditActivity
import com.akitektuo.educationalaid.activity.EditActivity.Companion.KEY_NAME
import com.akitektuo.educationalaid.activity.MainActivity
import com.akitektuo.educationalaid.storage.database.Database
import com.akitektuo.educationalaid.storage.preference.SettingsPreference
import com.akitektuo.educationalaid.storage.preference.SettingsPreference.Companion.KEY_CREATED
import com.akitektuo.educationalaid.storage.preference.SettingsPreference.Companion.KEY_LANGUAGE
import com.akitektuo.educationalaid.storage.preference.SettingsPreference.Companion.KEY_SOUND
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_settings.*
import java.util.*

/**
 * Created by Akitektuo on 31.12.2017.
 */
class SettingsFragment : Fragment() {

    companion object {
        const val LANGUAGE_EN = "en"
        const val LANGUAGE_RO = "ro"
        private const val REQUEST_CODE_UPDATE_USER = 1
    }

    private lateinit var preference: SettingsPreference
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        preference = SettingsPreference(context!!)
        auth = FirebaseAuth.getInstance()

        textSound.setOnClickListener {
            switchSound.isChecked = !switchSound.isChecked
        }
        switchSound.isChecked = preference.getBoolean(KEY_SOUND)!!
        switchSound.setOnCheckedChangeListener({ _, bool ->
            preference.set(KEY_SOUND, bool)
        })

        val builderLanguage = AlertDialog.Builder(context!!)
        builderLanguage.setTitle(R.string.dialog_language_title)
        builderLanguage.setMessage(R.string.dialog_language_body)
        builderLanguage.setPositiveButton(R.string.yes, { _, _ ->
            when (preference.getString(KEY_LANGUAGE)) {
                LANGUAGE_EN -> {
                    preference.set(KEY_LANGUAGE, LANGUAGE_RO)
                }
                LANGUAGE_RO -> {
                    preference.set(KEY_LANGUAGE, LANGUAGE_EN)
                }
            }
            activity?.recreate()
        })
        builderLanguage.setNegativeButton(R.string.no, null)
        textLanguage.setOnClickListener {
            builderLanguage.show()
        }

        textSignOut.setOnClickListener {
            auth.signOut()
            preference.set(KEY_CREATED, false)
            (activity as MainActivity).signIn()
        }

        textEditProfile.setOnClickListener {
            startActivityForResult(Intent(context, EditActivity::class.java), REQUEST_CODE_UPDATE_USER)
        }

        val database = Database()
        val userId = auth.currentUser?.uid!!
        database.getUser(userId) {
            if (it.admin) {
                textReset.setOnClickListener {
                    database.getUserLessonAll(userId) {
                        it.forEach {
                            it.started = false
                            database.editUserLesson(it)
                            database.getLesson(it.lessonId) {
                                database.getChapterAll(it) {
                                    it.forEach {
                                        val chapter = it
                                        if (chapter.position == 1) {
                                            database.getUserStatus(userId, it.id) {
                                                it.status = 1
                                                database.editUserStatus(it)
                                            }
                                        } else {
                                            database.getUserStatus(userId, it.id) {
                                                it.status = 0
                                                database.editUserStatus(it)
                                            }
                                        }
                                        database.getModuleAll(it.id) {
                                            it.forEach {
                                                val module = it
                                                if (module.position == 1 && chapter.position == 1) {
                                                    database.getUserStatus(userId, it.id) {
                                                        it.status = 1
                                                        database.editUserStatus(it)
                                                    }
                                                } else {
                                                    database.getUserStatus(userId, it.id) {
                                                        it.status = 0
                                                        database.editUserStatus(it)
                                                    }
                                                }
                                                database.getModuleIQAll(it.id) {
                                                    it.forEach {
                                                        if (it.position == 1 && chapter.position == 1 && module.position == 1) {
                                                            database.getUserMIQ(userId, it.id) {
                                                                it.locked = false
                                                                database.editUserMIQ(it)
                                                            }
                                                        } else {
                                                            database.getUserMIQ(userId, it.id) {
                                                                it.locked = true
                                                                database.editUserMIQ(it)
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                imageBarSignOut.visibility = GONE
                imageReset.visibility = GONE
                textReset.visibility = GONE
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_UPDATE_USER -> {
                if (resultCode == RESULT_OK) {
                    if (data?.data != null && data.extras != null)
                        (activity as MainActivity).updateUser(data.data, data.extras.getString(KEY_NAME, auth.currentUser?.displayName))
                }
            }
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
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