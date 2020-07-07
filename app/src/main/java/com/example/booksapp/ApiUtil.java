package com.example.booksapp;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class ApiUtil {
    //this class helps interact with the google books api
    //https://www.googleapis.com/books/v1/volumes/?q=android
    //this class will not be instantiated
    // https://www.googleapis.com/books/v1/volumes?q=cooking&key=AIzaSyCVJQvM-E9kHktDihI8W32i-m7ux1burFg
    private ApiUtil(){}
    //base url
    public static final String BASE_API_URL =
            "https://www.googleapis.com/books/v1/volumes";
    public static final String  QUERY_PARAMETER_KEY = "q";
    public static final String KEY = "key";
    //API KEY from google  https://console.developers.google.com/apis/credentials?folder=&organizationId=&project=dispatch-235115
    public static final String API_KEY = "AIzaSyCVJQvM-E9kHktDihI8W32i-m7ux1burFg";

    //fields for advanced search --constants to hold the query parameters expected by the api
    public static final String TITLE = "intitle";
    public static final String AUTHOR = "author";
    public static final String PUBLISHER = "inpublisher";
    public static final String ISBN = "isbn";

    //METHOD TO BUILD URL
    public static URL buildUrl(String title){
       // String fullUrl = BASE_API_URL + "?q=" + title; -- time to use best practices
        //use uri to build uri, its better
        URL url = null;
        Uri uri = Uri.parse(BASE_API_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAMETER_KEY, title)
                .appendQueryParameter(KEY, API_KEY)
                .build();
        try{
           // url = new URL(fullUrl);
            //convert the uri to url
            url = new URL(uri.toString());
            //lower throttling == more
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return url;
    }

    //handle advanced search
    //builds url using the data entered by the user in advance search form
    public static URL buildUrl(String title, String author, String publisher, String isbn){
        //build query string
        URL url = null;
        StringBuilder sb = new StringBuilder();
        if(!title.isEmpty()) sb.append(TITLE + title + "+");
        if(!author.isEmpty()) sb.append(AUTHOR + author + "+");
        if(!publisher.isEmpty()) sb.append(PUBLISHER + publisher + "+");
        if(!isbn.isEmpty()) sb.append(ISBN + isbn + "+");
        //remove the plus sign at the end of the query
        sb.setLength(sb.length() -1);
        //string builder is ready - convert it to string
        String query = sb.toString();
        Uri uri = Uri.parse(BASE_API_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAMETER_KEY, query)
                .appendQueryParameter(KEY, API_KEY)
                .build();
        //build a url from the uri
        try{
            url = new URL(uri.toString());
        }catch(Exception e){
            e.printStackTrace();
        }
        return url; //next call this method in the advanced search activity -- in the oncreate method
        //retrieve edit text with the databinding library

    }
    //next - connect to the api
    public static String getJson(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try{
            //create input stream to read data
            InputStream stream = connection.getInputStream();
            //convert stream to string
            Scanner scanner = new Scanner(stream);
            scanner.useDelimiter("\\A");//regex
            //check whther data exists
            boolean hasData = scanner.hasNext();
            if(hasData){
                return scanner.next();
            }else{
                return null;
            }
        }
        catch (Exception e){
            Log.d("Error", e.toString());
            return null;
        }
        finally{
            connection.disconnect();
        }
        //next- call this method from activity and display
    }

    //method to return array list of books from json
    public static ArrayList<Book> getBooksFromJson(String json){
    //note, this constants represent the properties in the json
        /*
        eg victor {
        age: 23,
        sex: male

        }
        final int PERSON = "victor"
        final String SEX = "sex"
         */
        final String ID = "id";
        final String TITLE = "title";
        final String SUBTITLE = "subtitle";
        final String AUTHORS = "authors";
        final String PUBLISHER = "publisher";
        final String PUBLISHED_DATE = "publishedDate";
        final String ITEMS = "items";
        final String VOLUMEINFO = "volumeInfo";
        final String DESCRIPTION = "description";
        final String IMAGELINKS = "imageLinks";
        final String THUMBNAIL = "thumbnail";

       // ArrayList<Book> books = null;
        ArrayList<Book> books = new ArrayList<Book>();
        //parse json
        try{
            //crate json object from json string
            JSONObject jsonBooks = new JSONObject(json);
            //json will be returned
            //get array that has all the books
            JSONArray arrayBooks = jsonBooks.getJSONArray(ITEMS);
            //we need to know how  were retrieved
            int numberOfBooks = arrayBooks.length();
            //loop through books
            for(int i = 0; i < numberOfBooks; i++){
                //create a single json object
                JSONObject bookJson = arrayBooks.getJSONObject(i);
                //get volumeinfo
                JSONObject volumeInfoJSON = bookJson.getJSONObject(VOLUMEINFO);
                //get image
                JSONObject imageLinkJson = null;
                if(volumeInfoJSON.has(IMAGELINKS)){
                    imageLinkJson = volumeInfoJSON.getJSONObject(IMAGELINKS);
                }

                //get authors.  NOTE -- authors is an array
                int authorNum;
                try{
                    //get number of authors
                    authorNum = volumeInfoJSON.getJSONArray(AUTHORS).length();
                }catch (Exception ex){
                    authorNum = 0;
                }

                //create string array to hold authors
                String[] authors = new String[authorNum];

                for(int j = 0; j < authorNum; j++){
                    authors[j] = volumeInfoJSON.getJSONArray(AUTHORS).get(j).toString();
                }
                //create a new book by retrieving all the data needed
                Book book = new Book(
                        bookJson.getString(ID),
                        volumeInfoJSON.getString(TITLE),
                        //SUBTITLE may not be available, so use ternary operator to check
                        (volumeInfoJSON.isNull(SUBTITLE)?"":volumeInfoJSON.getString(SUBTITLE)),
                        authors,
                        (volumeInfoJSON.isNull(PUBLISHER)?"":volumeInfoJSON.getString(PUBLISHER)),
                        (volumeInfoJSON.isNull(PUBLISHED_DATE)?"":volumeInfoJSON.getString(PUBLISHED_DATE)),
                        (volumeInfoJSON.isNull(DESCRIPTION)?"":volumeInfoJSON.getString(DESCRIPTION)),
                        (imageLinkJson==null)?"": imageLinkJson.getString(THUMBNAIL)
                        );
                //next go to the Book class and create the bnding adapter
                //add new book to the booklist array
                books.add(book);
                //next-- write into the textview, data retrieved from the json
                //go to the onpostexecute method --in mainactivity.java
                //create array that will hold authors
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return books;
        //it's best practice in android to use constants for the strings to be retrieved from the json string


    }
}
