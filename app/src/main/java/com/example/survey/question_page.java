package com.example.survey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static android.view.View.GONE;

public class
question_page extends AppCompatActivity {
    private Question[] questions;
    String id;
    int nowQuestion;
    private Bundle ID;
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_page);
        FinishAll.activityList.add(question_page.this);
        init();
        String questionJson=null;//DANGER
        cursor.moveToFirst();
        questionJson=cursor.getString(cursor.getColumnIndex("surveyJsonString"));
        Log.i("SQL","try get questions:"+cursor.getString(cursor.getColumnIndex("surveyJsonString")));
        while(cursor.moveToNext()){
            questionJson=cursor.getString(cursor.getColumnIndex("surveyJsonString"));
            Log.i("SQL","get data from DB:"+ questionJson);
        }
        loadQuestion(questionJson);
        nowQuestion = 0;
        showQuestion(nowQuestion);
    }

    public void init(){
        Intent i=getIntent();
        ID =i.getExtras();

        dbHelper=new DBHelper(this,"Survey.db",null,1);
        db=dbHelper.getReadableDatabase();
        String surveyID=ID.getString("id");


        String sql="SELECT* FROM survey WHERE surveyID="+"'"+surveyID+"'";

        cursor=db.rawQuery(sql,new String[]{});
        Log.i("SQL","Questionire recodes number:"+ cursor.getCount());
        Log.i("SQL","target id: "+String.valueOf(surveyID));
    }

    private void loadQuestion(String json) {
        try {
            Log.i("JSON",json);
            //read from "question.JSON"
            JSONObject jsonObject = new JSONObject(json);
            jsonObject = jsonObject.getJSONObject("survey");

            id = jsonObject.getString("id");
            int size = jsonObject.getInt("len");
            questions = new Question[size];
            JSONArray questionArray = jsonObject.getJSONArray("questions");
            for(int i=0; i<size; i++)
                questions[i] = new Question(questionArray.getJSONObject(i), true);

        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    private void showQuestion(int i) {
        Question now = questions[i];
        //if(true) return;
        ((TextView)findViewById(R.id.question_number)).setText("Question "+ (i+1));
        ((TextView)findViewById(R.id.question)).setText(now.getQuestion());
        if(i == questions.length-1)
            ((Button)findViewById(R.id.next)).setText("FINISH");

        EditText editText = findViewById(R.id.editText);
        RadioGroup radioGroup = findViewById(R.id.rd);

        //clear history
        if(now.getType().equals("fill-in")) {
            radioGroup.setVisibility(GONE);
            editText.setText("");
            editText.setVisibility(View.VISIBLE);
            return;
        }
        radioGroup.setVisibility(View.VISIBLE);
        editText.setVisibility(GONE);
        radioGroup.removeAllViews();

        //add options
        String options[] = now.getOptions();

        //add RadioButton when type is "single"
        if(now.getType().equals("single")) {
            for(int j=0; j<options.length; j++) {
                RadioButton rb = new RadioButton(this);
                rb.setText(options[j]);
                rb.setTextSize(18);
                radioGroup.addView(rb);
            }
            return;
        }

        //add RadioButton when type is "multiple"
        for(int j=0; j<options.length; j++) {
            CheckBox cb = new CheckBox(this);
            cb.setText(options[j]);
            cb.setTextSize(18);
            radioGroup.addView(cb);
        }
    }

    public void next(View v) {
        //record answer
        Question now = questions[nowQuestion];
        EditText editText = findViewById(R.id.editText);
        RadioGroup radioGroup = findViewById(R.id.rd);
        if(now.getType().equals("fill-in")) {
            String ans = editText.getText().toString();
            if(ans.length() == 0) {
                Toast.makeText(this,"Answer should not be empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            now.setAnswer(new String[] {ans});
        }
        else if(now.getType().equals("single")) {
            int id = radioGroup.getCheckedRadioButtonId();
            if(id == -1) {
                Toast.makeText(this,"Answer should not be empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            now.setAnswer(new String[] { ((RadioButton)findViewById(id)).getText().toString()});
        }
        else {
            ArrayList<String> ans = new ArrayList<String>();
            int size = radioGroup.getChildCount();
            for(int i=0; i<size; i++) {
                CheckBox cb = (CheckBox) radioGroup.getChildAt(i);
                if(cb.isChecked())
                    ans.add(cb.getText().toString());
            }
            if(ans.size() == 0) {
                Toast.makeText(this,"Answer should not be empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            String[] ans_str = new String[ans.size()];
            for(int i=0; i<ans.size(); i++)
                ans_str[i] = ans.get(i);
            now.setAnswer(ans_str);
        }

        //go to next question
        nowQuestion++;
        if(nowQuestion != questions.length) {
            showQuestion(nowQuestion);
            return;
        }

        //go to report
        try {
            Intent intent = new Intent(this, report.class);
            intent.putExtra("len", questions.length);
            for(int i=0; i<questions.length; i++) {
                JSONObject result = questions[i].getResult();
                intent.putExtra("Question"+i, result.toString());
            }
            startActivity(intent);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
