package gr.android.survey.data.networkCalls

import gr.android.survey.data.remoteEntities.AnswerSubmissionRequest
import gr.android.survey.data.remoteEntities.RemoteSurvey
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SurveyApi {

    @GET("Http://192.168.2.2:8068/questions")
//    @GET("/questions")
    suspend fun getQuestions(): Response<RemoteSurvey>

    @POST("Http://192.168.2.2:8068/question/submit")
//    @POST("/question/submit")
    suspend fun postQuestion(@Body answerSubmissionRequest: AnswerSubmissionRequest): Response<Unit>

}