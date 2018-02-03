package com.akitektuo.educationalaid.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.akitektuo.educationalaid.R
import com.firebase.client.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*


/**
 * Created by Akitektuo on 31.12.2017.
 */
class ProfileFragment : Fragment() {

    private lateinit var firebase: Firebase
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Firebase.setAndroidContext(context)
        firebase = Firebase("https://educational-aid.firebaseio.com/")
        auth = FirebaseAuth.getInstance()
        textName.text = auth.currentUser?.displayName
        Picasso.with(context).load(auth.currentUser?.photoUrl).into(imageProfile)
    }

    private fun toast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

}