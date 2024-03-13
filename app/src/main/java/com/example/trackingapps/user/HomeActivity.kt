package com.example.trackingapps.user

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.trackingapps.databinding.ActivityHomeBinding
import com.example.trackingapps.model.UserModel
import com.example.trackingapps.utils.Utils
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.example.trackingapps.R
import com.example.trackingapps.admin.TrackActivity

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    internal var user: UserModel? = null
    private lateinit var sharedPreferences: SharedPreferences
    private var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        binding.tvMenuPengemudi.text = binding.tvMenuPengemudi.text.toString()
            .replace("{Nama}", sharedPreferences.getString("name", null)!!)
            .replace("{Role}", sharedPreferences.getString("role", null)!!)

        binding.btnLogout.setOnClickListener {
            Utils.logoutHelper(this)
        }
        binding.btTrack.setOnClickListener {
            val intent = Intent(this, TrackingAngkot::class.java)
            startActivity(intent)
        }

        binding.btInfoTrayek.setOnClickListener {
            val intent = Intent(this, TrackActivity::class.java)
            startActivity(intent)
        }

        binding.btCallCenter.setOnClickListener {
            Utils.redirectWhatsapp(this)
        }
    }
}