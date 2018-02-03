package com.akitektuo.educationalaid.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import com.akitektuo.educationalaid.R
import com.akitektuo.educationalaid.adapter.PagerAdapter
import com.akitektuo.educationalaid.component.TabbedPagerComponent
import com.akitektuo.educationalaid.fragment.LearnFragment
import com.akitektuo.educationalaid.fragment.ProfileFragment
import com.akitektuo.educationalaid.fragment.SettingsFragment
import com.akitektuo.educationalaid.storage.preference.SettingsPreference
import com.akitektuo.educationalaid.storage.preference.SettingsPreference.Companion.KEY_CREATED
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var preference: SettingsPreference
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var credential: AuthCredential
    private lateinit var storage: StorageReference
    private lateinit var alertLoading: AlertDialog
    private var name: String? = null
    private var selectedImage: Uri? = null
    private val REQUEST_CODE_GOOGLE = 1
    private val REQUEST_CODE_IMAGE = 2
    private val REQUEST_CODE_STORAGE = 3
    private val REQUEST_CODE_CROP = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance().reference.child("users")

        preference = SettingsPreference(this)
        if (!preference.getBoolean(KEY_CREATED)!!) {
            println("First start-up")
            signIn()
        } else {
            buildFragments()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(SettingsFragment.Language(newBase).wrap())
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun signIn() {
        val builder = AlertDialog.Builder(this)
        builder.setView(R.layout.dialog_load)
        builder.setTitle(getString(R.string.loading))
        builder.setCancelable(false)
        alertLoading = builder.create()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut().addOnCompleteListener {
            signInDialog()
        }
    }

    private fun signInDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.sign_in_google))
        builder.setMessage(getString(R.string.sign_in_google_reason))
        builder.setPositiveButton(getString(R.string.sign_in), { _, _ ->
            startActivityForResult(googleSignInClient.signInIntent, REQUEST_CODE_GOOGLE)
        })
        builder.setCancelable(false)
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        when (requestCode) {
            REQUEST_CODE_GOOGLE -> {
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent)
                if (result.isSuccess) {
                    credential = GoogleAuthProvider.getCredential(result.signInAccount?.idToken, null)
                    //if user is new
                    auth.signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful) {
                            if (auth.currentUser?.displayName.isNullOrEmpty()) {
                                auth.signOut()
                                pickNameDialog()
                            } else {
                                //save data
                                preference.setDefault()
                                buildFragments()
                            }
                        } else {
                            toast(getString(R.string.error))
                            signInDialog()
                        }
                    }
                } else {
                    signInDialog()
                    toast(getString(R.string.error))
                }
            }
            REQUEST_CODE_IMAGE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val intentCrop = Intent("com.android.camera.action.CROP")

                    intentCrop.setDataAndType(intent?.data, "image/*")
                    intentCrop.putExtra("crop", true)
                    intentCrop.putExtra("outputX", 96)
                    intentCrop.putExtra("outputY", 96)
                    intentCrop.putExtra("aspectX", 1)
                    intentCrop.putExtra("aspectY", 1)
                    intentCrop.putExtra("scale", true)
                    intentCrop.putExtra("return-data", true)
                    intentCrop.putExtra(MediaStore.EXTRA_OUTPUT, intent?.data)
                    startActivityForResult(intentCrop, REQUEST_CODE_CROP)
                } else {
                    pickPictureDialog()
                }
            }
            REQUEST_CODE_CROP -> {
                if (resultCode == Activity.RESULT_OK) {
                    selectedImage = bitmapToUri(intent?.extras?.getParcelable("data")!!)
                    authWithGoogle()
                } else {
                    pickPictureDialog()
                }
            }
        }
    }

    private fun bitmapToUri(bitmap: Bitmap): Uri {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ByteArrayOutputStream())
        return Uri.parse(MediaStore.Images.Media.insertImage(contentResolver, bitmap, "user", null))
    }

    private fun authWithGoogle() {
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                //save data
                preference.setDefault()
                if (selectedImage != null) {
                    //save stuff
                    alertLoading.show()
                    storage.child(auth.currentUser?.uid as String).putFile(selectedImage!!).addOnSuccessListener {
                        // it.getDownloadUrl()
                        val profileUpdate = UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(it.downloadUrl)
                                .build()
                        auth.currentUser?.updateProfile(profileUpdate)?.addOnCompleteListener {
                            alertLoading.dismiss()
                            buildFragments()
                        }
                    }
                } else {
                    alertLoading.show()
                    val profileUpdate = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()
                    auth.currentUser?.updateProfile(profileUpdate)?.addOnCompleteListener {
                        alertLoading.dismiss()
                        buildFragments()
                    }
                }
            } else {
                toast(getString(R.string.error))
                signInDialog()
            }
        }
    }

    private fun pickNameDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.enter_name))
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_input, null)
        val editName = view.findViewById<EditText>(R.id.editName)
        builder.setView(view)
        builder.setPositiveButton(getString(R.string.continue_button), { _, _ ->
            name = editName.text.toString()
            if (name.isNullOrEmpty()) {
                pickNameDialog()
                toast(getString(R.string.empty_fields))
                return@setPositiveButton
            }
            pickPictureDialog()
        })
        builder.setCancelable(false)
        builder.show()
    }

    private fun pickPictureDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.pick_image))
        builder.setPositiveButton(getString(R.string.continue_button), { _, _ ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    choosePicture()
                } else {
                    ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_STORAGE)
                }
            } else {
                choosePicture()
            }
        })
        builder.setNegativeButton(getString(R.string.later), { _, _ ->
            authWithGoogle()
        })
        builder.setCancelable(false)
        builder.show()
    }

    private fun choosePicture() {
        val getIntent = Intent(Intent.ACTION_GET_CONTENT)
        getIntent.type = "image/*"
        val pickIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = "image/*"
        val chooserIntent = Intent.createChooser(getIntent, getString(R.string.pick_image))
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))
        startActivityForResult(chooserIntent, REQUEST_CODE_IMAGE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    choosePicture()
                } else {
                    toast(getString(R.string.permission))
                    ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_STORAGE)
                }
            }
        }
    }

    private fun buildFragments() {
        val fragments = ArrayList<PagerAdapter.TabFragment>()
        fragments.add(PagerAdapter.TabFragment(LearnFragment(), R.drawable.learn, R.drawable.learn_selected))
        fragments.add(PagerAdapter.TabFragment(ProfileFragment(), R.drawable.profile, R.drawable.profile_selected))
        fragments.add(PagerAdapter.TabFragment(SettingsFragment(), R.drawable.settings, R.drawable.settings_selected))
        TabbedPagerComponent(this, pager, tab, fragments, 1)
    }

}
