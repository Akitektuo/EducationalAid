package com.akitektuo.educationalaid.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.akitektuo.educationalaid.R
import com.akitektuo.educationalaid.adapter.ChapterViewHolder
import com.akitektuo.educationalaid.fragment.SettingsFragment
import com.akitektuo.educationalaid.storage.database.Database
import com.firebase.ui.database.FirebaseRecyclerAdapter
import kotlinx.android.synthetic.main.activity_lesson.*

class LessonActivity : AppCompatActivity() {

    private lateinit var database: Database
    private lateinit var lessonId: String

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(SettingsFragment.Language(newBase!!).wrap())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson)
        buttonBack.setOnClickListener { finish() }

        database = Database()

        listChapters.setHasFixedSize(true)
        listChapters.layoutManager = LinearLayoutManager(this)

        lessonId = intent.getStringExtra("key_id")
        database.getLesson(lessonId, {
            textLessonName.text = it.name
        })
    }

    override fun onStart() {
        super.onStart()
        val firebaseAdapter = object : FirebaseRecyclerAdapter<Database.Chapter, ChapterViewHolder>(
                Database.Chapter::class.java,
                R.layout.item_chapter,
                ChapterViewHolder::class.java,
                database.databaseChapters
        ) {
            override fun populateViewHolder(viewHolder: ChapterViewHolder, model: Database.Chapter, position: Int) {
                viewHolder.makeGone()
                database.isLessonAvailableForChapter(model.id, lessonId, {
                    database.getModulesForChapter(model, {
                        viewHolder.bind(ChapterViewHolder.Chapter(this@LessonActivity, model.name, model.status, it.count { it.status != 0 }, it.size, model.image, model.imageLocked, {
                            val intent = Intent(this@LessonActivity, ChapterActivity::class.java)
                            intent.putExtra("key_id", model.id)
                            startActivity(intent)
                        }))
                    })
                })
            }
        }
        listChapters.adapter = firebaseAdapter

    }

}
