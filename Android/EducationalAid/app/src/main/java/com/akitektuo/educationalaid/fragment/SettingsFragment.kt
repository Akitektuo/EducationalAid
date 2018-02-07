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
        textReset.setOnClickListener {
            val userId = auth.currentUser?.uid!!
//            val moduleId = "-L4a3Y_jPrig_Tm9SW-z"
//
//            val info1 = database.addInfo(Database.Info("The HTML File", "HTML files are text files, so you can use any text editor to create your first webpage. \n" +
//                    "There are some very nice HTML editors available; you can choose the one that works for you. For now let's write our examples in Notepad.",
//                    "https://api.sololearn.com/DownloadFile?id=2466", "You can run, save, and share your HTML codes on our Code Playground, without installing any additional software."))
//            database.addUserMIQ(Database.UserMIQ(userId, moduleId, "", info1, false, 1))
//            val question2 = database.addQuestion(Database.Question("What type of editor is used to edit HTML code?", "_?_text_?_ editor", 0))
//            database.addUserMIQ(Database.UserMIQ(userId, moduleId, question2, "", true, 2))
//            val info3 = database.addInfo(Database.Info("The HTML File", "Add the basic HTML structure to the text editor with \"This is a line of text\" in the body section.\n\n" +
//                    "<html>\n   <head>\n   </head>\n   <body>\n      This is a line of text. \n   </body>\n</html>\n\nIn our example, the file is saved as first.html \n\n" +
//                    "When the file is opened, the following result is displayed in the web browser:", "https://api.sololearn.com/DownloadFile?id=2527",
//                    "Don’t forget to save the file. HTML file names should end in either .html or .htm"))
//            database.addUserMIQ(Database.UserMIQ(userId, moduleId, "", info3, false, 3))
//            val question4 = database.addQuestion(Database.Question("What is the correct extension for HTML files?", ".css\n.txt\n_?_.html\n.exe", 1))
//            database.addUserMIQ(Database.UserMIQ(userId, moduleId, question4, "", true, 4))
//            val info5 = database.addInfo(Database.Info("The <title> Tag", "To place a title on the tab describing the web page, add a <title> element to your head section:\n\n" +
//                    "<html>\n   <head>\n      <title>first page</title>\n   </head>\n   <body>\n      This is a line of text. \n   </body>\n</html>\n\nThis will produce the following result:",
//                    "https://api.sololearn.com/DownloadFile?id=2528", "The title element is important because it describes the page and is used by search engines."))
//            database.addUserMIQ(Database.UserMIQ(userId, moduleId, "", info5, false, 5))
//            val question6 = database.addQuestion(Database.Question("Where should you put the title tag?", "_?_Between the head tags\nBefore the html tag\nBetween the body tags\nAfter the closing html tag",
//                    1))
//            database.addUserMIQ(Database.UserMIQ(userId, moduleId, question6, "", true, 6))

//            val module13 = database.addModule(Database.Module(chapter1, "Creating Your First HTML Page", 3))
//            val module14 = database.addModule(Database.Module(chapter1, "Creating a Blog", 4))
//            val module15 = database.addModule(Database.Module(chapter1, "Module 1 Quiz", 5))
//            val chapter2 = database.addChapter(Database.Chapter(lessonId, "HTML Basics", "https://png.icons8.com/ios/256/ffffff/web.png", "https://png.icons8.com/ios/256/A6A6A6/web.png", 2))
//            val chapter3 = database.addChapter(Database.Chapter(lessonId, "Challenges", "https://png.icons8.com/ios/256/ffffff/trophy.png", "https://png.icons8.com/ios/256/A6A6A6/trophy.png", 3))
//            val chapter4 = database.addChapter(Database.Chapter(lessonId, "HTML5", "https://png.icons8.com/ios/256/ffffff/html-5.png", "https://png.icons8.com/ios/256/A6A6A6/html-5.png", 4))

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