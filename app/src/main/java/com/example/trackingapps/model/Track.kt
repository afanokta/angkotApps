package com.example.trackingapps.model

import java.io.Serializable

data class Track(
    var uid: String = "",
    var izinArmada: Int? = 0,
    var jumlahArmada: Int? = 0,
    var kodeTrayek: String? = null,
    var trayek: String? = null,
    var tarifPelajar: Int? = 0,
    var tarifUmum: Int? = 0,
) : Serializable