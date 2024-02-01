package iss.workshop.gamerecommender.fragment;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import iss.workshop.gamerecommender.R;
import iss.workshop.gamerecommender.adapter.GameListActivityAdapter;

public class FriendsFragment extends Fragment {

    private final String[] names ={"David","Brad","Mike","Steve","John"};
    private final String[] avatars ={"avatar","avatar","avatar","avatar","avatar"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_friends, container, false);
        SearchView searchView=view.findViewById(R.id.search);

        setContent(names,view);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String[] filteredStrings = filterStrings(names,query);
                setContent(filteredStrings,view);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()){
                    setContent(names,view);
                }
                return false;
            }
        });
        return view;
    }
    private void setContent(String[] filtertexts,View view){
        GameListActivityAdapter adapter=new GameListActivityAdapter(requireContext(), avatars,filtertexts);

        ListView listView=view.findViewById(R.id.gamelist);
        if(listView!=null){
            listView.setAdapter(adapter);
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
}