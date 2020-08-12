package com.azat.runningtracker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/*************************
 * Created by AZAT SAYAN *
 *                       *
 * Contact: @theazat     *
 *                       *
 * 16/06/2020 - 8:20 PM  *
 ************************/
@Database(
    entities = [Run::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class RunningDatabase : RoomDatabase() {

    abstract fun getRunDao(): RunDao
}