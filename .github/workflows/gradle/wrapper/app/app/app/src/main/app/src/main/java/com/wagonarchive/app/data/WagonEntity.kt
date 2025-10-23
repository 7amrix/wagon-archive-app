package com.wagonarchive.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wagons")
data class WagonEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val serialNumber: String,
    val date: Long,
    val location: String,
    val notes: String,
    val status: String
)
