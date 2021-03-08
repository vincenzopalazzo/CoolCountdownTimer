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

import java.text.SimpleDateFormat
import java.util.Locale

/**
 * @author https://github.com/vincenzopalazzo
 */
object Utils {

    fun formattingTimeStamp(timestamp: Long, pattern: String = "hh:mm:ss"): String {
        val dateFormat = SimpleDateFormat(pattern, Locale.ENGLISH)
        return dateFormat.format(timestamp)
    }

    fun formattingString(horus: Int, minutes: Int, seconds: Int): String {
        var stringHours = horus.toString()
        var stringMinutes = minutes.toString()
        var stringSecond = seconds.toString()
        if (horus < 9) {
            stringHours = "0%d".format(horus)
        }
        if (minutes < 9)
            stringMinutes = "0%d".format(minutes)
        if (seconds < 9)
            stringSecond = "0%d".format(seconds)
        return "%s:%s:%s".format(stringHours, stringMinutes, stringSecond)
    }
}
