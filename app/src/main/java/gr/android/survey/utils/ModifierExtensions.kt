package gr.android.survey.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun Modifier.setNoRippleClickable(onClick: (() -> Unit)? = null): Modifier = composed {
    this.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
    ) {
        onClick?.invoke()
    }
}

fun (() -> Unit).debounce(
    debounceTime: Long = 300L
): () -> Unit {
    var job: Job? = null

    return {
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {
            delay(debounceTime)
            this@debounce()
        }
    }
}