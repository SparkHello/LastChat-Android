package com.lastchat.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.lastchat.app.data.local.entity.AssistantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssistantDao {
    @Query("SELECT * FROM assistants ORDER BY createdAt DESC")
    fun getAll(): Flow<List<AssistantEntity>>

    @Query("SELECT * FROM assistants WHERE id = :id")
    suspend fun getById(id: String): AssistantEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(assistant: AssistantEntity)

    @Update
    suspend fun update(assistant: AssistantEntity)

    @Delete
    suspend fun delete(assistant: AssistantEntity)

    @Query("DELETE FROM assistants WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM assistants WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<AssistantEntity>>
}
