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
import com.akitektuo.educationalaid.storage.database.Database
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
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var preference: SettingsPreference
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var credential: AuthCredential
    private lateinit var fragmentProfile: ProfileFragment
    private lateinit var database: Database
    private var name: String? = null
    private var selectedImage: Uri? = null

    companion object {
        private const val REQUEST_CODE_GOOGLE = 1
        private const val REQUEST_CODE_STORAGE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        database = Database()

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

    fun updateUser(imageUri: Uri, name: String) {
        val builder = AlertDialog.Builder(this)
        builder.setView(R.layout.dialog_load)
        builder.setTitle(getString(R.string.loading))
        builder.setCancelable(false)
        val alertLoading = builder.create()
        alertLoading.show()
        if (imageUri == auth.currentUser?.photoUrl) {
            val profileUpdate = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
            auth.currentUser?.updateProfile(profileUpdate)?.addOnCompleteListener {
                database.getUser(auth.currentUser?.uid!!, {
                    it.name = name
                    database.editUser(it, { _, _ ->
                        alertLoading.dismiss()
                        fragmentProfile.updateUserInfo()
                    })
                })
            }
        } else {
            val profileUpdate = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .setPhotoUri(imageUri)
                    .build()
            database.getUser(auth.currentUser?.uid!!, {
                it.name = name
                database.editUser(it, { _, user ->
                    auth.currentUser?.updateProfile(profileUpdate)?.addOnCompleteListener {
                        alertLoading.dismiss()
                        fragmentProfile.updateUserInfo()
                    }
                }, imageUri)
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        when (requestCode) {
            REQUEST_CODE_GOOGLE -> {
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent)
                if (result.isSuccess) {
                    credential = GoogleAuthProvider.getCredential(result.signInAccount?.idToken, null)
                    auth.signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful) {
                            database.isNewUser(auth.currentUser?.uid!!, {
                                if (it) {
                                    if (auth.currentUser?.displayName.isNullOrEmpty()) {
                                        auth.signOut()
                                        pickNameDialog()
                                    } else {
                                        database.addUser(Database.User(auth.currentUser?.displayName!!, auth.currentUser?.email!!, auth.currentUser?.photoUrl.toString(), id = auth.currentUser?.uid!!))
                                        preference.setDefault()
                                        buildFragments()
                                    }
                                } else {
                                    preference.setDefault()
                                    buildFragments()
                                }
                            })
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
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(intent)
                if (resultCode == Activity.RESULT_OK) {
                    selectedImage = result.uri
                    authWithGoogle()
                } else {
                    pickPictureDialog()
                }
            }
        }
    }

    private fun authWithGoogle() {
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val builder = AlertDialog.Builder(this)
                builder.setView(R.layout.dialog_load)
                builder.setTitle(getString(R.string.loading))
                builder.setCancelable(false)
                val alertLoading = builder.create()
                preference.setDefault()
                if (selectedImage != null) {
                    //save stuff
                    alertLoading.show()
                    database.addUser(Database.User(name!!, auth.currentUser?.email!!, id = auth.currentUser?.uid!!), selectedImage, {
                        auth.currentUser?.updateProfile(it)?.addOnCompleteListener {
                            alertLoading.dismiss()
                            buildFragments()
                        }
                    })
                } else {
                    database.addUser(Database.User(name!!, auth.currentUser?.email!!, auth.currentUser?.photoUrl.toString(), id = auth.currentUser?.uid!!))
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
                    CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(this)
                } else {
                    ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_STORAGE)
                }
            } else {
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(this)
            }
        })
        builder.setNegativeButton(getString(R.string.later), { _, _ ->
            authWithGoogle()
        })
        builder.setCancelable(false)
        builder.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(this)
                } else {
                    toast(getString(R.string.permission))
                    ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_STORAGE)
                }
            }
        }
    }

    private fun buildFragments() {
        val fragments = ArrayList<PagerAdapter.TabFragment>()
        fragmentProfile = ProfileFragment()
        fragments.add(PagerAdapter.TabFragment(LearnFragment(), R.drawable.learn, R.drawable.learn_selected))
        fragments.add(PagerAdapter.TabFragment(fragmentProfile, R.drawable.profile, R.drawable.profile_selected))
        fragments.add(PagerAdapter.TabFragment(SettingsFragment(), R.drawable.settings, R.drawable.settings_selected))
        TabbedPagerComponent(this, pager, tab, fragments, 1)
    }

}
