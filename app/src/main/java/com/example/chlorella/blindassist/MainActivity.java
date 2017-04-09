package com.example.chlorella.blindassist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.chlorella.blindassist.CameraActivity.testCameraKit;
import com.example.chlorella.blindassist.Setting.SettingsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.listView)
    ListView listView;

    // Remenber modify to string resource
    private String[] myClassNames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        myClassNames = getResources().getStringArray(R.array.classNames);
        // Assign the adapter to this ListActivity
        listView = (ListView) findViewById(R.id.listView);
        ListAdapter adapter = new ListAdapter(this, myClassNames);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent nextActivity;
                switch (i) {
                    case 0:
                        nextActivity = new Intent(MainActivity.this, testCameraKit.class);
                        startActivity(nextActivity);
                        break;
                    case 1:
                        nextActivity = new Intent(MainActivity.this, AnalyzeColorActivity.class);
                        startActivity(nextActivity);
                        break;
                    case 2:
                        nextActivity = new Intent(MainActivity.this, DescribeActivity.class);
                        startActivity(nextActivity);
                        break;
                    case 3:
                        nextActivity = new Intent(MainActivity.this, RecognizeActivity.class);
                        startActivity(nextActivity);
                        break;
                    case 4:
                        nextActivity = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(nextActivity);
                }
            }
        });
    }
}
