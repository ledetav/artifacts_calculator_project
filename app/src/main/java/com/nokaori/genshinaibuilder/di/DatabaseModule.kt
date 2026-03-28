package com.nokaori.genshinaibuilder.di

import android.content.Context
import androidx.room.Room
import com.nokaori.genshinaibuilder.data.local.AppDatabase
import com.nokaori.genshinaibuilder.data.local.MIGRATION_1_2
import com.nokaori.genshinaibuilder.data.local.MIGRATION_2_3
import com.nokaori.genshinaibuilder.data.local.MIGRATION_3_4
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
        val hasPrepackaged = try {
            context.assets.open("prepackaged.db").close()
            true
        } catch (_: Exception) {
            false
        }

        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "genshin_optimizer.db"
        )
        // При первой установке Room скопирует prepackaged.db из assets (если файл есть).
        // На уже установленных устройствах (v1 БД) — применится MIGRATION_1_2.
        .apply { if (hasAsset) createFromAsset("prepackaged.db") }
        .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
        .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
        .addCallback(object : RoomDatabase.Callback() {
            override fun onOpen(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                super.onOpen(db)
                // Принудительно создаем таблицу Room, если она отсутствует.
                // Это предотвращает SQLiteException: no such table: room_table_modification_log
                db.execSQL("CREATE TABLE IF NOT EXISTS room_table_modification_log (table_id INTEGER PRIMARY KEY, invalidated INTEGER NOT NULL DEFAULT 0)")
            }
        })
        .enableMultiInstanceInvalidation()
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