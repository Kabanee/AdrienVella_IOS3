package com.example.projet.DB;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.projet.dao.UserDao;
import com.example.projet.model.User;

@Database(entities = {User.class}, version = 1)
public abstract class UserDB extends RoomDatabase {
    public abstract UserDao UserDao();
}
