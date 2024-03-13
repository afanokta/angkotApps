package com.example.trackingapps.model

import java.util.Date

data class Location(
    var CarId: String = "",
    var kodeTrayek: String? = "",
    var lat: Double? = 0.0,
    var long: Double? = 0.0,
    var lastUpdate: String? = Date().toString(),
    var tracking: Boolean? = false,
    var angkotFull: Boolean? = false
)
