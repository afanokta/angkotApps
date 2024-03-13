package com.example.trackingapps.driver

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.trackingapps.R
import com.example.trackingapps.R.color.BG_track
import com.example.trackingapps.R.color.BSA_track
import com.example.trackingapps.databinding.ActivityDriverHomeBinding
import com.example.trackingapps.service.LocationService
import com.example.trackingapps.utils.Utils
import com.google.firebase.database.FirebaseDatabase

class DriverHomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityDriverHomeBinding
    lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var sharedPreferences: SharedPreferences
    private var shareLoc: Boolean = false
    private var userId: String? = ""
    private var kodeTrayek: String? = ""
    private var angkotFull: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityDriverHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        shareLoc = sharedPreferences.getBoolean("shareLoc", false)
        angkotFull = sharedPreferences.getBoolean("angkotFull", false)

        title = "Menu Pengemudi"
        userId = sharedPreferences.getString("uid", null)
        kodeTrayek = sharedPreferences.getString("kodeTrayek", null)

        getNotifPermisision()

        binding.tvMenuPengemudi.text = binding.tvMenuPengemudi.text.toString()
            .replace("{Nama}", sharedPreferences.getString("name", null)!!)
            .replace("{Role}", sharedPreferences.getString("role", null)!!)

        binding.btLogOut.setOnClickListener {
            val editor = sharedPreferences.edit()
            shareLoc = !shareLoc
            editor.putBoolean("shareLoc", shareLoc)
            stopService(Intent(this, LocationService::class.java).putExtra("userId", userId))

            Utils.logoutHelper(this)
        }

        binding.btShareLoc.text =
            if (shareLoc) "Berhenti Berbagi Lokasi" else "Tidak Berbagi Lokasi"
        binding.tvStatusLokasi.text =
            if (shareLoc) "Status Pengemudi: Sedang Berbagi Lokasi" else "Status Pengemudi: Tidak Berbagi Lokasi"
        binding.tvStatusLokasi.setTextColor(
            if (shareLoc) resources.getColor(BSA_track) else resources.getColor(
                BG_track
            )
        )

        binding.btStatusSeat.text = if (angkotFull) "Kursi Tersedia" else "Kursi Penuh"
        binding.tvStatusKursi.text =
            if (angkotFull) "Status Kursi: Tersedia" else "Status Kursi: Penuh"
        binding.tvStatusKursi.setTextColor(
            if (angkotFull) resources.getColor(BSA_track) else resources.getColor(
                BG_track
            )
        )

        val editor = sharedPreferences.edit()
        binding.btShareLoc.setOnClickListener {
            shareLoc = !shareLoc
            editor.putBoolean("shareLoc", shareLoc)
            if (shareLoc) {
                binding.tvStatusLokasi.text = "Status Pengemudi: Sedang Berbagi Lokasi"
                binding.tvStatusLokasi.setTextColor(resources.getColor(BSA_track))
                binding.btShareLoc.text = "Berhenti Berbagi Lokasi"
                ContextCompat.startForegroundService(
                    this, Intent(this, LocationService::class.java).putExtra("userId", userId)
                        .putExtra("kodeTrayek", kodeTrayek)
                        .putExtra("angkotFull", angkotFull)
                )
            } else {
                binding.tvStatusLokasi.text = "Status Pengemudi: Tidak Berbagi Lokasi"
                binding.tvStatusLokasi.setTextColor(resources.getColor(BG_track))
                binding.btShareLoc.text = "Bagikan Lokasi"
                stopService(
                    Intent(this, LocationService::class.java).putExtra("userId", userId)
                        .putExtra("kodeTrayek", kodeTrayek)
                        .putExtra("angkotFull", angkotFull)
                )
            }
        }

        binding.btStatusSeat.setOnClickListener {
            angkotFull = !angkotFull
            editor.putBoolean("angkotFull", angkotFull)
            binding.btStatusSeat.text = if (angkotFull) "Kursi Tersedia" else "Kursi Penuh"
            binding.tvStatusKursi.text =
                if (angkotFull) "Status Kursi: Tersedia" else "Status Kursi: Penuh"
            binding.tvStatusKursi.setTextColor(
                if (angkotFull) resources.getColor(BSA_track) else resources.getColor(
                    BG_track
                )
            )
        }

        binding.btCallCenter.setOnClickListener {
            Utils.redirectWhatsapp(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, LocationService::class.java).putExtra("userId", userId))
    }

    private fun getNotifPermisision() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                100
            )
        }
    }
}