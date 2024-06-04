package gr.android.survey.data.dataSource

import gr.android.survey.data.networkCalls.SurveyApi
import gr.android.survey.data.remoteEntities.AnswerSubmissionRequest
import gr.android.survey.data.remoteEntities.RemoteSurvey
import gr.android.survey.data.remoteEntities.RemoteSurveyItem
import gr.android.survey.data.utils.Result
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class QuestionsNetworkDataSourceImplTest {

    @Mock
    private lateinit var api: SurveyApi

    private lateinit var dataSource: QuestionsNetworkDataSource

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        val testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        dataSource = QuestionsNetworkDataSourceImpl(api)
    }

    @After
    fun cleanUp() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test getQuestions success`() = runTest {
        // Given
        val remoteSurvey = RemoteSurvey()
        remoteSurvey.addAll(arrayListOf(RemoteSurveyItem(1,"Question 1")))
        val response = Response.success(remoteSurvey)
        Mockito.`when`(api.getQuestions()).thenReturn(response)

        // When
        val result = dataSource.getQuestions()

        // Then
        assert(result is Result.Success)
        assertEquals(remoteSurvey, (result as Result.Success).data)
    }

    @Test
    fun `test postQuestions success`() = runTest {
        // Given
        val request = AnswerSubmissionRequest(1 , "Answer 1")
        val response = Response.success(Unit)
        Mockito.`when`(api.postQuestion(request)).thenReturn(response)

        // When
        val result = dataSource.postQuestions(request)

        // Then
        assert(result is Result.Success)
        assertEquals(Unit, (result as Result.Success).data)
    }

    @Test
    fun `test getQuestions failure`() = runTest {
        // Given
        val response = Response.error<RemoteSurvey>(400, mock())
        Mockito.`when`(api.getQuestions()).thenReturn(response)

        // When
        val result = dataSource.getQuestions()

        // Then
        assert(result is Result.ClientError)
    }

    @Test
    fun `test postQuestions failure`() = runTest {
        // Given
        val request = AnswerSubmissionRequest(1 , "Answer 1")
        val response = Response.error<Unit>(500, mock())
        Mockito.`when`(api.postQuestion(request)).thenReturn(response)

        // When
        val result = dataSource.postQuestions(request)

        // Then
        assert(result is Result.ServerError)
    }
}
