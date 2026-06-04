package com.lastchat.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.lastchat.app.data.local.entity.ProviderConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProviderConfigDao {
    @Query("SELECT * FROM providers ORDER BY createdAt DESC")
    fun getAll(): Flow<List<ProviderConfigEntity>>

    @Query("SELECT * FROM providers WHERE id = :id")
    suspend fun getById(id: String): ProviderConfigEntity?

    @Query("SELECT * FROM providers WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefault(): ProviderConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(provider: ProviderConfigEntity)

    @Update
    suspend fun update(provider: ProviderConfigEntity)

    @Delete
    suspend fun delete(provider: ProviderConfigEntity)

    @Query("DELETE FROM providers WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE providers SET isDefault = 0")
    suspend fun clearDefault()

    @Query("UPDATE providers SET isDefault = 1 WHERE id = :id")
    suspend fun setDefault(id: String)
}
