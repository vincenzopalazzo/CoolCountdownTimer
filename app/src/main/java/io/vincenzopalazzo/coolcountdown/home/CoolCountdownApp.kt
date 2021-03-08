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

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.chrisbanes.accompanist.coil.CoilImage
import io.vincenzopalazzo.coolcountdown.R
import io.vincenzopalazzo.coolcountdown.model.TimerViewModel
import kotlinx.coroutines.launch

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
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Cool Countdown")
                },
                elevation = 12.dp,
                navigationIcon = {
                    Icon(
                        Icons.Filled.Menu, "Munu button",
                        modifier = Modifier
                            .height(30.dp)
                            .width(30.dp)
                            .clickable(
                                onClick = {
                                    coroutineScope.launch {
                                        scaffoldState.drawerState.open()
                                    }
                                }
                            )
                    )
                }
            )
        },
        drawerContent = {
            DrawerView()
        },
    ) { paddingValues ->
        BodyView(viewModel, modifier = Modifier.padding(paddingValues))
    }
}

@Composable
fun DrawerView(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .wrapContentHeight(align = Alignment.Bottom)
            .fillMaxWidth()
            .height(140.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Surface(
            color = MaterialTheme.colors.surface
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                CoilImage(
                    data = "https://avatars.githubusercontent.com/u/17150045?s=460&u=ad52a04f992830b4d4bbe50d3fb6f89ee361537a&v=4",
                    contentDescription = "Avatar Icon",
                    modifier = Modifier.size(90.dp)
                )
            }
        }
    }
    val context = LocalContext.current
    val authorRef = listOf("Twitter", "Github")
    Surface(
        color = MaterialTheme.colors.secondary,
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn {
            items(authorRef) { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .clickable(
                            onClick = {
                                val url: String
                                if (item === "Twitter") {
                                    url = "https://twitter.com/PalazzoVincenzo"
                                } else {
                                    url = "https://github.com/vincenzopalazzo"
                                }
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                try {
                                    context.startActivity(intent)
                                } catch (ex: ActivityNotFoundException) {
                                    // if Chrome browser not installed
                                    intent.setPackage(null)
                                    context.startActivity(intent)
                                }
                            }
                        )
                ) {
                    if (item === "Twitter") {
                        MakeIcon(
                            painter = painterResource(id = R.drawable.ic_twitter),
                            text = "@PalazzoVincenzo",
                            contentDescription = "Tweetter Icon"
                        )
                    } else {
                        MakeIcon(
                            painter = painterResource(id = R.drawable.ic_github),
                            text = "@vincenzopalazzo",
                            contentDescription = "Github link"
                        )
                    }
                }
                Divider(color = MaterialTheme.colors.background)
            }
        }
    }
}

@Composable
fun MakeIcon(painter: Painter, text: String, contentDescription: String? = null) {
    Icon(
        painter = painter,
        tint = Color.Unspecified,
        modifier = Modifier
            .padding(5.dp)
            .width(35.dp)
            .height(35.dp),
        contentDescription = contentDescription // decorative element
    )
    Spacer(modifier = Modifier.width(20.dp))
    Text(text = text, color = MaterialTheme.colors.onBackground)
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
    val stateView = remember { mutableStateOf(StatusTimer.STOPPED) }
    val transition = updateTransition(targetState = stateView)
    val state = transition.animateInt { state ->
        when (state.value) {
            StatusTimer.STOPPED -> StatusTimer.STOPPED.state
            StatusTimer.RUNNING -> StatusTimer.RUNNING.state
        }
    }
    when (state.value) {
        0 -> MainOffView(stateView = stateView)
        1 -> MainOnView(viewModel = viewModel, stateView = stateView)
    }
}
