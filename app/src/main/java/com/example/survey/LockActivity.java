package com.example.survey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.wangnan.library.GestureLockView;
import com.wangnan.library.listener.OnGestureLockListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class LockActivity extends AppCompatActivity {
    private GestureLockView gestureLockView;
    private TextView tv_code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        FinishAll.activityList.add(LockActivity.this);

        gestureLockView=findViewById(R.id.glv);
        tv_code=findViewById(R.id.tv_code);
        String code=listToString(exitCode());
        tv_code.setText(code);

        gestureLockView.setGestureLockListener(new OnGestureLockListener(){

            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(String progress) {

            }

            @Override
            public void onComplete(String result) {
                String code=tv_code.getText().toString();
                if(TextUtils.isEmpty(result)){
                    return;
                }
                if(code.equals(result)){
                    Intent intent = new Intent(LockActivity.this, QuestionnaireActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(LockActivity.this,"unlock failed",Toast.LENGTH_SHORT).show();
                    gestureLockView.showErrorStatus(600);
                    String code1=listToString(exitCode());
                    tv_code.setText(code1);
                }
            }
        });

    }

    //  Mask return key
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
    }

    public ArrayList exitCode(){
        ArrayList<Integer> arrayList=new ArrayList<>();
        List<Integer> list = Arrays.asList(2, 4, 6,8);
        Random ra=new Random();
        int i=0;
        while(i<100){
            int temp=ra.nextInt(9);
            if(i==0) {
                arrayList.add(temp);
                i++;
                continue;
            }
            int length=arrayList.size();
            int pre=arrayList.get(length-1);
            int dv=Math.abs(temp-pre);
            if(length>=4)
                break;
            if(arrayList.contains(temp))
                continue;
            if(temp==4||pre==4) {
                arrayList.add(temp);
                continue;
            }
            if(list.contains(dv)){
                continue;
            }
            arrayList.add(temp);

            i++;
        }
        if(arrayList.size()<4){
            arrayList.clear();
            arrayList.add(1);
            arrayList.add(5);
            arrayList.add(8);
            arrayList.add(4);
        }
        return arrayList;
    }

    public String listToString(ArrayList<?> arrayList){
        StringBuffer sb=new StringBuffer();
        if (arrayList != null && arrayList.size() > 0) {
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i) == null || arrayList.get(i) == "") {
                    continue;
                }
                sb.append(arrayList.get(i));
            }
        }
        return sb.toString();
    }
}
