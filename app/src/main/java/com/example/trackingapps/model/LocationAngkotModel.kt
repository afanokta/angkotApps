package com.example.trackingapps.model

import java.util.*

data class LocationAngkotModel(
    var name: String? = "",
    var CarId: String = "",
    var lat: Double? = 0.0,
    var long: Double? = 0.0,
    var lastUpdate: String? = Date().toString(),
    var tracking: Boolean? = false,
    var platKendaraan: String? = "",
    var kodeTrayek: String? = "",
    var angkotFull: Boolean? = false,
)
