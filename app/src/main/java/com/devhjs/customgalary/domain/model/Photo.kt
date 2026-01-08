package com.devhjs.customgalary.domain.model

data class Photo(
    val id: Long,
    val uri: String,
    val dateTaken: Long,
    val name: String
)
