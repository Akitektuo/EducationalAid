package com.akitektuo.educationalaid.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.akitektuo.educationalaid.R
import com.akitektuo.educationalaid.activity.ModuleActivity
import com.akitektuo.educationalaid.storage.database.Database
import com.akitektuo.educationalaid.util.Tool.Companion.load
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_info.*

/**
 * Created by Akitektuo on 03.01.2018.
 */

class InfoFragment : Fragment() {

    companion object {
        const val KEY_ID = "key_id"
        const val KEY_LOCKED = "key_locked"
        const val KEY_ID_MIQ = "key_umiq"
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var database: Database
    private lateinit var moduleIQId: String
    private var isLocked: Boolean = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.fragment_info, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        database = Database()
        auth = FirebaseAuth.getInstance()
        val bundle = arguments
        moduleIQId = bundle?.getString(KEY_ID_MIQ)!!
        isLocked = bundle.getBoolean(KEY_LOCKED)
        // get data from db for ID
        database.getInfo(bundle.getString(KEY_ID)!!, {
            textTitle.text = it.title
            textContent.text = it.content
            if (it.image.isEmpty()) {
                imageContent.visibility = View.GONE
            } else {
                load(context!!, it.image, imageContent)
            }
            if (it.importance.isEmpty()) {
                layoutImportant.visibility = View.GONE
            } else {
                textImportant.text = it.importance
            }
            if (isLocked) {
                imageLocked.visibility = View.VISIBLE
            }

        })
//        textTitle.text = "The text-shadow Property"
//        textContent.text = "The text-shadow property defines one or more comma-separated shadow effects, to be applied to the text content of the current element.\n\nThe image below shows how the text-shadow property is applied:"
//        if (.isNullOrEmpty()) {
//                imageContent.visibility = View.GONE
//            } else {
//            imageContent.setImageResource(resources.getIdentifier("content", "drawable", context?.packageName))
//            }
//        if ("- The offset-x and offset-y values are required for the CSS text-shadow property.\n- The color value is not required, but since the default for the text-shadow is transparent, the text-shadow will not appear unless you specify a color value.".isNullOrEmpty()) {
//            layoutImportant.visibility = View.GONE
//        } else {
//            textImportant.text = "- The offset-x and offset-y values are required for the CSS text-shadow property.\n- The color value is not required, but since the default for the text-shadow is transparent, the text-shadow will not appear unless you specify a color value."
//        }

//        if (bundle?.getInt(KEY_ID) == 3) {
//            imageLocked.visibility = View.VISIBLE
//        }

        buttonContinue.setOnClickListener {
            (activity as ModuleActivity).continueOnClick(moduleIQId)
        }
    }

    fun unlockFragment() {
        imageLocked.visibility = View.GONE
        isLocked = false
        database.getUserMIQ(auth.currentUser?.uid!!, moduleIQId, {
            if (it.locked) {
                it.locked = false
                database.editUserMIQ(it)
            }
        })
    }

}