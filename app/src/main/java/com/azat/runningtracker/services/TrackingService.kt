package com.azat.runningtracker.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.observe
import com.azat.runningtracker.R
import com.azat.runningtracker.other.Constants
import com.azat.runningtracker.other.Constants.Companion.ACTION_PAUSE_SERVICE
import com.azat.runningtracker.other.Constants.Companion.ACTION_START_OR_RESUME_SERVICE
import com.azat.runningtracker.other.Constants.Companion.ACTION_STOP_SERVICE
import com.azat.runningtracker.other.Constants.Companion.FASTEST_LOCATION_UPDATE_INTERVAL
import com.azat.runningtracker.other.Constants.Companion.LOCATION_UPDATE_INTERVAL
import com.azat.runningtracker.other.Constants.Companion.NOTIFICATION_CHANNEL_ID
import com.azat.runningtracker.other.Constants.Companion.NOTIFICATION_CHANNEL_NAME
import com.azat.runningtracker.other.Constants.Companion.NOTIFICATION_ID
import com.azat.runningtracker.other.TrackingUtility
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/*************************
 * Created by AZAT SAYAN *
 *                       *
 * Contact: @theazat     *
 *                       *
 * 07/07/2020 - 8:05 AM  *
 ************************/
/* Why we extends form LifecycleService?
* we will need to observe from life to the objects inside of this service class
* The observed function of a live data object needs the lifecycle owner
* and if we don't specify this LifecycleService() here we can't pass
* an instance of this service as lifecycle owner to that observed function
*
* We want to manage the communication from our activity to our service so on
* the one hand we have the activity to service communication or fragment to
* service communication and for that we will simply use intents, so whenever
* we want to send a command to our service then we will simply send an intent
* to that service with an action attached and inside of this service class then check
* what that action is.
*
* Also we need to worry about the communication from our service to our
* TrackingFragment because we need to to coordinate of the user data.
* So that we basically have two major options to do that.
* One option is Singleton pattern.
* The other option would be to make this service a bound service so that
* basically means that the service acts as kind of server and clients can
* bind to it so the client would be our fragment in this case but
* that is much more work and Singleton pattern makes this really much easier.
* */
typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

@AndroidEntryPoint
class TrackingService : LifecycleService() {

    private val timeRunInSeconds = MutableLiveData<Long>()

    private var isFirstRun = true
    private var serviceKilled = false

    companion object {
        val timeRunInMillis = MutableLiveData<Long>()
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<Polylines>()
    }

    /**
     * Base notification builder that contains the settings every notification will have
     */
    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    /**
     * Builder of the current notification
     */
    private lateinit var curNotification: NotificationCompat.Builder

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate() {
        super.onCreate()
        curNotification = baseNotificationBuilder
        postInitialValues()

        isTracking.observe(this) {
            updateNotificationTrackingState(it)
            updateLocationChecking(it)
        }
    }

    private fun postInitialValues() {
        timeRunInMillis.postValue(0L)
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
        timeRunInSeconds.postValue(0L)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                        serviceKilled = false
                    } else {
                        startTimer()
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("Paused Service")
                    pauseService()
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("Stopped service.")
                    killService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * Stops the service properly.
     */
    private fun killService() {
        serviceKilled = true
        isFirstRun = true
        pauseService()
        postInitialValues()
        stopForeground(true)
        stopSelf()
    }

    /**
     * Enables or disables location tracking according to the tracking state.
     */
    @SuppressLint("MissingPermission")
    private fun updateLocationChecking(isTracking: Boolean) {
        if (isTracking) {
            if (TrackingUtility.hasLocationPermissions(this)) {
                val request = LocationRequest().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_LOCATION_UPDATE_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    /**
     * Location Callback that receives location updates and adds them to pathPoints.
     */
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            if (isTracking.value!!) {
                result?.locations?.let { locations ->
                    for (location in locations) {
                        addPathPoint(location)
                    }
                }
            }
        }
    }

    private var isTimerEnabled = false
    private var lapTime = 0L // time since we started the timer
    private var timeRun = 0L // total time of the timer
    private var timeStarted = 0L // the time when we started the timer
    private var lastSecondTimestamp = 0L

    /**
     * Starts the timer for the tracking.
     */
    private fun startTimer() {
        addEmptyPolyline()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true
        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!) {
                // time difference between now and time started
                lapTime = System.currentTimeMillis() - timeStarted
                // post the new laptime
                timeRunInMillis.postValue(timeRun + lapTime)
                // if a new second was reached, we want to update timeRunInSeconds, too
                if (timeRunInMillis.value!! >= lastSecondTimestamp + 1000L) {
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
                    lastSecondTimestamp += 1000L
                }
                delay(Constants.TIMER_UPDATE_INTERVAL)
            }
            timeRun += lapTime
        }
    }

    /**
     * Disables the timer and tracking.
     */
    private fun pauseService() {
        isTimerEnabled = false
        isTracking.postValue(false)
    }

    /**
     * This adds the location to the last list of pathPoints.
     */
    private fun addPathPoint(location: Location?) {
        location?.let {
            val pos = LatLng(location.latitude, location.longitude)
            pathPoints.value?.apply {
                last().add(pos)
                pathPoints.postValue(this)
            }
        }
    }

    /**
     * Will add an empty polyline in the pathPoints list or initialize it if empty.
     */
    private fun addEmptyPolyline() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))

    /**
     * Starts this service as a foreground service and creates the necessary notification
     */
    private fun startForegroundService() {
        Timber.d("TrackingService started.")

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        startForeground(NOTIFICATION_ID, curNotification.build())
        //curNotification = curNotification.setContentIntent(getActivityPendingIntent())
        startTimer()
        isTracking.postValue(true)

        // updating notification
        timeRunInSeconds.observe(this) {
            if (!serviceKilled) {
                val notification = curNotification
                    .setContentText(TrackingUtility.getFormattedStopWatchTime(it * 1000L))
                notificationManager.notify(NOTIFICATION_ID, notification.build())
            }
        }
    }

    /**
     * Updates the action buttons of the notification
     */
    @SuppressLint("RestrictedApi")
    private fun updateNotificationTrackingState(isTracking: Boolean) {
        val notificationActionText = if (isTracking) "Pause" else "Resume"
        val pendingIntent = if (isTracking) {
            val pauseIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_PAUSE_SERVICE
            }
            PendingIntent.getService(this, 1, pauseIntent, FLAG_UPDATE_CURRENT)
        } else {
            val resumeIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_START_OR_RESUME_SERVICE
            }
            PendingIntent.getService(this, 2, resumeIntent, FLAG_UPDATE_CURRENT)
        }
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        curNotification.mActions.clear()

        if (!serviceKilled) {
            curNotification = baseNotificationBuilder
                .addAction(R.drawable.ic_pause_black_24dp, notificationActionText, pendingIntent)
            notificationManager.notify(NOTIFICATION_ID, curNotification.build())
        }
    }

    @SuppressLint("RestrictedApi")
    private fun updateNotificationTrackingState2(isTracking: Boolean) {
        val notificationActionText = if (isTracking) "Pause" else "Resume"
        val pendingIntent = PendingIntent.getService(
            this,
            if (isTracking) 1 else 2,
            Intent(this, TrackingService::class.java).apply {
                action = if (isTracking) ACTION_PAUSE_SERVICE else ACTION_START_OR_RESUME_SERVICE
            },
            FLAG_UPDATE_CURRENT
        )

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        curNotification.mActions.clear()
        if (!serviceKilled) {

            curNotification = baseNotificationBuilder

                .addAction(R.drawable.ic_pause_black_24dp, notificationActionText, pendingIntent)

            notificationManager.notify(NOTIFICATION_ID, curNotification.build())

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel =
            NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }
}