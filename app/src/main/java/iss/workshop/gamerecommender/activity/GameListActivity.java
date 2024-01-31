package iss.workshop.gamerecommender.activity;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import iss.workshop.gamerecommender.R;
import iss.workshop.gamerecommender.adapter.GameListActivityAdapter;

public class GameListActivity extends AppCompatActivity {
    private final String[] texts={};
    private final String[] images={};
    //use for sample,get from java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);

        SearchView searchView=findViewById(R.id.search);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String[] filteredStrings = filterStrings(texts,query);
                setContent(filteredStrings);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()){
                    setContent(texts);
                }
                return false;
            }
        });

        ImageButton filter=findViewById(R.id.filter);
        registerForContextMenu(filter);
    }

    private void setContent(String[] filtertexts){
        GameListActivityAdapter adapter=new GameListActivityAdapter(this,images,filtertexts);

        ListView listView=findViewById(R.id.gamelist);
        if(listView!=null){
            listView.setAdapter(adapter);
        }
    }

    private String[] filterStrings(String[] strings, String targetString) {
        List<String> filteredStrings = new ArrayList<>();
        for (String s : strings) {
            if (s.contains(targetString)) {
                filteredStrings.add(s);
            }
        }
        String[] arrayString = filteredStrings.toArray(new String[0]);
        return arrayString;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo){
        if(v.getId()==R.id.filter){
            MenuInflater inflater=getMenuInflater();
            inflater.inflate(R.menu.contextmenu,menu);
        }
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