package com.example.projet.DB;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.projet.dao.AccountDao;
import com.example.projet.model.Account;

@Database(entities = {Account.class}, version = 1)
public abstract class AccountDB extends RoomDatabase {
    public abstract AccountDao AccountDao();
}
