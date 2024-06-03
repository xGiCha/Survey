package gr.android.survey.data.repository

import gr.android.survey.data.remoteEntities.AnsweredQuestionItem
import gr.android.survey.data.remoteEntities.RemoteSurvey
import gr.android.survey.utils.Button
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface AnsweredQuestionsRepository {
    val item: SharedFlow<AnsweredQuestionItem>
    val allReady: SharedFlow<Boolean>
    val submittedQuestions: SharedFlow<Int>
    val questionCounter: StateFlow<Int>

    suspend fun setAnsweredQuestion(id: Int, question: String?, isSubmitted: Boolean?, answeredText: String)
    suspend fun getAnsweredQuestionByIndex(buttonAction: String)
    suspend fun setInitQuestionList(questionList: RemoteSurvey)
}

class AnsweredQuestionsRepositoryImp(
): AnsweredQuestionsRepository{

    private val answeredQuestions: ArrayList<AnsweredQuestionItem> = arrayListOf()
    private var _questionCounter = 1
    private var index = 0

    private val questionCounterFlow: MutableStateFlow<Int> = MutableStateFlow(1)
    override val questionCounter: StateFlow<Int> = questionCounterFlow

    private val submittedQuestionsFlow: MutableSharedFlow<Int> = MutableSharedFlow()
    override val submittedQuestions: SharedFlow<Int> = submittedQuestionsFlow

    private val allReadyFlow: MutableSharedFlow<Boolean> = MutableSharedFlow()
    override val allReady: SharedFlow<Boolean> = allReadyFlow

    private val itemFlow: MutableSharedFlow<AnsweredQuestionItem> = MutableSharedFlow()
    override val item: SharedFlow<AnsweredQuestionItem> = itemFlow

    override suspend fun setAnsweredQuestion(
        id: Int,
        question: String?,
        isSubmitted: Boolean?,
        answeredText: String
    ) {

        val index = answeredQuestions.indexOfFirst { it.id == id }
        if (index != -1) {
            val selectedQuestion = answeredQuestions[index]
            val updatedQuestion = selectedQuestion.copy(answeredText = answeredText, isSubmitted = isSubmitted)
            answeredQuestions[index] = updatedQuestion
        }
    }

    override suspend fun getAnsweredQuestionByIndex(buttonAction: String) {
        when(buttonAction) {
            Button.NEXT.action -> {
                questionCounterFlow.emit(++_questionCounter)
                ++index
            }
            Button.PREVIOUS.action -> {
                questionCounterFlow.emit(--_questionCounter)
                --index
            }
            Button.CURRENT.action -> {
                questionCounterFlow.emit(_questionCounter)
                index
            }
        }
        submittedQuestionsFlow.emit(answeredQuestions.filter { it.isSubmitted == true }.size)
        itemFlow.emit(answeredQuestions[index])
    }

    override suspend fun setInitQuestionList(questionList: RemoteSurvey) {
        if (questionList.isNotEmpty()) {
            answeredQuestions.addAll(
                questionList.map { listItem ->
                    AnsweredQuestionItem(
                        id = listItem.id,
                        question = listItem.question
                    )
                }
            )
            allReadyFlow.emit(true)
        }
    }
}