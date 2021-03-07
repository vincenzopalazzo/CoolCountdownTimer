/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge.model

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * @author https://github.com/vincenzopalazzo
 */
class TimerViewModel : ViewModel() {

    private var _time: MutableLiveData<Long> = MutableLiveData(0L)
    private lateinit var countDownTimer: CountDownTimer

    var time: LiveData<Long> = _time
    private var _finished: MutableLiveData<Boolean> = MutableLiveData(true)
    private var finished: LiveData<Boolean> = _finished

    /**
     * This method is treagger from the button
     * and the variable time is inizialized with this method
     */
    fun createAndRunTimer(valueTime: Long) {
        countDownTimer = object : CountDownTimer(valueTime, 10) {
            override fun onTick(millisUntilFinished: Long) {
                _time.value = millisUntilFinished
            }

            override fun onFinish() {
                _time.value = 0L
                _finished.value = true
            }
        }
        countDownTimer.start()
        _finished.value = false
    }

    fun timerIsRunning(): Boolean {
        return finished.value == false
    }

    fun cancelTimer() {
        countDownTimer.cancel()
    }
}
