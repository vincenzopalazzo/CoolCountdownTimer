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
    private var finished: LiveData<Boolean> = _finished;

    /**
     * This method is treagger from the button
     * and the variable time is inizialized with this method
     */
    fun createAndRunTimer(valueTime: Long) {
        countDownTimer = object: CountDownTimer(valueTime, 100) {
            override fun onTick(millisUntilFinished: Long) {
                _time.value = millisUntilFinished
            }

            override fun onFinish() {
                _time.value = 0L;
            }
        }
        countDownTimer.start()
    }

    fun timerIsRunning(): Boolean{
        return finished.value == true
    }

    fun cancelTimer(){
        countDownTimer.cancel()
    }
}