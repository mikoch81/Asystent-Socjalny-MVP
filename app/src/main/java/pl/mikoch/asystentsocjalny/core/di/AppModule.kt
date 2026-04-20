package pl.mikoch.asystentsocjalny.core.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import pl.mikoch.asystentsocjalny.core.data.CaseDocumentStore
import pl.mikoch.asystentsocjalny.core.data.CaseStore
import pl.mikoch.asystentsocjalny.core.data.DraftStore
import pl.mikoch.asystentsocjalny.core.data.KnowledgeRepository

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideKnowledgeRepository(@ApplicationContext context: Context): KnowledgeRepository =
        KnowledgeRepository(context)

    @Provides
    @Singleton
    fun provideDraftStore(@ApplicationContext context: Context): DraftStore =
        DraftStore(context)

    @Provides
    @Singleton
    fun provideCaseStore(@ApplicationContext context: Context): CaseStore =
        CaseStore(context)

    @Provides
    @Singleton
    fun provideCaseDocumentStore(@ApplicationContext context: Context): CaseDocumentStore =
        CaseDocumentStore(context)
}
