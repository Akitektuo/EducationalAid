package com.akitektuo.educationalaid.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.akitektuo.educationalaid.R
import com.akitektuo.educationalaid.adapter.ChapterAdapter
import com.akitektuo.educationalaid.adapter.ChapterViewHolder
import com.akitektuo.educationalaid.fragment.SettingsFragment
import com.akitektuo.educationalaid.storage.database.Database
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_lesson.*

class LessonActivity : AppCompatActivity() {

    private lateinit var database: Database
    private lateinit var auth: FirebaseAuth
    private lateinit var lessonId: String

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(SettingsFragment.Language(newBase!!).wrap())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson)
        buttonBack.setOnClickListener { finish() }

        database = Database()
        auth = FirebaseAuth.getInstance()

        listChapters.setHasFixedSize(true)
        listChapters.layoutManager = LinearLayoutManager(this)

        lessonId = intent.getStringExtra("key_id")
        database.getLesson(lessonId, {
            textLessonName.text = it.name
        })
    }

    override fun onStart() {
        super.onStart()

        val userId = auth.currentUser?.uid!!
        val adapter = ChapterAdapter()
        database.getChapters {
            it.forEach {
                val model = it
                database.isChapterAvailableForLesson(model.id, lessonId, {
                    database.getUserStatus(userId, model.id, {
                        val status = it.status
                        database.getModulesForChapter(model, {
                            val modules = it
                            database.databaseUsersStatus.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(error: DatabaseError?) {

                                }

                                override fun onDataChange(data: DataSnapshot?) {
                                    val userStatus = ArrayList<Database.UserStatus>()
                                    data?.children?.mapNotNullTo(userStatus, { it.getValue(Database.UserStatus::class.java) })
                                    val modulesForCount = ArrayList<Database.UserStatus>()
                                    for (x in modules) {
                                        userStatus.filter { it.userId == userId && it.statusId == x.id }.forEach { modulesForCount.add(it) }
                                    }
                                    adapter.add(ChapterViewHolder.Chapter(this@LessonActivity, model.name, status, modulesForCount.count { it.status != 0 } - 1, it.size, model.image, model.imageLocked, {
                                        val intent = Intent(this@LessonActivity, ChapterActivity::class.java)
                                        intent.putExtra("key_id", model.id)
                                        startActivity(intent)
                                    }))
                                }
                            })

                        })
                    })
                })
            }
        }
//        val firebaseAdapter = object : FirebaseRecyclerAdapter<Database.Chapter, ChapterViewHolder>(
//                Database.Chapter::class.java,
//                R.layout.item_chapter,
//                ChapterViewHolder::class.java,
//                database.databaseChapters
//        ) {
//            override fun populateViewHolder(viewHolder: ChapterViewHolder, model: Database.Chapter, position: Int) {
//                viewHolder.makeGone()
//                database.isChapterAvailableForLesson(model.id, lessonId, {
//                    database.getUserStatus(userId, model.id, {
//                        val status = it.status
//                        database.getModulesForChapter(model, {
//                            val modules = it
//                            database.databaseUsersStatus.addListenerForSingleValueEvent(object : ValueEventListener {
//                                override fun onCancelled(error: DatabaseError?) {
//
//                                }
//
//                                override fun onDataChange(data: DataSnapshot?) {
//                                    val userStatus = ArrayList<Database.UserStatus>()
//                                    data?.children?.mapNotNullTo(userStatus, { it.getValue(Database.UserStatus::class.java) })
//                                    val modulesForCount = ArrayList<Database.UserStatus>()
//                                    for (x in modules) {
//                                        userStatus.filter { it.userId == userId && it.statusId == x.id }.forEach { modulesForCount.add(it) }
//                                    }
//                                    viewHolder.bind(ChapterViewHolder.Chapter(this@LessonActivity, model.name, status, modulesForCount.count { it.status != 0 } - 1, it.size, model.image, model.imageLocked, {
//                                        val intent = Intent(this@LessonActivity, ChapterActivity::class.java)
//                                        intent.putExtra("key_id", model.id)
//                                        startActivity(intent)
//                                    }))
//                                }
//                            })
//
//                        })
//                    })
//                })
//            }
//        }
        listChapters.adapter = adapter

    }

}
