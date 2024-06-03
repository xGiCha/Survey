package gr.android.survey.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager

data class Question(val id: Int, val question: String)



@OptIn(ExperimentalPagerApi::class)
@Composable
fun HorizontalPagerView() {
    val questions = listOf(
        Question(1, "What is your favourite colour?"),
        Question(2, "What is your favourite food?"),
        Question(3, "What is your favourite country?"),
        Question(4, "What is your favourite sport?"),
        Question(5, "What is your favourite team?"),
        Question(6, "What is your favourite programming language?"),
        Question(7, "What is your favourite song?"),
        Question(8, "What is your favourite band?"),
        Question(9, "What is your favourite music?"),
        Question(10, "What is your favourite brand?")
    )
    HorizontalPager(
        count = questions.size,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        QuestionPage(questions[page])
    }
}

@Composable
fun QuestionPage(question: Question) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = question.question)
    }
}

@Preview
@Composable
fun PreviewHorizontalPagerView() {
    HorizontalPagerView()
}
