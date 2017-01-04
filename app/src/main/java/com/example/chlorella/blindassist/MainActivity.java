package com.example.chlorella.blindassist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {
    // Remenber modify to string resource
    private String[] classNames = {
            "item1",
            "item2",
            "item3",
            "Setting",
    };
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        ListAdapter adapter = new ListAdapter(this, classNames);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        Intent nextActivity = new Intent(MainActivity.this,AndroidCameraApi.class);
                        startActivity(nextActivity);
                        break;
                    case 3:
                        nextActivity = new Intent(MainActivity.this,SettingsActivity.class);
                        startActivity(nextActivity);
                }
            }
        });
    }


}
