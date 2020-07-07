package com.example.booksapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URL;

public class SearchActivity extends AppCompatActivity {
//steps for creating advanced search feature
   /*
   * 1. create an emty activity
   * 2. go to the actiivty layoit file and change the root element to LinearLayout
   * 3 design the view
   * go to book_list_menu and add it as a menu in the xml
   * go to main activity and
   *  */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //retrieve all the textfields for entering advanced search
        //can also be retrieved using the databinding library
        final EditText etTitle = (EditText) findViewById(R.id.etTitle);
        final EditText etAuthor = (EditText) findViewById(R.id.etAuthor);
        final EditText etPublisher = (EditText) findViewById(R.id.etPublisher);
        final EditText etIsbn = (EditText) findViewById(R.id.etIsbn);
        final Button button = (Button) findViewById(R.id.btnSearch);

        //listen to onclick event
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //read content of our views and put then in a string
                String title = etTitle.getText().toString().trim();
                String author = etAuthor.getText().toString().trim();
                String publisher = etPublisher.getText().toString().trim();
                String isbn = etIsbn.getText().toString().trim();

                //check if input is empty
                if(title.isEmpty() && author.isEmpty() && publisher.isEmpty() && isbn.isEmpty()){
                    String message = getString(R.string.no_search_term);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
                else{
                    //if at least one string is not empty
                    URL queryURL = ApiUtil.buildUrl(title, author, publisher, isbn);
                    //shared preferences
                    Context context = getApplicationContext();
                    //pull the position-will be 0 at first
                    int position = SPUtils.getPreferenceInt(context, SPUtils.POSITION);
                    //nb, user has max of 5 slots
                    //update position based on condition
                    if(position == 0 || position == 5){
                        position = 1;
                    }else{
                        position++;
                    }
                    //now we know the position to update --create string containing the key
                    String key = SPUtils.QUERY + String.valueOf(position);
                    String value = title + "," + author + "," + publisher + "," + isbn;
                    SPUtils.setPreferenceString(context, key, value);
                    SPUtils.setPreferenceInt(context, SPUtils.POSITION, position);
                    //create an intent to get to the book list activity
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    //pass the queryUrl as an extra
                    intent.putExtra("Query", queryURL);
                    startActivity(intent); //next go the oncreate method mainactivity, get the intent and the string
                }
            }
        });
    }
}