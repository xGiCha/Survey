package gr.android.survey.domain.usecases

import gr.android.survey.data.repository.AnsweredQuestionsRepository
import gr.android.survey.domain.uiModels.AnsweredQuestionUiModel
import gr.android.survey.utils.mapToAnsweredQuestionUiModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

interface AnsweredQuestionUseCase {

    val item: SharedFlow<AnsweredQuestionUiModel>
    val allReady: SharedFlow<Boolean>
    val submittedQuestions: SharedFlow<Int>

    suspend fun setAnsweredQuestion(id: Int, question: String?, isSubmitted: Boolean?, answeredText: String)
    suspend fun getAnsweredQuestionByIndex(buttonAction: String, index: Int)
    fun reset()
}

class AnsweredQuestionUseCaseImpl(
    private val answeredQuestionsRepository: AnsweredQuestionsRepository
) : AnsweredQuestionUseCase {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val submittedQuestionsFlow: MutableSharedFlow<Int> = MutableSharedFlow(replay = 1)
    override val submittedQuestions: SharedFlow<Int> = submittedQuestionsFlow

    private val allReadyFlow: MutableSharedFlow<Boolean> = MutableSharedFlow(replay = 1)
    override val allReady: SharedFlow<Boolean> = allReadyFlow

    private val itemFlow: MutableSharedFlow<AnsweredQuestionUiModel> = MutableSharedFlow(replay = 1)
    override val item: SharedFlow<AnsweredQuestionUiModel> = itemFlow

    init {
        scope.launch(Dispatchers.IO) {
            answeredQuestionsRepository.allReady.collectLatest {
                allReadyFlow.emit(it)
            }
        }
        scope.launch(Dispatchers.IO) {
            answeredQuestionsRepository.item.collectLatest {
                itemFlow.emit(it.mapToAnsweredQuestionUiModel())
            }
        }

        scope.launch(Dispatchers.IO) {
            answeredQuestionsRepository.submittedQuestions.collectLatest {
                submittedQuestionsFlow.emit(it)
            }
        }

    }

    override suspend fun setAnsweredQuestion(
        id: Int,
        question: String?,
        isSubmitted: Boolean?,
        answeredText: String
    ) {
        answeredQuestionsRepository.setAnsweredQuestion(id, question, isSubmitted, answeredText)
    }

    override suspend fun getAnsweredQuestionByIndex(buttonAction: String, index: Int) {
        answeredQuestionsRepository.getAnsweredQuestionByIndex(buttonAction, index)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun reset() {
        allReadyFlow.resetReplayCache()
        itemFlow.resetReplayCache()
        submittedQuestionsFlow.resetReplayCache()
    }
}