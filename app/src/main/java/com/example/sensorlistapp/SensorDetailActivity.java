package com.example.sensorlistapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class SensorDetailActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;

    private TextView tvSensorName;
    private TextView tvValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sensor_detail);
        tvSensorName = findViewById(R.id.tvSensorName);
        tvValues = findViewById(R.id.tvValues);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        int sensorType = getIntent().getIntExtra(MainActivity.EXTRA_SENSOR_TYPE, -1);
        int sensorIndex = getIntent().getIntExtra(MainActivity.EXTRA_SENSOR_INDEX, -1);

        List<Sensor> allSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        // 1) Пытаемся взять датчик по индексу (наиболее точное совпадение)
        if (sensorIndex >= 0 && sensorIndex < allSensors.size()) {
            sensor = allSensors.get(sensorIndex);
        }

        // 2) Fallback: если что-то пошло не так — берём первый датчик нужного типа
        if (sensor == null && sensorType != -1) {
            List<Sensor> byType = sensorManager.getSensorList(sensorType);
            if (!byType.isEmpty()) {
                sensor = byType.get(0);
            }
        }

        if (sensor == null) {
            tvSensorName.setText("Датчик не найден");
            tvValues.setText("Не удалось получить доступ к выбранному датчику.");
            return;
        }

        tvSensorName.setText(sensor.getName() + "\n(" + sensor.getStringType() + ")");
        tvValues.setText("Ожидание данных...");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensor != null) {
            // SENSOR_DELAY_NORMAL — разумный компромисс между частотой и расходом батареи.
            // Можно заменить на SENSOR_DELAY_UI / SENSOR_DELAY_GAME при необходимости.
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Обязательно снимаем слушатель, иначе приложение продолжит «сосать» батарею в фоне
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        StringBuilder sb = new StringBuilder();

        // У некоторых датчиков (например, «датчик шагов») значение только одно.
        if (event.values.length == 1) {
            sb.append("value: ").append(String.format("%.4f", event.values[0]));
        } else {
            for (int i = 0; i < event.values.length; i++) {
                sb.append("value[").append(i).append("]: ")
                        .append(String.format("%.4f", event.values[i]))
                        .append("\n");
            }
        }

        sb.append("\n\nТочность: ").append(accuracyToString(event.accuracy));
        sb.append("\nВремя (нс): ").append(event.timestamp);

        tvValues.setText(sb.toString());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Можно игнорировать — информация о точности уже выводится в onSensorChanged
    }

    private String accuracyToString(int accuracy) {
        switch (accuracy) {
            case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                return "HIGH";
            case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                return "MEDIUM";
            case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                return "LOW";
            case SensorManager.SENSOR_STATUS_UNRELIABLE:
                return "UNRELIABLE";
            default:
                return "UNKNOWN (" + accuracy + ")";
        }
    }


}