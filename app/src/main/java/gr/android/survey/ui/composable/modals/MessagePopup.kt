package gr.android.survey.ui.composable.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gr.android.survey.R
import gr.android.survey.utils.MessageState
import kotlinx.coroutines.delay

@Composable
fun FullScreenMessagePopup(
    state: MessageState,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    onClickableBackground: (Boolean) -> Unit,
    errorMessage: String,
    tryAgainBtnVisibility: Boolean,
    disableDelay: Boolean = false
) {
    onClickableBackground(false)

    if (!disableDelay) {
        LaunchedEffect(Unit) {
            delay(3000L) // Wait for 3 seconds
            onDismiss()
            onClickableBackground(true)
        }
    }

    FullScreenMessagePopupContent(
        state = state,
        onRetry = onRetry,
        errorMessage = errorMessage,
        tryAgainBtnVisibility = tryAgainBtnVisibility
    )
}

@Composable
fun FullScreenMessagePopupContent(
    state: MessageState,
    onRetry: () -> Unit,
    errorMessage: String,
    tryAgainBtnVisibility: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .wrapContentSize(Alignment.Center)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.2f)
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            when (state) {
                is MessageState.Success -> {
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.post_success_message),
                            style = TextStyle(color = Color.Green, fontSize = 20.sp),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                is MessageState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = errorMessage,
                            style = TextStyle(color = Color.Red, fontSize = 20.sp),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if(tryAgainBtnVisibility) {
                            DebouncedButton(onClick = onRetry) {
                                Text(text = stringResource(id = R.string.try_again))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    FullScreenMessagePopup(
        state = MessageState.Error,
        onRetry = {},
        onDismiss = {},
        errorMessage = "error",
        tryAgainBtnVisibility = true,
        onClickableBackground = {}
    )
}
