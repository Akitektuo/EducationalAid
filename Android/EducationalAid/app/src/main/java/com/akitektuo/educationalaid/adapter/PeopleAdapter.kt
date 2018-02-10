package com.akitektuo.educationalaid.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.akitektuo.educationalaid.R

/**
 * Created by Akitektuo on 10.02.2018.
 */
class PeopleAdapter(private val people: ArrayList<Person>) : RecyclerView.Adapter<PeopleAdapter.ViewHolder>() {

    data class Person(val context: Context, val image: String, val name: String, val email: String, val isFollowed: Boolean, val onClick: (isFollowed: Boolean) -> Unit)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_people, parent, false))
    }

    override fun getItemCount(): Int = people.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(people[position])

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val imagePerson = view.findViewById<ImageView>(R.id.imagePerson)
        private val textName = view.findViewById<TextView>(R.id.textName)
        private val textEmail = view.findViewById<TextView>(R.id.textEmail)
        private val buttonFollow = view.findViewById<ImageView>(R.id.buttonFollow)

        fun bind(person: Person) = with(person) {
            //            Picasso.with(context).load(image).into(imagePerson)
            textName.text = name
            textEmail.text = email
            if (isFollowed) {
                buttonFollow.setImageResource(R.drawable.unfollow)
            }
            buttonFollow.setOnClickListener {
                onClick(isFollowed)
                if (isFollowed) {
                    buttonFollow.setImageResource(R.drawable.follow)
                } else {
                    buttonFollow.setImageResource(R.drawable.unfollow)
                }
            }
        }

    }

}