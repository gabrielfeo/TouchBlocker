package com.gabrielfeo.touchblocker.ui.manage

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ManageScreen(
    canBlock: Boolean,
    serviceRunning: Boolean,
    onGrantPermissionClick: () -> Unit,
    onStartBlockingClick: () -> Unit,
) {
    Scaffold {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (canBlock) {
                if (serviceRunning) {
                    CurrentlyBlockingMessage(modifier = Modifier.offset(y = (-200).dp))
                }
                AnimatedVisibility(visible = !serviceRunning) {
                    StartBlockingButton(onStartBlockingClick)
                }
            } else {
                GrantPermissionButton(onGrantPermissionClick)
            }
        }
    }
}

@Composable
private fun GrantPermissionButton(onGrantPermissionClick: () -> Unit) {
    OutlinedButton(
        onClick = onGrantPermissionClick,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFFB60000),
            backgroundColor = Color(0x0DFF0000)
        )
    ) {
        Text("Grant permission")
    }
}

@Composable
private fun StartBlockingButton(onStartBlockingClick: () -> Unit) {
    Button(
        onClick = onStartBlockingClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.White,
            contentColor = Color.Black,
        )
    ) {
        Text("Start blocking")
    }
}

@Composable
private fun CurrentlyBlockingMessage(modifier: Modifier = Modifier) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MessageTextParagraph("It works!")
        Spacer(modifier = Modifier.size(4.dp))
        MessageTextParagraph("Please use the notification to toggle blocking on/off")
        Spacer(modifier = Modifier.size(2.dp))
        val tip = buildAnnotatedString {
            pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
            append("Pro tip: ")
            pop()
            append("you can double-tap the screen twice to stop blocking")
        }
        MessageTextParagraph(tip)
    }
}

@Composable
private fun MessageTextParagraph(text: String) = MessageTextParagraph(AnnotatedString(text))

@Composable
private fun MessageTextParagraph(text: AnnotatedString) {
    Text(text, color = Color.Black, textAlign = TextAlign.Center, fontSize = 18.sp)
}

@Preview("Without permission")
@Composable
private fun ManageScreenWithoutPermission() {
    ManageScreen(
        canBlock = false,
        serviceRunning = false,
        onGrantPermissionClick = {},
        onStartBlockingClick = {}
    )
}

@Preview("With permission")
@Composable
private fun ManageScreenWithPermission() {
    ManageScreen(
        canBlock = true,
        serviceRunning = false,
        onGrantPermissionClick = {},
        onStartBlockingClick = {}
    )
}

@Preview("Service running")
@Composable
private fun ManageScreenServiceRunning() {
    ManageScreen(
        canBlock = true,
        serviceRunning = true,
        onGrantPermissionClick = {},
        onStartBlockingClick = {}
    )
}