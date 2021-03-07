package com.example.projet.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.projet.model.User;

@Dao
public interface UserDao {
    @Query("select id, name, lastname from User where lastname = :login")
    User getUser(String login);

    @Query("select id, password from User where lastname = :ID and password = :Password")
    User getLogin(String ID, byte[] Password);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);
}
