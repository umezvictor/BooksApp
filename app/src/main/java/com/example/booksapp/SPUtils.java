package com.example.booksapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class SPUtils {
    //this class holds shared preferences --persists data --not like a database, just simple data
    //helps us save latest user searches in advanced search
    //nb, there will never be more than 5 queries in the shared prefereces at any point
    private SPUtils(){}
    public static final String PREF_NAME = "BooksPreferences";

    //we need a way to track user input sicne there are just 5 slots available
    public static final String POSITION = "position";
    public static final String QUERY = "query";//next go to search activity
    //init sharedPreferences
    //only the method creating the pref can read the preferencesm
    public static SharedPreferences getPref(Context context){
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    //get the string
    public  static  String getPreferenceString(Context context, String key){
        return getPref(context).getString(key, ""); //"" = default value

    }

    //get int methid
    //get the string
    public  static  int getPreferenceInt(Context context, String key){
        return getPref(context).getInt(key,0);

    }

    //wrtite string
    public  static  void setPreferenceString(Context context, String key, String value){
        //retrieve editor object
        SharedPreferences.Editor editor = getPref(context).edit();
        //add string
        editor.putString(key, value);
        editor.apply();//commit changes
    }

    //wrtite int
    public  static  void setPreferenceInt(Context context, String key, int value){
        //retrieve editor object
        SharedPreferences.Editor editor = getPref(context).edit();
        //add string
        editor.putInt(key, value);
        editor.apply();//commit changes
    }

    //retrieve all the queries in the shared preferences

    public static ArrayList<String> getQueryList(Context context){
        ArrayList<String> queryList = new ArrayList<String>();
        for(int i = 1; i <= 5; i++){
            //remeber sharedpreferences not > 5 slots
            String query = getPref(context).getString(QUERY + String.valueOf(i), "");
            if(!query.isEmpty()){
                //add the query items to the menu, remove comas

                query = query.replace(",", " ");
                queryList.add(query.trim());
            }
        }
        return queryList;//next  go to mainactivity, make the query items appear as menu items  -- do that in oncreateoptions menu
    }
}
