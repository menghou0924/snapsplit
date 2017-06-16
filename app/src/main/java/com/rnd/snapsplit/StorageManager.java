package com.rnd.snapsplit;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by menghou0924 on 6/5/17.
 */

public class StorageManager {

    private static final String TAG = "StorageManager";
    private Context context;

    public StorageManager(Context ctx) {
        super();
        context = ctx;
    }

    public void saveFile(String filename, String text) {
        try {
            FileOutputStream outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(text.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getFile(String filename) {
        StringBuilder sb = new StringBuilder();
        String line;

        try {
            FileInputStream fileIn = context.openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fileIn);

            BufferedReader bufferedReader = new BufferedReader(isr);

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

        } catch (FileNotFoundException e) {
            this.saveFile(filename, "");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    public String getValueFromFile(String filename, String key) {
        String value = "";
        try {
            JSONObject obj = new JSONObject(this.getFile(filename));
            value = obj.optString(key);
        }
        catch (JSONException e) {
            Log.e(TAG, "ERROR: ");
            e.printStackTrace();
        }
        return value;
    }

    public Boolean removeFile(String filename) {
        File dir = context.getFilesDir();
        File file = new File(dir, filename);
        return file.delete();
    }

    public void clearFile(String filename) {
        this.saveFile(filename, "");
    }

    public Boolean isFileEmpty(String filename) {
        String file = this.getFile(filename);
        return file.isEmpty();
    }
}