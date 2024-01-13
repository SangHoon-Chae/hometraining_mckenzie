package com.example.hometraining_mckenzie;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.util.List;

public class AccelerometerService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean startRecording = false;
    private static final int SAMPLING_RATE = SensorManager.SENSOR_DELAY_NORMAL;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Check for the start flag in the Intent extras
        if (intent != null && intent.hasExtra("startRecording")) {
            startRecording = intent.getBooleanExtra("startRecording", false);
        }

        // Rest of the onStartCommand method
        // Initialize the sensor manager and accelerometer sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SAMPLING_RATE);
            }
        }
        return START_STICKY; // or other appropriate return value
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister the sensor listener when the service is destroyed
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Handle sensor data changes here
        if (startRecording) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Do something with the accelerometer data
            // Format the data as needed
            //String sensorData = String.format("X: %.2f, Y: %.2f, Z: %.2f", x, y, z);
            float[] sensorData = new float[3];

            sensorData[0] = x;
            sensorData[1] = y;
            sensorData[2] = z;

            // Send the sensor data to the MainActivity using a broadcast
            Intent broadcastIntent = new Intent("SENSOR_DATA");
            broadcastIntent.putExtra("sensorDataFloat", sensorData);
            sendBroadcast(broadcastIntent);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for this example
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Not needed for this example
        return null;
    }
}