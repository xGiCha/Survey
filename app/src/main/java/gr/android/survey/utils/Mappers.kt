package gr.android.survey.utils

import gr.android.survey.data.remoteEntities.AnsweredQuestionItem
import gr.android.survey.data.remoteEntities.RemoteSurvey
import gr.android.survey.domain.uiModels.AnsweredQuestionUiModel
import gr.android.survey.domain.uiModels.QuestionItemUiModel
import gr.android.survey.domain.uiModels.QuestionsUiModel


fun RemoteSurvey.mapToQuestionsUiModel(): QuestionsUiModel {
    val items = arrayListOf<QuestionItemUiModel>()
    for (item in  this) {
        val questionUiItem = QuestionItemUiModel(
            id = item.id,
            question = item.question
        )
        items.add(questionUiItem)
    }

    return QuestionsUiModel(items)

}

fun AnsweredQuestionItem.mapToAnsweredQuestionUiModel(): AnsweredQuestionUiModel {
    return AnsweredQuestionUiModel(
        id = this.id,
        question = this.question,
        isSubmitted = this.isSubmitted,
        answeredText = this.answeredText,
        index = this.index ?: 0,
        listSize = this.listSize
    )
}