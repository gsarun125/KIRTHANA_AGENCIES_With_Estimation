package com.ka.billingsystem.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.ka.billingsystem.databinding.FragmentUserHomeBinding
import com.ka.billingsystem.services.LogoutService
import com.ka.billingsystem.utils.SetDialogStyle

class UserHomeFragment : Fragment() {
    private lateinit var binding: FragmentUserHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Logger.initialize(requireContext())
        Logger.log("Stared", " onCreateView")
        try {
            binding.invoiceCard.setOnClickListener {
                startActivity(Intent(requireContext(), SalesActivity::class.java))
            }
            binding.historyCard.setOnClickListener {
                startActivity(Intent(requireContext(), HistoryActivity::class.java))
            }
            binding.recentinvoice.setOnClickListener {
                startActivity(Intent(requireContext(), RecentInvoiceActivity::class.java))
            }
            binding.deleteCard.setOnClickListener {
                startActivity(Intent(requireContext(), DeletedInvoice::class.java))
            }
        } catch (e: Exception) {
            Logger.log("Crashed", " onCreateView")
        }
        Logger.log("Ended", " onCreateView")
    }
}