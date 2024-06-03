package gr.android.survey.domain.uiModels

data class QuestionsUiModel(
    val questions: List<QuestionItemUiModel>? = null
)

data class QuestionItemUiModel(
    val id: Int? = null,
    val question: String? = null
)
