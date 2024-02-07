package iss.workshop.gamerecommender.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.io.IOException;

import iss.workshop.gamerecommender.api.RetrofitAPI;
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
    private ProgressBar loadingPB;

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
        loadingPB = findViewById(R.id.idLoadingPB);

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
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
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

        //Show the progress bar
        loadingPB.setVisibility(View.VISIBLE);

        //Create JsonObject for data
        JsonObject userData = new JsonObject();
        userData.addProperty("username", usernameEditText.getText().toString().trim());
        userData.addProperty("displayName", displayNameEditText.getText().toString().trim());
        userData.addProperty("password", passwordEditText.getText().toString().trim());

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
                    Toast.makeText(RegistrationActivity.this, "Data added to API", Toast.LENGTH_SHORT).show();

                    //Hide progress bar and clear user filled data
                    loadingPB.setVisibility(View.GONE);
                    usernameEditText.setText("");
                    displayNameEditText.setText("");
                    passwordEditText.setText("");
                    reenterPasswordEditText.setText("");

                    //Redirect to "Login" page
                    Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                    startActivity(intent);

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
}