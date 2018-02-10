package com.akitektuo.educationalaid.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.akitektuo.educationalaid.R
import com.akitektuo.educationalaid.adapter.PeopleAdapter
import com.akitektuo.educationalaid.storage.database.Database
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_find.*
import java.util.*
import kotlin.collections.ArrayList

class FindActivity : AppCompatActivity() {

    private lateinit var database: Database
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String
    private lateinit var people: ArrayList<PeopleAdapter.Person>
    private lateinit var adapter: PeopleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find)
        buttonBack.setOnClickListener { finish() }

        database = Database()
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid!!

        listPeople.setHasFixedSize(true)
        listPeople.layoutManager = LinearLayoutManager(this)

        people = ArrayList()
        adapter = PeopleAdapter(people)

        showUsersForSearch(editSearch.text.toString())

        listPeople.adapter = adapter
        buttonSearch.setOnClickListener {
            showUsersForSearch(editSearch.text.toString())
        }
    }

    private fun showUsersForSearch(search: String) {
        database.databaseUsers.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val users = ArrayList<Database.User>()
                val usersTemp = ArrayList<Database.User>()
                data?.children?.mapNotNullTo(usersTemp, { it.getValue(Database.User::class.java) })
                usersTemp.filter { it.id == userId }.forEach { usersTemp.remove(it) }
                usersTemp.filter { it.name.contains(search, true) || it.email.contains(search, true) }.forEach { users.add(it) }
                people.clear()
                adapter.notifyDataSetChanged()
                for (user in users) {
                    database.getUserFollower(userId, user.id) {
                        val userConnections = it
                        people.add(PeopleAdapter.Person(this@FindActivity, user.image, user.name, user.email, userConnections.size != 0, {
                            if (it) {
                                database.removeUserFollower(userConnections[0].id)
                                database.addAction(Database.Action(userId, 2, user.id, Date().time))
                            } else {
                                database.addUserFollower(Database.UserFollower(user.id, userId))
                                database.addAction(Database.Action(userId, 1, user.id, Date().time))
                            }
                        }))
                        adapter.notifyItemInserted(people.size - 1)
                    }
                }
            }

        })
    }

}
