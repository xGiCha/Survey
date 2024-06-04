package gr.android.survey.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import gr.android.survey.data.dataSource.QuestionsNetworkDataSource
import gr.android.survey.data.remoteEntities.AnswerSubmissionRequest
import gr.android.survey.data.remoteEntities.RemoteSurvey
import gr.android.survey.data.remoteEntities.RemoteSurveyItem
import gr.android.survey.data.utils.Result
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class QuestionsRepositoryImpTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var questionsNetworkDataSource: QuestionsNetworkDataSource

    private lateinit var repository: QuestionsRepository
    val testDispatcher = StandardTestDispatcher()
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        repository = QuestionsRepositoryImp(questionsNetworkDataSource)
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
        val response = Result.Success(remoteSurvey)
        Mockito.`when`(questionsNetworkDataSource.getQuestions()).thenReturn(response)

        // When
        repository.getQuestions()

        // then
        val result = repository.items.first()
        assert(result is Result.Success)
        assertEquals(remoteSurvey, (result as Result.Success).data)
    }

    @Test
    fun `test postQuestions success`() = runTest {
        // Given
        val request = AnswerSubmissionRequest(1 , "Answer 1")
        val response = Result.Success(Unit)
        Mockito.`when`(questionsNetworkDataSource.postQuestions(request)).thenReturn(response)

        // When
        repository.postQuestions(request)

        // Then
        val result = repository.postAnsweredQuestionResult.first()
        assert(result is Result.Success)
        assertEquals(true, (result as Result.Success).data)
    }

    @Test
    fun `test getQuestions network error`() = runTest {
        // Given
        val response = Result.NetworkError("Network Error")
        Mockito.`when`(questionsNetworkDataSource.getQuestions()).thenReturn(response)

        // When
        repository.getQuestions()

        // Then
        val result = repository.items.first()
        assert(result is Result.NetworkError)
    }

    @Test
    fun `test postQuestions network error`() = runTest {
        // Given
        val request = AnswerSubmissionRequest(1 , "Answer 1")
        val response = Result.NetworkError("Network Error")
        Mockito.`when`(questionsNetworkDataSource.postQuestions(request)).thenReturn(response)

        // When
        repository.postQuestions(request)

        // Then
        val result = repository.postAnsweredQuestionResult.first()
        assert(result is Result.NetworkError)
    }

    @Test
    fun `test getQuestions client error`() = runTest {
        // Given
        val response = Result.ClientError(400, -1, "Client Error")
        Mockito.`when`(questionsNetworkDataSource.getQuestions()).thenReturn(response)

        // When
        repository.getQuestions()

        // Then
        val result = repository.items.first()
        assert(result is Result.ClientError)
        assertEquals("Client Error", (result as Result.ClientError).errorMessage)
    }

    @Test
    fun `test postQuestions client error`() = runTest {
        // Given
        val request = AnswerSubmissionRequest(1 , "Answer 1")
        val response = Result.ClientError(400, -1, "Client Error")
        Mockito.`when`(questionsNetworkDataSource.postQuestions(request)).thenReturn(response)

        // When
        repository.postQuestions(request)

        // Then
        val result = repository.postAnsweredQuestionResult.first()
        assert(result is Result.ClientError)
        assertEquals("Client Error", (result as Result.ClientError).errorMessage)
    }

    @Test
    fun `test getQuestions server error`() = runTest {
        // Given
        val response = Result.ServerError(500, -1, "Server Error")
        Mockito.`when`(questionsNetworkDataSource.getQuestions()).thenReturn(response)

        // When
        repository.getQuestions()

        // Then
        val result = repository.items.first()
        assert(result is Result.ServerError)
        assertEquals("Server Error", (result as Result.ServerError).errorMessage)
    }

    @Test
    fun `test postQuestions server error`() = runTest {
        // Given
        val request = AnswerSubmissionRequest(1 , "Answer 1")
        val response = Result.ServerError(500, -1, "Server Error")
        Mockito.`when`(questionsNetworkDataSource.postQuestions(request)).thenReturn(response)

        // When
        repository.postQuestions(request)

        // Then
        val result = repository.postAnsweredQuestionResult.first()
        assert(result is Result.ServerError)
        assertEquals("Server Error", (result as Result.ServerError).errorMessage)
    }
}
