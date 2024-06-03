package gr.android.survey.domain.usecases

import gr.android.survey.data.remoteEntities.AnswerSubmissionRequest
import gr.android.survey.data.remoteEntities.RemoteSurvey
import gr.android.survey.data.repository.AnsweredQuestionsRepository
import gr.android.survey.data.repository.QuestionsRepository
import gr.android.survey.data.utils.Result
import gr.android.survey.domain.uiModels.QuestionsUiModel
import gr.android.survey.domain.utils.Resource
import gr.android.survey.utils.mapToQuestionsUiModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

interface QuestionsUseCase {
    val questions: SharedFlow<Resource<QuestionsUiModel>>
    val postAnsweredQuestionResult: SharedFlow<Resource<Boolean>>

    suspend fun getQuestions()
    suspend fun postQuestions(answerSubmissionRequest: AnswerSubmissionRequest)
}

class QuestionsUseCaseImpl(
    private val questionsRepository: QuestionsRepository,
    private val answeredQuestionsRepository: AnsweredQuestionsRepository
) : QuestionsUseCase {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val questionsItemsFlow: MutableSharedFlow<Resource<QuestionsUiModel>> = MutableSharedFlow()
    override val questions: SharedFlow<Resource<QuestionsUiModel>> = questionsItemsFlow

    private val postAnsweredQuestionResultFlow: MutableSharedFlow<Resource<Boolean>> = MutableSharedFlow()
    override val postAnsweredQuestionResult: SharedFlow<Resource<Boolean>> = postAnsweredQuestionResultFlow


    init {
        scope.launch(Dispatchers.IO) {
            questionsRepository.items.collectLatest {
                when (it) {
                    is Result.Success -> {
                        questionsItemsFlow.emit(Resource.Success(it.data?.mapToQuestionsUiModel() ?: QuestionsUiModel()))
                        answeredQuestionsRepository.setInitQuestionList(it.data ?: RemoteSurvey())
                    }
                    is Result.NetworkError, is  Result.ServerError-> {
                        questionsItemsFlow.emit(Resource.Error("Something went wrong"))
                    }
                }
            }
        }
        scope.launch(Dispatchers.IO) {
            questionsRepository.postAnsweredQuestionResult.collectLatest {
                when (it) {
                    is Result.Success -> {
                        postAnsweredQuestionResultFlow.emit(Resource.Success(true))
                    }
                    is Result.NetworkError, is Result.ServerError-> {
                        postAnsweredQuestionResultFlow.emit(Resource.Error("Something went wrong"))
                    }
                }
            }
        }
    }

    override suspend fun getQuestions() {
        scope.launch(Dispatchers.IO) {
            questionsItemsFlow.emit(Resource.Loading())
            questionsRepository.getQuestions()
        }
    }

    override suspend fun postQuestions(answerSubmissionRequest: AnswerSubmissionRequest) {
        scope.launch(Dispatchers.IO) {
            questionsItemsFlow.emit(Resource.Loading())
            questionsRepository.postQuestions(answerSubmissionRequest)
        }
    }
}