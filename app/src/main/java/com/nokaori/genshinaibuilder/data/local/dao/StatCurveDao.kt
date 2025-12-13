package com.nokaori.genshinaibuilder.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nokaori.genshinaibuilder.data.local.entity.StatCurveEntity

@Dao
interface StatCurveDao {
    @Query("SELECT * FROM stat_curves WHERE id = :curveId")
    suspend fun getCurve(curveId: String): StatCurveEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurves(curves: List<StatCurveEntity>)
}