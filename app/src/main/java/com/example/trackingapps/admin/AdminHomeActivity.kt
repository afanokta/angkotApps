package com.example.trackingapps.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.trackingapps.LoginActivity
import com.example.trackingapps.databinding.ActivityAdminHomeBinding
import com.example.trackingapps.driver.SupirRegisterActivity
import com.example.trackingapps.user.LocationTrackingActivity
import com.example.trackingapps.user.TrackingAngkot
import com.example.trackingapps.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AdminHomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityAdminHomeBinding
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var dbRef: DatabaseReference
    lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        title = "Menu Admin"
        supportActionBar?.hide()
        mAuth = FirebaseAuth.getInstance()
        binding = ActivityAdminHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btLogOut.setOnClickListener {
            Utils.logoutHelper(this)
        }

        binding.btRuteTrayek.setOnClickListener {
            val intent = Intent(this, TrackActivity::class.java)
            startActivity(intent)
        }

        binding.btTambahPengemudi.setOnClickListener {
            startActivity(Intent(this, SupirRegisterActivity::class.java))
        }

        binding.btLiveTracking.setOnClickListener {
            startActivity(Intent(this, TrackingAngkot::class.java))
        }
    }
}