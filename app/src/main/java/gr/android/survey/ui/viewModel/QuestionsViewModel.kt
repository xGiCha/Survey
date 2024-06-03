package gr.android.survey.ui.viewModel

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gr.android.survey.data.remoteEntities.AnswerSubmissionRequest
import gr.android.survey.data.repository.ClearSurveyUseCase
import gr.android.survey.domain.uiModels.AnsweredQuestionUiModel
import gr.android.survey.domain.uiModels.QuestionsUiModel
import gr.android.survey.domain.usecases.AnsweredQuestionUseCase
import gr.android.survey.domain.usecases.QuestionsUseCase
import gr.android.survey.domain.utils.Resource
import gr.android.survey.utils.Button
import gr.android.survey.utils.SurveyRemoteState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionsViewModel @Inject constructor(
    private val questionsUseCase: QuestionsUseCase,
    private val answeredQuestionUseCase: AnsweredQuestionUseCase,
    private val clearSurveyUseCase: ClearSurveyUseCase
): ViewModel() {

    private var _uiState = MutableQuestionsUiState()
    val uiState: QuestionsUiState = _uiState

    init {
        getQuestionsCollector()
        postQuestionCollector()
        allReadCollector()
        answeredQuestionItemCollector()
        submittedQuestionsNumCollector()
    }

    private fun getQuestionsCollector(){
        viewModelScope.launch {
            questionsUseCase.getQuestions()
            questionsUseCase.questions.collectLatest {
                when(it) {
                    is Resource.Success -> {
                        updateAnsweredQuestion(it.data)
                        updateQuestionsListSize(it.data?.questions?.size ?: 0)
                        updateLoaderVisibility(false)
                    }
                    is Resource.Error -> {
                        updateLoaderVisibility(false)
                        updateSurveyState(SurveyRemoteState.FETCH_LIST_ERROR)
                        updateErrorMessage(it.message ?: "")
                    }
                }
            }
        }
    }

    fun getAnsweredQuestionByIndex(buttonAction: String = Button.CURRENT.action, index: Int) {
        viewModelScope.launch {
            answeredQuestionUseCase.getAnsweredQuestionByIndex(buttonAction, index)
        }
    }

    private fun submittedQuestionsNumCollector() {
        viewModelScope.launch {
            answeredQuestionUseCase.submittedQuestions.collectLatest {
                updateSubmittedQuestions(it)
            }
        }
    }

    private fun answeredQuestionItemCollector() {
        viewModelScope.launch {
            answeredQuestionUseCase.item.collectLatest {
                updateAnsweredQuestion(it)
            }
        }
    }

    private fun allReadCollector() {
        viewModelScope.launch {
            answeredQuestionUseCase.allReady.collectLatest {
                if (it) {
                    getAnsweredQuestionByIndex(index = 0)
                }
            }
        }
    }

    private fun postQuestionCollector() {
        viewModelScope.launch {
            questionsUseCase.postAnsweredQuestionResult.collectLatest {
                when(it) {
                    is Resource.Success -> {
                        _uiState.answeredQuestionUiModel.let {
                            updateSurveyState(SurveyRemoteState.POST_SUCCESS)
                            setAnsweredQuestion(
                                id = it?.id ?: -1,
                                answeredText = _uiState.answerText ?: "",
                                isSubmitted = true,
                            )
                        }
                        updateLoaderVisibility(false)
                    }
                    is Resource.Error -> {
                        updateLoaderVisibility(false)
                        updateSurveyState(SurveyRemoteState.POST_ERROR)
                        updateErrorMessage(it.message ?: "")
                    }
                }
            }
        }
    }
    fun postQuestion(id: Int, answer: String) {
        updateAnsweredText(answer)
        viewModelScope.launch{
            questionsUseCase.postQuestions(AnswerSubmissionRequest(id, answer))
        }
    }

    private fun setAnsweredQuestion(
        id: Int,
        question: String? = null,
        isSubmitted: Boolean? = null,
        answeredText: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            answeredQuestionUseCase.setAnsweredQuestion(id, question, isSubmitted, answeredText)
        }
    }

    private fun updateAnsweredQuestion(answeredQuestionUiModel: AnsweredQuestionUiModel?) {
        _uiState.answeredQuestionUiModel = answeredQuestionUiModel
    }

    private fun updateAnsweredQuestion(questionsUiModel: QuestionsUiModel?) {
        _uiState.questionsUiModel = questionsUiModel
    }

    private fun updateAnsweredText(answeredText: String) {
        _uiState.answerText = answeredText
    }

    fun updateSurveyState(surveyState: SurveyRemoteState) {
        _uiState.surveyState = surveyState
    }

    private fun updateQuestionsListSize(questionsListSize: Int) {
        _uiState.questionsListSize = questionsListSize
    }

    private fun updateLoaderVisibility(loaderVisibility: Boolean) {
        _uiState.loaderVisibility = loaderVisibility
    }

    private fun updateSubmittedQuestions(submittedQuestions: Int) {
        _uiState.submittedQuestions = submittedQuestions
    }

    fun updateQuestionCounter(questionCounter: Int) {
        _uiState.questionCounter = questionCounter
    }


    fun updateClickableBackground(clickableBackground: Boolean) {
        _uiState.clickableBackground = clickableBackground
    }

    fun updateErrorMessage(errorMessage: String) {
        _uiState.errorMessage = errorMessage
    }

    fun resetSurvey() {
        clearSurveyUseCase.resetSurvey()
    }

    @Stable
    interface QuestionsUiState {
        val answeredQuestionUiModel: AnsweredQuestionUiModel?
        val questionsUiModel: QuestionsUiModel?
        val answerText: String?
        val surveyState: SurveyRemoteState
        val questionsListSize: Int
        val loaderVisibility: Boolean
        val submittedQuestions: Int
        val questionCounter: Int
        val clickableBackground: Boolean
        val errorMessage: String?
    }

    class MutableQuestionsUiState(
        answeredQuestionUiModel: AnsweredQuestionUiModel? = null,
        questionsUiModel: QuestionsUiModel? = null
    ): QuestionsUiState {
        override var answeredQuestionUiModel: AnsweredQuestionUiModel? by mutableStateOf(answeredQuestionUiModel)
        override var questionsUiModel: QuestionsUiModel? by mutableStateOf(questionsUiModel)
        override var answerText: String? by mutableStateOf(null)
        override var surveyState: SurveyRemoteState by mutableStateOf(SurveyRemoteState.OTHER)
        override var questionsListSize: Int by mutableIntStateOf(0)
        override var loaderVisibility: Boolean by mutableStateOf(false)
        override var submittedQuestions: Int by mutableIntStateOf(0)
        override var questionCounter: Int by mutableIntStateOf(1)
        override var clickableBackground: Boolean by mutableStateOf(true)
        override var errorMessage: String? by mutableStateOf(null)
    }
}