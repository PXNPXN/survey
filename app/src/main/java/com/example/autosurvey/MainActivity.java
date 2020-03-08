package com.example.autosurvey;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.OptionalDouble;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener {
    private Button mButton1;
    private TextView mTextView1;
    private TextView mTextView2;
    private RadioGroup mGroup;
    private Button mButton;
    private CheckBox []mCheckBoxes;
    private int length;
    private StringBuffer stringBuffer;
    private Gson gson;
    private String[]  qusetions;
    private String[] options;
    private String[] types;
    private String[] temp;
    private String[] MultiOptions;
    private int num=0;
    private RelativeLayout layout;
    private  RelativeLayout.LayoutParams params =null;
    private  Bundle[] Data;
    private Bundle[]QuestionData;
    private Bundle Test;
    private Bundle QuestionNum;
    private String data=null;
    private CheckBox mCheckBox;
    private Boolean Rules=false;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        init();
    }

    @SuppressLint("WrongViewCast")
    private void init() {
        /**
         *  getID and initialize
         */
        mButton=(Button)findViewById(R.id.bt_welcome);
        mCheckBox=(CheckBox)findViewById(R.id.cb_welcome);
        mCheckBox.setOnCheckedChangeListener(new Rules());
        mButton.setOnClickListener(this);
        getJson();
        Data=new Bundle[length];
        QuestionNum=new Bundle();
        QuestionData=new Bundle[length];
        for(int i=0;i<length;i++){
            try{
                Data[i]=new Bundle();
                QuestionData[i]=new Bundle();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }




    private class Rules implements CompoundButton.OnCheckedChangeListener {
        /**
         *  Check the rules in welcome-layout
         * @param buttonView
         * @param isChecked
         */
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(mCheckBox.isChecked()){
                Rules=true;
                Toast.makeText(MainActivity.this,"已同意服从条例！",Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onClick(View v) {
        /**
         * Button Listener in welcome layout
         */
        switch (v.getId()) {
            case R.id.bt_welcome:
                if(Rules==true){
                    CreateLayout();
                }
                else{
                    Toast.makeText(MainActivity.this,"请勾选服从条例！",Toast.LENGTH_LONG).show();
                }
                break;
            default:
        }
    }

    public void skip(View v){
        /*
        * skipping Report activity
        */
        Intent intent=new Intent(MainActivity.this,Report.class);
        QuestionNum.putInt("length",length);
        for(int i=0;i<length;i++){
            intent.putExtras(Data[i]);
            intent.putExtras(QuestionData[i]);
        }
        intent.putExtras(QuestionNum);
        startActivity(intent);
    }


    public void getJson() {
        /**
         *  Get json file from local
         */
        stringBuffer = new StringBuffer();
        try {
            InputStream is = MainActivity.this.getClass().getClassLoader().
                    getResourceAsStream("assets/" + "sample.json");
            InputStreamReader streamReader = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(streamReader);
            String line;
            while ((line = reader.readLine()) != null) {
                //stringBuilder.append(line);
                stringBuffer.append(line);
            }
            reader.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
         GsonJson(stringBuffer);
    }


    public void GsonJson(StringBuffer stringBuffer){
        /**
         * Parse the json file in Gson
         */
        gson=new Gson();
        surveyBean bean=gson.fromJson(stringBuffer.toString(),surveyBean.class);
        length=bean.getSurvey().getLen();
        List<surveyBean.SurveyBean.QuestionsBean> objects=bean.getSurvey().getQuestions();
        qusetions=new String[length];
        options=new String[length];
        types=new String[length];
        int i=0;
        for(surveyBean.SurveyBean.QuestionsBean object:objects){
                qusetions[i]=object.getQuestion();
                options[i]=object.getOptions();
                types[i]=object.getType();
                i++;
        }
    }


    @SuppressLint("ResourceType")
    public void CreateLayout(){
        /**
         * Create mast Layout
         */
        if(num<length){
            String type=types[num].substring(0,types[num].length());
            layout= new  RelativeLayout(this);
            if(type.equals("single")){
                num++;
                CreatTextViewHead();
                CreatTextViewQuestion();
                CreatRadioButtonOptions();
                CreatButton();
                setContentView(layout);
            }
            else{
                num++;
                CreatTextViewHead();
                CreatTextViewQuestion();
                CreatCheckBoxOptions();
                CreatButton();
                setContentView(layout);
            }

        }
        else{
            ButtonEventNext();
            setContentView(R.layout.fininsh_survey);
        }
    }

    private void CreatCheckBoxOptions() {
        /**
         * Multi options :Choose Create CheckBox
         */
        params=new RelativeLayout.LayoutParams(1000,1000);
        temp=options[num-1].substring(1,options[num-1].length()-1).split(",");
        mCheckBoxes=new CheckBox[temp.length];
        int top;
        MultiOptions=new String[temp.length];
        for(int i=0;i<temp.length;i++){
            mCheckBoxes[i]=new CheckBox(this);
            mCheckBoxes[i].setText(temp[i]);
            mCheckBoxes[i].setId(View.generateViewId());
            params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            if(i==0){
                params.setMargins(200,550,0,0);
            }
            else{
                top=550+80*i;
                params.setMargins(200,top,0,0);
            }
           layout.addView(mCheckBoxes[i],params);
        }
        CheckBoxEvent();
    }

    private void CheckBoxEvent() {
        for(int i=0;i<temp.length;i++){
            mCheckBoxes[i].setOnCheckedChangeListener(this);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
         for (int i=0;i<temp.length;i++){
             if(mCheckBoxes[i].isChecked()){
                 MultiOptions[i]=mCheckBoxes[i].getText().toString();
             }else{
                 MultiOptions[i]=null;
             }
         }
    }


    @SuppressLint("ResourceType")
    public void CreatTextViewHead(){
        /**
         * Create Headline
         */
        mTextView1=new TextView(this);
        String questionNum="Question "+num;
        mTextView1.setText(questionNum);
        mTextView1.setTextSize(40);
        mTextView1.getPaint().setFakeBoldText(true);
        mTextView1.setTextColor(0xff000033);
        params=new RelativeLayout.LayoutParams(1000,120);
        params.setMargins(40,20,0,0);
        layout.addView(mTextView1,params);
    }

    @SuppressLint("ResourceType")
    public void CreatTextViewQuestion(){
        /**
         * Create questions TextView
         */
        mTextView2=new TextView(this);
        mTextView2.setText(qusetions[num-1]);
        QuestionData[num-1].putString("question"+String.valueOf(num),qusetions[num-1]);
        mTextView2.setTextSize(20);
        mTextView2.setTextColor(0xff000033);
        params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(100,260,20,0);
        layout.addView(mTextView2,params);
    }

    @SuppressLint("ResourceType")
    public void CreatRadioButtonOptions(){
        /**
         * Create RadioButton options
         */
        params=new RelativeLayout.LayoutParams(1000,1000);
        mGroup=new RadioGroup(this);
        params.setMargins(200,550,0,0);
        layout.addView(mGroup,params);
        temp=options[num-1].substring(1,options[num-1].length()-1).split(",");
        for(int i=0;i<temp.length;i++){
            RadioButton mRadioButton=new RadioButton(this);
            mRadioButton.setId(i);
            mRadioButton.setText(temp[i]);
            RadioGroup.LayoutParams layoutParams=new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,10,0,0);
            mGroup.addView(mRadioButton,layoutParams);
            RadioGroupEvent();
        }
    }

    public void RadioGroupEvent(){
        /**
         * set RadioGroup event
         */
        mGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton tempButton = (RadioButton)findViewById(checkedId);
        data=tempButton.getText().toString();
    }


    @SuppressLint("ResourceType")
    public void CreatButton(){
        /**
         * Create the name of "NEXT" Button
         */
        mButton1=new Button(this);
        mButton1.setText("NEXT");
        mButton1.setBackgroundColor(0xff008577);
        params=new RelativeLayout.LayoutParams(971,RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.setMargins(52,0,0,79);
        layout.addView(mButton1,params);
        ButtonEventNext();
    }

    public void ButtonEventNext(){
        /**
         * "NEXT" Button event
         */
        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type=types[num-1].substring(0,types[num-1].length());
                String Multi="";
                if(type.equals("single")){
                    if(data==null){
                        Toast.makeText(MainActivity.this,"请选择本题答案！",Toast.LENGTH_LONG).show();
                    }
                    else{
                        Data[num-1].putString(String.valueOf(num),data);
                        CreateLayout();
                        data=null;
                    }
                }
                else{
                    int j=0;
                    for(int i=0;i<temp.length;i++){
                        if(MultiOptions[i]==null){
                            j++;
                        }
                        else{
                            Multi=Multi+MultiOptions[i];
                        }

                    }
                    if(j==temp.length){
                        Toast.makeText(MainActivity.this,"请选择本题答案！",Toast.LENGTH_LONG).show();
                    }
                    else{
                        Data[num-1].putString(String.valueOf(num),Multi);
                        //for(int i=0;i<temp.length)
                        CreateLayout();
                    }
                }


            }
        });
    }


}
