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
package io.vincenzopalazzo.coolcountdown.utils

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Locale

enum class LevelTime(private val level: String) {
    GOOD("GOOD"), // There is enough time,
    NORMAL("NORMAL"), // Orange last minutes
    DANGER("DANGER"); // Last 30 seconds

    override fun toString(): String {
        return "LevelTime(level='$level')"
    }
}
/**
 * @author https://github.com/vincenzopalazzo
 */
object Utils {

    private var TAG: String = Utils::class.qualifiedName!!

    fun formattingTimeStamp(timestamp: Long, pattern: String = "hh:mm:ss"): String {
        val dateFormat = SimpleDateFormat(pattern, Locale.ENGLISH)
        return dateFormat.format(timestamp)
    }

    fun formattingString(horus: Int, minutes: Int, seconds: Int, milliseconds: Int): String {
        var stringHours = horus.toString()
        var stringMinutes = minutes.toString()
        var stringSecond = seconds.toString()
        var stringMilli = milliseconds.toString()
        if (horus < 9) {
            stringHours = "0%d".format(horus)
        }
        if (minutes < 9)
            stringMinutes = "0%d".format(minutes)
        if (seconds < 9)
            stringSecond = "0%d".format(seconds)
        if (milliseconds < 9)
            stringMilli = "0%d".format(milliseconds)
        return "%s:%s:%s".format(stringHours, stringMinutes, stringSecond)
    }

    fun checkMissingTime(horus: Int, minutes: Int, seconds: Int, milliseconds: Int): LevelTime {
        if (horus == 0 && minutes == 0) {
            if (seconds <= 30) {
                Log.d(TAG, LevelTime.DANGER.toString())
                return LevelTime.DANGER
            } else if (seconds <= 60) {
                Log.d(TAG, LevelTime.NORMAL.toString())
                return LevelTime.NORMAL
            }
        }
        return LevelTime.GOOD
    }
}
