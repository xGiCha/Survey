package gr.android.survey.data.repository

import gr.android.survey.data.dataSource.QuestionsNetworkDataSource
import gr.android.survey.data.remoteEntities.AnsweredQuestionItem
import gr.android.survey.data.remoteEntities.RemoteSurvey
import gr.android.survey.data.remoteEntities.RemoteSurveyItem
import gr.android.survey.data.utils.Result
import gr.android.survey.domain.uiModels.QuestionsUiModel
import gr.android.survey.utils.Button
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface AnsweredQuestionsRepository {
    val items: SharedFlow<List<AnsweredQuestionItem>>

    val item: SharedFlow<AnsweredQuestionItem>
    val allReady: SharedFlow<Boolean>

    suspend fun setAnsweredQuestion(id: Int, question: String?, isSubmitted: Boolean?, answeredText: String)

    suspend fun getAnsweredQuestionByIndex(buttonAction: String, index: Int)

    suspend fun setInitQuestionList(questionList: RemoteSurvey)
}

class AnsweredQuestionsRepositoryImp(
): AnsweredQuestionsRepository{

    private val answeredQuestions: ArrayList<AnsweredQuestionItem> = arrayListOf()

    private val allReadyFlow: MutableSharedFlow<Boolean> = MutableSharedFlow()
    override val allReady: SharedFlow<Boolean> = allReadyFlow

    private val itemFlow: MutableSharedFlow<AnsweredQuestionItem> = MutableSharedFlow()
    override val item: SharedFlow<AnsweredQuestionItem> = itemFlow

    private val itemsFlow: MutableSharedFlow<List<AnsweredQuestionItem>> = MutableSharedFlow()
    override val items: SharedFlow<List<AnsweredQuestionItem>> = itemsFlow

    override suspend fun setAnsweredQuestion(
        id: Int,
        question: String?,
        isSubmitted: Boolean?,
        answeredText: String
    ) {

        val index = answeredQuestions.indexOfFirst { it.id == id }
        if (index != -1) {
            val selectedQuestion = answeredQuestions[index]
            val updatedQuestion = selectedQuestion.copy(answeredText = answeredText)
            answeredQuestions[index] = updatedQuestion
        }
    }

    override suspend fun getAnsweredQuestionByIndex(buttonAction: String, index: Int) {
        val newIndex = index - 1
        val myIndex = when (buttonAction) {
            Button.NEXT.action -> {
                if (newIndex + 1 < answeredQuestions.size) newIndex + 1 else newIndex
            }

            Button.PREVIOUS.action -> {
                if (newIndex - 1 >= 0) newIndex - 1 else newIndex
            }

            else -> index
        }
        itemFlow.emit(answeredQuestions[myIndex])
    }

    override suspend fun setInitQuestionList(questionList: RemoteSurvey) {
        questionList.forEachIndexed { index, listItem ->
            answeredQuestions.add(
                AnsweredQuestionItem(
                    id = listItem.id,
                    question = listItem.question,
                    index = index + 1,
                    listSize = questionList.size
                )
            )
        }
        if (questionList.size > 0)
            allReadyFlow.emit(true)
    }
}