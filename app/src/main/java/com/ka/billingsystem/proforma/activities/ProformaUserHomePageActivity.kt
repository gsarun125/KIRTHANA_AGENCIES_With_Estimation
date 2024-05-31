package com.ka.billingsystem.proforma.activities

import android.annotation.SuppressLint
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
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ka.billingsystem.R
import com.ka.billingsystem.activities.DeletedInvoice
import com.ka.billingsystem.activities.EditSignature
import com.ka.billingsystem.activities.HistoryActivity
import com.ka.billingsystem.activities.LoginActivity
import com.ka.billingsystem.activities.RecentInvoiceActivity
import com.ka.billingsystem.activities.SalesActivity
import com.ka.billingsystem.activities.UserHomePageActivity
import com.ka.billingsystem.crash.Logger
import com.ka.billingsystem.database.DataBaseHandler
import com.ka.billingsystem.databinding.ActivityUserHomePageBinding
import com.ka.billingsystem.databinding.ActivityUserHomePageProformaBinding
import com.ka.billingsystem.services.LogoutService
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProformaUserHomePageActivity : AppCompatActivity() {
    private val db = DataBaseHandler(this)
    lateinit var binding: ActivityUserHomePageProformaBinding
    var SHARED_PREFS = "shared_prefs"
    private lateinit var sharedpreferences: SharedPreferences
    var SPuser: String? = null
    var USER_KEY = "user_key"
    var LastLogout: Long? = null
    private var handler: Handler? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        Logger.initialize(this);
        Logger.log("Proforma Started", " onCreate")
        try {
            super.onCreate(savedInstanceState)
            sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
            SPuser = sharedpreferences.getString(USER_KEY, null)

            binding = ActivityUserHomePageProformaBinding.inflate(layoutInflater)
            setContentView(binding.root)

            handler = Handler()

            Lastdateset()
            handler!!.postDelayed(runnableCode, 1000 * 60);


            startService(Intent(this, LogoutService::class.java))

            binding.btnlogout.setOnClickListener {

                val popupMenu = PopupMenu(this@ProformaUserHomePageActivity, it)
                popupMenu.menuInflater.inflate(R.menu.threedotproforma, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    val ch = menuItem.itemId
                    if(ch==R.id.userHome){
                        val intent = Intent(this, UserHomePageActivity::class.java)
                        startActivity(intent)
                    }
                    else if (ch == R.id.Logout) {
                        logOut()
                    }
                    true
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    popupMenu.setForceShowIcon(true)
                };
                popupMenu.show()

            }

            binding.invoiceCard.setOnClickListener {
                val intent = Intent(this, ProformaSalesActivity::class.java)
                startActivity(intent)

            }

            binding.historyCard.setOnClickListener {
                val intent = Intent(this, ProformaHistoryActivity::class.java)
                startActivity(intent)

            }


            binding.recentinvoice.setOnClickListener {
                val intent = Intent(this, ProfomaRecentInvoiceActivity::class.java)
                startActivity(intent)


            }
            binding.deleteCard.setOnClickListener {
                val intent = Intent(this, ProfomaDeletedInvoice::class.java)
                startActivity(intent)

            }
        } catch (e: Exception) {

            Logger.log("Proforma Crashed", " onCreate")
        }
        Logger.log("Proforma Ended", " onCreate")
    }

    /**
     * Perform logout operation.
     */
    private fun logOut() {
        Logger.log("Proforma Started", "logOut")
        try {
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            builder.setMessage(R.string.press_ok_to_logout)
            val title = SpannableStringBuilder("Alert !")
            title.setSpan(
                AbsoluteSizeSpan(18, true), 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
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


    override fun onBackPressed() {
        logOut()
    }

    /**
     * Set the last login date and time information.
     */
    private fun Lastdateset() {
        Logger.log("Proforma Started", "Lastdateset")
        try {
            val c1: Cursor
           var userName: String? =null
            c1 = db.getValue("SELECT Last_Logout,user_name FROM user WHERE user_id ='" + SPuser + "'")!!
            if (c1.moveToFirst()) {
                @SuppressLint("Range") val data1: Long =
                    c1.getLong(c1.getColumnIndex("Last_Logout"))

                @SuppressLint("Range") val data2: String =
                    c1.getString(c1.getColumnIndex("user_name"))
                userName=data2
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
            binding.welometxt.text = "Hii!.. " +capitalizeEachWord(userName);
        } catch (e: Exception) {
            Logger.log("Proforma Crashed", "logOut")
        }
        Logger.log("Proforma Ended", "logOut")
    }

    private fun capitalizeEachWord(input: String?): String? {
        val result = StringBuilder()
        var capitalizeNext = true
        if (input != null) {
            for (ch in input.toCharArray()) {
                if (Character.isWhitespace(ch)) {
                    capitalizeNext = true
                    result.append(ch)  // Add the whitespace
                } else if (capitalizeNext) {
                    result.append(ch.titlecaseChar())
                    capitalizeNext = false
                } else {
                    result.append(ch)
                }
            }
        }
        return result.toString()
    }
    /**
     * A Runnable object that runs a method periodically.
     */
    private val runnableCode: Runnable = object : Runnable {
        override fun run() {
            // Run the method periodically
            Lastdateset()

            // Schedule the method to run again after a delay
            handler!!.postDelayed(
                this, (1000 * 60).toLong()
            ) // Run every 60 seconds (adjust as needed)
        }
    }

    override fun onDestroy() {
        // Remove the callback when the activity is destroyed to prevent memory leaks
        handler!!.removeCallbacks(runnableCode)
        super.onDestroy()
    }
}