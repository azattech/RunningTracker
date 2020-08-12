package com.azat.runningtracker.repository

import com.azat.runningtracker.db.Run
import com.azat.runningtracker.db.RunDao
import javax.inject.Inject

/*************************
 * Created by AZAT SAYAN *
 *                       *
 * Contact: @theazat     *
 *                       *
 * 22/06/2020 - 10:11 PM *
 ************************/
class MainRepository @Inject constructor(
    val runDao: RunDao
) {
    suspend fun insertRun(run: Run) = runDao.insertRun(run)

    suspend fun deleteRun(run: Run) = runDao.deleteRun(run)

    //ders 7 dakika 4.20 tekrar et
    fun getAllRunsSortedByDate() = runDao.getAllRunsSortedByDate()

    fun getAllRunsSortedByTimeInMillis() = runDao.getAllRunsSortedByTimeInMillis()

    fun getAllRunsSortedByDistance() = runDao.getAllRunsSortedByDistance()

    fun getAllRunsSortedByCaloriesBurned() = runDao.getAllRunsSortedByCaloriesBurned()

    fun getAllRunsSortedByAvgSpeed() = runDao.getAllRunsSortedByAvgSpeed()

    fun getTotalDistance() = runDao.getTotalDistance()

    fun getTotalTimeInMillis() = runDao.getTotalTimeInMillis()

    fun getTotalAvgSpeed() = runDao.getTotalAvgSpeed()

    fun getTotalCaloriesBurned() = runDao.getTotalCaloriesBurned()
}