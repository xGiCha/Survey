package gr.android.survey.utils

enum class Button(val action: String) {
    NEXT("NEXT"),
    PREVIOUS("PREVIOUS"),
    CURRENT("CURRENT")
}

enum class SurveyRemoteState(val value: String) {
    FETCH_LIST_ERROR("FETCH_LIST_ERROR"),
    POST_ERROR("POST_ERROR"),
    POST_SUCCESS("POST_SUCCESS"),
    OTHER("OTHER")
}

sealed class MessageState {
    object Success : MessageState()
    object Error : MessageState()
}