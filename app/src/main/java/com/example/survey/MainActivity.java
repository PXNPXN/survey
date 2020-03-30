package com.example.survey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FinishAll.activityList.add(MainActivity.this);
        result =findViewById(R.id.textView4);
    }

    public void goToSurvey(android.view.View V) {
        CheckBox cb_accept = findViewById(R.id.accept);
        if(!cb_accept.isChecked())
            return;
        Intent intent = new Intent(this, question_page.class);
        Intent i=getIntent();
        Bundle ID =i.getExtras();
        String surveyID=ID.getString("id");
        intent.putExtra("id",surveyID);
        startActivity(intent);
    }

}
