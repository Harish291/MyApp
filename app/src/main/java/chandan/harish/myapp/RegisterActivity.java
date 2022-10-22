package chandan.harish.myapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import chandan.harish.myapp.pojo.Users;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class RegisterActivity extends AppCompatActivity {

    TextView textView_login;
    EditText e_user_name, e_user_email, e_user_password, e_user_mobile;
    Button e_user_register;
    String users_name, users_email, users_password, users_mobile;
    static final String KEY_EMPTY_R = "";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        validInputs_R();
        e_user_name = findViewById(R.id.editTextUsername);
        e_user_email = findViewById(R.id.editTextEmail);
        e_user_password = findViewById(R.id.editTextPassword);
        e_user_mobile = findViewById(R.id.phonenumber);
        e_user_register = findViewById(R.id.buttonRegister);
        textView_login = findViewById(R.id.textViewLogin);

        e_user_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                users_name = e_user_name.getText().toString().trim();
                users_email = e_user_email.getText().toString().trim();
                users_password = e_user_password.getText().toString().trim();
                users_mobile = e_user_mobile.getText().toString().trim();

                //NetworkCheck
                if (!isDeviceOnline_R()) {
                    //No Network
                    activate_online_device_R();
                } else {
                    //Network On
                    if (validInputs_R()) {
                        register_user();
                    }

                }
            }
        });

    }

    private boolean isDeviceOnline_R() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network networkInfo = connectivityManager.getActiveNetwork();
        return (networkInfo != null);
    }

    private boolean validInputs_R() {
        if (KEY_EMPTY_R.equals(users_name)) {
            e_user_name.setError("The username field is empty!");
            e_user_name.requestFocus();
            return false;
        }
        if (KEY_EMPTY_R.equals(users_email)) {
            e_user_email.setError("The email field is empty!");
            e_user_email.requestFocus();
            return false;
        }
        if (KEY_EMPTY_R.equals(users_mobile)) {
            e_user_mobile.setError("The phone number field is empty!");
            e_user_mobile.requestFocus();
            return false;
        }
        if (KEY_EMPTY_R.equals(users_password)) {
            e_user_password.setError("The password field is empty!");
            e_user_password.requestFocus();
            return false;
        }
        return true;
    }


    private void register_user() {
        displayloader();
        Gson gson = new GsonBuilder().setLenient().create();
        
        Retrofit retrofit_r = new Retrofit.Builder()
                .baseUrl(ApiConnect.LOGIN_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiConnect loginInterface = retrofit_r.create(ApiConnect.class);
        Call<Users> userCall = loginInterface.getUserRegister(users_name, users_email, users_mobile, users_password);
        userCall.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(@NonNull Call<Users> call, @NonNull Response<Users> response) {
                Users users = response.body();
                assert users != null;
                if(users.getStatus().equals("true"))
                {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, users.getMessage(), Toast.LENGTH_SHORT).show();
                   // startActivity(new Intent(RegisterActivity.this,Dashboard.class));
                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Not Registered Successfully", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Users> call, @NonNull Throwable t) {
                Toast.makeText(RegisterActivity.this, "Message :: "+t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }
        });

    }

    private void displayloader() {

        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setMessage("LOGGING IN.... Please Wait!");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

    }

    private void activate_online_device_R() {

        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle("Network Error");
        builder.setMessage("No network connection available.");
        builder.setIcon(R.drawable.logo);
        builder.setPositiveButton("ACTIVATE INTERNET!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivityForResult(intent, 9003);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();

    }

}
