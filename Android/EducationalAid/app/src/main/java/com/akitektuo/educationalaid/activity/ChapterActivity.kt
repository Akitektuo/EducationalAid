package com.akitektuo.educationalaid.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.akitektuo.educationalaid.R
import com.akitektuo.educationalaid.adapter.ModuleAdapter
import com.akitektuo.educationalaid.adapter.ModuleViewHolder
import com.akitektuo.educationalaid.fragment.SettingsFragment
import com.akitektuo.educationalaid.storage.database.Database
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_chapter.*

class ChapterActivity : AppCompatActivity() {

    private lateinit var database: Database
    private lateinit var chapterId: String
    private lateinit var auth: FirebaseAuth

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(SettingsFragment.Language(newBase!!).wrap())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chapter)
        buttonBack.setOnClickListener { finish() }

        database = Database()
        auth = FirebaseAuth.getInstance()
        chapterId = intent.getStringExtra("key_id")

        listModules.setHasFixedSize(true)
        listModules.layoutManager = LinearLayoutManager(this)

        database.getChapter(chapterId, {
            textChapterName.text = it.name
        })

    }

    override fun onStart() {
        super.onStart()
        val adapter = ModuleAdapter()
        database.getModuleAll(chapterId) {
            val total = it.size
            it.forEach {
                val model = it
                database.isModuleAvailableForChapter(model.id, chapterId, {
                    database.getUserStatus(auth.currentUser?.uid!!, model.id, {
                        val status = it.status
                        database.getModuleIQAll(model.id) {
                            adapter.add(ModuleViewHolder.Module(this@ChapterActivity, model.position, total, model.name, it.count { it.question }, status, {
                                val intent = Intent(this@ChapterActivity, ModuleActivity::class.java)
                                intent.putExtra("key_id", model.id)
                                startActivity(intent)
                            }))
                        }
                    })
                })
            }
        }
        listModules.adapter = adapter
//        database.getModuleAll(chapterId, {
//            val total = it.size
//            val firebaseAdapter = object : FirebaseRecyclerAdapter<Database.Module, ModuleViewHolder>(
//                    Database.Module::class.java,
//                    R.layout.item_module,
//                    ModuleViewHolder::class.java,
//                    database.databaseModules
//            ) {
//                override fun populateViewHolder(viewHolder: ModuleViewHolder, model: Database.Module, position: Int) {
//                    viewHolder.makeGone()
//                    database.isModuleAvailableForChapter(model.id, chapterId, {
//                        database.getUserStatus(auth.currentUser?.uid!!, model.id, {
//                            val status = it.status
//                            database.getModuleIQAll(model.id) {
//                                viewHolder.bind(ModuleViewHolder.Module(this@ChapterActivity, model.position, total, model.name, it.count { it.question }, status, {
//                                    val intent = Intent(this@ChapterActivity, ModuleActivity::class.java)
//                                    intent.putExtra("key_id", model.id)
//                                    startActivity(intent)
//                                }))
//                            }
//                        })
//                    })
//                }
//            }
//            listModules.adapter = firebaseAdapter
//        })
    }
}
