package com.example.gainfunction.core.di

import android.content.Context
import com.example.gainfunction.data.local.AppDatabase
import com.example.gainfunction.data.local.ExerciseDefinitionDao
import com.example.gainfunction.domain.usecases.ExerciseUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module that provides application-level dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    /**
     * Provides the Room database instance
     */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }
    
    /**
     * Provides the ExerciseDefinitionDao
     */
    @Provides
    @Singleton
    fun provideExerciseDefinitionDao(appDatabase: AppDatabase): ExerciseDefinitionDao {
        return appDatabase.exerciseDefinitionDao()
    }
    
    /**
     * Provides the ExerciseUseCases
     */
    @Provides
    @Singleton
    fun provideExerciseUseCases(exerciseDefinitionDao: ExerciseDefinitionDao): ExerciseUseCases {
        return ExerciseUseCases(exerciseDefinitionDao)
    }
} 