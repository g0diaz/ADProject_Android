package iss.workshop.gamerecommender.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import iss.workshop.gamerecommender.R;

public class PrefActivity extends AppCompatActivity{
    private final String[] genres={"RPG","Action","Racing","FPS","Indie","Massively Multiplayer"
                                ,"Sports","Free To Play"};
    //need to get data from java
    private List<String> selected=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pref);

        PrefActivityAdapter adapter=new PrefActivityAdapter(this,genres);

        ListView listView=findViewById(R.id.listview);
        if(listView!=null){
            listView.setAdapter(adapter);
        }

        Button conBtn=findViewById(R.id.conBtn);
        conBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSelectedGenres();
                Intent intent=new Intent(PrefActivity.this,GameListActivity.class);
                intent.putStringArrayListExtra("preferences", (ArrayList<String>) selected);
                startActivity(intent);

                //need to post data to java
            }
        });
    }

    private void saveSelectedGenres() {
        selected.clear();

        ListView listView = findViewById(R.id.listview);
        if (listView != null) {
            int itemCount = listView.getCount();
            for (int i = 0; i < itemCount; i++) {
                View itemView = listView.getChildAt(i);
                CheckBox checkBox = itemView.findViewById(R.id.checkbox);

                if (checkBox.isChecked()) {
                    selected.add(checkBox.getText().toString());
                }
            }
        }
    }
}