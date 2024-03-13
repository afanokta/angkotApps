package com.example.trackingapps.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.trackingapps.LoginActivity
import com.example.trackingapps.databinding.ActivityRegisterBinding
import com.example.trackingapps.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseDatabase = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()




        binding.buttonRegister.setOnClickListener {
            var pass = binding.editTextPassword.text.toString()
            var passConfirm = binding.editTextPasswordConfirm.text.toString()
            var email = binding.editTextEmail.text.toString()
            Log.w("FORM", pass.toString() + passConfirm + email)
            if (pass.isBlank() || passConfirm.isBlank() || email.isBlank()) {
                Toast.makeText(this, "Form Tidak Boleh Kosong !!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pass != passConfirm) {
                Toast.makeText(this, "Password Tidak Sama", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerUser(email, pass)
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.TVlogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }


    fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val name = binding.editTextName.text.toString()
                    val userSave = UserModel(
                        user?.uid.toString(),
                        name,
                        user?.email.toString(),
                        "PENUMPANG",
                        null,
                        null
                    )
                    db = firebaseDatabase.getReference("users")
                    db.child(user?.uid.toString()).setValue(userSave)
                    Toast.makeText(this, "Akun Berhasil Dibuat", Toast.LENGTH_SHORT).show()
                } else {
                    // Registration failed
                    Toast.makeText(this, "Akun Gagal Dibuat", Toast.LENGTH_SHORT).show()
                    Log.w("Registration", "createUserWithEmail:failure", task.exception)
                }
            }

    }
}