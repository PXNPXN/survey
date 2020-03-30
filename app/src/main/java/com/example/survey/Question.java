package com.example.survey;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Question {
    private String type;
    private String question;
    private String[] options;
    private String[] answers;

    Question(JSONObject jsob, boolean isQuestion) throws JSONException {
        //load type, question. options
        if(isQuestion) {
            type = jsob.getString("type");
            question = jsob.getString("question");
            if(!type.equals("fill-in")) {
                JSONArray array = jsob.getJSONArray("options");
                int size = array.length();
                options = new String[size];
                for(int i=0; i<size; i++)
                    options[i] = array.getJSONObject(i).getString(Integer.toString(i+1));
            }
        }
        //loading question, answer
        else {
            question = jsob.getString("question");
            JSONArray array = jsob.getJSONArray("answers");
            int size = array.length();
            answers = new String[size];
            for(int i=0; i<size; i++)
                answers[i] = array.getString(i);
        }
    }

    public JSONObject getResult() throws JSONException {
        if(answers == null)
            return null;
        JSONObject ret = new JSONObject();
        JSONArray ans = new JSONArray();

        ret.put("question",question);
        for(int i=0; i<answers.length; i++) {
            JSONObject object = new JSONObject();
            object.put(Integer.toString(i+1), answers[i]);
            ans.put(i, answers[i]);
        }
        ret.put("answers", ans);
        Log.i("info",ret.toString());

        return ret;
    }

    public String getQuestion() {
        return question;
    }

    public String getType() {
        return type;
    }

    public void setAnswer(String[] answer) {
        this.answers = answer;
    }

    public String[] getAnswers() {
        return answers;
    }

    public String[] getOptions() {
        return options;
    }
}
