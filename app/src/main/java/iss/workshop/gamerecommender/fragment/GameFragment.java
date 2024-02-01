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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import iss.workshop.gamerecommender.R;
import iss.workshop.gamerecommender.adapter.GameListActivityAdapter;

public class GameFragment extends Fragment
implements AdapterView.OnItemClickListener {

    private final String[] texts={"PalWorld","Conan Exiles","Octopath Traveler","Green Hell","Diablo"};
    private final String[] images={"palworld","conan_exiles","octopath_traveler","green_hell","diablo"};
    //use for sample,get from java
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_game, container, false);
        SearchView searchView=view.findViewById(R.id.search);

        TitleBarFragment titleBarFragment = new TitleBarFragment();
        Bundle arguments = new Bundle();
        arguments.putString("title", "Games");
        titleBarFragment.setArguments(arguments);

        // Add the TitleBarFragment to the placeholder in this fragment's layout
        getChildFragmentManager().beginTransaction()
                .add(R.id.title_bar_placeholder, titleBarFragment)
                .commit();

        setContent(texts,view);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String[] filteredStrings = filterStrings(texts,query);
                setContent(filteredStrings,view);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()){
                    setContent(texts,view);
                }
                return false;
            }
        });

        ImageButton filter=view.findViewById(R.id.filter);
        registerForContextMenu(filter);
        return view;
    }
    private void setContent(String[] filtertexts,View view){
        GameListActivityAdapter adapter=new GameListActivityAdapter(requireContext(),images,filtertexts);

        ListView listView=view.findViewById(R.id.gamelist);
        if(listView!=null){
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
        }
    }

    private String[] filterStrings(String[] strings, String targetString) {
        List<String> filteredStrings = new ArrayList<>();
        targetString = targetString.toLowerCase();
        for (String s : strings) {
            if (s.toLowerCase().contains(targetString)) {
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
            MenuInflater inflater=requireActivity().getMenuInflater();
            inflater.inflate(R.menu.contextmenu,menu);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> av,View v,int pos,long id){
        Bundle bundle=new Bundle();
        bundle.putString("gamename",texts[pos]);
        bundle.putString("image",images[pos]);

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