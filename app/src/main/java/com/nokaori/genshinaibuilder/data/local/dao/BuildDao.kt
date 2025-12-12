package com.nokaori.genshinaibuilder.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nokaori.genshinaibuilder.data.local.entity.CharacterBuildEntity
import com.nokaori.genshinaibuilder.data.local.entity.TeamBuildEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BuildDao {
    // --- CHARACTER BUILDS ---
    @Query("SELECT * FROM character_builds ORDER BY id DESC")
    fun getAllCharacterBuilds(): Flow<List<CharacterBuildEntity>>

    @Query("SELECT * FROM character_builds WHERE character_encyclopedia_id = :charId ORDER BY id DESC")
    fun getBuildsForCharacter(charId: Int): Flow<List<CharacterBuildEntity>>

    @Query("SELECT * FROM character_builds WHERE id = :buildId")
    suspend fun getCharacterBuildById(buildId: Int): CharacterBuildEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterBuild(build: CharacterBuildEntity): Long

    @Update
    suspend fun updateCharacterBuild(build: CharacterBuildEntity)

    @Delete
    suspend fun deleteCharacterBuild(build: CharacterBuildEntity)

    // --- TEAM BUILDS ---
    @Query("SELECT * FROM team_builds ORDER BY id DESC")
    fun getAllTeamBuilds(): Flow<List<TeamBuildEntity>>

    @Query("SELECT * FROM team_builds WHERE id = :teamId")
    suspend fun getTeamBuildById(teamId: Int): TeamBuildEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeamBuild(team: TeamBuildEntity)

    @Update
    suspend fun updateTeamBuild(team: TeamBuildEntity)

    @Delete
    suspend fun deleteTeamBuild(team: TeamBuildEntity)
}