package gr.android.survey.domain.usecases

import gr.android.survey.data.dataSource.QuestionsNetworkDataSource
import gr.android.survey.data.remoteEntities.AnswerSubmissionRequest
import gr.android.survey.data.remoteEntities.RemoteSurvey
import gr.android.survey.data.remoteEntities.RemoteSurveyItem
import gr.android.survey.data.repository.AnsweredQuestionsRepository
import gr.android.survey.data.repository.QuestionsRepository
import gr.android.survey.data.repository.QuestionsRepositoryImp
import gr.android.survey.data.utils.Result
import gr.android.survey.domain.utils.Resource
import gr.android.survey.utils.mapToQuestionsUiModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class QuestionsUseCaseTest {

    @Mock
    private lateinit var questionsNetworkDataSource: QuestionsNetworkDataSource

    private lateinit var questionsRepository: QuestionsRepository

    @Mock
    private lateinit var answeredQuestionsRepository: AnsweredQuestionsRepository

    private lateinit var useCase: QuestionsUseCase

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        questionsRepository = QuestionsRepositoryImp(questionsNetworkDataSource)
        useCase = QuestionsUseCaseImpl(questionsRepository, answeredQuestionsRepository)
    }

    @After
    fun cleanUp() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test getQuestions emits Success`() = runTest(testDispatcher) {
        // Given
        val remoteSurvey = RemoteSurvey().apply {
            addAll(arrayListOf(RemoteSurveyItem(1, "Question 1"), RemoteSurveyItem(2, "Question 2")))
        }
        val response = Result.Success(remoteSurvey)
        Mockito.`when`(questionsNetworkDataSource.getQuestions()).thenReturn(response)

        // when
        useCase.getQuestions()

        // Then
        val result = useCase.questions.first()
        assert(result is Resource.Success)
        assertEquals(remoteSurvey.mapToQuestionsUiModel(), (result as Resource.Success).data)

    }

    @Test
    fun `test postQuestions emits Success on postAnsweredQuestionResult flow when repository returns Success`() = runTest {
        // Given
        val request = AnswerSubmissionRequest(1 , "Answer 1")
        val response = Result.Success(Unit)
        Mockito.`when`(questionsNetworkDataSource.postQuestions(request)).thenReturn(response)

        // When
        useCase.postQuestions(request)

        // Then
        val result = useCase.postAnsweredQuestionResult.first()
        assertTrue(result is Resource.Success)
        assertEquals(true, (result as Resource.Success).data)
    }

    @Test
    fun `test getQuestions emits Error on questions flow when repository returns ServerError`() = runTest {
        // Given
        val response = Result.NetworkError("Network Error")
        Mockito.`when`(questionsNetworkDataSource.getQuestions()).thenReturn(response)

        // When
        useCase.getQuestions()

        // Then
        val result = useCase.questions.first()
        assertTrue(result is Resource.Error)
        assertEquals("Network Error", (result as Resource.Error).message)
    }

    @Test
    fun `test postQuestions emits Error on postAnsweredQuestionResult flow when repository returns ClientError`() = runTest {
        // Given
        val errorMessage = "Client Error"
        val request = AnswerSubmissionRequest(1 , "Answer 1")
        val response = Result.ClientError(400, -1, "Client Error")
        Mockito.`when`(questionsNetworkDataSource.postQuestions(request)).thenReturn(response)

        // When
        useCase.postQuestions(request)

        // Then
        val result = useCase.postAnsweredQuestionResult.first()
        assertTrue(result is Resource.Error)
        assertEquals(errorMessage, (result as Resource.Error).message)
    }
}
