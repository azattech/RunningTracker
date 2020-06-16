package com.azat.runningtracker.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverters
import java.io.ByteArrayOutputStream

/*************************
 * Created by AZAT SAYAN *
 *                       *
 * Contact: @theazat     *
 *                       *
 * 16/06/2020 - 8:14 PM  *
 ************************/
class Converters {

    @TypeConverters
    fun toBitmap(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    @TypeConverters
    fun fromBitmap(bmp: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }
}