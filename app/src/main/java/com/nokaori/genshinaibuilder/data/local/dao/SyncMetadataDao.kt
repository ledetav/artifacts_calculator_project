package com.nokaori.genshinaibuilder.data.local.dao

import androidx.room.*
import com.nokaori.genshinaibuilder.data.local.entity.SyncMetadataEntity

@Dao
interface SyncMetadataDao {
    @Upsert
    suspend fun upsert(entity: SyncMetadataEntity)

    @Query("SELECT value FROM sync_metadata WHERE `key` = :key")
    suspend fun getValue(key: String): Long?
}
