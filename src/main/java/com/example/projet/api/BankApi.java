package com.example.projet.api;

import com.example.projet.model.Account;
import com.example.projet.model.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface BankApi {
    @GET("accounts")
    Call<List<Account>> getAllAccounts();

    @GET("config/1")
    Call<User> getUser();
}
