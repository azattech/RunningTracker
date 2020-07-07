package com.azat.runningtracker.services

import android.content.Intent
import androidx.lifecycle.LifecycleService
import com.azat.runningtracker.other.Constants.ACTION_PAUSE_SERVICE
import com.azat.runningtracker.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.azat.runningtracker.other.Constants.ACTION_STOP_SERVICE
import timber.log.Timber

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
* Soı that we basically have two major options to do that.
* One option is Singleton pattern.
* The other option would be to make this service a bound service so that
* basically means that the service acts as kind of server and clients can
* bind to it so the client would be our fragment in this case but
* that is much more work and Singleton pattern makes this really much easier.
* */
class TrackingService : LifecycleService() {

    // whenever we send a command to our service so whenever we send an Intent with an action attached to this service class
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    Timber.d("Started or resume service")
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("Paused service")
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("Stopped service")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}