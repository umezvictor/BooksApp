package com.example.booksapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BookViewHolder> {

    ArrayList<Book> books;
    public BooksAdapter(ArrayList<Book> books){
        this.books = books;
    }
    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       //called when recyclerview needs a new viewholder
        //here we need to inflate the rowlayout defined in book_list_item
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.book_list_item, parent, false);
        return new BookViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
    //called to display the data
        //get the book from the current position
        Book book = books.get(position);
        //bind it to the holder
        holder.bind(book);
    }

    @Override
    public int getItemCount() {
        //return the size of the book array list
        return books.size();
        //next, delete textview from activity_main.xml and addd a recycler view that will take all the space of the screen
        // write the data into the recyclerview through the adapter
    }
//after implementing parcelable, then come here and implement View.Onclisklistener
    public class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvTitle;
        TextView tvAuthors;
        TextView tvDate;
        TextView  tvPublisher;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvAuthors = (TextView) itemView.findViewById(R.id.tvAuthors);
            tvDate = (TextView) itemView.findViewById(R.id.tvPublishedDate);
            tvPublisher = (TextView) itemView.findViewById(R.id.tvPublisher);
            itemView.setOnClickListener(this); //set onclicklistener here after
        }

        //bind book data to textviews
        public void bind (Book book){
            //Adapter defines the view and binds data
            tvTitle.setText(book.title);
            //since authors is an array, define a string to hold them
            String authors = "";
            tvAuthors.setText(book.authors);
            tvDate.setText(book.publishedDate);
            tvPublisher.setText(book.publishers);

        }

    @Override
    public void onClick(View view) {
    //retrieve the position of the item that was selected
        int position = getAdapterPosition();
        //get the book user clicked on launch the book detail activity
        Book selectedBook = books.get(position);
        //launch book detail activity when a single book is clicked on
        Intent intent = new Intent(view.getContext(), BookDetailActivity.class);
        intent.putExtra("Book", selectedBook);
        view.getContext().startActivity(intent);
        //next go to oncreate method maincactivity.java and read the book from the bundle
        //when you get there-call getParcelable method of the intent
    }
}
}
