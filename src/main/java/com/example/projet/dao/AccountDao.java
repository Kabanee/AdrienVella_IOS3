package com.example.projet.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.projet.model.Account;

import java.util.List;

@Dao
public interface AccountDao {
    @Query("select * from Account")
    List<Account> getAllAccounts();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Account account);
}
