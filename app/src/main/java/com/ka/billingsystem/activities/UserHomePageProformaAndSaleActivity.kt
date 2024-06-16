package com.ka.billingsystem.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.format.DateUtils
import android.text.style.AbsoluteSizeSpan
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.ka.billingsystem.R
import com.ka.billingsystem.crash.Logger
import com.ka.billingsystem.database.DataBaseHandler
import com.ka.billingsystem.databinding.ActivityProformaAndSaleBinding
import com.ka.billingsystem.databinding.ActivityUserHomePageBinding
import com.ka.billingsystem.proforma.activities.ProformaUserHomeFragment
import com.ka.billingsystem.proforma.activities.ProformaUserHomePageActivity
import com.ka.billingsystem.services.LogoutService
import com.ka.billingsystem.utils.SetDialogStyle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserHomePageProformaAndSaleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProformaAndSaleBinding

    private val db = DataBaseHandler(this)
    private var handler: Handler? = null
    private var SPuser: String? = null
    private val USER_KEY = "user_key"
    private var LastLogout: Long? = null
    private lateinit var sharedpreferences: SharedPreferences
    private val SHARED_PREFS = "shared_prefs"
    private var firstLogin: Boolean = false
    private var isUserHomeFragmentDisplayed = true


    override fun onCreate(savedInstanceState: Bundle?) {
        Logger.initialize(this);
        Logger.log("Stared", " onCreate")

        super.onCreate(savedInstanceState)
        binding = ActivityProformaAndSaleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        SPuser = sharedpreferences.getString(USER_KEY, null)

        handler = Handler()
        Lastdateset()
        handler!!.postDelayed(runnableCode, 1000 * 60)

        val qurry = "Select firstLogin from user where id=1"
        val cursor = db.getValue(qurry)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                // Assuming 'firstLogin' is a boolean column stored as an integer (0 or 1)
                firstLogin = cursor.getInt(cursor.getColumnIndexOrThrow("firstLogin")) == 1
                Log.d("arun", "First Login: $firstLogin")
            }
            cursor.close()
        }

        if (firstLogin){
            if (NetworkUtils.isNetworkAvailable(applicationContext)) {
                showDriveSignDialog()
            }
            db.UpdateLogin()
        }
        Lastdateset()
        handler!!.postDelayed(runnableCode, 1000 * 60);
        val account = GoogleSignIn.getLastSignedInAccount(this)

        if (account != null) {
            // User credentials found, initialize Drive service
            initializeDriveService(account.email!!)
            // uploadPDFFile();
        }

        startService(Intent(this, LogoutService::class.java))

        val fragmentToDisplay = intent.getStringExtra("fragment_to_display")

        // Load the default fragment (UserHomeFragment)
        if (savedInstanceState == null) {
            if (fragmentToDisplay == "ProformaUserHomeFragment") {
                isUserHomeFragmentDisplayed = false
                replaceFragment(ProformaUserHomeFragment(), R.drawable.ic_remove)
            } else {
                replaceFragment(UserHomeFragment(), R.drawable.ic_add)
            }
        }

        binding.toggleButton.setOnClickListener {
            val clickAnimation = AnimationUtils.loadAnimation(this, R.anim.click_animation)
            it.startAnimation(clickAnimation)
            toggleFragment()
        }
        binding.btnlogout.setOnClickListener {

            val popupMenu = PopupMenu(this@UserHomePageProformaAndSaleActivity, it)
            popupMenu.menuInflater.inflate(R.menu.threedot, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                val ch = menuItem.itemId
                if (ch == R.id.EditSignature2) {
                    val intent = Intent(this, EditSignature::class.java)
                    startActivity(intent)
                } else if (ch == R.id.Logout) {
                    logOut()
                }
                true
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                popupMenu.setForceShowIcon(true)
            };
            popupMenu.show()

        }

    }

    private fun replaceFragment(fragment: Fragment, iconResId: Int) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
        binding.toggleButton.setImageResource(iconResId)
    }
    private fun toggleFragment() {
        if (isUserHomeFragmentDisplayed) {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, ProformaUserHomeFragment())
                .commit()
            binding.toggleButton.setImageResource(R.drawable.ic_remove)
        } else {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, UserHomeFragment())
                .commit()
            binding.toggleButton.setImageResource(R.drawable.ic_add)
        }
        isUserHomeFragmentDisplayed = !isUserHomeFragmentDisplayed
    }
     private fun Lastdateset() {
        Logger.log("Started", "Lastdateset")
        try {
            val c1: Cursor
            var userName: String? = null
            c1 = db.getValue("SELECT Last_Logout,user_name FROM user WHERE user_id ='" + SPuser + "'")!!
            if (c1.moveToFirst()) {
                @SuppressLint("Range") val data1: Long = c1.getLong(c1.getColumnIndex("Last_Logout"))
                @SuppressLint("Range") val data2: String = c1.getString(c1.getColumnIndex("user_name"))
                userName = data2
                LastLogout = data1
            }
            if (LastLogout != 0L) {
                val lastLogoutTimestamp = LastLogout!!
                val lastLogoutDate = Date(lastLogoutTimestamp)
                val currentTimeMillis = System.currentTimeMillis()
                val timeDifference = currentTimeMillis - lastLogoutTimestamp
                val relativeTimeSpan = DateUtils.getRelativeTimeSpanString(
                    lastLogoutTimestamp,
                    currentTimeMillis,
                    DateUtils.MINUTE_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_RELATIVE
                )
                if (timeDifference > DateUtils.DAY_IN_MILLIS) {
                    val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
                    val formattedDate = dateFormat.format(lastLogoutDate)
                    binding.LastLogout.text = "Last Login: $formattedDate"
                } else {
                    binding.LastLogout.text = "Last Login: $relativeTimeSpan"
                }
            } else {
                binding.LastLogout.text = ""
            }
            binding.welometxt.text = "Hii!.. " + capitalizeEachWord(userName)
        } catch (e: Exception) {
            Logger.log("Crashed", "Lastdateset")
        }
        Logger.log("Ended", "Lastdateset")
    }
    private fun showDriveSignDialog() {
        Logger.log("Started", "logOut")
        try {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Press ok to get Store the Data")
            val title = SpannableStringBuilder("Do you want to Store the in Google Drive")
            title.setSpan(
                AbsoluteSizeSpan(23, true),
                0,
                title.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            builder.setTitle(title)
            builder.setCancelable(true)
            builder.setNegativeButton(
                "Cancel"
            ) { dialog, which ->
                dialog.dismiss()
                Toast.makeText(
                    applicationContext,
                    R.string.you_press_cancel_button,
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
            builder.setPositiveButton(
                "Ok"
            ) { dialog, which ->
                val account = GoogleSignIn.getLastSignedInAccount(this@UserHomePageProformaAndSaleActivity)
                if (account != null) {
                    // User credentials found, initialize Drive service
                    initializeDriveService(account.email.toString())
                    // uploadPDFFile();
                } else {
                    // User credentials not found, request sign-inF
                    requestSignIn()
                }
            }

            val alertDialog = builder.create()

            alertDialog.setOnShowListener {
                val positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                val negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                SetDialogStyle.setDialogStyle(this,positiveButton, negativeButton)
            }
            alertDialog.show()
        } catch (e: java.lang.Exception) {
            Logger.log("Crashed", "logOut")
        }
        Logger.log("Ended", "logOut")
    }

    private fun requestSignIn() {
        val signInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(Scope(DriveScopes.DRIVE_FILE))
                .build()
        val client = GoogleSignIn.getClient(this, signInOptions)

        // The result of the sign-in Intent is handled in onActivityResult.
        startActivityForResult(client.signInIntent, 400)
    }

    private fun initializeDriveService(accountName: String) {
        val credential = GoogleAccountCredential.usingOAuth2(
            this@UserHomePageProformaAndSaleActivity,
            listOf(DriveScopes.DRIVE_FILE)
        )
        credential.setSelectedAccountName(accountName)
        val googleDriveService = Drive.Builder(
            AndroidHttp.newCompatibleTransport(),
            GsonFactory(),
            credential
        )
            .setApplicationName("My Drive")
            .build()
        Log.d("g", "Drive service created successfully")
        LoginActivity.driveServiceHelper = DriveServiceHelper(googleDriveService)
        Log.d("g", "DriveServiceHelper initialized successfully")
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Logger.log("Started", "onActivityResult")
        try {

            when (requestCode) {
                400 -> if (resultCode == RESULT_OK) {
                    if (data != null) {
                        hanleSiginIntent(data)
                    }
                } else {
                    // Handle unsuccessful sign-in
                    Log.e("g", "Sign-in failed with resultCode=$resultCode")
                    Toast.makeText(this, "Sign-in failed. Please try again.", Toast.LENGTH_SHORT)
                        .show()
                }

                else -> Log.e("g", "Unknown requestCode: $requestCode")
            }
            super.onActivityResult(requestCode, resultCode, data)
        } catch (e: java.lang.Exception) {
            Logger.log("Crashed", "onActivityResult")
            Log.e("g", "onActivityResult()-An exception occurred", e)
        }
        Logger.log("Ended", "onActivityResult")
    }
    private fun hanleSiginIntent(data: Intent) {
        GoogleSignIn.getSignedInAccountFromIntent(data)
            .addOnSuccessListener { googleSignInAccount ->
                Log.d("g", "Google sign-in successful")
                val credential = GoogleAccountCredential.usingOAuth2(
                    this@UserHomePageProformaAndSaleActivity,
                    listOf(DriveScopes.DRIVE_FILE)
                )
                credential.setSelectedAccount(googleSignInAccount.account)
                val googleDriveService = Drive.Builder(
                    AndroidHttp.newCompatibleTransport(),
                    GsonFactory(),
                    credential
                )
                    .setApplicationName("My Drive")
                    .build()
                Log.d("g", "Drive service created successfully")
                initializeDriveService(googleSignInAccount.account!!.name)
                Toast.makeText(
                    this@UserHomePageProformaAndSaleActivity,
                    "Sign-in successfully: " + googleSignInAccount.account!!.name,
                    Toast.LENGTH_SHORT
                ).show()

                LoginActivity.driveServiceHelper = DriveServiceHelper(googleDriveService)
                Log.d("g", "DriveServiceHelper initialized successfully")
            }
            .addOnFailureListener { e -> // Handle sign-in failure
                Log.e("g", "Sign-in failed: " + e.message)
                Toast.makeText(
                    this@UserHomePageProformaAndSaleActivity,
                    "Sign-in failed: " + e.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


    private fun capitalizeEachWord(input: String?): String {
        val result = StringBuilder()
        var capitalizeNext = true
        input?.forEach { ch ->
            if (ch.isWhitespace()) {
                capitalizeNext = true
                result.append(ch)
            } else if (capitalizeNext) {
                result.append(ch.titlecaseChar())
                capitalizeNext = false
            } else {
                result.append(ch)
            }
        }
        return result.toString()
    }
    private val runnableCode: Runnable = object : Runnable {
        override fun run() {
            Lastdateset()
            handler?.postDelayed(this, (1000 * 60).toLong())
        }
    }

    override fun onDestroy() {
        // Remove the callback when the activity is destroyed to prevent memory leaks
        handler!!.removeCallbacks(runnableCode)
        super.onDestroy()
    }

    /**
     * Perform logout operation.
     */
    private fun logOut() {
        Logger.log("Stared", "logOut")
        try {
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            builder.setMessage(R.string.press_ok_to_logout)
            val title = SpannableStringBuilder("Alert !")
            title.setSpan(
                AbsoluteSizeSpan(20, true), 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            builder.setTitle(title)

            builder.setCancelable(true)
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(this, R.string.you_press_cancel_button, Toast.LENGTH_SHORT).show()

            }
            builder.setPositiveButton(
                "Ok",
                DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                    val time = System.currentTimeMillis()
                    db.lastLogout(SPuser, time)
                    val editor = sharedpreferences.edit()

                    editor.remove("user_key")
                    editor.remove("password_key")

                    editor.apply()

                    val i = Intent(this, LoginActivity::class.java)
                    startActivity(i)
                    finish()
                })
            val alertDialog = builder.create()
            alertDialog.show()
        } catch (e: Exception) {
            Logger.log("Crashed", "logOut")
        }
        Logger.log("Ended", "logOut")
    }
}
