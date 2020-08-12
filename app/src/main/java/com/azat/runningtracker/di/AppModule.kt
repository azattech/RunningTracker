package com.azat.runningtracker.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.azat.runningtracker.db.RunDao
import com.azat.runningtracker.db.RunningDatabase
import com.azat.runningtracker.other.Constants.Companion.DATABASE_NAME
import com.azat.runningtracker.other.Constants.Companion.KEY_FIRST_TIME_TOGGLE
import com.azat.runningtracker.other.Constants.Companion.KEY_NAME
import com.azat.runningtracker.other.Constants.Companion.KEY_WEIGHT
import com.azat.runningtracker.other.Constants.Companion.SHARED_PREFERENCES_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
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

/**
 * AppModule, provides application wide singletons
 */
@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAppDb(app: Application): RunningDatabase {
        return Room.databaseBuilder(app, RunningDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideRunDao(db: RunningDatabase): RunDao {
        return db.getRunDao()
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(app: Application) =
        app.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideName(sharedPreferences: SharedPreferences) =
        sharedPreferences.getString(KEY_NAME, "") ?: ""

    @Singleton
    @Provides
    fun provideWeight(sharedPreferences: SharedPreferences) =
        sharedPreferences.getFloat(KEY_WEIGHT, 80f)

    @Singleton
    @Provides
    fun provideFirstTimeToggle(sharedPreferences: SharedPreferences) = sharedPreferences.getBoolean(
        KEY_FIRST_TIME_TOGGLE, true
    )

}