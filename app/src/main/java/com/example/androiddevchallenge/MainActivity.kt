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
package com.example.androiddevchallenge

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.model.TimerViewModel
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.utils.Utils
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.datetimepicker
import java.time.ZoneId
import java.time.temporal.TemporalField

/**
 * @author https://github.com/vincenzopalazzo
 */
class MainActivity : AppCompatActivity() {
    private lateinit var timeViewModel: TimerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timeViewModel = TimerViewModel()
        setContent {
            MyTheme {
                MyApp(timeViewModel)
            }
        }
    }

}

// Start building your app here!
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyApp(viewModel: TimerViewModel = TimerViewModel()) {
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
fun BodyView(viewModel: TimerViewModel = TimerViewModel(), modifier: Modifier) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                Text(text = "Somethings")
            }
            Row (verticalAlignment = Alignment.CenterVertically
            ){
                TimerScreen(viewModel)
            }
            Row(verticalAlignment = Alignment.Bottom) {
                MainButtonApp(viewModel)
            }
        }

    }
}

@Composable
fun TimerScreen(timerViewModel: TimerViewModel = TimerViewModel()){
    val time: Long by timerViewModel.time.observeAsState(0L)
    TimerView(timeValue = time)
}

@Composable
fun TimerView(timeValue: Long){
    Card(
        elevation = 10.dp,
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
    ) {
        var text = "00:00:00"
        if (timeValue != 0L)
            text = Utils.formattingTimeStamp(timeValue)
        Text(
            modifier = Modifier.wrapContentWidth(align = Alignment.CenterHorizontally),
            text = text)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainButtonApp(viewModel: TimerViewModel = TimerViewModel()) {
    val dialog = MaterialDialog()
    dialog.build {
        datetimepicker { dateTime ->
            val millisecond = dateTime
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
            viewModel.createAndRunTimer(millisecond)
        }
    }
    Button(onClick = { dialog.show() }) {
        Text("Click here")
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
