package com.akitektuo.educationalaid.activity

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import com.akitektuo.educationalaid.R
import com.akitektuo.educationalaid.fragment.SettingsFragment
import com.akitektuo.educationalaid.storage.database.Database
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_data.*

class DataActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: Database

    private val lessons = ArrayList<Database.Lesson>()
    private val chapters = ArrayList<Database.Chapter>()
    private val modules = ArrayList<Database.Module>()

    private val lessonNames = ArrayList<String>()
    private val chapterNames = ArrayList<String>()
    private val moduleNames = ArrayList<String>()

    private var focusedLesson = Database.Lesson()
    private var focusedChapter = Database.Chapter()
    private var focusedModule = Database.Module()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data)

        buttonBack.setOnClickListener {
            finish()
        }

        auth = FirebaseAuth.getInstance()
        database = Database()

        setupAutoCompleteTextViews()
    }

    private fun setupAutoCompleteTextViews() {
        setupLessons()
        setupChapters()
        setupModules()
        textCreate.setOnClickListener {
            openIQDialog()
        }
    }

    private fun setupLessons() {
        lessons.clear()
        lessonNames.clear()
        lessonNames.add(getString(R.string.create_new_lesson))

        database.getLessons {
            it.forEach {
                lessonNames.add(it.name)
                lessons.add(it)
            }
            editLesson.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, lessonNames))
        }

        editLesson.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(text: Editable?) {
                if (text.isNullOrBlank() || lessons.none { it.name == text.toString() }) {
                    editChapter.visibility = View.GONE
                    editChapter.setText("")
                    if (text.toString() == getString(R.string.create_new_lesson)) {
                        openLessonDialog()
                    }
                } else {
                    editChapter.visibility = View.VISIBLE
                    chapters.clear()
                    chapterNames.clear()
//                    chapterNames.add(getString(R.string.create_new_chapter))
                    lessons.forEach {
                        if (it.name == text.toString()) {
                            focusedLesson = it
                            database.getChapterAll(it) {
                                it.forEach {
                                    chapterNames.add(it.name)
                                    chapters.add(it)
                                }
                                editChapter.setAdapter(ArrayAdapter(this@DataActivity, android.R.layout.simple_list_item_1, chapterNames))
                            }
                        }
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
    }

    private fun setupChapters() {
        editChapter.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(text: Editable?) {
                if (text.isNullOrBlank() || chapters.none { it.name == text.toString() }) {
                    editModule.visibility = View.GONE
                    editModule.setText("")
                    if (text.toString() == getString(R.string.create_new_chapter)) {
                        openChapterDialog()
                    }
                } else {
                    editModule.visibility = View.VISIBLE
                    modules.clear()
                    moduleNames.clear()
                    moduleNames.add(getString(R.string.create_new_module))
                    chapters.forEach {
                        if (it.name == text.toString()) {
                            focusedChapter = it
                            database.getModulesForChapter(it) {
                                it.forEach {
                                    moduleNames.add(it.name)
                                    modules.add(it)
                                }
                                editModule.setAdapter(ArrayAdapter(this@DataActivity, android.R.layout.simple_list_item_1, moduleNames))
                            }
                        }
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
    }

    private fun setupModules() {
        editModule.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(text: Editable?) {
                if (text.isNullOrBlank() || modules.none { it.name == text.toString() }) {
                    textCreate.visibility = View.GONE
                    if (text.toString() == getString(R.string.create_new_module)) {
                        openModuleDialog()
                    }
                } else {
                    textCreate.visibility = View.VISIBLE
                    modules.forEach {
                        if (it.name == text.toString()) {
                            focusedModule = it
                        }
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
    }

    private fun openLessonDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.create_new_lesson))
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_create_lesson, null)
        val editName = view.findViewById<EditText>(R.id.editLessonName)
        builder.setView(view)
        builder.setPositiveButton(getString(R.string.create), { _, _ ->
            val name = editName.text.toString()
            if (name.isEmpty()) {
                openLessonDialog()
            } else {
                val alertLoading = buildLoadingDialog()
                alertLoading.show()
                database.addLesson(auth.currentUser?.uid!!, Database.Lesson(name.trim(), 2, "https://cdn.brainpop.com/socialstudies/famoushistoricalfigures/napoleonbonaparte/icon.png")) {
                    editLesson.setText(name)
                    alertLoading.dismiss()
                }
            }
        })
        builder.setNeutralButton(R.string.cancel, { _, _ ->
            editLesson.setText("")
        })
        builder.setCancelable(false)
        builder.show()
    }

    private fun openChapterDialog() {
        database.databaseUsers.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val users = ArrayList<Database.User>()
                data?.children?.mapNotNullTo(users, { it.getValue(Database.User::class.java) })
                users.forEach {
                    database.addUserStatus(Database.UserStatus(it.id, "-LBN2XywX-VStgw4v9EK", 1))
                    database.addUserStatus(Database.UserStatus(it.id, "-LBN2XyyAMOPT3JVjZ8n", 0))
                    database.addUserStatus(Database.UserStatus(it.id, "-LBN2XyzmY4-oFRj0gJI", 0))
                }
            }
        })
    }

    private fun openModuleDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.create_new_module))
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_create_module, null)
        val editName = view.findViewById<EditText>(R.id.editModuleName)
        val editPosition = view.findViewById<EditText>(R.id.editModulePosition)
        builder.setView(view)
        builder.setPositiveButton(getString(R.string.create), { _, _ ->
            val name = editName.text.toString()
            val positionString = editPosition.text.toString()
            if (name.isEmpty() || positionString.isEmpty()) {
                openLessonDialog()
            } else {
                val position = Integer.valueOf(positionString)
                if (position > 0) {
                    val id = database.addModule(Database.Module(focusedChapter.id, name, position))
                    database.databaseUsers.addListenerForSingleValueEvent(object : ValueEventListener {

                        override fun onCancelled(p0: DatabaseError?) {

                        }

                        override fun onDataChange(data: DataSnapshot?) {
                            val users = ArrayList<Database.User>()
                            data?.children?.mapNotNullTo(users, { it.getValue(Database.User::class.java) })
                            users.forEach {
                                var status = 0
                                if (position == 1) {
                                    status = 1
                                }
                                database.addUserStatus(Database.UserStatus(it.id, id, status))
                            }
                        }
                    })
                } else {
                    openLessonDialog()
                }
            }
        })
        builder.setNeutralButton(R.string.cancel, { _, _ ->
            editModule.setText("")
        })
        builder.setCancelable(false)
        builder.show()
    }

    private fun openIQDialog() {

    }

    private fun buildLoadingDialog() : AlertDialog {
        val builderLoading = AlertDialog.Builder(this)
        builderLoading.setView(R.layout.dialog_load)
        builderLoading.setTitle(getString(R.string.loading))
        builderLoading.setCancelable(false)
        return builderLoading.create()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(SettingsFragment.Language(newBase).wrap())
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

}
