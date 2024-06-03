package gr.android.survey.ui.composable.modals

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import gr.android.survey.R
import gr.android.survey.utils.debounce

@Composable
fun DebouncedButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    debounceTime: Long = 300L,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    content: @Composable RowScope.() -> Unit
) {
    val debouncedOnClick = onClick.debounce(debounceTime)

    Button(
        onClick = debouncedOnClick,
        modifier = modifier,
        colors = colors,
        content = content
    )
}
