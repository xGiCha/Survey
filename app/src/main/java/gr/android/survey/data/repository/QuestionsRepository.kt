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
            is Result.NetworkError -> {
                itemsFlow.emit(Result.NetworkError(Exception()))
            }
            is Result.ClientError -> {
                itemsFlow.emit(Result.ClientError(response.httpCode, response.errorCode, response.errorMessage))
            }
            is Result.ServerError -> {
                itemsFlow.emit(Result.ServerError(response.httpCode, response.errorCode, response.errorMessage))

            }
        }
    }

    override suspend fun postQuestions(answerSubmissionRequest: AnswerSubmissionRequest) {
        when(val response = questionsNetworkDataSource.postQuestions(answerSubmissionRequest)) {
            is Result.Success -> {
                postAnsweredQuestionResultFlow.emit(Result.Success(true))
            }
            is Result.NetworkError -> {
                postAnsweredQuestionResultFlow.emit(Result.NetworkError(Exception()))
            }
            is Result.ClientError -> {
                postAnsweredQuestionResultFlow.emit(Result.ClientError(response.httpCode, response.errorCode, response.errorMessage))
            }
            is Result.ServerError -> {
                postAnsweredQuestionResultFlow.emit(Result.ServerError(response.httpCode, response.errorCode, response.errorMessage))

            }
        }
    }
}