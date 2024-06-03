package gr.android.survey.data.repository

interface ClearSurveyUseCase {
    fun resetSurvey()
}

class ClearSurveyUseCaseImpl(
    private val answeredQuestionsRepository : AnsweredQuestionsRepository
): ClearSurveyUseCase {

    override fun resetSurvey() {
        answeredQuestionsRepository.resetValues()
    }

}