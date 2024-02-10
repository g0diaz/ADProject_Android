package iss.workshop.gamerecommender.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import iss.workshop.gamerecommender.R;
import iss.workshop.gamerecommender.adapter.FriendProfileGamesAdapter;
import iss.workshop.gamerecommender.api.RetrofitClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameFragment extends Fragment
        implements AdapterView.OnItemClickListener {

    private List<String> titles;
    private List<String> urls ;
    private List<Integer> gameIds;

    private SearchView searchView;
    private Spinner spinner;
    private String searchMethod;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_game, container, false);
        titles= new ArrayList<>();
        urls= new ArrayList<>();
        gameIds = new ArrayList<>();

        displayGames(view);

        searchView=view.findViewById(R.id.search);
        handelSearchSubmit(searchView,view);

        spinner=view.findViewById(R.id.searchChoice);
        handleSpinner(spinner);

//        ImageButton filter=view.findViewById(R.id.filter);
//        registerForContextMenu(filter);
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
                if (response.isSuccessful() && response.body() != null){
                    JsonArray games = response.body();

                    for (JsonElement game : games){
                        JsonObject gameObj = game.getAsJsonObject();
                        String title = gameObj.get("title").getAsString();
                        String url = gameObj.get("imageUrl").getAsString();
                        int gameId = gameObj.get("id").getAsInt();

                        titles.add(title);
                        urls.add(url);
                        gameIds.add(gameId);
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
        FriendProfileGamesAdapter adapter=new FriendProfileGamesAdapter(requireContext(),urls,titles);

        ListView listView=view.findViewById(R.id.gamelist);
        if(listView!=null){
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
        }
    }

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v,
//                                    ContextMenu.ContextMenuInfo menuInfo){
//        if(v.getId()==R.id.filter){
//            MenuInflater inflater=requireActivity().getMenuInflater();
//            inflater.inflate(R.menu.contextmenu,menu);
//        }
//    }

    @Override
    public void onItemClick(AdapterView<?> av,View v,int pos,long id){
        Bundle bundle=new Bundle();
        bundle.putInt("gameId", gameIds.get(pos));

        switch(searchMethod){
            case "Game":
                GamedetailFragment gamedetailFragment=new GamedetailFragment();
                gamedetailFragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout,gamedetailFragment)
                        .addToBackStack("gameFragment")
                        .commit();
                break;
            case "Developer":
                ProfileDetailFragment profileDetailFragment =new ProfileDetailFragment();
                profileDetailFragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, profileDetailFragment)
                        .addToBackStack("gameFragment")
                        .commit();
                break;
            case "User":
                ProfileDetailFragment userDetailFragment=new ProfileDetailFragment();
                userDetailFragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout,userDetailFragment)
                        .addToBackStack("gameFragment")
                        .commit();
                break;
        }
    }

//    @Override
//    public boolean onContextItemSelected(MenuItem item){
//        int id=item.getItemId();
//        if(id==R.id.topseller){
//
//        }
//        if(id==R.id.trending){
//
//        }
//        if(id==R.id.toprate){
//
//        }
//        return super.onContextItemSelected(item);
//    }

    private void handleSpinner(Spinner spinner){
        String[] searchMethods = getResources().getStringArray(R.array.search_methods);
        ArrayAdapter adapter
                = new ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                searchMethods);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchMethod = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }
    private void handleSearchMethodSelection(String searchMethod,View view,String searchQuery) {
        switch (searchMethod) {
            case "Game":
                displaySearchResult(view,searchQuery,"Game");
                break;
            case "Developer":
                displaySearchResult(view,searchQuery,"Developer");
                break;
            case "User":
                displaySearchResult(view,searchQuery,"User");
                break;
        }
    }
    private void handelSearchSubmit(SearchView searchView,View view){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                handleSearchMethodSelection(searchMethod,view,query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()){
                }
                return false;
            }
        });
    }
    private void displaySearchResult(View view,String query,String type){
        JsonObject searchData=new JsonObject();
        System.out.println(query);
        searchData.addProperty("query",query);
        searchData.addProperty("type",type);

        RetrofitClient retrofitClient=new RetrofitClient();
        Call<ResponseBody> call=retrofitClient
                .getAPI().getSearchResult(searchData);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null){
                    try {
                        String responseBodyString = response.body().string();
                        JsonArray jsonArray = JsonParser.parseString(responseBodyString).getAsJsonArray();
                        titles=new ArrayList<>();
                        urls=new ArrayList<>();
                        switch(type) {
                            case "Game":
                                setTitlesandUrls(jsonArray,"title","imageUrl");
                                break;
                            case "Developer":
                                setTitlesandUrls(jsonArray,"displayName","displayImageUrl");
                                break;
                            case "User":
                                setTitlesandUrls(jsonArray,"displayName","displayImageUrl");
                                break;
                        }
                        setContent(view);
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Cannot fetch the games: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setTitlesandUrls(JsonArray jsonArray,String titlename,String urlname){
        for (JsonElement e : jsonArray){
            JsonObject obj = e.getAsJsonObject();
            String title = obj.get(titlename).getAsString();
            String url = obj.get(urlname).getAsString();

            titles.add(title);
            urls.add(url);
        }
    }
}