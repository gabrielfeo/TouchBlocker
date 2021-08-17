package com.gabrielfeo.touchblocker.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * State to be observed in case the Activity is running when the notification is
 * canceled. Otherwise, can presume to be inactive.
 */
object TransientState {
    var isTouchBlockActive by mutableStateOf(false)
}