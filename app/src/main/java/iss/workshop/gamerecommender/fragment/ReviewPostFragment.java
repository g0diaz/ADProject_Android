package iss.workshop.gamerecommender.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import iss.workshop.gamerecommender.R;
import iss.workshop.gamerecommender.adapter.ImageLoader;
import iss.workshop.gamerecommender.api.RetrofitClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewPostFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review_post, container, false);

        if (getActivity() != null) {
            TextView titleTextView = getActivity().findViewById(R.id.activity_feed_title);
            if (titleTextView != null) {
                titleTextView.setText("Review");
            }
        }

        Bundle args=getArguments();
        if(args!=null) {
            String gameTitle = args.getString("gameName");
            String imageUrl = args.getString("imgUrl");

            ImageView gameImageView=view.findViewById(R.id.gameimage);
            ImageLoader.loadImage(getContext(), imageUrl, gameImageView);

            TextView gameTitleTextView = view.findViewById(R.id.game_title);
            gameTitleTextView.setText(gameTitle);

            Button submitBtn = view.findViewById(R.id.submitBtn);
            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean like = getReviewFeedback();
                    submitReview(like);
                }
            });
        }

        return view;
    }

    private boolean getReviewFeedback() {
        RadioGroup radioGroup = getView().findViewById(R.id.radioGroup);
        int selectedId = radioGroup.getCheckedRadioButtonId();

        if(selectedId == R.id.recommendBtn){
            return true;
        }
        else
            return false;
    }

    private void submitReview(boolean like) {
        Bundle args = getArguments();
        if(args != null) {
            int gameId = args.getInt("gameId", 0);
            String gameTitle = args.getString("gameName");
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
            int userId = sharedPreferences.getInt("userId", 0);

            EditText reviewBodyEditText = getView().findViewById(R.id.reviewBodyEditText);

            JsonObject reviewData = new JsonObject();
            reviewData.addProperty("message", reviewBodyEditText.getText().toString().trim());
            reviewData.addProperty("gameTitle", gameTitle);
            reviewData.addProperty("userId", userId);
            reviewData.addProperty("gameId", gameId);
            reviewData.addProperty("userFb", like);

            RetrofitClient retrofitClient = new RetrofitClient();
            Call<ResponseBody> call = retrofitClient
                    .getAPI()
                    .reviewGame(reviewData);

            //Enqueue the call
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    //Handle the server response
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Review submitted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to submit review", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}