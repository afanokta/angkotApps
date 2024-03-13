package com.example.trackingapps.service

import android.Manifest
import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.trackingapps.driver.DriverHomeActivity
//import com.example.trackingapps.Manifest
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import com.example.trackingapps.model.Location as LocationModel

class LocationService : Service() {
    private lateinit var dbRef: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private var isServiceRunning = false
    private lateinit var currentLocation: MyLocationListener
    private var userId: String? = ""
    private var kodeTrayek: String? = ""
    private var angkotFull: Boolean? = false
    private lateinit var extras: Bundle
    private lateinit var notificationManager: NotificationManager
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        dbRef = FirebaseDatabase.getInstance().getReference("location")
        isServiceRunning = true
        sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        currentLocation = MyLocationListener()
        currentLocation.getUserLocation()
        createNotificationChannel()
        showNotification()
    }

    private fun showNotification() {
        val intent = Intent(this, DriverHomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        var builder = NotificationCompat.Builder(this, "notif_channel")
            .setSmallIcon(R.drawable.notification_icon_background)
            .setContentTitle("Berbagi Lokasi Aktif")
            .setContentText("Anda sedang berbagi lokasi untuk supir angkutan")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Anda sedang berbagi lokasi untuk supir angkutan")
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH).build()
        builder.flags = Notification.FLAG_NO_CLEAR
        notificationManager.notify(1234, builder)
        startForeground(1, builder)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        extras = intent?.extras!!
        userId = extras.getString("userId")
        kodeTrayek = extras.getString("kodeTrayek")
        angkotFull = sharedPreferences.getBoolean("angkotFull", false   )
        Log.w("USERID", userId.toString())
        Thread(
            Runnable {
                kotlin.run {
                    while (true) {
                        try {
                            var lat = currentLocation.myLocation?.latitude
                            var long = currentLocation.myLocation?.longitude
                            val df = SimpleDateFormat("yyyy-MMM-dd HH:mm:ss")
                            val date = Date()
                            Log.w("latitude", "lat: $lat - long: $long at: $date")
                            val location =
                                LocationModel(
                                    userId.toString(),
                                    kodeTrayek.toString(),
                                    lat,
                                    long,
                                    df.format(date),
                                    isServiceRunning,
                                    angkotFull
                                )
                            dbRef.child(userId!!).setValue(location)
                        } catch (e: Exception) {
                            Log.w("ERROR", e.message.toString())
                        }
                        if (!isServiceRunning) {
                            break
                        }
                        Thread.sleep(10000)
                    }
                }
            }
        ).start()
//        return super.onStartCommand(intent, flags, startId)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceRunning = false
        destroyNotification()
        Toast.makeText(applicationContext, "berhenti berbagi", Toast.LENGTH_SHORT).show()
        Log.w("SERVICE STOPPED", "service berhenti")
        stopSelf()
    }

    inner class MyLocationListener : LocationListener {
        var myLocation: Location? = null

        constructor() : super() {
            myLocation = Location("me")

        }

        fun getUserLocation() {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
        }

        override fun onLocationChanged(location: Location) {
            myLocation = location
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Tracking Apps"
            val descriptionText = "ini deskripsi"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("notif_channel", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        Log.w("notifManager", notificationManager.toString())
    }

    private fun destroyNotification() {
        if (notificationManager != null) {
            notificationManager.cancelAll()
        }
    }

}