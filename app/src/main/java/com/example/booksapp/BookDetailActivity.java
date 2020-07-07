package com.example.booksapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.example.booksapp.databinding.ActivityBookDetailBinding;

public class BookDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        //read parcel from bundle
        //from here, the book details will be held in the book object
        Book book = getIntent().getParcelableExtra("Book");
        //next, pass the data to the ui using Databainding

        /*
        Enable databinding --do this in gradle configuration
        Create view model  -- use Book class
        update the layout files
        activate the binding in the activity

        data binding is a better way to bind data than findviewbyid
        when you enable data bindng ensure you use layout as root tag in your activity
        followed  by a data element
        */

        //final step to use data binding is to activate it in the activity
        //that's what we'll do here
        //android studio creates this "ActivityBookDetailBinding' for us after enabling data binding in layout file
        //layoutfileconverted to pascal case plus the binding suffix
        ActivityBookDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_book_detail);
        //pass current book to this object
        binding.setBook(book);
    }
}