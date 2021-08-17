package com.gabrielfeo.touchblocker

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ManageScreen(
    canBlock: Boolean,
    serviceRunning: Boolean,
    onGrantPermissionClick: () -> Unit,
    onStartBlockingClick: () -> Unit,
) {
    Scaffold {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (canBlock) {
                if (serviceRunning) {
                    RunningStatusText()
                } else {
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
private fun RunningStatusText() {
    StatusTextParagraph("Running!")
    Spacer(modifier = Modifier.size(2.dp))
    StatusTextParagraph("Please use the notification to toggle blocking on/off")
}

@Composable
private fun StatusTextParagraph(text: String) {
    Text(text, color = Color.Black, textAlign = TextAlign.Center, fontSize = 18.sp)
}

@Preview("Without permission")
@Composable
fun ManageScreenWithoutPermission() {
    ManageScreen(
        canBlock = false,
        serviceRunning = false,
        onGrantPermissionClick = {},
        onStartBlockingClick = {}
    )
}

@Preview("With permission")
@Composable
fun ManageScreenWithPermission() {
    ManageScreen(
        canBlock = true,
        serviceRunning = false,
        onGrantPermissionClick = {},
        onStartBlockingClick = {}
    )
}

@Preview("Service running")
@Composable
fun ManageScreenServiceRunning() {
    ManageScreen(
        canBlock = true,
        serviceRunning = true,
        onGrantPermissionClick = {},
        onStartBlockingClick = {}
    )
}