package com.nokaori.genshinaibuilder.di

import android.content.Context
import androidx.room.Room
import com.nokaori.genshinaibuilder.data.local.AppDatabase
import com.nokaori.genshinaibuilder.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "genshin_optimizer.db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }
    
    @Provides
    fun provideArtifactDao(db: AppDatabase): ArtifactDao = db.artifactDao()

    @Provides
    fun provideWeaponDao(db: AppDatabase): WeaponDao = db.weaponDao()

    @Provides
    fun provideCharacterDao(db: AppDatabase): CharacterDao = db.characterDao()

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    fun provideBuildDao(db: AppDatabase): BuildDao = db.buildDao()
    
    @Provides
    fun provideStatCurveDao(db: AppDatabase): StatCurveDao = db.statCurveDao()
}