package gr.android.survey.data.repository


import gr.android.survey.data.remoteEntities.RemoteSurvey
import gr.android.survey.data.remoteEntities.RemoteSurveyItem
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class AnsweredQuestionsRepositoryTest {

    private lateinit var repository: AnsweredQuestionsRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        val testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        repository = AnsweredQuestionsRepositoryImp()
    }

    @After
    fun cleanUp() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test setInitQuestionList emits true on allReadyFlow`() = runTest {
        // Given
        val remoteSurvey = RemoteSurvey().apply {
            addAll(arrayListOf(RemoteSurveyItem(1, "Question 1"), RemoteSurveyItem(2, "Question 2")))
        }

        // When
        repository.setInitQuestionList(remoteSurvey)

        // Then
        val result = repository.allReady.first()
        assertTrue(result)
    }

    @Test
    fun `test getAnsweredQuestionByIndex emits correct item on itemFlow`() = runTest {
        // Given
        val remoteSurvey = RemoteSurvey().apply {
            addAll(arrayListOf(RemoteSurveyItem(1, "Question 1"), RemoteSurveyItem(2, "Question 2")))
        }
        repository.setInitQuestionList(remoteSurvey)

        // When
        repository.getAnsweredQuestionByIndex("someAction", 0)

        // Then
        val result = repository.item.first()
        assertEquals(1, result.id)
    }

    @Test
    fun `test setAnsweredQuestion updates item in the list`() = runTest {
        // Given
        val remoteSurvey = RemoteSurvey().apply {
            addAll(arrayListOf(RemoteSurveyItem(1, "Question 1"), RemoteSurveyItem(2, "Question 2")))
        }
        repository.setInitQuestionList(remoteSurvey)

        // When
        repository.setAnsweredQuestion(1, "Question 1", true, "Answered Text")

        // Then
        repository.getAnsweredQuestionByIndex("someAction", 0)
        val result = repository.item.first()
        assertEquals("Answered Text", result.answeredText)
        assertEquals(true, result.isSubmitted)
    }

    @Test
    fun `test resetValues clears the data`() = runTest {
        // Given
        val remoteSurvey = RemoteSurvey().apply {
            addAll(arrayListOf(RemoteSurveyItem(1, "Question 1"), RemoteSurveyItem(2, "Question 2")))
        }
        repository.setInitQuestionList(remoteSurvey)
        repository.setAnsweredQuestion(1, "Question 1", true, "Answered Text")
        repository.getAnsweredQuestionByIndex("someAction", 0)

        // When
        repository.resetValues()

        // Then
        assertTrue(repository.submittedQuestions.replayCache.isEmpty())
        assertTrue(repository.allReady.replayCache.isEmpty())
        assertTrue(repository.item.replayCache.isEmpty())
    }
}
