package gr.android.survey.ui.composable.modals

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import gr.android.survey.utils.debounce

@Composable
fun DebouncedButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    debounceTime: Long = 200L,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    enable: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    val debouncedOnClick = onClick.debounce(debounceTime)

    Button(
        onClick = debouncedOnClick,
        modifier = modifier,
        colors = colors,
        content = content,
        enabled = enable
    )
}
