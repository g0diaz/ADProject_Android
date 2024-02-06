package iss.workshop.gamerecommender.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import iss.workshop.gamerecommender.R;
import iss.workshop.gamerecommender.activity.LoginActivity;
import iss.workshop.gamerecommender.adapter.GameListActivityAdapter;
import iss.workshop.gamerecommender.api.RetrofitClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameFragment extends Fragment
implements AdapterView.OnItemClickListener {

    List<String> titles = new ArrayList<>();
    List<String> urls = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_game, container, false);
        displayGames(view);

        ImageButton filter=view.findViewById(R.id.filter);
        registerForContextMenu(filter);
        return view;
    }

    public void displayGames(View view){

        //Create a call to server using Retrofit for fetching games
        RetrofitClient retrofitClient = new RetrofitClient();
        Call<JsonArray> call = retrofitClient
                .getAPI()
                .getAllGames();

        //Enqueue the call
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                System.out.println(response);
                if (response.isSuccessful() && response.body() != null){
                    JsonArray games = response.body();
                    // JsonObject element = result.get(0).getAsJsonObject();

                    for (JsonElement game : games){
                        JsonObject gameObj = game.getAsJsonObject();
                        String title = gameObj.get("title").getAsString();
                        String url = gameObj.get("imageUrl").getAsString();

                        titles.add(title);
                        urls.add(url);
                    }

                    setContent(view);

                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(getContext(), "Cannot fetch the games: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setContent(View view){
        GameListActivityAdapter adapter=new GameListActivityAdapter(requireContext(),urls,titles);

        ListView listView=view.findViewById(R.id.gamelist);
        if(listView!=null){
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo){
        if(v.getId()==R.id.filter){
            MenuInflater inflater=requireActivity().getMenuInflater();
            inflater.inflate(R.menu.contextmenu,menu);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> av,View v,int pos,long id){
        Bundle bundle=new Bundle();
        bundle.putString("title",titles.get(pos));
        bundle.putString("url", urls.get(pos));

        GamedetailFragment gamedetailFragment=new GamedetailFragment();
        gamedetailFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.frame_layout,gamedetailFragment)
                .addToBackStack("gameFragment")
                .commit();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        int id=item.getItemId();
        if(id==R.id.topseller){

        }
        if(id==R.id.trending){

        }
        if(id==R.id.toprate){

        }
        return super.onContextItemSelected(item);
    }
}