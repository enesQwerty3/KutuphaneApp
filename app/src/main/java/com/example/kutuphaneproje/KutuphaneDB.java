package com.example.kutuphaneproje;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {User.class, FavouriteBook.class}, version = 2, exportSchema = true) //new version 2
public abstract class KutuphaneDB extends RoomDatabase{
    public abstract IUserDAO getUserDAO();
    public abstract IFavouriteBookDAO getFavouriteBookDAO();
    private static final String databaseName = "kutuphaneAppDB.db";
    private static KutuphaneDB _kutuphaneDB;

    static final Migration MIGRATION1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase supportSQLiteDatabase) {
            //
            supportSQLiteDatabase.execSQL("ALTER TABLE favourite_books ADD COLUMN book_id TEXT");
        }
    };

    public static KutuphaneDB get_kutuphaneDB(Context context){ //singleton pattern
        if(_kutuphaneDB == null){
            _kutuphaneDB =
                    Room.databaseBuilder(context, KutuphaneDB.class, databaseName)
                        .allowMainThreadQueries()
                        .addMigrations(MIGRATION1_2)
                        .build();
        }
        return _kutuphaneDB;
    }

    public static void destroy_kutuphaneDB(){
        _kutuphaneDB = null;
    }
}