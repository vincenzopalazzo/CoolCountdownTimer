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
package io.vincenzopalazzo.coolcountdown.model

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
    var finished: LiveData<Boolean> = _finished

    var millisecond = 0
    var seconds = 0
    var minutes = 0
    var hours = 0

    /**
     * This method is treagger from the button
     * and the variable time is inizialized with this method
     */
    fun createAndRunTimer(valueTime: Long) {
        countDownTimer = object : CountDownTimer(valueTime, 1) {
            override fun onTick(millisUntilFinished: Long) {
                _time.value = millisUntilFinished
                convertMillisecondInTimeObject(millisUntilFinished)
            }

            override fun onFinish() {
                _time.value = 0L
                _finished.value = true
                seconds = 0
                minutes = 0
                hours = 0
                millisecond = 0
            }
        }
        _finished.value = false
        countDownTimer.start()
    }

    // Source https://stackoverflow.com/a/22641900/10854225
    // FIXME(vincenzopalazzo): Improve this view with a more clean logic
    private fun convertMillisecondInTimeObject(millis: Long) {
        this.millisecond = (millis / 1000).toInt() % 10
        this.seconds = (millis / 1000).toInt() % 60
        this.minutes = (millis / (1000 * 60) % 60).toInt()
        this.hours = (millis / (1000 * 60 * 60) % 24).toInt()
    }

    fun timerIsRunning(): Boolean {
        return finished.value == false
    }

    fun cancelTimer() {
        _finished.value = true
        _time.value = 0L
        countDownTimer.cancel()
    }
}
