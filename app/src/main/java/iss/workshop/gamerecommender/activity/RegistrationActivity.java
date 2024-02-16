package iss.workshop.gamerecommender.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import iss.workshop.gamerecommender.R;
import iss.workshop.gamerecommender.api.RetrofitClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText displayNameEditText;
    private EditText passwordEditText;
    private EditText reenterPasswordEditText;
    private Button createAccountButton;
    private TextView signinLink;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Mapping
        usernameEditText = findViewById(R.id.username);
        displayNameEditText = findViewById(R.id.displayName);
        passwordEditText = findViewById(R.id.password);
        reenterPasswordEditText = findViewById(R.id.reenter_password);
        createAccountButton = findViewById(R.id.create_account_button);
        signinLink = findViewById(R.id.sign_in_link);

        //Set OnClickListener for "Create Account" button
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Call "Validate" and "PostData" method
                if (validate()) {
                    postData();
                }
            }
        });

        //Underline "Signin" text
        TextView textView = (TextView) findViewById(R.id.sign_in_link);
        SpannableString content = new SpannableString(textView.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);

        //Set OnClickListener for "Create Account" button
        signinLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationActivity.this, PrefActivity.class);
                startActivity(intent);
            }
        });
    }
    //Method to validate user filled data
    private boolean validate() {
        if (usernameEditText.getText().toString().isEmpty() ||
                displayNameEditText.getText().toString().isEmpty() ||
                passwordEditText.getText().toString().isEmpty() ||
                reenterPasswordEditText.getText().toString().isEmpty()) {
            Toast.makeText(RegistrationActivity.this, "All fields are required", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!passwordEditText.getText().toString().equals(reenterPasswordEditText.getText().toString())) {
            Toast.makeText(RegistrationActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    //Method to post user filled data
    private void postData() {

        //Create JsonObject for data
        JsonObject userData = new JsonObject();
        username=usernameEditText.getText().toString().trim();
        password=passwordEditText.getText().toString().trim();
        userData.addProperty("username", username);
        userData.addProperty("displayName", displayNameEditText.getText().toString().trim());
        userData.addProperty("password", password);

        //Create a call to server using Retrofit for creating user
        RetrofitClient retrofitClient = new RetrofitClient();
        Call<ResponseBody> call = retrofitClient
                .getAPI()
                .createUser(userData);

        //Enqueue the call
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                //Handle the server response
                if (response.isSuccessful()) {

                    Toast.makeText(RegistrationActivity.this, "Account Created!", Toast.LENGTH_SHORT).show();

                    //Clear the edit texts
                    usernameEditText.setText("");
                    displayNameEditText.setText("");
                    passwordEditText.setText("");
                    reenterPasswordEditText.setText("");

                    login(username,password);

                } else {
                    String errorMessage = "Error: " + response.code();
                    try {
                        errorMessage += " - " + response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(RegistrationActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            //Handle failure
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(RegistrationActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void login(String username, String password) {

        //Create JsonObject for data
        JsonObject loginData = new JsonObject();
        loginData.addProperty("username", username);
        loginData.addProperty("password", password);

        RetrofitClient retrofitClient = new RetrofitClient();
        //Create a call to server using Retrofit for login
        Call<ResponseBody> call = retrofitClient
                .getAPI()
                .loginUser(loginData);

        //Enqueue the call
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //Handle the server response
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        //Using gson library to parse the response
                        String responseBodyString = response.body().string();
                        JsonObject jsonObject = JsonParser.parseString(responseBodyString).getAsJsonObject();
                        String sessionId = jsonObject.get("sessionId").getAsString();
                        int userId = jsonObject.get("userId").getAsInt();

                        //Store the username, sessionId and boolean in SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", username);
                        editor.putString("sessionId", sessionId);
                        editor.putBoolean("loggedIn", true);
                        editor.putInt("userId", userId);
                        editor.apply();

                        //Redirect to "Game List" page or "Pref" page

                        Intent intent = new Intent(RegistrationActivity.this, PrefActivity.class);

                        startActivity(intent);
                        finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(RegistrationActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }

            //Handle failure
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(RegistrationActivity.this, "Login failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}