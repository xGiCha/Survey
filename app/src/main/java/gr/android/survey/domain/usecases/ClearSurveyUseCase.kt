package gr.android.survey.domain.usecases

import gr.android.survey.data.repository.AnsweredQuestionsRepository
import gr.android.survey.data.repository.QuestionsRepository

interface ClearSurveyUseCase {
    fun resetSurvey()
}

class ClearSurveyUseCaseImpl(
    private val answeredQuestionsRepository : AnsweredQuestionsRepository,
    private val questionsRepository : QuestionsRepository,
    private val answeredQuestionUseCase : AnsweredQuestionUseCase,
    private val questionsUseCase : QuestionsUseCase
): ClearSurveyUseCase {

    override fun resetSurvey() {
        answeredQuestionsRepository.resetValues()
        questionsRepository.resetQuestions()
        answeredQuestionUseCase.reset()
        questionsUseCase.reset()
    }

}