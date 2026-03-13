package com.nokaori.genshinaibuilder.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nokaori.genshinaibuilder.data.local.converters.Converters
import com.nokaori.genshinaibuilder.data.local.dao.*
import com.nokaori.genshinaibuilder.data.local.entity.*

@Database(
    entities = [
        // 1. Encyclopedia: Artifacts
        ArtifactSetEntity::class,
        ArtifactPieceEntity::class,

        // 2. Encyclopedia: Weapons
        WeaponEntity::class,
        WeaponRefinementEntity::class,
        WeaponPromotionEntity::class,

        // 3. Encyclopedia: Characters
        CharacterEntity::class,
        CharacterPromotionEntity::class,
        CharacterConstellationEntity::class,
        CharacterTalentEntity::class,

        // 4. Common Math
        StatCurveEntity::class,

        // 5. User Inventory
        UserCharacterEntity::class,
        UserWeaponEntity::class,
        UserArtifactEntity::class,

        // 6. Builds
        CharacterBuildEntity::class,
        TeamBuildEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun artifactDao(): ArtifactDao
    abstract fun weaponDao(): WeaponDao
    abstract fun characterDao(): CharacterDao
    abstract fun statCurveDao(): StatCurveDao

    abstract fun userDao(): UserDao
    abstract fun buildDao(): BuildDao
}