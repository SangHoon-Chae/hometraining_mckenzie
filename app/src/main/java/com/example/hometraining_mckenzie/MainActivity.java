package com.example.hometraining_mckenzie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hometraining_mckenzie.design.Adapter;
import com.example.hometraining_mckenzie.file_system.SensorFileManager;
import com.example.hometraining_mckenzie.imu_data.IMUSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private IMUSession mIMUSession;
    private final Handler mHandler = new Handler();
    private TextView mLabelAccelDataX, mLabelAccelDataY, mLabelAccelDataZ;
    private RecyclerView exerList;
    private List<String> titles;
    private List<Integer> images;
    private Adapter adapter;
    private Button startButton;
    private Button endButton;
    private boolean startFlag = false;
    private String sensorData;
    private String currentExercise;
    private SensorManager sensorManager;
    private SensorFileManager sensorFileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Sensor accelerometer;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ExerChoice
        titles = new ArrayList<>();
        images = new ArrayList<>();
        sensorData = new String("Start of file \n");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        exerList = findViewById(R.id.exList);
        exerList.setHasFixedSize(false);

        initializeViews();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sensorFileManager = new SensorFileManager(this);

//        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        if (sensorManager != null) {
//            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//            if (accelerometer != null) {
//                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
//            } else {
//                Toast.makeText(this, "Accelerometer not available", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(this, "Sensor Service not available", Toast.LENGTH_SHORT).show();
//        }

        titles.add("EXERCISE 1");
        titles.add("EXERCISE 2");
        titles.add("EXERCISE 3");
//        titles.add("Squat");

        images.add(R.drawable.exer1);
        images.add(R.drawable.exer2);
        images.add(R.drawable.exer3);
//        images.add(R.drawable.exer4);

        adapter = new Adapter(this, titles, images);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        exerList.setLayoutManager(gridLayoutManager);
        exerList.setAdapter(adapter);

        // Register the listener for SharedPreferences changes
        SharedPreferences sharedPreferences = getSharedPreferences("exer_type", MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save the data to the file
                sensorData = sensorData.concat("Exercise type is " + currentExercise + "\n");
                startFlag = true;

                //버튼 색깔 변경
                startButton.setBackgroundResource(R.color.colorButtonPressed);
            }
        });

        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save the data to the file
                if(startFlag) {
                    startFlag = false;
                    sensorData = sensorData.concat("End of file \n");
                    sensorFileManager.saveSensorData(sensorData);
                    finish();
                }
                else
                    finish();
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Handle changes in SharedPreferences here
        if (key.equals("exer")) {
            String flagValue = sharedPreferences.getString("exer", "");

            // Update the currentExercise based on the flag value
            updateImageView(Integer.valueOf(flagValue));
        }
    }

    private void updateImageView(int flagValue) {
        // Use the flag value to determine which image to display in the ImageView
        ImageView imageView = findViewById(R.id.exerciseImage);

        switch (flagValue) {
            case 1:
                currentExercise = "Q-SET";
                imageView.setImageResource(R.drawable.exer1);
                break;
            case 2:
                currentExercise = "Q-Walk";
                imageView.setImageResource(R.drawable.exer2);
                break;
            case 3:
                currentExercise = "Side-Walk";
                imageView.setImageResource(R.drawable.exer3);
                break;
//            case 4:
//                currentExercise = "Squat";
//                imageView.setImageResource(R.drawable.exer4);
//                break;
            // Add cases for other flag values and corresponding images
        }
    }
    private void initializeViews() {
        startButton = (Button) findViewById(R.id.start_button);
        endButton = (Button) findViewById(R.id.end_button);

        mLabelAccelDataX = (TextView) findViewById(R.id.label_accel_X);
        mLabelAccelDataY = (TextView) findViewById(R.id.label_accel_Y);
        mLabelAccelDataZ = (TextView) findViewById(R.id.label_accel_Z);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(startFlag) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                // Format the data as needed
                sensorData = sensorData.concat(String.format("X: %.2f, Y: %.2f, Z: %.2f", x, y, z) + "\n");

                mLabelAccelDataX.setText(String.format(Locale.US, "%.3f", x));
                mLabelAccelDataY.setText(String.format(Locale.US, "%.3f", y));
                mLabelAccelDataZ.setText(String.format(Locale.US, "%.3f", z));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for this example
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
}