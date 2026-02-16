package com.nokaori.genshinaibuilder.di

import com.nokaori.genshinaibuilder.data.repository.*
import com.nokaori.genshinaibuilder.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindArtifactRepository(
        impl: ArtifactRepositoryImpl
    ): ArtifactRepository

    @Binds
    @Singleton
    abstract fun bindCharacterRepository(
        impl: CharacterRepositoryImpl
    ): CharacterRepository

    @Binds
    @Singleton
    abstract fun bindWeaponRepository(
        impl: WeaponRepositoryImpl
    ): WeaponRepository

    @Binds
    @Singleton
    abstract fun bindGameDataRepository(
        impl: GameDataRepositoryImpl
    ): GameDataRepository
    
    @Binds
    @Singleton
    abstract fun bindThemeRepository(
        impl: ThemeRepositoryImpl
    ): ThemeRepository
}