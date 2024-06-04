package gr.android.survey.ui.viewModel

import gr.android.survey.data.dataSource.QuestionsNetworkDataSource
import gr.android.survey.data.remoteEntities.AnswerSubmissionRequest
import gr.android.survey.data.remoteEntities.RemoteSurvey
import gr.android.survey.data.remoteEntities.RemoteSurveyItem
import gr.android.survey.data.repository.AnsweredQuestionsRepository
import gr.android.survey.data.repository.AnsweredQuestionsRepositoryImp
import gr.android.survey.data.repository.QuestionsRepository
import gr.android.survey.data.repository.QuestionsRepositoryImp
import gr.android.survey.data.utils.Result
import gr.android.survey.domain.uiModels.QuestionItemUiModel
import gr.android.survey.domain.uiModels.QuestionsUiModel
import gr.android.survey.domain.usecases.AnsweredQuestionUseCase
import gr.android.survey.domain.usecases.AnsweredQuestionUseCaseImpl
import gr.android.survey.domain.usecases.ClearSurveyUseCase
import gr.android.survey.domain.usecases.ClearSurveyUseCaseImpl
import gr.android.survey.domain.usecases.QuestionsUseCase
import gr.android.survey.domain.usecases.QuestionsUseCaseImpl
import gr.android.survey.utils.SurveyRemoteState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class QuestionsViewModelTest {

    @Mock
    private lateinit var questionsNetworkDataSource: QuestionsNetworkDataSource

    private lateinit var questionsUseCase: QuestionsUseCase

    private lateinit var answeredQuestionUseCase: AnsweredQuestionUseCase

    private lateinit var questionsRepository: QuestionsRepository

    private lateinit var answeredQuestionsRepository: AnsweredQuestionsRepository

    @Mock
    private lateinit var clearSurveyUseCase: ClearSurveyUseCase

    private lateinit var viewModel: QuestionsViewModel

    val testDispatcher = StandardTestDispatcher()


    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        answeredQuestionsRepository = AnsweredQuestionsRepositoryImp()
        answeredQuestionUseCase = AnsweredQuestionUseCaseImpl(answeredQuestionsRepository)
        questionsRepository = QuestionsRepositoryImp(questionsNetworkDataSource)
        questionsUseCase = QuestionsUseCaseImpl(questionsRepository, answeredQuestionsRepository)
        clearSurveyUseCase = ClearSurveyUseCaseImpl(answeredQuestionsRepository, questionsRepository, answeredQuestionUseCase, questionsUseCase)
    }

    @After
    fun cleanUp() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test getQuestionsCollector emits Success`() = runTest {
        // Given
        val remoteSurvey = RemoteSurvey().apply {
            addAll(arrayListOf(RemoteSurveyItem(1, "Question 1")))
        }
        val questionsUiModel = QuestionsUiModel(listOf(QuestionItemUiModel(id = 1, question = "Question 1")))
        val response = Result.Success(remoteSurvey)
        Mockito.`when`(questionsNetworkDataSource.getQuestions()).thenReturn(response)

        // When
        viewModel = QuestionsViewModel(questionsUseCase, answeredQuestionUseCase, clearSurveyUseCase)
        questionsUseCase.getQuestions()
        runCurrent()

        // Then
        assertEquals(questionsUiModel, viewModel.uiState.questionsUiModel)
        assertEquals(SurveyRemoteState.OTHER, viewModel.uiState.surveyState)
        assertEquals(1, viewModel.uiState.questionsListSize)
        assertEquals(false, viewModel.uiState.loaderVisibility)
        assertEquals(null, viewModel.uiState.errorMessage)
    }

    @Test
    fun `test getQuestionsCollector emits Error`() = runTest {
        // Given
        val response = Result.ClientError(400, -1, "Client Error")
        Mockito.`when`(questionsNetworkDataSource.getQuestions()).thenReturn(response)

        // When
        viewModel = QuestionsViewModel(questionsUseCase, answeredQuestionUseCase, clearSurveyUseCase)
        questionsUseCase.getQuestions()
        runCurrent()

        // Then
        assertEquals(null, viewModel.uiState.questionsUiModel)
        assertEquals(0, viewModel.uiState.questionsListSize)
        assertEquals(false, viewModel.uiState.loaderVisibility)
    }

    @Test
    fun `test getAnsweredQuestionByIndex`() = runTest {
        // Given
        val index = 1
        val remoteSurvey = RemoteSurvey().apply {
            addAll(
                arrayListOf(
                    RemoteSurveyItem(1, "Question 1"),
                    RemoteSurveyItem(2, "Question 2")
                )
            )
        }
        // when
        answeredQuestionsRepository.setInitQuestionList(remoteSurvey)
        answeredQuestionsRepository.getAnsweredQuestionByIndex("", index)
        viewModel = QuestionsViewModel(questionsUseCase, answeredQuestionUseCase, clearSurveyUseCase)
        viewModel.getAnsweredQuestionByIndex(index = index)

        // Then
        assertEquals(0, viewModel.uiState.submittedQuestions)
        assertEquals(SurveyRemoteState.OTHER, viewModel.uiState.surveyState)
    }

    @Test
    fun `test postQuestionCollector emits Success`() = runTest {
        // Given
        val request = AnswerSubmissionRequest(1 , "Answer 1")
        val response = Result.Success(Unit)
        Mockito.`when`(questionsNetworkDataSource.postQuestions(request)).thenReturn(response)

        // When
        viewModel = QuestionsViewModel(questionsUseCase, answeredQuestionUseCase, clearSurveyUseCase)
        questionsRepository.postQuestions(request)
        runCurrent()

        // Then
        assertEquals(SurveyRemoteState.POST_SUCCESS, viewModel.uiState.surveyState)
    }

}
