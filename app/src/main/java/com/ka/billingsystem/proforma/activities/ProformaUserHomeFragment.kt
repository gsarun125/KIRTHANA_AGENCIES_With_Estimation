package com.ka.billingsystem.proforma.activities
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ka.billingsystem.R
import com.ka.billingsystem.activities.LoginActivity
import com.ka.billingsystem.activities.UserHomePageActivity
import com.ka.billingsystem.crash.Logger
import com.ka.billingsystem.database.DataBaseHandler
import com.ka.billingsystem.databinding.FragmentProformaUserHomeBinding
import com.ka.billingsystem.proforma.activities.ProformaHistoryActivity
import com.ka.billingsystem.proforma.activities.ProformaSalesActivity
import com.ka.billingsystem.proforma.activities.ProfomaDeletedInvoice
import com.ka.billingsystem.proforma.activities.ProfomaRecentInvoiceActivity
import com.ka.billingsystem.services.LogoutService


class ProformaUserHomeFragment : Fragment() {
        private val db: DataBaseHandler by lazy { DataBaseHandler(requireContext()) }
        private lateinit var binding: FragmentProformaUserHomeBinding
        private lateinit var sharedpreferences: SharedPreferences
        private var SPuser: String? = null
        private val SHARED_PREFS = "shared_prefs"
        private val USER_KEY = "user_key"

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
        ): View? {
            sharedpreferences =
                requireContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
            Logger.initialize(requireContext())
            Logger.log("Proforma Started", " onCreateView")

            binding = FragmentProformaUserHomeBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            SPuser = sharedpreferences.getString(USER_KEY, null)

            context?.startService(Intent(context, LogoutService::class.java))


            binding.invoiceCard.setOnClickListener {
                val intent = Intent(context, ProformaSalesActivity::class.java)
                startActivity(intent)
            }

            binding.historyCard.setOnClickListener {
                val intent = Intent(context, ProformaHistoryActivity::class.java)
                startActivity(intent)
            }

            binding.recentinvoice.setOnClickListener {
                val intent = Intent(context, ProfomaRecentInvoiceActivity::class.java)
                startActivity(intent)
            }

            binding.deleteCard.setOnClickListener {
                val intent = Intent(context, ProfomaDeletedInvoice::class.java)
                startActivity(intent)
            }

            Logger.log("Proforma Ended", " onViewCreated")
        }
}