package gr.android.survey.ui.composable.modals

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import gr.android.survey.R
import gr.android.survey.utils.MessageState
import gr.android.survey.utils.SurveyRemoteState

@Composable
fun MessagePopUpStateModal(
    surveyState: SurveyRemoteState,
    onSurveyState: ((SurveyRemoteState) -> Unit)? = null,
    inputValue: MutableState<TextFieldValue>,
    onClickableBackground: (Boolean) -> Unit,
    onAnswerText: (Int, String) -> Unit,
    id: Int?,
) {
    when(surveyState) {
        SurveyRemoteState.POST_SUCCESS -> {
            FullScreenMessagePopup(
                MessageState.Success,
                onRetry = {},
                onDismiss = {
                    onSurveyState?.invoke(SurveyRemoteState.OTHER)
                },
                errorMessage = "",
                tryAgainBtnVisibility = false,
                onClickableBackground = onClickableBackground
            )
            inputValue.value = TextFieldValue("")
        }
        SurveyRemoteState.POST_ERROR -> {
            FullScreenMessagePopup(
                MessageState.Error,
                onRetry = {
                    onAnswerText(id ?: -1, inputValue.value.text)
                },
                onDismiss = {
                    onSurveyState?.invoke(SurveyRemoteState.OTHER)
                },
                errorMessage = stringResource(id = R.string.post_error_message),
                tryAgainBtnVisibility = true,
                onClickableBackground = onClickableBackground
            )
        }
        SurveyRemoteState.FETCH_LIST_ERROR -> {
            FullScreenMessagePopup(
                MessageState.Error,
                onRetry = {},
                onDismiss = {
                    onSurveyState?.invoke(SurveyRemoteState.OTHER)
                },
                errorMessage = stringResource(id = R.string.server_error_message),
                tryAgainBtnVisibility = false,
                disableDelay = true,
                onClickableBackground = onClickableBackground
            )
        }
        SurveyRemoteState.OTHER -> {}
    }
}