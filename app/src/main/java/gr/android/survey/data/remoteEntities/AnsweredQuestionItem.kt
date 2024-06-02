package gr.android.survey.data.remoteEntities

data class AnsweredQuestionItem(
    val id: Int? = null,
    val question: String? = null,
    val isSubmitted: Boolean? = null,
    val answeredText: String? = null,
    val index: Int? = null,
    val listSize: Int = 0
)