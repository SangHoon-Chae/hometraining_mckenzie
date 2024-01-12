package com.example.hometraining_mckenzie.imu_data;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.example.hometraining_mckenzie.MainActivity;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class IMUSession implements SensorEventListener {

    // properties
    private final static String LOG_TAG = IMUSession.class.getName();

    private MainActivity mContext;
    private SensorManager mSensorManager;
    private HashMap<String, Sensor> mSensors = new HashMap<>();
    private float mInitialStepCount = -1;

    private AtomicBoolean mIsRecording = new AtomicBoolean(false);
    private AtomicBoolean mIsWritingFile = new AtomicBoolean(false);

    private float[] mAcceMeasure = new float[3];
    private float[] mGyroMeasure = new float[3];
    private float[] mMagnetMeasure = new float[3];

    private float[] mAcceBias = new float[3];
    private float[] mGyroBias = new float[3];
    private float[] mMagnetBias = new float[3];


    // constructor
    public IMUSession(MainActivity context) {

        // initialize object and sensor manager
        mContext = context;
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);

        // setup and register various sensors
        mSensors.put("acce", mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        mSensors.put("acce_uncalib", mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED));
        mSensors.put("gyro", mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
        mSensors.put("gyro_uncalib", mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED));
        mSensors.put("linacce", mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION));
        mSensors.put("gravity", mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY));
        mSensors.put("magnet", mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
        mSensors.put("magnet_uncalib", mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED));
        mSensors.put("rv", mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR));
        mSensors.put("game_rv", mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR));
        mSensors.put("magnetic_rv", mSensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR));
        mSensors.put("step", mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER));
        mSensors.put("pressure", mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE));
        registerSensors();
    }


    // methods
    public void registerSensors() {
        for (Sensor eachSensor : mSensors.values()) {
            mSensorManager.registerListener(this, eachSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    public void unregisterSensors() {
        for (Sensor eachSensor : mSensors.values()) {
            mSensorManager.unregisterListener(this, eachSensor);
        }
    }
    @Override
    public void onSensorChanged(final SensorEvent sensorEvent) {

        // set some variables
        float[] values = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
        boolean isFileSaved = (mIsRecording.get() && mIsWritingFile.get());

        // update each sensor measurements
        long timestamp = sensorEvent.timestamp;
        Sensor eachSensor = sensorEvent.sensor;
        switch (eachSensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                mAcceMeasure[0] = sensorEvent.values[0];
                mAcceMeasure[1] = sensorEvent.values[1];
                mAcceMeasure[2] = sensorEvent.values[2];
                break;

            case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
                mAcceBias[0] = sensorEvent.values[3];
                mAcceBias[1] = sensorEvent.values[4];
                mAcceBias[2] = sensorEvent.values[5];
                break;

            case Sensor.TYPE_GYROSCOPE:
                mGyroMeasure[0] = sensorEvent.values[0];
                mGyroMeasure[1] = sensorEvent.values[1];
                mGyroMeasure[2] = sensorEvent.values[2];
                break;

            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                mGyroBias[0] = sensorEvent.values[3];
                mGyroBias[1] = sensorEvent.values[4];
                mGyroBias[2] = sensorEvent.values[5];
                break;

            case Sensor.TYPE_LINEAR_ACCELERATION:
                break;

            case Sensor.TYPE_GRAVITY:
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                mMagnetMeasure[0] = sensorEvent.values[0];
                mMagnetMeasure[1] = sensorEvent.values[1];
                mMagnetMeasure[2] = sensorEvent.values[2];
                break;

            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                mMagnetBias[0] = sensorEvent.values[3];
                mMagnetBias[1] = sensorEvent.values[4];
                mMagnetBias[2] = sensorEvent.values[5];
                break;

            case Sensor.TYPE_ROTATION_VECTOR:
                break;

            case Sensor.TYPE_GAME_ROTATION_VECTOR:
                break;

            case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
                break;

            case Sensor.TYPE_STEP_COUNTER:
                if (mInitialStepCount < 0) {
                    mInitialStepCount = sensorEvent.values[0] - 1;
                }
                values[0] = sensorEvent.values[0] - mInitialStepCount;
                break;

            case Sensor.TYPE_PRESSURE:
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    // getter and setter
    public boolean isRecording() {
        return mIsRecording.get();
    }

    public float[] getAcceMeasure() {
        return mAcceMeasure;
    }

    public float[] getGyroMeasure() {
        return mGyroMeasure;
    }

    public float[] getMagnetMeasure() {
        return mMagnetMeasure;
    }

    public float[] getAcceBias() {
        return mAcceBias;
    }

    public float[] getGyroBias() {
        return mGyroBias;
    }

    public float[] getMagnetBias() {
        return mMagnetBias;
    }
}