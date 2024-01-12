package com.example.hometraining_mckenzie.file_system;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SensorFileManager {

    private static final String TAG = "SensorFileManager";
    private String fileName = "";

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss", Locale.getDefault());

    private final Context context;

    public SensorFileManager(Context context) {
        this.context = context;
    }
    public void saveSensorData(String data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveSensorDataScopedStorage(data);
        } else {
            saveSensorDataLegacy(data);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void saveSensorDataScopedStorage(String data) {
        String date;

        Calendar c = Calendar.getInstance();
        date = dateFormat.format(c.getTime());

        fileName = fileName.concat("sensorData" + date + ".txt");

        ContentResolver resolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS);

        Uri contentUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        Uri uri = resolver.insert(contentUri, contentValues);

        if (uri != null) {
            try (OutputStream outputStream = resolver.openOutputStream(uri)) {
                assert outputStream != null;
                outputStream.write(data.getBytes());
                Log.d(TAG, "Data saved to file using Scoped Storage: " + data);
            } catch (IOException e) {
                Log.e(TAG, "Error writing to file: " + e.getMessage());
            }
        }
    }

    private void saveSensorDataLegacy(String data) {
        if (isExternalStorageWritable()) {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), fileName);

            try (FileOutputStream fileOutputStream = new FileOutputStream(file, true)) {
                fileOutputStream.write(data.getBytes());
                Log.d(TAG, "Data saved to file using Legacy Storage: " + data);
            } catch (IOException e) {
                Log.e(TAG, "Error writing to file: " + e.getMessage());
            }
        } else {
            Log.e(TAG, "External storage not available or writable.");
        }
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}