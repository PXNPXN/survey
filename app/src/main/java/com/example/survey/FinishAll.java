package com.example.survey;

import android.app.Activity;

import java.util.LinkedList;
import java.util.List;

public class FinishAll {
    public static List<Activity> activityList = new LinkedList();
    public static void exit(){
        for(Activity activity:activityList){
            activity.finish();
        }
        System.exit(0);

    }

}
