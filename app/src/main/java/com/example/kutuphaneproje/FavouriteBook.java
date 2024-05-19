package com.example.kutuphaneproje;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "favourite_books",
        foreignKeys = {
        @ForeignKey(
                entity = User.class, parentColumns = "id", childColumns = "user_id", onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
        )
})
public class FavouriteBook{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int favourite_id;
    @ColumnInfo(name = "book_name")
    private String bookName;
    @ColumnInfo(name = "author_name")
    private String authorName;
    @ColumnInfo(name = "user_id")
    private int userID;
    private String isbn;
    @ColumnInfo(name = "book_id")
    private String book_id;
    public FavouriteBook(int favourite_id, String bookName, String authorName, String isbn, int userID, String book_id){
        this.favourite_id = favourite_id;
        this.bookName = bookName;
        this.authorName = authorName;
        this.isbn = isbn;
        this.userID = userID;
        this.book_id = book_id;
    }
    @Ignore
    public FavouriteBook(){ //to prevent null pointer exception

    }

    public String getBookName(){
        return bookName;
    }

    public void setBookName(String bookName){
        this.bookName = bookName;
    }

    public String getAuthorName(){
        return authorName;
    }

    public void setAuthorName(String authorName){
        this.authorName = authorName;
    }

    public int getUserID(){
        return userID;
    }

    public void setUserID(int userID){
        this.userID = userID;
    }

    public int getFavourite_id(){
        return this.favourite_id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getBook_id() {
        return book_id;
    }

    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }
}
