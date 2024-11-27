package com.example.kutuphaneproje;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface IFavouriteBookDAO{
    @Insert
    public void insertBook(FavouriteBook book);
    @Update
    public void updateBook(FavouriteBook book);
    @Delete
    public void deleteBook(FavouriteBook book);
    @Query("SELECT * FROM FAVOURITE_BOOKS WHERE user_id=:userId")
    public List<FavouriteBook> listFavouriteBooks(int userId);
    @Query("DELETE FROM FAVOURITE_BOOKS WHERE id=:id")
    public void deleteFavouriteBook(int id);
    @Query("SELECT * FROM FAVOURITE_BOOKS WHERE isbn=:isbn AND user_id=:userId")
    public FavouriteBook getFavouriteBook(String isbn, int userId);
    @Query("SELECT * FROM FAVOURITE_BOOKS WHERE book_id=:book_id AND user_id=:userId")
    public FavouriteBook getFavouriteBook2(String book_id, int userId);
}
