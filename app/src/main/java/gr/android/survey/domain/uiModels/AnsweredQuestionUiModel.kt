package gr.android.survey.domain.uiModels

data class AnsweredQuestionUiModel(
    val id: Int? = null,
    val question: String? = null,
    val isSubmitted: Boolean? = null,
    val answeredText: String? = null,
    val index: Int = 0,
    val listSize: Int = 0
)
