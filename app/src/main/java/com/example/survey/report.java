package com.example.survey;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class report extends AppCompatActivity {

    private final static int REQUEST_ACCESS_COARSE_LOCATION = 1;
    private final static int REQUEST_ACCESS_FINE_LOCATION = 1;
    private final static int REQUEST_READ_PHONE_STATE = 1;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    Question[] questions;
    Location loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        FinishAll.activityList.add(report.this);
        showReport();

        //about saving in database
        initLocManager();
        //getValues();
        saveToDB();
    }

    protected void showReport() {
        Intent intent = getIntent();
        int size = intent.getIntExtra("len", 0);
        questions = new Question[size];

        for(int i=0; i<size; i++) {
            //build questions
            try {
                String str = intent.getStringExtra("Question"+i);
                JSONObject jsonObject = new JSONObject(str);
                questions[i] = new Question(jsonObject, false);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //show questions
            String answer = new String();
            String[] ansArray = questions[i].getAnswers();
            for(int j=0; j<ansArray.length; j++) {
                answer += ansArray[j];
                answer += "\n";
            }
            addItem(Integer.toString(i+1) + ". " + questions[i].getQuestion(), answer);
        }
    }

    protected void addItem(String question, String answer) {
        LinearLayout layout = findViewById(R.id.answer_area);

        TextView q = new TextView(this);
        LinearLayout.LayoutParams param =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        param.gravity = Gravity.CENTER_HORIZONTAL;
        param.bottomMargin = 10;
        q.setLayoutParams(param);
        q.setText(question);
        q.setTextSize(20);

        TextView a = new TextView(this);
        LinearLayout.LayoutParams param2 =
                new LinearLayout.LayoutParams(650,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        param2.gravity = Gravity.CENTER_HORIZONTAL;
        param2.bottomMargin = 60;
        a.setLayoutParams(param2);
        a.setText(answer);
        a.setTextSize(16);

        layout.addView(q);
        layout.addView(a);
    }


    public void exit(android.view.View V) {
        Intent intent = new Intent(this, LockActivity.class);
        startActivity(intent);
    }

    public void storeInApp(android.view.View V) {
        Intent intent = getIntent();
        try {
            String output = getResultFileContent();

            File f = getFilesDir();
            File file = new File(f, "info.json");
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            if(file.length() == 0) {
                raf.seek(file.length());
                raf.write(("{\"results\":[" + output + "]}").getBytes());
            }
            else {
                raf.seek(file.length()-2);
                raf.write((",\n" + output + "]}").getBytes());
            }
            raf.close();

            Toast.makeText(report.this, "Store in app: success", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(report.this, "failed: File Error", Toast.LENGTH_LONG).show();
        }
    }

    public void storeInSDCard(android.view.View V) {
        Intent intent = getIntent();
        askForPermission();
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_UNKNOWN)) {
            Toast.makeText(report.this, "You don't have a SD card!", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            String output = getResultFileContent();

            File f = getExternalFilesDir(null);
            File file = new File(f, "info.json");
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            if(file.length() == 0) {
                raf.seek(file.length());
                raf.write(("{\"results\":[" + output + "]}").getBytes());
            }
            else {
                raf.seek(file.length()-2);
                raf.write((",\n" + output + "]}").getBytes());
            }
            raf.close();

            Toast.makeText(report.this, "Store inside SD card: success", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(report.this, "failed: File Error", Toast.LENGTH_LONG).show();
        }
    }

    private String getResultFileContent() {
        String output = new String();
        try {
            JSONArray jarray = new JSONArray();
            for(int i=0; i<questions.length; i++) {
                JSONObject oneItem = questions[i].getResult();
                jarray.put(oneItem);
            }
            output = jarray.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return output;
    }

    public void askForPermission() {
        int permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    //save location, time, IMEI, answer(in JSON) to database
    public void saveToDB() {
        DBHelper myHelper = new DBHelper(this,"Survey.db",null,1);
        ContentValues values = getValues();

        if (values == null) {
            Toast.makeText(report.this, "Store failed, please retry!", Toast.LENGTH_LONG).show();
            return;
        }

        SQLiteDatabase db = myHelper.getWritableDatabase();
        long id = db.insert("answer", null, values);
        if (id == -1)
            Log.i("SQL", "failed");
        else
            Log.i("SQL", "success");
            Log.i("SQL","answer saved:"+values);
        db.close();
    }

    public ContentValues getValues() {
        String answer = getResultFileContent();
        long timeStamp = getTimeStamp();
        String IMEI = getIMEI();
        getLocation();

        //validation check
        if (answer == null)
            return null;
        if (IMEI == null)
            Log.i("getValues", "IMEI null");
        else
            Log.i("IMEI", IMEI);
        double latitude = 0;
        double longitude = 0;
        if (loc == null)
            Log.i("getValues", "location null");
        else {
            latitude = loc.getLatitude();
            longitude = loc.getLongitude();
            Log.i("latitude", Double.toString(latitude));
            Log.i("longitude", Double.toString(longitude));
        }
        Log.i("timestamp", "" + timeStamp);
        Log.i("answer", answer);

        //put all parameters into @param values
        ContentValues cv = new ContentValues();
        cv.put("IMEI", IMEI);
        cv.put("timestamp", timeStamp);
        cv.put("answer", answer);
        cv.put("latitude", latitude);
        cv.put("longitude", longitude);
        cv.put("sync",0);
        return cv;
    }

    public String getIMEI() {
        if (Build.VERSION.SDK_INT >= 29)
            return null;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Log.i("IMEI", "no permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
            return null;
        }

        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        try {
            if(Build.VERSION.SDK_INT >= 26) {
                String k = tm.getImei();
                return tm.getImei();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public long getTimeStamp() {
        return System.currentTimeMillis();
    }

    private LocationManager locationManager;
    private String locationProvider;

    private void initLocManager() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        List<String> providers = locationManager.getProviders(true);
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            Log.i("provider", "gps provider");
            locationProvider = LocationManager.GPS_PROVIDER;
        } else
        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            Log.i("provider", "network provider");
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
            Log.i("provider", "no provider");
            return;
        }
    }

    // set @param loc
    public void getLocation() {
        //ask for permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Log.i("WHY","!!!");
        loc = locationManager.getLastKnownLocation(locationProvider);
//        if(loc == null)
//            locationManager.requestLocationUpdates(locationProvider, 0, 0,locationListener);
    }
    void sync() {

        DBHelper myHelper = new DBHelper(this,"Survey.db",null,1);

        SQLiteDatabase db = myHelper.getReadableDatabase();
        SQLiteDatabase dbWrite = myHelper.getWritableDatabase();
        String sql="SELECT* FROM answer WHERE sync = 0";
        Cursor cursor=db.rawQuery(sql,new String[]{});

        JSONObject syncAnswerArray = new JSONObject();
        while(cursor.moveToNext()){
            JSONObject syncAnswer = new JSONObject();
            String latitude=cursor.getString(cursor.getColumnIndex("latitude"));
            String  longitude=cursor.getString(cursor.getColumnIndex(" longitude"));
            String timestamp=cursor.getString(cursor.getColumnIndex("timestamp"));
            String IMEI=cursor.getString(cursor.getColumnIndex("IMEI"));
            String answer=cursor.getString(cursor.getColumnIndex("answer"));
            try {
                syncAnswer.put("latitude",latitude);
                syncAnswer.put("longitude",longitude);
                syncAnswer.put("timestamp",timestamp);
                syncAnswer.put("IMEI",IMEI);
                syncAnswer.put("answer",answer);
                syncAnswerArray.put("array",syncAnswer);
            }
            catch (JSONException e){
                Log.e("JSON",e.toString());
            }


        }

        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(syncAnswerArray.toString()).build();
            Response response = client.newCall(request).execute();
            String responseData = response.body().string();

            /*
            if(isOK(responseData){
                dbWrite.update(//TODO)
            }

             */


        } catch (Exception e) {
            Log.e("HTTP", e.toString());
            //e.printStackTrace();
        }
        cursor.close();
    }
}
