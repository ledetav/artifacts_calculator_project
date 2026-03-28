package com.nokaori.genshinaibuilder.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Версия 1 → 2: схема таблиц не изменилась.
 * Смена версии нужна для корректной работы Room.createFromAsset("prepackaged.db"):
 * при первой установке Room копирует prepackaged.db (версия 1) и тут же
 * применяет эту миграцию, поднимая активную БД до версии 2.
 * Данные пользователя (user_*, character_builds, team_builds) не затрагиваются.
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // No schema changes — version bump only.
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Удаляем все данные из справочных таблиц, так как структура изменилась
        // Пользовательские данные сохраняются
        
        db.execSQL("DROP TABLE IF EXISTS character_talents")
        db.execSQL("DROP TABLE IF EXISTS character_constellations")
        db.execSQL("DROP TABLE IF EXISTS character_promotions")
        db.execSQL("DROP TABLE IF EXISTS weapon_refinements")
        db.execSQL("DROP TABLE IF EXISTS weapon_promotions")
        db.execSQL("DROP TABLE IF EXISTS artifact_pieces_data")
        db.execSQL("DROP TABLE IF EXISTS characters_data")
        db.execSQL("DROP TABLE IF EXISTS weapons_data")
        db.execSQL("DROP TABLE IF EXISTS artifact_sets_data")
        
        // Создаем таблицы заново с новой структурой
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS characters_data (
                id INTEGER NOT NULL,
                language TEXT NOT NULL,
                name TEXT NOT NULL,
                rarity INTEGER NOT NULL,
                element TEXT NOT NULL,
                weapon_type TEXT NOT NULL,
                base_hp_lvl1 REAL NOT NULL,
                base_atk_lvl1 REAL NOT NULL,
                base_def_lvl1 REAL NOT NULL,
                ascension_stat_type TEXT NOT NULL,
                curve_id TEXT NOT NULL,
                icon_url TEXT NOT NULL,
                splash_url TEXT NOT NULL,
                tags_dictionary TEXT NOT NULL DEFAULT '{}',
                PRIMARY KEY(id, language)
            )
        """)
        
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS weapons_data (
                id INTEGER NOT NULL,
                language TEXT NOT NULL,
                name TEXT NOT NULL,
                type TEXT NOT NULL,
                rarity INTEGER NOT NULL,
                icon_url TEXT NOT NULL,
                base_atk_lvl1 REAL NOT NULL,
                sub_stat_type TEXT,
                sub_stat_base_value REAL,
                atk_curve_id TEXT NOT NULL,
                sub_stat_curve_id TEXT,
                PRIMARY KEY(id, language)
            )
        """)
        
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS artifact_sets_data (
                id INTEGER NOT NULL,
                language TEXT NOT NULL,
                name TEXT NOT NULL,
                rarities TEXT NOT NULL,
                bonus_2pc TEXT NOT NULL,
                bonus_4pc TEXT NOT NULL,
                icon_url TEXT NOT NULL,
                PRIMARY KEY(id, language)
            )
        """)
        
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS artifact_pieces_data (
                id INTEGER NOT NULL,
                language TEXT NOT NULL,
                set_id INTEGER NOT NULL,
                slot TEXT NOT NULL,
                name TEXT NOT NULL,
                icon_url TEXT NOT NULL,
                PRIMARY KEY(id, language),
                FOREIGN KEY(set_id, language) REFERENCES artifact_sets_data(id, language) ON DELETE CASCADE
            )
        """)
        db.execSQL("CREATE INDEX IF NOT EXISTS index_artifact_pieces_data_set_id_language ON artifact_pieces_data(set_id, language)")
        
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS character_promotions (
                character_id INTEGER NOT NULL,
                language TEXT NOT NULL,
                ascension_level INTEGER NOT NULL,
                add_hp REAL NOT NULL,
                add_atk REAL NOT NULL,
                add_def REAL NOT NULL,
                ascension_stat_value REAL NOT NULL,
                PRIMARY KEY(character_id, language, ascension_level),
                FOREIGN KEY(character_id, language) REFERENCES characters_data(id, language) ON DELETE CASCADE
            )
        """)
        db.execSQL("CREATE INDEX IF NOT EXISTS index_character_promotions_character_id_language ON character_promotions(character_id, language)")
        
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS character_constellations (
                character_id INTEGER NOT NULL,
                language TEXT NOT NULL,
                `order` INTEGER NOT NULL,
                name TEXT NOT NULL,
                description TEXT NOT NULL,
                icon_url TEXT NOT NULL,
                talent_level_up_target TEXT,
                PRIMARY KEY(character_id, language, `order`),
                FOREIGN KEY(character_id, language) REFERENCES characters_data(id, language) ON DELETE CASCADE
            )
        """)
        db.execSQL("CREATE INDEX IF NOT EXISTS index_character_constellations_character_id_language ON character_constellations(character_id, language)")
        
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS character_talents (
                character_id INTEGER NOT NULL,
                language TEXT NOT NULL,
                order_index INTEGER NOT NULL,
                type TEXT NOT NULL,
                name TEXT NOT NULL,
                description TEXT NOT NULL,
                icon_url TEXT NOT NULL,
                scaling_attributes TEXT NOT NULL,
                PRIMARY KEY(character_id, language, order_index),
                FOREIGN KEY(character_id, language) REFERENCES characters_data(id, language) ON DELETE CASCADE
            )
        """)
        db.execSQL("CREATE INDEX IF NOT EXISTS index_character_talents_character_id_language ON character_talents(character_id, language)")
        
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS weapon_promotions (
                weapon_id INTEGER NOT NULL,
                language TEXT NOT NULL,
                ascension_level INTEGER NOT NULL,
                add_atk REAL NOT NULL,
                add_sub_stat REAL,
                PRIMARY KEY(weapon_id, language, ascension_level),
                FOREIGN KEY(weapon_id, language) REFERENCES weapons_data(id, language) ON DELETE CASCADE
            )
        """)
        db.execSQL("CREATE INDEX IF NOT EXISTS index_weapon_promotions_weapon_id_language ON weapon_promotions(weapon_id, language)")
        
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS weapon_refinements (
                weapon_id INTEGER NOT NULL,
                language TEXT NOT NULL,
                passive_name TEXT NOT NULL,
                descriptions TEXT NOT NULL,
                PRIMARY KEY(weapon_id, language),
                FOREIGN KEY(weapon_id, language) REFERENCES weapons_data(id, language) ON DELETE CASCADE
            )
        """)
        db.execSQL("CREATE INDEX IF NOT EXISTS index_weapon_refinements_weapon_id_language ON weapon_refinements(weapon_id, language)")
        
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS sync_metadata (
                `key` TEXT NOT NULL,
                value INTEGER NOT NULL,
                PRIMARY KEY(`key`)
            )
        """)
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Adds tags_dictionary column that was missing from MIGRATION_2_3.
        // The column stores a JSON-serialised Map<String,String> via Room TypeConverter.
        // SQLite does not support "ADD COLUMN IF NOT EXISTS", so we guard with try-catch:
        // prepackaged.db (version 4) already contains this column, and attempting to
        // ALTER again would throw "duplicate column name".
        try {
            db.execSQL(
                "ALTER TABLE characters_data ADD COLUMN tags_dictionary TEXT NOT NULL DEFAULT '{}'"
            )
        } catch (_: Exception) {
            // Column already exists — safe to ignore.
        }
    }
}