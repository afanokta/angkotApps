package com.example.trackingapps.utils

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.trackingapps.LoginActivity
import com.example.trackingapps.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserChecking(var context: Context, var mAuth: FirebaseAuth) : AppCompatActivity() {
//    private var job: Job = Job()
//    var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
//    override val coroutineContext: CoroutineContext
//        get() = Dispatchers.Main + job
//
//    init {
//        mAuth = FirebaseAuth.getInstance()
//    }
//
//    fun UserCheck(): FirebaseUser? {
//        var user = mAuth.currentUser
//        return user
//    }
//
//    fun UserRolePermission(role: String?) {
//        var user = UserCheck()
//        if (user == null) {
//            val intent = Intent(context, LoginActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//        var userDb = getUser()
////        if () {
////
////        }
//    }
//
//    suspend fun getUser(): UserModel = suspendCoroutine { continuation ->
//        firebaseDatabase.getReference("users").child(user?.uid.toString()).get()
//            .addOnSuccessListener {
//                var userDb: UserModel = UserModel()
//                userDb = it.getValue(UserModel::class.java)!!
//                continuation.resume(userDb)
//            }
//    }
//
//    fun redirectToLogin() {
//
//    }

}