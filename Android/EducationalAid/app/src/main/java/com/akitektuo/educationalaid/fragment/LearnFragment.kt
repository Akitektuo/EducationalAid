package com.akitektuo.educationalaid.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.akitektuo.educationalaid.R
import com.akitektuo.educationalaid.activity.LessonActivity
import com.akitektuo.educationalaid.adapter.LessonViewHolder
import com.akitektuo.educationalaid.storage.database.Database
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_learn.*

/**
 * Created by Akitektuo on 31.12.2017.
 */

class LearnFragment : Fragment() {

    private lateinit var database: Database
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_learn, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        database = Database()
        auth = FirebaseAuth.getInstance()

        listLessons.setHasFixedSize(true)
        listLessons.layoutManager = LinearLayoutManager(context)

    }

    override fun onStart() {
        super.onStart()
        val userId = auth.currentUser?.uid!!
        val firebaseAdapter = object : FirebaseRecyclerAdapter<Database.Lesson, LessonViewHolder>(
                Database.Lesson::class.java,
                R.layout.item_lesson,
                LessonViewHolder::class.java,
                database.databaseLessons
        ) {
            override fun populateViewHolder(viewHolder: LessonViewHolder, model: Database.Lesson, position: Int) {
                viewHolder.makeGone()
                if (model.visibility != 0) {
                    database.isLessonAvailableForUser(userId, model.id, {
                        val isPaid = it
                        database.getUserMIQForLesson(userId, model, {
                            var progress = 0
                            if (model.started) {
                                progress = it.count { !it.locked }
                            }
                            viewHolder.bind(LessonViewHolder.Lesson(activity?.applicationContext!!, model.name, model.image, progress, it.size, {
                                if (isPaid) {
                                    val intent = Intent(context, LessonActivity::class.java)
                                    intent.putExtra("key_id", model.id)
                                    startActivity(intent)
                                } else {
                                    toast("Buying lessons will be implemented in other versions")
                                }
                            }))
                        })
                    })
                }
            }
        }
        listLessons.adapter = firebaseAdapter

    }

    private fun toast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

}