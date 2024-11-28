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
    private Sensor gyroscope;
    private boolean startRecording = false;
    private static final int SAMPLING_RATE = SensorManager.SENSOR_DELAY_NORMAL;

    private float[] accelerometerData = new float[3];
    private float[] gyroscopeData = new float[3];

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
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SAMPLING_RATE);
            }
            if (gyroscope != null) {
                sensorManager.registerListener(this, gyroscope, SAMPLING_RATE);
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
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accelerometerData[0] = event.values[0];
                accelerometerData[1] = event.values[1];
                accelerometerData[2] = event.values[2];
            } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                gyroscopeData[0] = event.values[0];
                gyroscopeData[1] = event.values[1];
                gyroscopeData[2] = event.values[2];
            }

            // Broadcast both accelerometer and gyroscope data
            Intent broadcastIntent = new Intent("SENSOR_DATA");
            broadcastIntent.putExtra("accelerometerData", accelerometerData);
            broadcastIntent.putExtra("gyroscopeData", gyroscopeData);

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