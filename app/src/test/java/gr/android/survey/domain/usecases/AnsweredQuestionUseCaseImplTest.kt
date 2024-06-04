package gr.android.survey.domain.usecases

import gr.android.survey.data.remoteEntities.RemoteSurvey
import gr.android.survey.data.remoteEntities.RemoteSurveyItem
import gr.android.survey.data.repository.AnsweredQuestionsRepository
import gr.android.survey.data.repository.AnsweredQuestionsRepositoryImp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class AnsweredQuestionUseCaseImplTest {

    private lateinit var answeredQuestionsRepository: AnsweredQuestionsRepository

    private lateinit var useCase: AnsweredQuestionUseCaseImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        answeredQuestionsRepository = AnsweredQuestionsRepositoryImp()
        useCase = AnsweredQuestionUseCaseImpl(answeredQuestionsRepository)
    }

    @Test
    fun `test getAnsweredQuestionByIndex`() = runTest {
        // Given
        val buttonAction = "Next"
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

        // When
        useCase.getAnsweredQuestionByIndex(buttonAction, index)

        // Then
        val result = useCase.submittedQuestions.first()
        assertTrue(result == 0)
    }

    @Test
    fun `test allReady flow`() = runTest {
        // Given
        val remoteSurvey = RemoteSurvey().apply {
            addAll(
                arrayListOf(
                    RemoteSurveyItem(1, "Question 1"),
                    RemoteSurveyItem(2, "Question 2")
                )
            )
        }

        // When
        answeredQuestionsRepository.setInitQuestionList(remoteSurvey)

        // Then
        val result = useCase.allReady.first()
        assertTrue(result)
    }

    @Test
    fun `test item flow`() = runTest {
        // Given
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
        answeredQuestionsRepository.getAnsweredQuestionByIndex("", 0)

        useCase.getAnsweredQuestionByIndex("", 0)

        // Then
        val result = useCase.item.first()
        assertEquals(1, result.id)
    }

    @Test
    fun `test submittedQuestions flow`() = runTest {
        // Given
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
        answeredQuestionsRepository.setAnsweredQuestion(
            remoteSurvey.first().id,
            remoteSurvey.first().question,
            true,
            "test"
        )
        answeredQuestionsRepository.getAnsweredQuestionByIndex("", 1)

        useCase.getAnsweredQuestionByIndex("", 1)

        // Then
        val result = useCase.submittedQuestions.first()
        assertEquals(1, result)
    }
}
