package gr.android.survey.ui.viewModel

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import gr.android.survey.data.remoteEntities.AnswerSubmissionRequest
import gr.android.survey.data.repository.AnsweredQuestionsRepository
import gr.android.survey.domain.uiModels.AnsweredQuestionUiModel
import gr.android.survey.domain.uiModels.QuestionsUiModel
import gr.android.survey.domain.usecases.AnsweredQuestionUseCase
import gr.android.survey.domain.usecases.QuestionsUseCase
import gr.android.survey.domain.utils.Resource
import gr.android.survey.utils.Button
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionsViewModel @Inject constructor(
    private val questionsUseCase: QuestionsUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val answeredQuestionUseCase: AnsweredQuestionUseCase
): ViewModel() {

    private val _uiState = MutableQuestionsUiState()
    val uiState: QuestionsUiState = _uiState

    init {
        viewModelScope.launch( Dispatchers.IO ) {
            questionsUseCase.getQuestions()
        }

        viewModelScope.launch( Dispatchers.IO ) {
            answeredQuestionUseCase.allReady.collectLatest {
                if (it) {
                    getAnsweredQuestionByIndex()
                }
            }
        }
        viewModelScope.launch( Dispatchers.IO ) {
            answeredQuestionUseCase.item.collectLatest {
                updateAnsweredQuestion(it)
            }
        }

        viewModelScope.launch( Dispatchers.IO ) {
            questionsUseCase.postAnsweredQuestionResult.collectLatest {
                when(it) {
                    is Resource.Success -> {
                        _uiState.answeredQuestionUiModel.let {
                            setAnsweredQuestion(
                                id = it?.id ?: -1,
                                answeredText = it?.answeredText ?: "",
                                index = it?.index,
                                isSubmitted = true
                            )
                        }
                    }
                    is Resource.Loading -> {}
                    is Resource.Error -> {}
                }
            }
        }
    }

    fun setAnsweredQuestion(
        id: Int,
        question: String? = null,
        isSubmitted: Boolean? = null,
        answeredText: String,
        index: Int? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            answeredQuestionUseCase.setAnsweredQuestion(id, question, isSubmitted, answeredText)
        }
    }

    fun getAnsweredQuestionByIndex(buttonAction: String = Button.CURRENT.action, index: Int = 0) {
        viewModelScope.launch(Dispatchers.IO) {
            answeredQuestionUseCase.getAnsweredQuestionByIndex(buttonAction, index)
        }
    }

    fun postQuesting(id: Int, answer: String) {
        viewModelScope.launch(Dispatchers.IO) {
            questionsUseCase.postQuestions(AnswerSubmissionRequest(id, answer))
        }
    }

    private fun updateAnsweredQuestion(answeredQuestionUiModel: AnsweredQuestionUiModel?) {
        _uiState.answeredQuestionUiModel = answeredQuestionUiModel
    }

    @Stable
    interface QuestionsUiState {
        val answeredQuestionUiModel: AnsweredQuestionUiModel?
    }

    class MutableQuestionsUiState(
        answeredQuestionUiModel: AnsweredQuestionUiModel? = null,
    ): QuestionsUiState {
        override var answeredQuestionUiModel: AnsweredQuestionUiModel? by mutableStateOf(answeredQuestionUiModel)
    }
}