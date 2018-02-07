package com.akitektuo.educationalaid.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.akitektuo.educationalaid.R
import com.akitektuo.educationalaid.fragment.SettingsFragment
import com.akitektuo.educationalaid.storage.database.Database
import com.akitektuo.educationalaid.util.Tool.Companion.load
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_edit.*

class EditActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var selectedImage: Uri
    private lateinit var database: Database

    companion object {
        const val KEY_NAME = "name"
        private const val REQUEST_CODE_STORAGE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        auth = FirebaseAuth.getInstance()
        database = Database()

        database.getUser(auth.currentUser?.uid!!, {
            //            Picasso.with(this).load(it.image).placeholder(R.drawable.profile_picture_default).into(imageProfile)
            load(this, it.image, imageProfile, R.drawable.profile_picture_default)
            editName.setText(it.name)
            selectedImage = Uri.parse(it.image)
        })
        buttonBack.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        imageRemove.setOnClickListener {
            selectedImage = Uri.parse("android.resource://com.akitektuo.educationalaid/drawable/profile_picture_default")
            imageProfile.setImageURI(selectedImage)
        }
        textChangePicture.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(this)
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_STORAGE)
                }
            } else {
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(this)
            }
        }
        textSave.setOnClickListener {
            val name = editName.text.toString()
            if (name.isEmpty()) {
                toast(getString(R.string.empty_fields))
            } else {
                val intent = Intent()
                intent.data = selectedImage
                intent.putExtra(KEY_NAME, name)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(this)
                } else {
                    toast(getString(R.string.permission))
                }
            }
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        when (requestCode) {
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(intent)
                if (resultCode == Activity.RESULT_OK) {
                    selectedImage = result.uri
                    Picasso.with(this).load(selectedImage).placeholder(R.drawable.profile_picture_default).into(imageProfile)
                }
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(SettingsFragment.Language(newBase).wrap())
    }
}
