package com.akitektuo.educationalaid.activity

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.akitektuo.educationalaid.R
import com.akitektuo.educationalaid.fragment.SettingsFragment
import com.akitektuo.educationalaid.storage.database.Database
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_data.*

class DataActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: Database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data)

        buttonBack.setOnClickListener {
            finish()
        }

        auth = FirebaseAuth.getInstance()
        database = Database()

        textCreate.setOnClickListener {
            // open dialog
        }

        editLesson.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(text: Editable?) {
                if (text.isNullOrBlank()) {
                    editChapter.visibility = View.GONE
                    editChapter.setText("")
                } else {
                    editChapter.visibility = View.VISIBLE
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })

        editChapter.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(text: Editable?) {
                if (text.isNullOrBlank()) {
                    editModule.visibility = View.GONE
                    editModule.setText("")
                } else {
                    editModule.visibility = View.VISIBLE
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })

        editModule.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(text: Editable?) {
                if (text.isNullOrBlank()) {
                    textCreate.visibility = View.GONE
                } else {
                    textCreate.visibility = View.VISIBLE
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })

        database.getLessons {
            val lessonsName = ArrayList<String>()
            it.forEach { lessonsName.add(it.name) }
            editLesson.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, lessonsName))
            toast(lessonsName.size.toString())
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(SettingsFragment.Language(newBase).wrap())
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

}
