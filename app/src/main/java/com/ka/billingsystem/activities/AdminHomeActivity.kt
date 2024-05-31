package com.ka.billingsystem.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.os.Bundle
import android.os.Handler
import android.text.format.DateUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ka.billingsystem.crash.Logger
import com.ka.billingsystem.database.DataBaseHandler
import com.ka.billingsystem.databinding.ActivityAdminHomeBinding
import com.ka.billingsystem.services.LogoutService
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AdminHomeActivity : AppCompatActivity() {
    private val db = DataBaseHandler(this)
    val storage_RQ = 101
    private var handler: Handler? = null
    lateinit var binding: ActivityAdminHomeBinding
    var SHARED_PREFS = "shared_prefs"
    private lateinit var sharedpreferences: SharedPreferences
    var SPuser: String? = null
    var USER_KEY = "user_key"
    var LastLogout: Long? = null
//arun
    override fun onCreate(savedInstanceState: Bundle?) {

        Logger.log("Started", " onCreate")
        try {
            super.onCreate(savedInstanceState)
            sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
            SPuser = sharedpreferences.getString(USER_KEY, null)
            binding = ActivityAdminHomeBinding.inflate(layoutInflater)
            setContentView(binding.root)
            handler = Handler()
            lastLoginDate()
            handler!!.postDelayed(runnableCode, 1000 * 60);

            startService(Intent(this, LogoutService::class.java))

            binding.historyCard.setOnClickListener {
                val intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)

            }

            binding.recentinvoice.setOnClickListener {
                val intent = Intent(this, RecentInvoiceActivity::class.java)
                startActivity(intent)

            }
            binding.deleteCard.setOnClickListener {
                val intent = Intent(this, DeletedInvoice::class.java)
                startActivity(intent)


            }
        } catch (e: Exception) {
            Log.e("CustomLogger", "Error saving log to file", e)
        }
        Logger.log("Ended", " onCreate")
    }


    /**
     * Set the last login date and time.
     */
    private fun lastLoginDate() {
        Logger.log("Started", "lastLoginDate")
        try {
            val c1: Cursor
            c1 = db.getValue("SELECT * FROM user WHERE user_id ='" + SPuser + "'")!!
            if (c1.moveToFirst()) {
                @SuppressLint("Range") val data1: Long =
                    c1.getLong(c1.getColumnIndex("Last_Logout"))
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
                    // Customize the date format based on your requirements
                    val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
                    val formattedDate = dateFormat.format(lastLogoutDate)

                    // Set the formatted date and time to the TextView
                    binding.LastLogout.text = "Last Login: $formattedDate"
                } else {
                    // Set the formatted relative time span to the TextView
                    binding.LastLogout.text = "Last Login: $relativeTimeSpan"
                }
            } else {
                binding.LastLogout.text = ""
            }
            binding.welometxt.text = "User name : " + SPuser
        } catch (e: Exception) {
            Logger.log("Crashed", "lastLoginDate")
        }
        Logger.log("Ended", "lastLoginDate")
    }

    /**
     * Runnable code to update last login information and schedule it to run periodically.
     */
    private val runnableCode: Runnable = object : Runnable {
        override fun run() {
            lastLoginDate()
            handler!!.postDelayed(
                this,
                (1000 * 60).toLong()
            )
        }
    }

    override fun onDestroy() {
        // Remove the callback when the activity is destroyed to prevent memory leaks
        handler!!.removeCallbacks(runnableCode)
        super.onDestroy()
    }


}
