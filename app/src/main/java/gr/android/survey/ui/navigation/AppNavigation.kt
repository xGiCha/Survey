package gr.android.survey.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import gr.android.survey.ui.composable.MainScreen
import gr.android.survey.ui.composable.SurveyScreen

@Composable
fun SurveyApp() {
    val navController = rememberNavController()
    SurveyNavHost(
        navController = navController
    )
}

@Composable
fun SurveyNavHost(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) {
            MainScreen(
                navigateToSurvey = {
                    navController.navigate(Screen.SurveyQuestions.route)
                }
            )
        }

        composable(Screen.SurveyQuestions.route) {
            SurveyScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

sealed class Screen(
    val route: String
) {
    data object Main : Screen("main")
    data object SurveyQuestions : Screen("surveyQuestions")
}