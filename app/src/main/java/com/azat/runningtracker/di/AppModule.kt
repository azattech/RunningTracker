package com.azat.runningtracker.di

import android.content.Context
import androidx.room.Room
import com.azat.runningtracker.db.RunningDatabase
import com.azat.runningtracker.other.Constants.RUNNING_DATABASE_NAME
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
}