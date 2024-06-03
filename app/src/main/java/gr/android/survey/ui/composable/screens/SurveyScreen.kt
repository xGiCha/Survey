package gr.android.survey.ui.composable.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import gr.android.survey.R
import gr.android.survey.domain.uiModels.QuestionsUiModel
import gr.android.survey.ui.composable.modals.DebouncedButton
import gr.android.survey.ui.composable.modals.LoaderModal
import gr.android.survey.ui.composable.modals.MessagePopUpStateModal
import gr.android.survey.ui.composable.modals.NetworkBanner
import gr.android.survey.ui.composable.modals.TopBar
import gr.android.survey.ui.viewModel.QuestionsViewModel
import gr.android.survey.utils.Button.CURRENT
import gr.android.survey.utils.Button.NEXT
import gr.android.survey.utils.Button.PREVIOUS
import gr.android.survey.utils.SurveyRemoteState
import kotlinx.coroutines.launch

@Composable
fun SurveyScreen(
    questionsViewModel: QuestionsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val answeredQuestion = questionsViewModel.uiState.answeredQuestionUiModel

    SurveyScreenContent(
        questionsUiModel = questionsViewModel.uiState.questionsUiModel,
        id = answeredQuestion?.id,
        questionCounter = questionsViewModel.uiState.questionCounter,
        listSize = questionsViewModel.uiState.questionsUiModel?.questions?.size,
        isSubmitted = answeredQuestion?.isSubmitted,
        answeredText = answeredQuestion?.answeredText,
        surveyState = questionsViewModel.uiState.surveyState,
        loaderVisibility = questionsViewModel.uiState.loaderVisibility,
        submittedQuestions = questionsViewModel.uiState.submittedQuestions,
        clickableBackground = questionsViewModel.uiState.clickableBackground,
        errorMessage = questionsViewModel.uiState.errorMessage,
        onAnswerText = { id, answeredText ->
            questionsViewModel.postQuestion(id, answeredText)
        },
        nextQuestion = { btnState, index ->
            questionsViewModel.getAnsweredQuestionByIndex(btnState, index)
        },
        previousQuestion = {btnState, index ->
            questionsViewModel.getAnsweredQuestionByIndex(btnState, index)
        },
        onSurveyState = { state, index ->
            questionsViewModel.updateSurveyState(state)
            questionsViewModel.getAnsweredQuestionByIndex(buttonAction = CURRENT.action, index)
        },
        onBack = onBack,
        onClickableBackground = {
            questionsViewModel.updateClickableBackground(it)
        },
        onQuestionCounter = {
            questionsViewModel.updateQuestionCounter(it)
        }
    )

}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalPagerApi::class)
@Composable
fun SurveyScreenContent(
    questionsUiModel: QuestionsUiModel? = null,
    id: Int? = null,
    questionCounter: Int? = null,
    listSize: Int? = null,
    isSubmitted: Boolean? = null,
    answeredText: String? = null,
    surveyState: SurveyRemoteState,
    loaderVisibility: Boolean = false,
    clickableBackground: Boolean = true,
    submittedQuestions: Int,
    errorMessage: String? = null,
    onAnswerText: (Int, String) -> Unit,
    nextQuestion: (String, Int) -> Unit,
    previousQuestion: (String, Int) -> Unit,
    onSurveyState: ((SurveyRemoteState, Int) -> Unit)? = null,
    onBack: () -> Unit,
    onClickableBackground: (Boolean) -> Unit,
    onQuestionCounter: (Int) -> Unit
) {
    val inputValue = remember { mutableStateOf(TextFieldValue()) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val initialPage = 0
    val pagerState = rememberPagerState(initialPage = initialPage)
    val coroutineScope = rememberCoroutineScope()

    val previousPageIndex = remember { mutableStateOf(pagerState.currentPage) }

    LaunchedEffect(pagerState.currentPage) {
        onQuestionCounter(pagerState.currentPage)
        if (pagerState.currentPage > previousPageIndex.value) {
            nextQuestion(NEXT.action, pagerState.currentPage)
        } else if (pagerState.currentPage < previousPageIndex.value) {
            previousQuestion(PREVIOUS.action, pagerState.currentPage)
        }
        previousPageIndex.value = pagerState.currentPage
    }

    Scaffold(
        topBar = {
            TopBar(
                clickableBackground = clickableBackground,
                onBack = onBack
            )
        },
        bottomBar = {
            NetworkBanner(
                onConnectivityChange = {}
            )
        }
    ) { contentPadding ->

        LoaderModal(loaderVisibility)

        Column(
            Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .verticalScroll(rememberScrollState())
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
                        .align(Alignment.CenterHorizontally),
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    text = stringResource(id = R.string.questions_submitted, submittedQuestions),
                    textAlign = TextAlign.Center,
                )

                HorizontalPager(
                    modifier = Modifier.height(300.dp),
                    count = listSize ?: 0,
                    state = pagerState
                ) { page ->
                    val questionItem = questionsUiModel?.questions?.get(page)

                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 24.dp)
                                .align(Alignment.CenterHorizontally)
                                .weight(1f),
                            style = TextStyle(fontSize = 24.sp),
                            text = questionItem?.question ?: "",
                            textAlign = TextAlign.Center,
                        )

                        if (isSubmitted == false || isSubmitted == null) {
                            TextField(
                                value = inputValue.value,
                                onValueChange = {
                                    inputValue.value = it
                                },
                                modifier = Modifier
                                    .padding(top = 8.dp, start = 8.dp, end = 8.dp),
                                label = { Text(stringResource(id = R.string.enter_answer)) },
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        focusManager.clearFocus()
                                        keyboardController?.hide()
                                    }
                                ),
                                enabled = clickableBackground
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
                }

                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (questionCounter != null && listSize != null) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(
                                id = R.string.questions_metrics,
                                questionCounter + 1,
                                listSize
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                DebouncedButton(
                    onClick = {
                        if ((isSubmitted == false || isSubmitted == null) && inputValue.value.text.isNotEmpty()) {
                            onAnswerText(id ?: -1, inputValue.value.text)
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                    },
                    modifier = Modifier
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.purple_500.takeIf { (isSubmitted == false || isSubmitted == null) && inputValue.value.text.isNotEmpty() }
                            ?: R.color.grey),
                        contentColor = colorResource(R.color.white),
                    )
                ) {
                    Text(stringResource(id = R.string.submit))
                }

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    DebouncedButton(
                        onClick = {
                            inputValue.value = TextFieldValue("")
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            coroutineScope.launch {
                                if (pagerState.currentPage > 0) {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 16.dp, bottom = 24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.orange.takeIf { pagerState.currentPage > 0 }
                                ?: R.color.grey),
                            contentColor = colorResource(R.color.white),
                        ),
                        enable = clickableBackground
                    ) {
                        Text(stringResource(id = R.string.previous_btn))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    DebouncedButton(
                        onClick = {
                            inputValue.value = TextFieldValue("")
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            coroutineScope.launch {
                                if (pagerState.currentPage < (listSize ?: 0) - 1) {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 16.dp, bottom = 24.dp),
                        enable = clickableBackground,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.green.takeIf {
                                pagerState.currentPage < (listSize ?: 0) - 1
                            } ?: R.color.grey),
                            contentColor = colorResource(R.color.white)
                        )
                    ) {
                        Text(stringResource(id = R.string.next_btn))
                    }
                }
            }
        }
    }

    MessagePopUpStateModal(
        id = id,
        surveyState = surveyState,
        onSurveyState = {
            onSurveyState?.invoke(it, pagerState.currentPage)
        },
        inputValue = inputValue,
        onClickableBackground = onClickableBackground,
        onAnswerText = onAnswerText,
        errorMessage = errorMessage
    )
}

@Preview(showBackground = true)
@Composable
fun SurveyScreenContentPreview() {
    SurveyScreenContent(
        onAnswerText = { _, _ -> },
        listSize = 20,
        isSubmitted = false,
        surveyState = SurveyRemoteState.OTHER,
        answeredText = "i like red",
        questionCounter = 20,
        nextQuestion = {_, _ ->},
        previousQuestion = {_, _ ->},
        submittedQuestions = 5,
        onBack = {},
        onClickableBackground = {},
        onQuestionCounter = {}
    )
}