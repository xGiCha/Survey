package gr.android.survey.ui.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
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

@Composable
fun SurveyScreen(
    questionsViewModel: QuestionsViewModel = hiltViewModel()
) {
    val answeredQuestion = questionsViewModel.uiState.answeredQuestionUiModel
    val counter: Int = 0

    SurveyScreenContent(
        id = answeredQuestion?.id,
        question = answeredQuestion?.question,
        questionCounter = answeredQuestion?.index,
        listSize = answeredQuestion?.listSize,
        answerText = { id, answeredText ->
            questionsViewModel.postQuesting(id, answeredText)
        },
        nextQuestion = {
            questionsViewModel.getAnsweredQuestionByIndex(it, answeredQuestion?.index ?: -1)
        },
        previousQuestion = {
            questionsViewModel.getAnsweredQuestionByIndex(it, answeredQuestion?.index ?: -1)
        }
    )

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SurveyScreenContent(
    id: Int? = null,
    question: String? = null,
    questionCounter: Int? = null,
    listSize: Int? = null,
    answerText: (Int, String) -> Unit,
    nextQuestion: (String) -> Unit,
    previousQuestion: (String) -> Unit
) {
    val inputValue = remember { mutableStateOf(TextFieldValue()) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val view = LocalView.current
    val insets = ViewCompat.getRootWindowInsets(view)

    Scaffold(
        topBar = {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                textAlign = TextAlign.Center,
                text = "Survey Questions"
            )
        }
    ) { contentPadding ->
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
                    modifier = Modifier.padding(top = 16.dp),
                    style = TextStyle(fontSize = 24.sp),
                    text = question ?: ""
                )

                TextField(
                    value = inputValue.value,
                    onValueChange = {
                        inputValue.value = it
                    },
                    modifier = Modifier.padding(top = 16.dp),
                    label = { Text("Enter answer") }
                )
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

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "Questions submitted: ${questionCounter}/${listSize}")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        answerText(id ?: -1, inputValue.value.text)
                    },
                    modifier = Modifier
                        .padding(top = 16.dp)
                ) {
                    Text("Submit")
                }

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center

                ) {
                    Button(
                        onClick = {
                            previousQuestion(PREVIOUS.action)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 16.dp, bottom = 24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.orange),
                            contentColor = colorResource(R.color.white),
                        )
                    ) {
                        Text("Preview")
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            nextQuestion(NEXT.action)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 16.dp, bottom = 24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.green),
                            contentColor = colorResource(R.color.white)
                        )
                    ) {
                        Text("Next")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SurveyScreenContentPreview() {
    SurveyScreenContent(
        question = "What is your favorite food?",
        answerText = { _, _ ->},
        listSize = 20,
        questionCounter = 5,
        nextQuestion = {},
        previousQuestion = {}
    )
}