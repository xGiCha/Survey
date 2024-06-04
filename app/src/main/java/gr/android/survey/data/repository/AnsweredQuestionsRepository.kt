package gr.android.survey.data.repository

import gr.android.survey.data.remoteEntities.AnsweredQuestionItem
import gr.android.survey.data.remoteEntities.RemoteSurvey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

interface AnsweredQuestionsRepository {
    val item: SharedFlow<AnsweredQuestionItem>
    val allReady: SharedFlow<Boolean>
    val submittedQuestions: SharedFlow<Int>

    suspend fun setAnsweredQuestion(id: Int, question: String?, isSubmitted: Boolean?, answeredText: String)
    suspend fun getAnsweredQuestionByIndex(buttonAction: String, index: Int)
    suspend fun setInitQuestionList(questionList: RemoteSurvey)

    fun resetValues()
}

class AnsweredQuestionsRepositoryImp(
): AnsweredQuestionsRepository{

    private val answeredQuestions: ArrayList<AnsweredQuestionItem> = arrayListOf()

    private val submittedQuestionsFlow: MutableSharedFlow<Int> = MutableSharedFlow(replay = 1)
    override val submittedQuestions: SharedFlow<Int> = submittedQuestionsFlow

    private val allReadyFlow: MutableSharedFlow<Boolean> = MutableSharedFlow(replay = 1)
    override val allReady: SharedFlow<Boolean> = allReadyFlow

    private val itemFlow: MutableSharedFlow<AnsweredQuestionItem> = MutableSharedFlow(replay = 1)
    override val item: SharedFlow<AnsweredQuestionItem> = itemFlow

    override suspend fun setAnsweredQuestion(
        id: Int,
        question: String?,
        isSubmitted: Boolean?,
        answeredText: String
    ) {

        if(answeredQuestions.isNotEmpty()) {
            val index = answeredQuestions.indexOfFirst { it.id == id }
            if (index != -1) {
                val selectedQuestion = answeredQuestions[index]
                val updatedQuestion =
                    selectedQuestion.copy(answeredText = answeredText, isSubmitted = isSubmitted)
                answeredQuestions[index] = updatedQuestion
            }
        }
    }

    override suspend fun getAnsweredQuestionByIndex(buttonAction: String, index: Int) {
        if(answeredQuestions.isNotEmpty()) {
            submittedQuestionsFlow.emit(answeredQuestions.filter { it.isSubmitted == true }.size)
            itemFlow.emit(answeredQuestions[index])
        }
    }

    override suspend fun setInitQuestionList(questionList: RemoteSurvey) {
        if (questionList.isNotEmpty()) {
            answeredQuestions.addAll(
                questionList.map { listItem ->
                    AnsweredQuestionItem(
                        id = listItem.id,
                    )
                }
            )
            allReadyFlow.emit(true)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun resetValues() {
        answeredQuestions.clear()
        submittedQuestionsFlow.resetReplayCache()
        allReadyFlow.resetReplayCache()
        itemFlow.resetReplayCache()
    }
}