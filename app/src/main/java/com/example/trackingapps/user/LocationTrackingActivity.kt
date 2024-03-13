package com.example.trackingapps.user

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.example.trackingapps.R
import com.example.trackingapps.model.LocationAngkotModel
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.example.trackingapps.utils.LocationPermissionHelper
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.observable.eventdata.MapLoadingErrorEventData
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.AnnotationPlugin
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.*
import com.mapbox.maps.plugin.delegates.listeners.OnMapLoadErrorListener
import java.lang.ref.WeakReference
import java.time.*
import java.time.format.DateTimeFormatter

/**
 * Tracks the user location on screen, simulates a navigation session.
 */
class LocationTrackingActivity : AppCompatActivity() {

    private lateinit var locationPermissionHelper: LocationPermissionHelper
    private lateinit var locationList: ArrayList<LocationAngkotModel>
    private lateinit var dbRef: DatabaseReference
    private lateinit var mapView: MapView
    private lateinit var annotationConfig: AnnotationConfig
    private lateinit var pointAnnotationManager: PointAnnotationManager
    private lateinit var location: LocationAngkotModel
    var annotationApi: AnnotationPlugin? = null
    var markerList: ArrayList<PointAnnotationOptions> = ArrayList()


    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(it).build())
    }

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder().center(Point.fromLngLat(112.525049, -7.870738)).build()
        )
        mapView.gestures.focalPoint = mapView.getMapboxMap().pixelForCoordinate(it)
    }

    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
//            onCameraTrackingDismissed()
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapView = MapView(this)
        setContentView(mapView)
        locationPermissionHelper = LocationPermissionHelper(WeakReference(this))
        locationPermissionHelper.checkPermissions {
            onMapReady()
        }
        locationList = arrayListOf<LocationAngkotModel>()
        dbRef = FirebaseDatabase.getInstance().getReference("location")
    }

    private fun onMapReady() {
        mapView.getMapboxMap().setCamera( //setting camera map
            CameraOptions.Builder()
                .zoom(12.0)
                .build()
        )
        mapView.getMapboxMap().loadStyleUri(
            "mapbox://styles/afanokta/clrmbao0b004501pe31ongvck",
            object : Style.OnStyleLoaded {
                override fun onStyleLoaded(style: Style) {
                    initLocationComponent()
                    setupGesturesListener()
                    getLocationList()
                }
            },
            object : OnMapLoadErrorListener {
                override fun onMapLoadError(eventData: MapLoadingErrorEventData) {
                    Toast.makeText(baseContext, "Map Error", Toast.LENGTH_SHORT).show()
                }

            }

        )
    }

    private fun setupGesturesListener() {
        mapView.gestures.addOnMoveListener(onMoveListener)
    }

    private fun initLocationComponent() {
        val locationComponentPlugin = mapView.location
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.locationPuck = LocationPuck2D(
                bearingImage = AppCompatResources.getDrawable(
                    this@LocationTrackingActivity,
                    R.drawable.mapbox_user_puck_icon,
                ),
                shadowImage = AppCompatResources.getDrawable(
                    this@LocationTrackingActivity,
                    R.drawable.mapbox_user_icon_shadow,
                ),
                scaleExpression = interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0.0)
                        literal(0.6)
                    }
                    stop {
                        literal(20.0)
                        literal(1.0)
                    }
                }.toJson()
            )
        }
        locationComponentPlugin.addOnIndicatorPositionChangedListener(
            onIndicatorPositionChangedListener
        )
        locationComponentPlugin.addOnIndicatorBearingChangedListener(
            onIndicatorBearingChangedListener
        )
    }

    private fun onCameraTrackingDismissed() {
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun getLocationList() {
        Log.w("TEST", "TEST from getlocationlist")
        initiateAnnotation()
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    clearAnnotation()
                    locationList = ArrayList()
                    for (locationSnapshot in snapshot.children) {
                        location = locationSnapshot.getValue(LocationAngkotModel::class.java)!!
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd HH:mm:ss")
                        var dt = LocalDateTime.parse(location?.lastUpdate, formatter)
                            .toEpochSecond(ZoneId.systemDefault().rules.getOffset(Instant.now()))
                        val localUnix = System.currentTimeMillis() / 1000
                        var minus = localUnix - dt
                        var bool = minus <= 10
                        if (!bool) {
                            continue
                        }
                        if (location.tracking == true) {
                            locationList.add(location)
                            Log.w("LocationModelAngkot", location.toString())
                        }
                    }
                    addMarkerToMap()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    applicationContext,
                    "Gagal Mendapatkan Data Lokasi",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        )
    }

    private fun initiateAnnotation() {
        annotationApi = mapView.annotations
        annotationConfig = AnnotationConfig(
            layerId = "map_annotation"
        )
        pointAnnotationManager = annotationApi?.createPointAnnotationManager(annotationConfig)!!
    }

    private fun clearAnnotation() {
        markerList = ArrayList()
        pointAnnotationManager.deleteAll()
    }

    fun addMarkerToMap() {
        clearAnnotation()
        pointAnnotationManager.addClickListener(OnPointAnnotationClickListener { annotation: PointAnnotation ->
            onClickMarker(annotation)
            true
        })
        for (i in 0 until locationList.size) {
            val kodeTrayek = locationList[i].kodeTrayek.toString()
            var ic_angkot: Int
            when (kodeTrayek) {
                "BSS" -> ic_angkot = R.drawable.ic_angkot_bss
                "BSA" -> ic_angkot = R.drawable.ic_angkot_bsa
                "BSB" -> ic_angkot = R.drawable.ic_angkot_bsb
                "BB" -> ic_angkot = R.drawable.ic_angkot_bb
                "BG" -> ic_angkot = R.drawable.ic_angkot_bg
                "BL" -> ic_angkot = R.drawable.ic_angkot_bl
                "BJL" -> ic_angkot = R.drawable.ic_angkot_bjl
                "BTL" -> ic_angkot = R.drawable.ic_angkot_btl
                "BK" -> ic_angkot = R.drawable.ic_angkot_bk
                else -> ic_angkot = R.drawable.ic_angkot_24
            }
            Log.w("ICON TRAYEK", kodeTrayek)
            val locationIcon = bitmapFromDrawableRes(baseContext, ic_angkot, kodeTrayek)
            var jsonObject = Gson().toJson(locationList[i])
            val pointAnnotationOptions: PointAnnotationOptions =
                PointAnnotationOptions()
                    .withPoint(
                        Point.fromLngLat(
                            locationList[i].long!!,
                            locationList[i].lat!!
                        )
                    )
                    .withData(
                        Gson().fromJson(
                            jsonObject.toString(),
                            JsonElement::class.java
                        )
                    )
                    .withIconImage(locationIcon!!)
            markerList.add(pointAnnotationOptions)
        }
        pointAnnotationManager.create(markerList)
    }

    fun onClickMarker(marker: PointAnnotation) {
        var jsonElement: JsonElement = marker.getData()!!
        var data = Gson().fromJson(jsonElement, LocationAngkotModel::class.java)
        FirebaseDatabase.getInstance().getReference("users")
            .child(data.CarId).get()
            .addOnSuccessListener {
                data.kodeTrayek =
                    it.child("kodeTrayek").getValue(String::class.java)
                data.name =
                    it.child("name").getValue(String::class.java)
                data.platKendaraan =
                    it.child("platKendaraan").getValue(String::class.java)

                AlertDialog.Builder(this)
                    .setTitle("Detail Angkot")
                    .setMessage(
                        "Nama Supir\t: ${data.name}\n" +
                                "Plat Kendaraan\t: ${data.platKendaraan}\n" +
                                "Kode Trayek\t: ${data.kodeTrayek} \n" +
                                "Terakhir Diupdate\t: ${data.lastUpdate}\n"
                    )
                    .setPositiveButton("OK") { dialog, whichButton ->
                        dialog.dismiss()
                    }.show()

            }.addOnFailureListener {
                Log.w("ERROR-FROM-DETAIL", it.message.toString())
            }
    }

    fun bitmapFromDrawableRes(
        context: Context,
        @DrawableRes resourceId: Int,
        kodeTrayek: String
    ): Bitmap? {
        var sourceDrawable = AppCompatResources.getDrawable(context, resourceId) ?: return null
        if (sourceDrawable is BitmapDrawable) return sourceDrawable.bitmap
        val constantState = sourceDrawable.constantState ?: return null
        val drawable = constantState.newDrawable().mutate()
        val bitmap: Bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth, drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}