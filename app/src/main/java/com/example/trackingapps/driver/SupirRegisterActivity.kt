package com.example.trackingapps.driver

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.trackingapps.LoginActivity
import com.example.trackingapps.databinding.ActivitySupirRegisterBinding
import com.example.trackingapps.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.example.trackingapps.R
import com.google.firebase.database.*

class SupirRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySupirRegisterBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private var trackList = ArrayList<String>()
    private var kodeTrayek: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivitySupirRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

        binding.buttonRegister.setOnClickListener {
            val name = binding.editTextName.text.toString()
            val email = binding.editTextEmail.text.toString()
            val pass = binding.editTextPassword.text.toString()
            val platKendaraan = binding.etPlatKendaraan.text.toString()
            val user = UserModel(null, name, email, "PENGEMUDI", platKendaraan, kodeTrayek)
            if (name.isBlank() || email.isBlank() || pass.isBlank() || kodeTrayek.isBlank() || platKendaraan.isBlank()) {
                Toast.makeText(this, "Form Harus Diisi", Toast.LENGTH_LONG).show()
            } else {
                driverRegister(user, pass)
                val intent = Intent(this, DriverHomeActivity::class.java)
                startActivity(intent)
            }
        }
        getKodeTrayek()
    }

    private fun driverRegister(userModel: UserModel?, pass: String) {
        mAuth.createUserWithEmailAndPassword(userModel!!.email.toString(), pass)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Toast.makeText(this, "Akun Gagal Dibuat", Toast.LENGTH_SHORT).show()
                    Log.w("Registration", "createUserWithEmail:failure", task.exception)
                } else {
                    val user = mAuth.currentUser
                    userModel.uid = user!!.uid
                    db = firebaseDatabase.getReference("users")
                    db.child(userModel.uid.toString()).setValue(userModel)
                    Toast.makeText(this, "Akun Berhasil Dibuat", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun getKodeTrayek() {
        FirebaseDatabase.getInstance().getReference("trayek")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        trackList = ArrayList()
                        for (trackSnapshot in snapshot.children) {
                            val kodeTrayek = trackSnapshot.child("kodeTrayek").value
                            trackList.add(kodeTrayek.toString())
                        }
                        Log.w("TRACKLIST", trackList.toString())
                    }
                    setAdapterDropdown()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("ERROR", error.message)
                }

            })

    }

    fun setAdapterDropdown(){
        var adapter =
            ArrayAdapter(this, R.layout.kode_trayek_list, R.id.tv_kode_trayek_list, trackList)
        binding.autoCompleteTextView.setAdapter(adapter)
        binding.autoCompleteTextView.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, view, i, l ->
                val itemSelected = adapterView.getItemAtPosition(i)
                kodeTrayek = itemSelected.toString()
            }
    }
}