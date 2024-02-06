package iss.workshop.gamerecommender.activity;

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
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import iss.workshop.gamerecommender.R;
import iss.workshop.gamerecommender.api.RetrofitClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button signupButton;
    private TextView forgotPasswordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is already logged in
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("loggedIn", false);

        if (isLoggedIn) {
            // Redirect to MainActivity if the user is already logged in
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        //Mapping
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        signupButton = findViewById(R.id.sign_up_button);
        forgotPasswordTextView = findViewById(R.id.forgot_password);

        //Set OnClickListener for "Login" button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                //Call "Login" method
                login(username, password);
            }
        });

        //Underline "Forgot Password" text
        TextView textView = (TextView) findViewById(R.id.forgot_password);
        SpannableString content = new SpannableString(textView.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);

        //Set OnClickListener for "Forgot Password" button
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        //Set OnClickListener for "Signup Password" button
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });
    }
   //Method to handle login
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
                        //Parse the response
                        String responseBodyString = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseBodyString);
                        String sessionId = jsonObject.getString("sessionId");

                        //Store the username, sessionId and boolean in SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", username);
                        editor.putString("sessionId", sessionId);
                        editor.putBoolean("loggedIn", true);
                        editor.apply();

                        //Redirect to "Game List" page
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }

            //Handle failure
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Login failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
