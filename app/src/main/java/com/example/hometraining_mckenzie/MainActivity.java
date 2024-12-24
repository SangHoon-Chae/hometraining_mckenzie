package com.example.hometraining_mckenzie;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
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
import android.content.Context;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private boolean recordingStarted = false;
    private final Handler mHandler = new Handler();
    private TextView mLabelAccelDataX, mLabelAccelDataY, mLabelAccelDataZ;

    private Button startButton;
    private Button endButton;
    private String sensorData;
    private String currentExercise;
    private SensorFileManager sensorFileManager;

    @Override
    public Intent registerReceiver(@Nullable BroadcastReceiver sensorDataReceiver, IntentFilter filter) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return super.registerReceiver(sensorDataReceiver, filter, Context.RECEIVER_EXPORTED);
        } else {
            return super.registerReceiver(sensorDataReceiver, filter);
        }
    }
    private BroadcastReceiver sensorDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals("SENSOR_DATA")) {
                // Get accelerometer and gyroscope data
                float[] accelerometerData = intent.getFloatArrayExtra("accelerometerData");
                float[] gyroscopeData = intent.getFloatArrayExtra("gyroscopeData");

                // Append data to the sensorData string
                sensorData = sensorData.concat(
                        String.format("Accel: %.2f\t%.2f\t%.2f\tGyro: %.2f\t%.2f\t%.2f\n",
                                accelerometerData[0], accelerometerData[1], accelerometerData[2],
                                gyroscopeData[0], gyroscopeData[1], gyroscopeData[2])
                );
                // Update UI with sensor data
                updateUI(accelerometerData, gyroscopeData);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Local variables
        List<String> titles;
        List<Integer> images;
        Adapter adapter;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start the AccelerometerService with the start flag
//        Intent serviceIntent = new Intent(this, AccelerometerService.class);
//        serviceIntent.putExtra("startRecording", true); // Set to true when you want to start recording
//        startService(serviceIntent);

        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter("SENSOR_DATA");
        registerReceiver(sensorDataReceiver, filter);

        //ExerChoice
        titles = new ArrayList<>();
        images = new ArrayList<>();
        sensorData = new String("");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        RecyclerView exerList = findViewById(R.id.exList);
        exerList.setHasFixedSize(false);

        initializeViews();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sensorFileManager = new SensorFileManager(this);

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
                // Toggle the recording state
                recordingStarted = !recordingStarted;

                if (recordingStarted) {
                    // Save the data to the file
                    if (currentExercise == null) {
                        showToast("Exercise 를 먼저 선택하여 주십시오.");
                        // If exercise not selected, revert the toggle state
                        recordingStarted = false;
                    } else {
                        sensorData = sensorData.concat(currentExercise + "\n");
                        sensorData = sensorData.concat("Accelerometer\n");

                        // Send the start flag to the AccelerometerService
                        Intent serviceIntent = new Intent(MainActivity.this, AccelerometerService.class);
                        serviceIntent.putExtra("startRecording", true);
                        startService(serviceIntent);

                        showToast("'"+ currentExercise + "'" + " DATA 저장 시작. END 버튼을 누르면 종료됩니다.");

                        // Change the button background based on the recording state
                        updateButtonBackground();
                    }
                } else {
                    // Save the data to the file
                    sensorData = sensorData.concat("End of file\n");
                    sensorFileManager.saveSensorData(sensorData);

                    showToast("'"+ currentExercise + "'" + "DATA 저장 중단. 다시 시작하려면 START 버튼을 누르세요.");

                    // Stop the AccelerometerService
                    stopService(new Intent(MainActivity.this, AccelerometerService.class));

                    // Change the button background based on the recording state
                    updateButtonBackground();
                }
            }
        });

        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save the data to the file
                sensorData = sensorData.concat("End of file\n");
                sensorFileManager.saveSensorData(sensorData);
                finish();
            }
        });
    }
    private void updateButtonBackground() {
        // Change the button background based on the recording state
        int buttonBackgroundResource = recordingStarted ?R.color.colorButtonPressed : R.drawable.button_background;
        startButton.setBackgroundResource(buttonBackgroundResource);
    }

    public void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(float[] accelerometerData, float[] gyroscopeData){
        // Update accelerometer data on UI
        mLabelAccelDataX.setText(String.format(Locale.US, "Accel X: %.3f", accelerometerData[0]));
        mLabelAccelDataY.setText(String.format(Locale.US, "Accel Y: %.3f", accelerometerData[1]));
        mLabelAccelDataZ.setText(String.format(Locale.US, "Accel Z: %.3f", accelerometerData[2]));

        // Assuming you have TextViews for gyroscope data
        TextView mLabelGyroDataX = findViewById(R.id.label_gyro_X);
        TextView mLabelGyroDataY = findViewById(R.id.label_gyro_Y);
        TextView mLabelGyroDataZ = findViewById(R.id.label_gyro_Z);

        // Update gyroscope data on UI
        mLabelGyroDataX.setText(String.format(Locale.US, "Gyro X: %.3f", gyroscopeData[0]));
        mLabelGyroDataY.setText(String.format(Locale.US, "Gyro Y: %.3f", gyroscopeData[1]));
        mLabelGyroDataZ.setText(String.format(Locale.US, "Gyro Z: %.3f", gyroscopeData[2]));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Handle changes in SharedPreferences here
        if (key.equals("exer")) {
            String exerType = sharedPreferences.getString("exer", "");

            // Update the currentExercise based on the flag value
            updateImageView(Integer.valueOf(exerType));
        }
    }

    private void updateImageView(int exerType) {
        // Use the flag value to determine which image to display in the ImageView
        ImageView imageView = findViewById(R.id.exerciseImage);

        switch (exerType) {
            case 1:
                // EXERCISE 1 = FlexExer
                currentExercise = "FlexExer";
                imageView.setImageResource(R.drawable.exer1);
                break;
            case 2:
                // EXERCISE 2 = ScapExer
                currentExercise = "ScapExer";
                imageView.setImageResource(R.drawable.exer2);
                break;
            case 3:
                // EXERCISE 3 = WallSlide
                currentExercise = "WallSlide";
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
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the BroadcastReceiver
        unregisterReceiver(sensorDataReceiver);

        // Stop the AccelerometerService
        stopService(new Intent(this, AccelerometerService.class));
    }
}