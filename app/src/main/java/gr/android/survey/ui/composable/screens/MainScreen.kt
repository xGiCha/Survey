package gr.android.survey.ui.composable.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import gr.android.survey.R
import gr.android.survey.ui.composable.modals.NetworkBanner
import gr.android.survey.ui.viewModel.QuestionsViewModel


@Composable
fun MainScreen(
    questionsViewModel: QuestionsViewModel = hiltViewModel(),
    navigateToSurvey: () -> Unit
) {

    MainScreenContent(
        navigateToSurvey = {
            navigateToSurvey()
            questionsViewModel.resetSurvey()
        }
    )

}

@Composable
fun MainScreenContent(
    navigateToSurvey: () -> Unit
) {
    val isNetworkConnected =  remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)) {
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .alpha(0f)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_left),
                        contentDescription = null
                    )
                }
                Text(
                    text = stringResource(id = R.string.welcome),
                    textAlign = TextAlign.Center,
                    style = TextStyle(fontSize = 20.sp),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
        bottomBar = {
            NetworkBanner { isConnected ->
                isNetworkConnected.value = isConnected
            }
        }
    ) { contentPadding ->
        Column(
            Modifier
                .padding(contentPadding)
                .fillMaxHeight()
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = navigateToSurvey,
                enabled = isNetworkConnected.value
            ) {
                Text(text = "Start survey")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenContentPreview() {
    MainScreenContent(
        navigateToSurvey = {}
    )
}