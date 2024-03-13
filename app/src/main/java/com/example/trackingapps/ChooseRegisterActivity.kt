package com.example.trackingapps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.trackingapps.databinding.ActivityChooseRegisterBinding
import com.example.trackingapps.driver.SupirRegisterActivity
import com.example.trackingapps.user.RegisterActivity

class ChooseRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btPenumpang.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btSupir.setOnClickListener{
            val intent = Intent(this, SupirRegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}