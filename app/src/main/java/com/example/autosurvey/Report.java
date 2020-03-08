package com.example.autosurvey;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

public class Report extends AppCompatActivity {
    private Bundle report;
    private ScrollView mScrollView;
    private LinearLayout mLayout;
    private TextView[] mTextView;
    private TextView[] mOption;
    private TextView mTextView1;
    private int length;
    private LinearLayout.LayoutParams params = null;
    private LinearLayout.LayoutParams params1 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        mLayout=(LinearLayout) this.findViewById(R.id.ll_report);
        GetData();
        CreatTextView(mLayout,params);
        SetData();
    }

    private void GetData() {
        /**
         * get options from MainActivity
         */
        Intent i = getIntent();
        report = i.getExtras();
        length = report.getInt("length");
    }

    private void CreatTextView(LinearLayout layout, LinearLayout.LayoutParams params) {
        /**
         * show answers in layout
         */
        int top;
        int topAnswer;
        String str;
        mTextView=new TextView[length];
        mOption=new TextView[length];
        for(int i=0;i<length;i++){
            str="question"+String.valueOf(i+1);
            mTextView[i]=new TextView(this);
            mTextView[i].setText(String.valueOf(i+1)+"."+report.getString(str));
            mTextView[i].setTextSize(20);
            params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(40,40,40,0);
            mOption[i]=new TextView(this);
            mOption[i].setText("Answer："+report.getString(String.valueOf(i+1)));
            mOption[i].setTextSize(20);
            params1=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            params1.setMargins(40,40,40,0);
            mLayout.addView(mTextView[i],params);
            mLayout.addView(mOption[i],params1);
        }
    }

    public void skip(View v){
        /*
         * skipping MainActivity
         */
        Intent intent=new Intent(Report.this,MainActivity.class);
        startActivity(intent);
    }

    public  void SetData(){
        /**
         *  Save answers as a json file in external storage and internal storage
         */
        String[] inputArr=new  String[length];
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Calendar calendar=Calendar.getInstance();
            int year=calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH)+1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
            String filename=year+"-"+month+"-"+day+"-"+hour+":"+minute+":"+second+".json";
            /***********************External storage********************/
            File sdfile =getExternalFilesDir(null);
            File savedata =new File(sdfile,filename);
            FileOutputStream fout=null;
            /*********************************************************/
            Log.e("TAG","Location: "+sdfile);

            /**********************Internal storage********************/
            FileOutputStream outputStream;
            /**********************************************************/
            try{
                outputStream=openFileOutput(filename, Context.MODE_PRIVATE);
                fout=new FileOutputStream(savedata);
                for(int i=0; i<length; i++){
                    int temp=i+1;
                    inputArr[i]=report.getString(String.valueOf(temp));
                    String input="{Question:"+ temp +",Answer:'"+inputArr[i]+"'}\n";
                    fout.write(input.getBytes());
                    outputStream.write(input.getBytes());
                }
                fout.flush();
                fout.close();
                outputStream.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }


}
