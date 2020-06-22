package com.azat.runningtracker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/*************************
 * Created by AZAT SAYAN *
 *                       *
 * Contact: @theazat     *
 *                       *
 * 20/06/2020 - 10:23 AM *
 ************************/
/* We just need to annotate this application class to mark  our application as injectable */
@HiltAndroidApp
class BaseApplication : Application()