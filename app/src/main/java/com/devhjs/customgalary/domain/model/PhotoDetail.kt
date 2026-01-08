package com.devhjs.customgalary.domain.model

data class PhotoDetail(
    val uri: String,
    val filename: String,
    val dateTaken: String,
    val time: String,
    val fileSize: String,
    val resolution: String,
    val location: String? // "Lat, Lon" or null
)
