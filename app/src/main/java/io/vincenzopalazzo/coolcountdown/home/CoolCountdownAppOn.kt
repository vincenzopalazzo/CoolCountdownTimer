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
package io.vincenzopalazzo.coolcountdown.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SettingsApplications
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.datetimepicker
import io.vincenzopalazzo.coolcountdown.R
import io.vincenzopalazzo.coolcountdown.model.TimerViewModel
import io.vincenzopalazzo.coolcountdown.ui.utils.ShowSnackBar
import io.vincenzopalazzo.coolcountdown.utils.LevelTime
import io.vincenzopalazzo.coolcountdown.utils.Utils
import java.time.Duration
import java.time.LocalDateTime

/**
 * @author https://github.com/vincenzopalazzo
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainOnView(viewModel: TimerViewModel, stateView: MutableState<StatusTimer>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TimerScreen(viewModel)
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MainButtonApp(viewModel, stateView)
    }
}

@Composable
fun TimerScreen(timerViewModel: TimerViewModel) {
    val time = timerViewModel.time.observeAsState(0L)
    TimerView(timerViewModel, timeValue = time)
}

// Holds the animation values.
private class TransitionData(
    color: State<Color>,
    val state: MutableState<LevelTime>
) {
    val color by color
}

@Composable
private fun updateTransitionData(levelTime: LevelTime = LevelTime.GOOD): TransitionData {
    val cosmoOrange = Color(255, 117, 24)
    val cosmoRed = Color(231, 76, 60)
    val normalColor = MaterialTheme.colors.primary
    val statusTime = remember { mutableStateOf(levelTime) }
    val transition = updateTransition(statusTime)
    val color = transition.animateColor(
        transitionSpec = { tween(durationMillis = 300, easing = FastOutSlowInEasing) }
    ) { state ->
        when (state.value) {
            LevelTime.NORMAL -> cosmoOrange
            LevelTime.DANGER -> cosmoRed
            else -> normalColor
        }
    }
    return remember(transition) { TransitionData(color, statusTime) }
}

@Composable
fun TimerView(
    timerViewModel: TimerViewModel,
    timeValue: State<Long>,
) {

    val transition = updateTransitionData()
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .wrapContentHeight(align = Alignment.CenterVertically)
            .fillMaxWidth()
            .padding(30.dp)
            .height(150.dp)
    ) {
        var text = "00:00:00"
        if (timeValue.value != 0L) {
            text = Utils.formattingString(
                timerViewModel.hours,
                timerViewModel.minutes,
                timerViewModel.seconds,
                timerViewModel.millisecond
            )
            transition.state.value = Utils.checkMissingTime(
                timerViewModel.hours,
                timerViewModel.minutes,
                timerViewModel.seconds,
                timerViewModel.millisecond
            )
        } else {
            transition.state.value = LevelTime.GOOD
        }
        // else // TODO restore View when the timer is finished
        //    stateView.value = StatusTimer.STOPPED
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                style = MaterialTheme.typography.body1,
                fontFamily = FontFamily(Font(R.font.dsdigitb, weight = FontWeight.Bold)),
                fontSize = 80.sp,
                color = transition.color,
                text = text
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainButtonApp(
    viewModel: TimerViewModel = TimerViewModel(),
    stateView: MutableState<StatusTimer>
) {
    val timerIsFinished = viewModel.finished.observeAsState()
    val snackBarVisible = remember { mutableStateOf(false) }
    val dialog = MaterialDialog()
    dialog.build {
        datetimepicker { dateTime ->
            // TODO: Fixed this, this print the data from now, this means 00:54 at the moment
            // but this is not true, we need to make an operation dateSet - now
            val nowTime = LocalDateTime.now()
            val between = Duration.between(nowTime, dateTime)
            if (between.isNegative || between.isZero) {
                snackBarVisible.value = true
            } else {
                viewModel.createAndRunTimer(between.toMillis())
            }
        }
    }
    if (snackBarVisible.value) {
        ShowSnackBar(
            text = "Wrong value choose",
            textButton = "Ok",
            action = { snackBarVisible.value = false }
        )
        return
    }
    OutlinedButton(
        onClick = {
            if (timerIsFinished.value == false) {
                viewModel.cancelTimer()
                stateView.value = StatusTimer.STOPPED
            } else {
                dialog.show()
            }
        },
        border = BorderStroke(2.dp, MaterialTheme.colors.primary),
        modifier = Modifier
            .padding(10.dp)
            .wrapContentSize(align = Alignment.Center)
            .wrapContentHeight(align = Alignment.Bottom)
            .height(60.dp)
            .width(180.dp),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row {
            val text: String
            if (timerIsFinished.value == false) {
                Icon(
                    Icons.Filled.StopCircle, "Stop Timer",
                    modifier = Modifier
                        .width(30.dp)
                        .height(30.dp)
                        .align(Alignment.CenterVertically)
                )
                text = "Stop Timer"
            } else {
                Icon(
                    Icons.Filled.SettingsApplications, "Set timer",
                    modifier = Modifier
                        .width(30.dp)
                        .height(30.dp)
                        .align(Alignment.CenterVertically)
                )
                text = "Setting Timer"
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text,
                color = MaterialTheme.colors.onBackground,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
    Spacer(
        modifier = Modifier
            .height(30.dp)
    )
}
