package gr.android.survey.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import gr.android.survey.data.dataSource.QuestionsNetworkDataSource
import gr.android.survey.data.dataSource.QuestionsNetworkDataSourceImpl
import gr.android.survey.data.networkCalls.SurveyApi
import gr.android.survey.data.repository.AnsweredQuestionsRepository
import gr.android.survey.data.repository.AnsweredQuestionsRepositoryImp
import gr.android.survey.data.repository.ClearSurveyUseCase
import gr.android.survey.data.repository.ClearSurveyUseCaseImpl
import gr.android.survey.data.repository.QuestionsRepository
import gr.android.survey.data.repository.QuestionsRepositoryImp
import gr.android.survey.domain.usecases.AnsweredQuestionUseCase
import gr.android.survey.domain.usecases.AnsweredQuestionUseCaseImpl
import gr.android.survey.domain.usecases.QuestionsUseCase
import gr.android.survey.domain.usecases.QuestionsUseCaseImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideQuestionsNetworkDataSource(
        surveyApi: SurveyApi,
    ): QuestionsNetworkDataSource {
        return QuestionsNetworkDataSourceImpl(surveyApi)
    }

    @Singleton
    @Provides
    fun provideQuestionsRepository(
        questionsNetworkDataSource: QuestionsNetworkDataSource,
    ): QuestionsRepository {
        return QuestionsRepositoryImp(questionsNetworkDataSource)
    }

    @Singleton
    @Provides
    fun provideQuestionUseCase(
        questionsRepository: QuestionsRepository,
        answeredQuestionsRepository: AnsweredQuestionsRepository
    ): QuestionsUseCase {
        return QuestionsUseCaseImpl(questionsRepository, answeredQuestionsRepository)
    }

    @Singleton
    @Provides
    fun provideAnsweredQuestionsRepository(
    ): AnsweredQuestionsRepository {
        return AnsweredQuestionsRepositoryImp()
    }

    @Singleton
    @Provides
    fun provideAnsweredQuestionUseCase(
        answeredQuestionsRepository: AnsweredQuestionsRepository
    ): AnsweredQuestionUseCase {
        return AnsweredQuestionUseCaseImpl(answeredQuestionsRepository)
    }

    @Singleton
    @Provides
    fun provideClearSurveyUseCase(
        answeredQuestionsRepository: AnsweredQuestionsRepository
    ): ClearSurveyUseCase {
        return ClearSurveyUseCaseImpl(answeredQuestionsRepository)
    }

}