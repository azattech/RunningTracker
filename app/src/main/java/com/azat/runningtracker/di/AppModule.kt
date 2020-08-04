package com.azat.runningtracker.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.azat.runningtracker.db.RunningDatabase
import com.azat.runningtracker.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.azat.runningtracker.other.Constants.KEY_NAME
import com.azat.runningtracker.other.Constants.KEY_WEIGHT
import com.azat.runningtracker.other.Constants.RUNNING_DATABASE_NAME
import com.azat.runningtracker.other.Constants.SHARED_PREFERENCES_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

/*************************
 * Created by AZAT SAYAN *
 *                       *
 * Contact: @theazat     *
 *                       *
 * 20/06/2020 - 10:36 AM  *
 ************************/

/** We want to able to inject our RunningDatabase into our Repository.
 * If want to inject that RunningDatabase then Dagger of course needs
 * to know to create that so it somehow needs a manual by us
 * how to create a RunningDatabase so it can inject that into our Repository
 * later on and because only we know how to create that RunningDatabase instance
 * we need to tell the Dagger and give Dagger kind of that manual so Dagger
 * also knows how to created and these manuals are put into so called modules files/classes
 *
 * Ders 6'yı tekrar et
 * */

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRunningDatabase(@ApplicationContext app: Context) = Room.databaseBuilder(
        app,
        RunningDatabase::class.java,
        RUNNING_DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideRunDao(db: RunningDatabase) = db.getRunDao()

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app: Context) =
        app.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideName(sharedPref: SharedPreferences) = sharedPref.getString(KEY_NAME, "") ?: ""

    @Singleton
    @Provides
    fun provideWeight(sharedPref: SharedPreferences) = sharedPref.getFloat(KEY_WEIGHT, 80f)

    @Singleton
    @Provides
    fun provideFirsTimeToggle(sharedPref: SharedPreferences) =
        sharedPref.getBoolean(KEY_FIRST_TIME_TOGGLE, true)

}