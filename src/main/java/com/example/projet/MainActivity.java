package com.example.projet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.mbms.MbmsErrors;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.example.projet.DB.AccountDB;
import com.example.projet.DB.UserDB;
import com.example.projet.dao.AccountDao;
import com.example.projet.model.Account;
import com.example.projet.dao.UserDao;
import com.example.projet.model.User;
import com.example.projet.api.BankApi;


public class MainActivity extends AppCompatActivity {
    private Bundle savedInstanceState;
    private Boolean paused = false;
    private Boolean stopped = false;

    private UserDao UserDao;
    private UserDB DatabaseUser;
    private AccountDB DatabaseAccount;
    public static User myUser;
    public static List<Account> myAccountsList;
    private BankApi BankApi;
    private ListView simpleListView;
    private final Executor backgroundExecutor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        initialisation(savedInstanceState);
    }
    @Override

    protected void onStart() {
        super.onStart();
        if (stopped){
            // ask for auth
            initialisation(savedInstanceState);
            stopped = false;
        }
    }

    @Override

    protected void onResume() {
        super.onResume();
        if (paused){
            // ask for auth
            paused = false;
        }
    }

    @Override

    protected void onPause() {
        super.onPause();
        setContentView(R.layout.waiting_screen);
        paused = true;
    }

    @Override

    protected void onStop() {
        super.onStop();
        setContentView(R.layout.waiting_screen);
        stopped = true;
    }

    private void startUi(Bundle savedInstanceState){
        if(savedInstanceState == null){
            setContentView(R.layout.activity_main); //set initial view
            final Button button = findViewById(R.id.refresh);
            fillUi();

            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Code here executes on main thread after user presses button
                    Toast.makeText(getApplicationContext(),"requête en cours",Toast.LENGTH_SHORT).show();
                    refresh();
                }
            });
        }
    }


    private void fillUi(){
        final TextView userInfoTextView = findViewById(R.id.Solde);
        String login = getIntent().getStringExtra("Login");
        myUser = DatabaseUser.UserDao().getUser(login);
        String userToDisplay = myUser.lastname + " " + myUser.name;
        userInfoTextView.setText(userToDisplay);
        simpleListView = (ListView) findViewById(R.id.container);

        List<String> accountsListToDisplay = new ArrayList<>();
        for (Account account : myAccountsList) {
            accountsListToDisplay.add("Compte : " + account.account_name + "\n" + "IBAN : " + account.iban + "\n" + "Solde : " + account.amount + account.currency);
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.data_view, R.id.Data, accountsListToDisplay);
        simpleListView.setAdapter(arrayAdapter);
    }

    private void initialisation(Bundle savedInstanceState){
        if(isOnline()) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://60102f166c21e10017050128.mockapi.io/labbbank/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            BankApi = retrofit.create(BankApi.class);

            backgroundExecutor.execute(()-> {
                DatabaseUser = Room.databaseBuilder(getApplicationContext(), UserDB.class, "user_database.db").build();
                DatabaseAccount = Room.databaseBuilder(getApplicationContext(), AccountDB.class, "accounts_database.db").build();
            });

            backgroundExecutor.execute(()-> {
                try {
                Response<List<Account>> responseAccounts = BankApi.getAllAccounts().execute();
                Response<User> responseUser = BankApi.getUser().execute();
                if(responseAccounts.isSuccessful() && responseUser.isSuccessful()){
                    List<Account> accounts = responseAccounts.body();
                    User user = responseUser.body();
                    user.password = Hashing(user.lastname);

                    // Save accounts in db
                    for(Account account : accounts){
                        DatabaseAccount.AccountDao().insert(account);
                    }
                    // save user in db
                    DatabaseUser.UserDao().insert(user);
                }
                else{
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this,"Erreur de connexion à l'API",Toast.LENGTH_LONG).show(); // Display in toast
                    });
                }
            } catch (IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
                String login = getIntent().getStringExtra("Login");
                myUser = DatabaseUser.UserDao().getUser(login);
                myAccountsList = DatabaseAccount.AccountDao().getAllAccounts();
                String password = getIntent().getStringExtra("Password");
                if(true){
                    runOnUiThread(() -> {
                        // start ui
                        startUi(savedInstanceState);
                    });
                }
                else{
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this,"Authentication failed",Toast.LENGTH_LONG).show();
                    });
                    finish();
                }
            });
        }
        else{
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this,"Pas de connexion à internet",Toast.LENGTH_LONG).show(); // Display in toast
            });
        }


    }
    private void refresh(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://60102f166c21e10017050128.mockapi.io/labbbank/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        BankApi = retrofit.create(BankApi.class);

        backgroundExecutor.execute(()-> {
            try {
            Response<List<Account>> responseAccounts = BankApi.getAllAccounts().execute();
            Response<User> responseUser = BankApi.getUser().execute();
            if(responseAccounts.isSuccessful() && responseUser.isSuccessful()){
                List<Account> accounts = responseAccounts.body();
                User user = responseUser.body();

                // Save accounts in db
                for(Account account : accounts){
                    DatabaseAccount.AccountDao().insert(account);
                }
                // save user in db
                DatabaseUser.UserDao().insert(user);
            }
            else{
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this,"Erreur de connexion à l'API",Toast.LENGTH_LONG).show(); // Display in toast
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
            String login = getIntent().getStringExtra("Login");
            myUser = DatabaseUser.UserDao().getUser(login);
            myAccountsList = DatabaseAccount.AccountDao().getAllAccounts();
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this,"Informations rafraichies !",Toast.LENGTH_LONG).show(); // Display in toast
                fillUi();
            });
        });
    }


    protected boolean authentication(String login, String password) throws NoSuchAlgorithmException {
        boolean result = false;
        byte[] Hashedpassword = Hashing(password);
        myUser = DatabaseUser.UserDao().getLogin(login, Hashedpassword);
        if(myUser != null) {
            result = true;
        }
        return result;
    }
    protected byte[] Hashing(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] hashedPassword = md.digest(password.getBytes());
        return hashedPassword;
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try
        {
            Process  mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int mExitValue = mIpAddrProcess.waitFor();
            if(mExitValue==0){
                return true;
            }else{
                return false;
            }
        }
        catch (InterruptedException ignore)
        {
            ignore.printStackTrace();
            System.out.println(" Exception:"+ignore);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println(" Exception:"+e);
        }
        return false;
    }
}