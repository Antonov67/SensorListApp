package com.example.sensorlistapp;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_SENSOR_TYPE = "extra_sensor_type";
    public static final String EXTRA_SENSOR_INDEX = "extra_sensor_index";

    private SensorManager sensorManager;
    private ListView listViewSensors;
    private List<Sensor> sensors;
    private List<String> sensorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        listViewSensors = findViewById(R.id.listView);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        sensorList = new ArrayList<>();

        for (Sensor sensor : sensors){
            String info = "Имя: " + sensor.getName()
                    + "\nТип: " + sensor.getStringType();
            sensorList.add(info);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                sensorList
                );

        listViewSensors.setAdapter(adapter);

        listViewSensors.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i >= sensors.size()) return;
                Sensor clicked = sensors.get(i);

                Intent intent = new Intent(MainActivity.this, SensorDetailActivity.class);
                intent.putExtra(EXTRA_SENSOR_TYPE, clicked.getType());
                // Индекс среди всех датчиков типа: сенсоры одного типа можно отличить по индексу
                intent.putExtra(EXTRA_SENSOR_INDEX, i);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}