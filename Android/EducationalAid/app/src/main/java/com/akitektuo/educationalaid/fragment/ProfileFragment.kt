package com.akitektuo.educationalaid.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import com.akitektuo.educationalaid.R
import com.akitektuo.educationalaid.activity.FindActivity
import com.akitektuo.educationalaid.adapter.FeedAdapter
import com.akitektuo.educationalaid.storage.database.Database
import com.akitektuo.educationalaid.util.Tool.Companion.load
import com.firebase.client.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_profile.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Akitektuo on 31.12.2017.
 */
class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: Database
    private lateinit var actions: ArrayList<FeedAdapter.Action>
    private lateinit var adapter: FeedAdapter
    private var userId = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Firebase.setAndroidContext(context)
        auth = FirebaseAuth.getInstance()
        database = Database()
        updateUserInfo()
        if (auth.currentUser != null) {
            userId = auth.currentUser?.uid!!
            database.databaseUsers.child(userId).addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError?) {
                }

                override fun onDataChange(data: DataSnapshot?) {
                    val user = data?.getValue(Database.User::class.java)
                    if (user != null) {
                        textLevel.text = getString(R.string.level, user.level)
                        textCurrentXp.text = getString(R.string.xp, user.currentXp)
                        val targetXp = (user.currentXp / 100 + 1) * 100
                        textTargetXp.text = getString(R.string.xp, targetXp)
                        progressLevel.progress = user.currentXp % 100
                    }
                    updateFeed()
                }
            })
        }

        textFindPeople.setOnClickListener {
            startActivity(Intent(context, FindActivity::class.java))
        }

        listFeed.setHasFixedSize(true)
        listFeed.layoutManager = LinearLayoutManager(context)
        actions = ArrayList()
        adapter = FeedAdapter(actions)
        listFeed.adapter = adapter

        updateFeed()
    }

    fun updateUserInfo() {
        if (auth.currentUser != null) {
            database.getUser(auth.currentUser?.uid!!, {
                textName.text = it.name
                load(context!!, it.image, imageProfile, R.drawable.profile_picture_default)
                textLevel.text = getString(R.string.level, it.level)
                textCurrentXp.text = getString(R.string.xp, it.currentXp)
                val targetXp = (it.currentXp / 100 + 1) * 100
                textTargetXp.text = getString(R.string.xp, targetXp)
                progressLevel.progress = it.currentXp % 100
            })
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    private fun updateFeed() {
        database.getActionAll(userId) {
            if (it.size == 0) {
                textFeedEmpty.visibility = VISIBLE
                listFeed.visibility = GONE
            } else {
                textFeedEmpty.visibility = GONE
                listFeed.visibility = VISIBLE
                actions.clear()
                it.sortedByDescending { it.date }.forEach {
                    val action = FeedAdapter.Action(it.type, "Error", SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(it.date))
                    println("Error - userId:${it.userId} messageId:${it.message}")
                    when (userId) {
                        it.userId -> {
                            when (it.type) {
                                0 -> {
                                    action.message = getString(R.string.you_joined)
                                    addAction(action)
                                }
                                1 -> database.getUser(it.message) {
                                    action.message = getString(R.string.you_follow, it.name)
                                    addAction(action)
                                }
                                2 -> database.getUser(it.message) {
                                    action.message = getString(R.string.you_unfollow, it.name)
                                    addAction(action)
                                }
                                3 -> database.getLesson(it.message) {
                                    action.message = getString(R.string.you_created, it.name)
                                    addAction(action)
                                }
                                4 -> database.getLesson(it.message) {
                                    action.message = getString(R.string.you_started, it.name)
                                    addAction(action)
                                }
                                5 -> database.getLesson(it.message) {
                                    action.message = getString(R.string.you_completed, it.name)
                                    addAction(action)
                                }
                                6 -> database.getUser(userId) {
                                    action.message = getString(R.string.you_reached, it.level)
                                    addAction(action)
                                }
                            }
                        }
                        it.message -> {
                            when (it.type) {
                                1 -> database.getUser(it.userId) {
                                    action.message = getString(R.string.user_follow_you, it.name)
                                    addAction(action)
                                }
                                2 -> database.getUser(it.userId) {
                                    action.message = getString(R.string.user_unfollow_you, it.name)
                                    addAction(action)
                                }
                            }
                        }
                        else -> {
                            when (it.type) {
                                0 -> database.getUser(it.userId) {
                                    action.message = getString(R.string.user_joined, it.name)
                                    addAction(action)
                                }
                                1 -> {
                                    val actionDB = it
                                    database.getUser(actionDB.userId) {
                                        val userAction = it
                                        database.getUser(actionDB.message) {
                                            action.message = getString(R.string.user_follow, userAction.name, it.name)
                                            addAction(action)
                                        }
                                    }
                                }
                                2 -> {
                                    val actionDB = it
                                    database.getUser(actionDB.userId) {
                                        val userAction = it
                                        database.getUser(actionDB.message) {
                                            action.message = getString(R.string.user_unfollow, userAction.name, it.name)
                                            addAction(action)
                                        }
                                    }
                                }
                                3 -> {
                                    val actionDB = it
                                    database.getUser(actionDB.userId) {
                                        val userAction = it
                                        database.getLesson(actionDB.message) {
                                            action.message = getString(R.string.user_created, userAction.name, it.name)
                                            addAction(action)
                                        }
                                    }
                                }
                                4 -> {
                                    val actionDB = it
                                    database.getUser(actionDB.userId) {
                                        val userAction = it
                                        database.getLesson(actionDB.message) {
                                            action.message = getString(R.string.user_started, userAction.name, it.name)
                                            addAction(action)
                                        }
                                    }
                                }
                                5 -> {
                                    val actionDB = it
                                    database.getUser(actionDB.userId) {
                                        val userAction = it
                                        database.getLesson(actionDB.message) {
                                            action.message = getString(R.string.user_completed, userAction.name, it.name)
                                            addAction(action)
                                        }
                                    }
                                }
                                6 -> {
                                    database.getUser(it.userId) {
                                        action.message = getString(R.string.user_reached, it.name, it.level)
                                        addAction(action)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun addAction(action: FeedAdapter.Action) {
        actions.add(action)
        adapter.notifyItemInserted(actions.size - 1)
    }

}