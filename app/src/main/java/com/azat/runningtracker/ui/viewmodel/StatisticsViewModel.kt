package com.azat.runningtracker.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.azat.runningtracker.repository.MainRepository

/*************************
 * Created by AZAT SAYAN *
 *                       *
 * Contact: @theazat     *
 *                       *
 * 22/06/2020 - 10:24 PM  *
 ************************/
class StatisticsViewModel @ViewModelInject constructor(
    val mainRepository: MainRepository
) : ViewModel()