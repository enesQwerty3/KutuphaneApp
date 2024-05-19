package com.example.kutuphaneproje;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface IUserDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void insertUser(User user);
    @Update
    public void updateUser(User user);
    @Delete
    public void deleteUser(User user);
    @Query("SELECT * FROM users WHERE username = :userName")
    public User getUserByUsername(String userName);
    @Query("SELECT * FROM users")
    public List<User> listAllUsers();
    @Query("SELECT * FROM users WHERE signed_in = true")
    public User getSignedInUser();
    @Query("UPDATE users SET signed_in = :isSignedIn where id =:userId ")
    public void setSignedInUser(int userId , Boolean isSignedIn);
}
