package com.akitektuo.educationalaid.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.akitektuo.educationalaid.R
import com.akitektuo.educationalaid.adapter.PeopleAdapter
import com.akitektuo.educationalaid.storage.database.Database
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_find.*

class FindActivity : AppCompatActivity() {

    private lateinit var database: Database
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find)
        buttonBack.setOnClickListener { finish() }

        database = Database()
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid!!

        listPeople.setHasFixedSize(true)
        listPeople.layoutManager = LinearLayoutManager(this)

        val people = ArrayList<PeopleAdapter.Person>()
        people.add(PeopleAdapter.Person(this, "", "Alex Copindean", "alexcopindean@yahoo.com", false, {}))
        people.add(PeopleAdapter.Person(this, "", "Alex Copindean", "alexcopindean@yahoo.com", true, {}))
        people.add(PeopleAdapter.Person(this, "", "Alex Copindean", "alexcopindean@yahoo.com", false, {}))
        people.add(PeopleAdapter.Person(this, "", "Alex Copindean", "alexcopindean@yahoo.com", false, {}))
        people.add(PeopleAdapter.Person(this, "", "Alex Copindean", "alexcopindean@yahoo.com", false, {}))
        people.add(PeopleAdapter.Person(this, "", "Alex Copindean", "alexcopindean@yahoo.com", false, {}))
        people.add(PeopleAdapter.Person(this, "", "Alex Copindean", "alexcopindean@yahoo.com", false, {}))
        people.add(PeopleAdapter.Person(this, "", "Alex Copindean", "alexcopindean@yahoo.com", false, {}))
        people.add(PeopleAdapter.Person(this, "", "Alex Copindean", "alexcopindean@yahoo.com", false, {}))
        people.add(PeopleAdapter.Person(this, "", "Alex Copindean", "alexcopindean@yahoo.com", false, {}))

        listPeople.adapter = PeopleAdapter(people)
    }
}
