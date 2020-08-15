package com.example.mypowerfulandroidapp.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mypowerfulandroidapp.models.AccountProperties

@Dao
interface AccountPropertiesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(accountProperties: AccountProperties): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOrIgnore(accountProperties: AccountProperties): Long

    @Query("SELECT * FROM accountProperties WHERE pk = :pk")
    fun searchByPK(pk: Int): AccountProperties?

    @Query("SELECT * FROM accountProperties WHERE email = :email")
    fun searchByEmail(email: String): AccountProperties?

}