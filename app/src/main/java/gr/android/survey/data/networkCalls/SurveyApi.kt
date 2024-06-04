package gr.android.survey.data.networkCalls

import gr.android.survey.data.remoteEntities.AnswerSubmissionRequest
import gr.android.survey.data.remoteEntities.RemoteSurvey
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SurveyApi {

    @GET("/questions")
    suspend fun getQuestions(): Response<RemoteSurvey>

    @POST("/question/submit")
    suspend fun postQuestion(@Body answerSubmissionRequest: AnswerSubmissionRequest): Response<Unit>

}