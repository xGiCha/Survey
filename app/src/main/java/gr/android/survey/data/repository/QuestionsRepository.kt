package gr.android.survey.data.repository

import gr.android.survey.data.dataSource.QuestionsNetworkDataSource
import gr.android.survey.data.remoteEntities.AnswerSubmissionRequest
import gr.android.survey.data.remoteEntities.RemoteSurvey
import gr.android.survey.data.utils.Result
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

interface QuestionsRepository {
    val items: SharedFlow<Result<RemoteSurvey>>
    val postAnsweredQuestionResult: SharedFlow<Result<Boolean>>

    suspend fun getQuestions()
    suspend fun postQuestions(answerSubmissionRequest: AnswerSubmissionRequest)
}

class QuestionsRepositoryImp(
    private val questionsNetworkDataSource: QuestionsNetworkDataSource
): QuestionsRepository{

    private val itemsFlow: MutableSharedFlow<Result<RemoteSurvey>> = MutableSharedFlow()
    override val items: SharedFlow<Result<RemoteSurvey>> = itemsFlow

    private val postAnsweredQuestionResultFlow: MutableSharedFlow<Result<Boolean>> = MutableSharedFlow()
    override val postAnsweredQuestionResult: SharedFlow<Result<Boolean>> = postAnsweredQuestionResultFlow

    override suspend fun getQuestions() {
        when(val response = questionsNetworkDataSource.getQuestions()) {
            is Result.Success -> {
                itemsFlow.emit(Result.Success(response.data))
            }
            else -> {
                itemsFlow.emit(Result.NetworkError(Exception()))
            }
        }
    }

    override suspend fun postQuestions(answerSubmissionRequest: AnswerSubmissionRequest) {
        when(questionsNetworkDataSource.postQuestions(answerSubmissionRequest)) {
            is Result.Success -> {
                postAnsweredQuestionResultFlow.emit(Result.Success(true))
            }
            else -> {
                postAnsweredQuestionResultFlow.emit(Result.NetworkError(Exception()))
            }
        }
    }
}