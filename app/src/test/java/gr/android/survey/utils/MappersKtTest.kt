package gr.android.survey.utils


import gr.android.survey.data.remoteEntities.AnsweredQuestionItem
import gr.android.survey.data.remoteEntities.RemoteSurvey
import gr.android.survey.data.remoteEntities.RemoteSurveyItem
import gr.android.survey.domain.uiModels.AnsweredQuestionUiModel
import gr.android.survey.domain.uiModels.QuestionItemUiModel
import gr.android.survey.domain.uiModels.QuestionsUiModel
import org.junit.Assert.assertEquals
import org.junit.Test

class MappingExtensionTest {

    @Test
    fun `test mapToQuestionsUiModel`() {
        // Given
        val remoteSurvey = RemoteSurvey().apply {
            add(RemoteSurveyItem(1, "Question 1"))
            add(RemoteSurveyItem(2, "Question 2"))
        }

        // When
        val result = remoteSurvey.mapToQuestionsUiModel()

        // Then
        val expected = QuestionsUiModel(
            arrayListOf(
                QuestionItemUiModel(1, "Question 1"),
                QuestionItemUiModel(2, "Question 2")
            )
        )
        assertEquals(expected, result)
    }

    @Test
    fun `test mapToAnsweredQuestionUiModel`() {
        // Given
        val answeredQuestionItem = AnsweredQuestionItem(
            id = 1,
            isSubmitted = true,
            answeredText = "Answered text"
        )

        // When
        val result = answeredQuestionItem.mapToAnsweredQuestionUiModel()

        // Then
        val expected = AnsweredQuestionUiModel(
            id = 1,
            isSubmitted = true,
            answeredText = "Answered text"
        )
        assertEquals(expected, result)
    }
}
