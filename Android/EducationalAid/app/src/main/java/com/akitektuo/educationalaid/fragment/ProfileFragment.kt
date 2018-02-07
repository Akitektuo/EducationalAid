package com.akitektuo.educationalaid.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.akitektuo.educationalaid.R
import com.akitektuo.educationalaid.storage.database.Database
import com.akitektuo.educationalaid.util.Tool.Companion.load
import com.firebase.client.Firebase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_profile.*


/**
 * Created by Akitektuo on 31.12.2017.
 */
class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: Database

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Firebase.setAndroidContext(context)
        auth = FirebaseAuth.getInstance()
        database = Database()
        updateUserInfo()
    }

    fun updateUserInfo() {
        database.getUser(auth.currentUser?.uid!!, {
            textName.text = it.name
            load(context!!, it.image, imageProfile, R.drawable.profile_picture_default)
//            Picasso.with(context).load(it.image).placeholder(R.drawable.profile_picture_default).into(imageProfile)
            textLevel.text = getString(R.string.level, it.level)
            textCurrentXp.text = getString(R.string.xp, it.currentXp)
            val targetXp = (it.currentXp / 100 + 1) * 100
            textTargetXp.text = getString(R.string.xp, targetXp)
            progressLevel.progress = it.currentXp % 100
        })
    }

    private fun toast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

}