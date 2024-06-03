package gr.android.survey.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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