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
package com.example.coolcountdown

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.datetimepicker
import io.vincenzopalazzo.coolcountdown.model.TimerViewModel
import io.vincenzopalazzo.coolcountdown.utils.Utils
import java.time.ZoneId
import java.util.Date

/**
 * @author https://github.com/vincenzopalazzo
 */
enum class StatusTimer(val state: Int) {
    RUNNING(1),
    STOPPED(0)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CoolCountdownApp() {
    val viewModel = viewModel(TimerViewModel::class.java)
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Cool Timer App")
                },
                elevation = 12.dp
            )
        }
    ) { paddingValues ->
        BodyView(viewModel, modifier = Modifier.padding(paddingValues))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BodyView(
    viewModel: TimerViewModel = TimerViewModel(),
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        BuildChip("#AndroidDevChallenge")
    }
    var stateView = remember { mutableStateOf(StatusTimer.STOPPED) }
    val transition = updateTransition(targetState = stateView)
    val state = transition.animateInt { state ->
        when (state.value) {
            StatusTimer.STOPPED -> StatusTimer.STOPPED.state
            StatusTimer.RUNNING -> StatusTimer.RUNNING.state
        }
    }
    if (state.value == 0) {

        Column(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedButton(
                onClick = {
                    stateView.value = StatusTimer.RUNNING
                },
                border = BorderStroke(2.dp, MaterialTheme.colors.primary),
                modifier = Modifier
                    .padding(10.dp)
                    .wrapContentSize(align = Alignment.Center)
                    .wrapContentHeight(align = Alignment.CenterVertically)
                    .height(150.dp)
                    .width(150.dp),
                shape = CircleShape,
            ) {
                Icon(
                    Icons.Filled.PlayCircle, "Run Timer",
                    modifier = Modifier
                        .width(30.dp)
                        .height(30.dp)
                )
            }
            Spacer(
                modifier = Modifier
                    .height(30.dp)
            )
        }
    } else {
        val dialog = MaterialDialog()
        dialog.build {
            datetimepicker { dateTime ->
                // TODO: Fixed this, this print the data from now, this means 00:54 at the moment
                // but this is not true, we need to make an operation dateSet - now
                val millisecond = dateTime
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
                val milliseconNow = Date().time
                viewModel.createAndRunTimer(millisecond - milliseconNow)
            }
        }
        dialog.show()
        Column(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TimerScreen(viewModel, stateView)
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
}

@Composable
fun BuildChip(label: String) {
    Box(
        modifier = Modifier
            .padding(8.dp)
    ) {
        Surface(
            elevation = 1.dp,
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colors.secondary
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    label,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.padding(8.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.button.copy(color = MaterialTheme.colors.primary)
                )
            }
        }
    }
}

@Composable
fun TimerScreen(timerViewModel: TimerViewModel, stateView: MutableState<StatusTimer>) {
    val time = timerViewModel.time.observeAsState(0L)
    TimerView(timerViewModel, timeValue = time, stateView)
}

@Composable
fun TimerView(timerViewModel: TimerViewModel, timeValue: State<Long>, stateView: MutableState<StatusTimer>) {
    Card(
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .wrapContentHeight(align = Alignment.CenterVertically)
            .fillMaxWidth()
            .padding(10.dp)
            .height(150.dp)
    ) {
        var text = "00:00:00"
        if (timeValue.value != 0L)
            text = Utils.formattingString(timerViewModel.hours, timerViewModel.minutes, timerViewModel.seconds)
        // else // TODO restore View when the timer is finished
        //    stateView.value = StatusTimer.STOPPED
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                style = MaterialTheme.typography.body1,
                fontSize = 50.sp,
                color = MaterialTheme.colors.primary,
                text = text
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainButtonApp(viewModel: TimerViewModel = TimerViewModel(), stateView: MutableState<StatusTimer>) {

    OutlinedButton(
        onClick = {
            if (viewModel.timerIsRunning()) {
                viewModel.cancelTimer()
                stateView.value = StatusTimer.STOPPED
            }
        },
        border = BorderStroke(2.dp, MaterialTheme.colors.primary),
        modifier = Modifier
            .padding(10.dp)
            .wrapContentSize(align = Alignment.Center)
            .wrapContentHeight(align = Alignment.Bottom)
            .height(60.dp)
            .width(150.dp),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row {
            Icon(
                Icons.Filled.StopCircle, "Stop Timer",
                modifier = Modifier
                    .width(30.dp)
                    .height(30.dp)
            )
            Text("Pause", color = MaterialTheme.colors.onBackground)
        }
    }
    Spacer(
        modifier = Modifier
            .height(30.dp)
    )
}
