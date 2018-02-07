package com.akitektuo.educationalaid.util

import android.content.Context
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

/**
 * Created by Akitektuo on 07.02.2018.
 */
class Tool {

    companion object {

        fun load(context: Context, image: String, target: ImageView, placeholder: Int? = null) {
            val picasso = Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE)
            if (placeholder != null) {
                picasso.placeholder(placeholder)
            }
            picasso.into(target, object : Callback {
                override fun onSuccess() {
                }

                override fun onError() {
                    val picasso = Picasso.with(context).load(image)
                    if (placeholder != null) {
                        picasso.placeholder(placeholder)
                    }
                    picasso.into(target)
                }
            })
        }

    }

}