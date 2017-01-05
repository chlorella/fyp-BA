package com.example.chlorella.blindassist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.chlorella.blindassist.CameraActivity.ColorDetectActivity;
import com.example.chlorella.blindassist.CameraActivity.ObjectDetectActivity;
import com.example.chlorella.blindassist.CameraActivity.SmallToolActivity;
import com.example.chlorella.blindassist.CameraActivity.WordDetectActivity;
import com.example.chlorella.blindassist.Setting.SettingsActivity;

public class MainActivity extends AppCompatActivity {
    // Remenber modify to string resource
    private String[] classNames = {
            "Tool",
            "Color Detector",
            "Object Detector",
            "Word Detector",
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
                Intent nextActivity;
                switch (i){
                    case 0:
                        nextActivity = new Intent(MainActivity.this,SmallToolActivity.class);
                        startActivity(nextActivity);
                        break;
                    case 1:
                        nextActivity = new Intent(MainActivity.this,ColorDetectActivity.class);
                        startActivity(nextActivity);
                        break;
                    case 2:
                        nextActivity = new Intent(MainActivity.this,ObjectDetectActivity.class);
                        startActivity(nextActivity);
                        break;
                    case 3:
                        nextActivity = new Intent(MainActivity.this,WordDetectActivity.class);
                        startActivity(nextActivity);
                        break;
                    case 4:
                        nextActivity = new Intent(MainActivity.this,SettingsActivity.class);
                        startActivity(nextActivity);
                }
            }
        });
    }


}
