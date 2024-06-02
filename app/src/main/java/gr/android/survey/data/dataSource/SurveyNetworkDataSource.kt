package gr.android.survey.data.dataSource

import gr.android.survey.data.networkCalls.SurveyApi
import gr.android.survey.data.remoteEntities.AnswerSubmissionRequest
import gr.android.survey.data.remoteEntities.RemoteSurvey
import gr.android.survey.data.utils.call
import gr.android.survey.data.utils.Result
import retrofit2.Response

interface QuestionsNetworkDataSource {
    suspend fun getQuestions(): Result<RemoteSurvey>
    suspend fun postQuestions(answerSubmissionRequest: AnswerSubmissionRequest): Result<Unit>
}
class QuestionsNetworkDataSourceImpl(
    private val api: SurveyApi
) : QuestionsNetworkDataSource {

    override suspend fun getQuestions(): Result<RemoteSurvey> {
        return call { api.getQuestions() }
    }

    override suspend fun postQuestions(answerSubmissionRequest: AnswerSubmissionRequest): Result<Unit> {
        return call { api.postQuestion(answerSubmissionRequest) }
    }
}