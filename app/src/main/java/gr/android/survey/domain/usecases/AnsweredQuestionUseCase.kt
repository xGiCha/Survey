package gr.android.survey.domain.usecases

import gr.android.survey.data.remoteEntities.AnsweredQuestionItem
import gr.android.survey.data.repository.AnsweredQuestionsRepository
import gr.android.survey.data.repository.QuestionsRepository
import gr.android.survey.data.utils.Result
import gr.android.survey.domain.uiModels.AnsweredQuestionUiModel
import gr.android.survey.domain.uiModels.QuestionsUiModel
import gr.android.survey.domain.utils.Resource
import gr.android.survey.utils.mapToAnsweredQuestionUiModel
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

interface AnsweredQuestionUseCase {

    val item: SharedFlow<AnsweredQuestionUiModel>
    val allReady: SharedFlow<Boolean>

    suspend fun setAnsweredQuestion(id: Int, question: String?, isSubmitted: Boolean?, answeredText: String)

    suspend fun getAnsweredQuestionByIndex(buttonAction: String, index: Int)
}

class AnsweredQuestionUseCaseImpl(
    private val answeredQuestionsRepository: AnsweredQuestionsRepository
) : AnsweredQuestionUseCase {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val allReadyFlow: MutableSharedFlow<Boolean> = MutableSharedFlow()
    override val allReady: SharedFlow<Boolean> = allReadyFlow

    private val itemFlow: MutableSharedFlow<AnsweredQuestionUiModel> = MutableSharedFlow()
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
}