package com.example.projet.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Account {
    @PrimaryKey
    @NonNull
    public Integer id;
    public String account_name;
    public Float amount;
    public String iban;
    public String currency;
}
