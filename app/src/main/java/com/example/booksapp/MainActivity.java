package com.example.booksapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
//import androidx.appcompat.widget.SearchView;
//import androidx.appcompat.widget.SearchView.OnQueryTextListener;

import android.widget.TextView;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
//activity_main represents activity_book_list from the tutorial
//MainActivity represent BookListActivity
/*
basic steps to note --
before creating recyclerview,
I created a new layout resource file --called book_list_item

*/

    //init progressbar
    private ProgressBar mLoadingProgress;
    //recyclerview
    private RecyclerView rvBooks;//assign it in the oncreate method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvBooks = (RecyclerView) findViewById(R.id.rv_books);
        //get the progressbar from the activity layout
        mLoadingProgress = (ProgressBar) findViewById(R.id.pb_loading);
        //do this after creating search activity
        //get the intent and the string
        Intent intent = getIntent();
        String query = intent.getStringExtra("Query");
        URL bookUrl;
        //create layoutmanager
        LinearLayoutManager booksLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        //set layoutmanager for the recyclerview
       rvBooks.setLayoutManager(booksLayoutManager);

        try {
            if(query == null || query.isEmpty()){
                //create url
                //if user made no input
                bookUrl = ApiUtil.buildUrl("cooking");
            }
            else{
                bookUrl = new URL(query);
            }
            // String JsonResult = ApiUtil.getJson(bookUrl);
            //tvResult.setText(JsonResult);  -- not needed
            new BooksQueryTask().execute(bookUrl);

        } catch (Exception e) {
            //e.printStackTrace();
            Log.d("error", e.getMessage());
        }//nect go to Book.java and check for books without cover image
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.book_list_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        //final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);  --  MenuItemCompat.getActionView has been deprecated
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);

        //bring in the queryitems from sharedpreferences -- sputils
        ArrayList<String> recentList = SPUtils.getQueryList(getApplicationContext());
        //get the sise
        int itemNum = recentList.size();
        //create a menu item
        MenuItem recentMenu;
        //add menu items
        for(int i =0; i<itemNum; i++){
            recentMenu = menu.add(Menu.NONE, i, Menu.NONE, recentList.get(i));
            //next, respond to the click --go to onoptionsitemselected
        }
        return true;
        //responde to user input
    }

    //here we integrate the search activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //check the value of the selected menu item
        //we'll have two cases
        switch(item.getItemId()){
            //case 1 -- advanced search
            case R.id.action_advanced_search:
                //launch search activity
                Intent intent = new Intent (this, SearchActivity.class);
                startActivity(intent);
                return true;
                //case two - default search-copy the code with the message and the parent class
            default:
                //get position
                int position = item.getItemId() + 1;
                String preferenceName = SPUtils.QUERY + String.valueOf(position);
                //get string that contiains the query
                String query = SPUtils.getPreferenceString(getApplicationContext(), preferenceName);


                //the split method will return more than 4 items
                String[] prefParams = query.split("\\,");
                String[] queryParams = new String[4];
                for(int i = 0; i<prefParams.length; i++){
                    queryParams[i] = prefParams[i];
                }
                //build url
                URL bookUrl = ApiUtil.buildUrl(
                        (queryParams[0] == null)?"":queryParams[0],
                        (queryParams[1] == null)?"":queryParams[1],
                        (queryParams[2] == null)?"":queryParams[2],
                        (queryParams[3] == null)?"":queryParams[3]
                );

                new BooksQueryTask().execute(bookUrl);
                return super.onOptionsItemSelected(item);
        }


    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        //runs when user searches for a book
        try{
            URL bookUrl = ApiUtil.buildUrl(query);
            new BooksQueryTask().execute(bookUrl);
        }catch (Exception e){
            Log.d("error", e.getMessage());
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    //this class extends AsyncTask class
    //it takes care of the long running task of calling the api
    //parameters AsyncTask<URL, Progress, Result>
    //URL - the long running task
    //Progress - type of progress unit published during the long running task - Void in this case, not needed
    //Result - type of result returned during the background operation
    public class BooksQueryTask extends AsyncTask<URL, Void, String>{

        //doinbackground method does not run in the main thread, so we have no access to the user interface
        //hence, we need to execute the onpostexecute method, will be called when doinbackgrund has completed
        @Override
        protected String doInBackground(URL... urls) {
            //for the params, you can pass zero or more url objects
            //the argument passed in this way, is always an array
            //get the first member of the array and pass it in a url that will call searchurl
            URL searchURL = urls[0];
            String result = null;
            try{
                result = ApiUtil.getJson(searchURL);
            }catch(IOException e){
                Log.d("Error", e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result){
            //super.onPostExecute(s);
            //super.onPostExecute(result);
            //copy and paste the code to be witten to the textview
           //replaced by recyclerview--- TextView tvResult = (TextView) findViewById(R.id.tvResponse);
            // init textview for error message
            TextView tvError = (TextView) findViewById(R.id.tv_error);
            //MAKE PROGRSSBAR INVISIBLE
            mLoadingProgress.setVisibility(View.INVISIBLE);
            try{
                //show error message if an error occurs
                //run app and put in airplane mode to test
                if(result == null){
                    //hide recycler view
                    rvBooks.setVisibility(View.INVISIBLE);
                    //show error
                    tvError.setVisibility(View.VISIBLE);
                }else{
                    //show recyclerview
                    rvBooks.setVisibility(View.VISIBLE);
                    //hide error view
                    tvError.setVisibility(View.INVISIBLE);

                    //tvResult.setText(result);
                    //create an arraylist to hold the books array to be returned from the api call
                    ArrayList<Book> books = ApiUtil.getBooksFromJson(result);
                    //create an empty string as a container for the result
                    String resultString = "";

                    //tvResult.setText(resultString); //next, use recyclerview to make the display better
                    //lastly, instead of setting the books  array to null, set it to an arraylist of Books -- go up in ApiUtils.java
                    //next, to call the asynctask, we need to instantiate the class and call the execute method
                    //do that in the onCreate function of the MainActivity class

                    //init BooksAdapter and bind data to recyclerview
                    BooksAdapter adapter = new BooksAdapter(books);
                    rvBooks.setAdapter(adapter);
                }


            }
            catch (Exception e){
                Log.d("error_occured", e.getMessage());
            }

        }

        //this method is called before the network call
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            //make the progress bar visible at this point
            mLoadingProgress.setVisibility(View.VISIBLE);//do reverse in onpostexecute
        }

    }
}
