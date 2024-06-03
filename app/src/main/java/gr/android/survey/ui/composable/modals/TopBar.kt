package gr.android.survey.ui.composable.modals

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gr.android.survey.R

@Composable
fun TopBar(
    clickableBackground: Boolean = true,
    onBack: () -> Unit
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)) {
        IconButton(
            onClick = { if (clickableBackground) onBack() },
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_left),
                contentDescription = null
            )
        }
        Text(
            text = stringResource(id = R.string.toolbar_title),
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 20.sp),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TopBarPreview() {
    TopBar(
        clickableBackground = true,
        onBack = {}
    )
}