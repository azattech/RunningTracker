package com.azat.runningtracker.db

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

/*************************
 * Created by AZAT SAYAN *
 *                       *
 * Contact: @theazat     *
 *                       *
 * 14/06/2020 - 8:07 PM  *
 ************************/
@Entity(tableName = "running_table")
data class Run(
    var img: Bitmap? = null,
    var timestamp: Long = 0L,
    var avgSpeedInKMH: Float = 0f,
    var distanceInMeters: Int = 0,
    var timeInMillis: Long = 0,
    var caloriesBurned: Int = 0
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
