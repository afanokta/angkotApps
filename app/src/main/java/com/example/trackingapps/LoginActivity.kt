package com.example.trackingapps

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import com.example.trackingapps.admin.AdminHomeActivity
import com.example.trackingapps.databinding.LoginPageBinding
import com.example.trackingapps.driver.DriverHomeActivity
import com.example.trackingapps.model.UserModel
import com.example.trackingapps.user.HomeActivity
import com.example.trackingapps.user.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: LoginPageBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var userData: UserModel
    var userRole: String? = null
    var userEmail: String? = null
    var userUid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.FOREGROUND_SERVICE,
            ),
            0
        )
        supportActionBar?.hide()
        binding = LoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        userRole = sharedPreferences.getString("role", null)
        userEmail = sharedPreferences.getString("email", null)
        userUid = sharedPreferences.getString("uid", null)
        binding.TvRegister.setOnClickListener {
            var intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val pass = binding.editTextPassword.text.toString()
            if (email.isBlank() || pass.isBlank()) {
                Toast.makeText(this, "Form Tidak Boleh Kosong!!", Toast.LENGTH_SHORT).show()
            } else {
                login(email, pass)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (userEmail != null && userRole != null) {
            lateinit var activity: Class<*>
            when (userRole) {
                "PENUMPANG" -> activity = HomeActivity::class.java
                "PENGEMUDI" -> activity = DriverHomeActivity::class.java
                "ADMIN" -> activity = AdminHomeActivity::class.java
            }
            val intent = Intent(this, activity)
            startActivity(intent)
            finish()
        }
    }

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                var dbRef = firebaseDatabase.getReference("users").child(user?.uid.toString())
                dbRef.get().addOnSuccessListener {
                    userData = it.getValue(UserModel::class.java)!!
                    val editor = sharedPreferences.edit()
                    editor.putString("email", userData.email)
                    editor.putString("name", userData.name)
                    editor.putString("uid", userData.uid)
                    editor.putString("role", userData.role)
                    editor.putString("kodeTrayek", userData.kodeTrayek)
                    editor.apply()
                    Toast.makeText(this, "Login Berhasil", Toast.LENGTH_LONG).show()
                    finish()
                    startActivity(intent)
                }.addOnFailureListener {
                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                    Log.w("ERROR", it.message.toString())
                }
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Login Error", Toast.LENGTH_LONG).show()
        }
    }
}