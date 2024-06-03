package gr.android.survey.ui.composable

import FullScreenMessagePopup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.navigation.compose.hiltViewModel
import gr.android.survey.R
import gr.android.survey.ui.viewModel.QuestionsViewModel
import gr.android.survey.utils.Button.NEXT
import gr.android.survey.utils.Button.PREVIOUS
import gr.android.survey.utils.Button.CURRENT
import gr.android.survey.utils.MessageState
import gr.android.survey.utils.SurveyRemoteState

@Composable
fun SurveyScreen(
    questionsViewModel: QuestionsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val answeredQuestion = questionsViewModel.uiState.answeredQuestionUiModel

    SurveyScreenContent(
        id = answeredQuestion?.id,
        question = answeredQuestion?.question,
        questionCounter = questionsViewModel.uiState.questionCounter,
        listSize = questionsViewModel.uiState.questionsListSize,
        isSubmitted = answeredQuestion?.isSubmitted,
        answeredText = answeredQuestion?.answeredText,
        surveyState = questionsViewModel.uiState.surveyState,
        loaderVisibility = questionsViewModel.uiState.loaderVisibility,
        submittedQuestions = questionsViewModel.uiState.submittedQuestions,
        onAnswerText = { id, answeredText ->
            questionsViewModel.postQuestion(id, answeredText)
        },
        nextQuestion = {
            questionsViewModel.getAnsweredQuestionByIndex(it)
        },
        previousQuestion = {
            questionsViewModel.getAnsweredQuestionByIndex(it)
        },
        onSurveyState = {
            questionsViewModel.updateSurveyState(it)
            questionsViewModel.getAnsweredQuestionByIndex(buttonAction = CURRENT.action)
        },
        onBack = onBack
    )

}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SurveyScreenContent(
    id: Int? = null,
    question: String? = null,
    questionCounter: Int? = null,
    listSize: Int? = null,
    isSubmitted: Boolean? = null,
    answeredText: String? = null,
    surveyState: SurveyRemoteState,
    loaderVisibility: Boolean = false,
    submittedQuestions: Int,
    onAnswerText: (Int, String) -> Unit,
    nextQuestion: (String) -> Unit,
    previousQuestion: (String) -> Unit,
    onSurveyState: ((SurveyRemoteState, ) -> Unit)? = null,
    onBack: () -> Unit
) {
    val inputValue = remember { mutableStateOf(TextFieldValue()) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val view = LocalView.current
    val insets = ViewCompat.getRootWindowInsets(view)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Survey Questions",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_left),
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    ) { contentPadding ->

        when(loaderVisibility) {
            true -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            else -> {
                
            }
        }
        Box(
            Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            Column(
                Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier
                        .padding(top = 16.dp, start = 8.dp, end = 8.dp)
                        .align(Alignment.CenterHorizontally)
                        .weight(1f, fill = false),
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    text = "Questions submitted: $submittedQuestions",
                    textAlign = TextAlign.Center,
                )

                Text(
                    modifier = Modifier
                        .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 24.dp)
                        .align(Alignment.CenterHorizontally)
                        .weight(1f, fill = false),
                    style = TextStyle(fontSize = 24.sp),
                    text = question ?: "",
                    textAlign = TextAlign.Center,
                )

                if(isSubmitted == false || isSubmitted == null) {
                    TextField(
                        value = inputValue.value,
                        onValueChange = {
                            inputValue.value = it
                        },
                        modifier = Modifier
                            .padding(top = 32.dp, start = 8.dp, end = 8.dp),
                        label = { Text("Enter answer") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                        )
                    )
                } else {
                    Text(
                        modifier = Modifier.padding(top = 32.dp, start = 8.dp, end = 8.dp),
                        text = answeredText ?: "",
                        style = TextStyle(fontSize = 16.sp),
                        textAlign = TextAlign.Center
                    )
                }

            }

            val isKeyboardVisible by remember {
                derivedStateOf {
                    insets?.isVisible(WindowInsetsCompat.Type.ime()) ?: false
                }
            }

            Column(
                modifier = Modifier
                    .align(if (isKeyboardVisible) Alignment.Center else Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if(questionCounter != null && listSize != null) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Questions: ${questionCounter}/${listSize}")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                DebouncedButton(
                    onClick = {
                        if((isSubmitted == false || isSubmitted == null) && inputValue.value.text.isNotEmpty()) {
                            onAnswerText(id ?: -1, inputValue.value.text)
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                    },
                    modifier = Modifier
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.purple_500.takeIf { (isSubmitted == false || isSubmitted == null) && inputValue.value.text.isNotEmpty()} ?: R.color.grey),
                        contentColor = colorResource(R.color.white),
                    )
                ) {
                    Text("Submit")
                }

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center

                ) {
                    Button(
                        onClick = {
                            inputValue.value = TextFieldValue("")
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            if (questionCounter != 1)
                                previousQuestion(PREVIOUS.action)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 16.dp, bottom = 24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.orange.takeIf { questionCounter != 1 } ?: R.color.grey),
                            contentColor = colorResource(R.color.white),
                        )
                    ) {
                        Text("Previes")
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            inputValue.value = TextFieldValue("")
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            if(questionCounter != listSize)
                                nextQuestion(NEXT.action)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 16.dp, bottom = 24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.green.takeIf { questionCounter != listSize } ?: R.color.grey),
                            contentColor = colorResource(R.color.white)
                        )
                    ) {
                        Text("Next")
                    }
                }
            }
        }

    }

    when(surveyState) {
        SurveyRemoteState.POST_SUCCESS -> {
            FullScreenMessagePopup(
                MessageState.Success,
                onRetry = {},
                onDismiss = {
                    onSurveyState?.invoke(SurveyRemoteState.OTHER)
                },
                errorMessage = "",
                tryAgainBtnVisibility = false
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
                },
                errorMessage = stringResource(id = R.string.post_error_message),
                tryAgainBtnVisibility = true
            )
        }
        SurveyRemoteState.FETCH_LIST_ERROR -> {
            FullScreenMessagePopup(
                MessageState.Error,
                onRetry = {
                    onAnswerText(id ?: -1, inputValue.value.text)
                },
                onDismiss = {
                },
                errorMessage = "",
                tryAgainBtnVisibility = false
            )
        }
        SurveyRemoteState.OTHER -> {}
    }
}

@Preview(showBackground = true)
@Composable
fun SurveyScreenContentPreview() {
    SurveyScreenContent(
        question = "What is your favorite food?",
        onAnswerText = { _, _ ->},
        listSize = 20,
        isSubmitted = true,
        surveyState = SurveyRemoteState.POST_SUCCESS,
        answeredText = "i like red",
        questionCounter = 20,
        nextQuestion = {},
        previousQuestion = {},
        submittedQuestions = 5,
        onBack = {}
    )
}