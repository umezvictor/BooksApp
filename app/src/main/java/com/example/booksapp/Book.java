package com.example.booksapp;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.squareup.picasso.Picasso;

//this is the model class
public class Book implements Parcelable {
    public String id;
    public String title;
    public String subTitle;
    //public String[] authors;
    public String authors;
    public String publishers;
    public String publishedDate;
    public String description;
    public  String thumbnail; //will hold the image

    //create constructor
    public Book(String id, String title, String subTitle,
                String[] authors, String publishers, String publishedDate, String thumbnail, String description) {
        this.id = id;
        this.title = title;
        this.subTitle = subTitle;
        this.authors = TextUtils.join(", ", authors);
        this.publishers = publishers;
        this.publishedDate = publishedDate;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    protected Book(Parcel in) {
        id = in.readString();
        title = in.readString();
        subTitle = in.readString();
        authors = in.readString();
        publishers = in.readString();
        publishedDate = in.readString();
        description = in.readString();
        thumbnail = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(subTitle);
        dest.writeString(authors);
        dest.writeString(publishers);
        dest.writeString(publishedDate);
        dest.writeString(description);
        dest.writeString(thumbnail);
    }

    //binding adapter -- using picaso
    @BindingAdapter({"android:imageUrl"})
    public static void loadImage(ImageView view, String imageUrl){
        //load images using picasso library-- ensure you add the dependency to gradle file
        //use picasso if imageurl is present in api response
        if(!imageUrl.isEmpty()){
            Picasso.with(view.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.book_open)//use this as placeholder for image to be returned from json url
                    .into(view);
            //next- use imageUrl attribute to pass thumbnail member in activity-book-detail.xml
        }else{
            //if image url is empty use local
            view.setBackgroundResource(R.drawable.book_open);
            //next, handle the case of null being returned from the google books api  --go to apiutils in the book object
        }
         }
}
