package com.example.kutuphaneproje;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User{
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private int userID;
    private String username;
    private String password;
    @ColumnInfo(name="signed_in")
    private Boolean signedIn;

    public User(int userID, String username, String password, Boolean signedIn){
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.signedIn = signedIn;
    }
    @Ignore
    public User(){

    }

    public int getUserID() {
        return userID;
    }
    public void setUserID(int userID){
        this.userID = userID;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getSignedIn() {
        return signedIn;
    }

    public void setSignedIn(Boolean signedIn) {
        this.signedIn = signedIn;
    }
}
