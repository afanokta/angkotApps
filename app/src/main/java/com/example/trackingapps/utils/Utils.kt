package com.example.trackingapps.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.widget.Toast
import com.example.trackingapps.LoginActivity
import com.example.trackingapps.R
import com.google.firebase.auth.FirebaseAuth

object Utils {
    fun startNewActivity(context: Context, clazz: Class<*>) {
        val intent = Intent(context, clazz)
        context?.startActivity(intent)
//        context?.finish()
    }

    fun logoutHelper(context: Context) {
//        mAuth.signOut()
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
        val intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
        (context as Activity).finish()
        Toast.makeText(context, "Berhasil Log Out", Toast.LENGTH_SHORT).show()
    }

    fun redirectWhatsapp(context: Context){
        val contactNumber: String = context.getString(R.string.callcenter_number)
        val message: String = context.getString(R.string.message_whatsapp)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("http://api.whatsapp.com/send?phone=$contactNumber&text=$message")
        context.startActivity(intent)
    }
}