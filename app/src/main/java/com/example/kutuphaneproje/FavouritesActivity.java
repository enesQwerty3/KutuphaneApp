package com.example.kutuphaneproje;

import static android.view.KeyEvent.KEYCODE_BACK;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.layout.ParentDataModifier;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavouritesActivity extends AppCompatActivity {
    private RecyclerView favouritesRecyclerView;
    private KutuphaneDB appDB;
    List<FavouriteBook> favoriteBookList;
    private CustomAdapterFavouriteBook adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourites_layout);
        initComponents();
        setRecyclerView();
        registerEventHandlers();
    }

    private void initComponents() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //action bar'daki geri tuşunu başlatmak için
        favouritesRecyclerView = findViewById(R.id.favouritesRecyclerView);
        appDB = KutuphaneDB.get_kutuphaneDB(FavouritesActivity.this);
    }

    private void setRecyclerView(){
        int userId = appDB.getUserDAO().getSignedInUser().getUserID();
        favoriteBookList = appDB.getFavouriteBookDAO().listFavouriteBooks(userId);
        LinearLayoutManager llm = new LinearLayoutManager(FavouritesActivity.this);
        favouritesRecyclerView.setLayoutManager(llm);
        favouritesRecyclerView.setHasFixedSize(true);
        adapter = new CustomAdapterFavouriteBook(FavouritesActivity.this, favoriteBookList);
        favouritesRecyclerView.setAdapter(adapter);
        favouritesRecyclerView.addItemDecoration(new DividerItemDecoration(FavouritesActivity.this, LinearLayoutManager.VERTICAL));
    }

    private void registerEventHandlers(){
    }

    @Override                   //action bar'daki geri butonunu işlevini durdurup intent ile geri dönmek için. Manifest'den dönüş acticity de kaldırılmalı
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:{
                if(adapter.isItemRemoved())
                    gotoHomepageActivity("refresh");
                else
                    gotoHomepageActivity(null);
                return true; //exit and return true
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode){
            case KEYCODE_BACK:{
                if(adapter.isItemRemoved())
                    gotoHomepageActivity("refresh");
                else
                    gotoHomepageActivity(null);
                return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    private void gotoHomepageActivity(String refreshAdapter)
    {
        Intent intent = new Intent(FavouritesActivity.this, HomepageActivity.class);
        //homepage activity'e dönüşte üstündeki activity'leri temizle en üstteyse single top ile tekrar onCreate'nin çalışmasını engelle
        if (refreshAdapter != null)
            intent.putExtra("refreshAdapter", refreshAdapter);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);  //return to homepage
    }
}