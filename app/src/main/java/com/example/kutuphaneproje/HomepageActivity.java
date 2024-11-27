package com.example.kutuphaneproje;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kutuphaneproje.api.Books;
import com.example.kutuphaneproje.api.BookApiService;
import com.example.kutuphaneproje.api.Item;
import com.example.kutuphaneproje.api.RetrofitInstance;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomepageActivity extends AppCompatActivity {
    private FloatingActionMenu fabMenu;
    private FloatingActionButton favouritesButton, signOutButton;
    private Spinner searchTypeSpinner;
    private String selectedSearchType;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private CustomAdapter adapter;
    private KutuphaneDB appDB;
    private List<Item> itemList;
    private int currentPage, pageSize;
    private TextView pageNumber;
    private ImageButton nextButton, previousButton;
    private Boolean avaliableForSearch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_layout);
        setTitle(getUserNameFromIntent());
        initComponents();
        registerEventHandlers();
    }

    private String getUserNameFromIntent() {  //wow!
        return getIntent() == null ? null : getIntent().getStringExtra("username");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        if(getIntent().getStringExtra("refreshAdapter") != null && adapter != null){
            adapter.notifyDataSetChanged();
        }
        super.onResume();
    }

    private void initComponents() {
        fabMenu = findViewById(R.id.homepageFabMenu);
        favouritesButton = findViewById(R.id.favouritesButton);
        signOutButton = findViewById(R.id.signOutButton);
        searchTypeSpinner = findViewById(R.id.searchTypeSpinner);
        searchView = findViewById(R.id.searchView);
        nextButton = findViewById(R.id.nextSearchResults);
        previousButton = findViewById(R.id.previousSearchResults);
        recyclerView = findViewById(R.id.searchResultsRecyclerView);
        pageNumber = findViewById(R.id.pageNumber);
        currentPage = 0;
        pageSize = 0;
        avaliableForSearch = true;
        appDB = KutuphaneDB.get_kutuphaneDB(HomepageActivity.this);
    }

    private void registerEventHandlers() {
        favouritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoFavourites();
                fabMenu.close(true);
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //delete username from prefs and direct user to sign in page
                SharedPreferences preferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove("username");
                editor.apply();
                int userId = appDB.getUserDAO().getUserByUsername(getUserNameFromIntent()).getUserID();
                appDB.getUserDAO().setSignedInUser(userId, false);
                gotoSignIn();
                fabMenu.close(true);
            }
        });

        searchTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSearchType = searchTypeSpinner.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(avaliableForSearch){
                    makeSearch(query, 0);  //start index = 0
                }

                else
                    Toast.makeText(HomepageActivity.this, R.string.waitApi, Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //list next 10 serch results
                changePage(1);
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //list previous 10 seacrch results
                changePage(-1);
            }
        });
    }

    private void changePage(int step){
        if(avaliableForSearch && !searchView.getQuery().toString().isEmpty() && pageSize != 0){
            if(currentPage!=0 && step == -1){
                --currentPage;
                String searchQuery = searchView.getQuery().toString();
                makeSearch(searchQuery, currentPage*10);
                pageNumber.setText(String.valueOf(currentPage+1));
            }

            else if(currentPage!=pageSize && step == 1){
                ++currentPage;
                String searchQuery = searchView.getQuery().toString();
                makeSearch(searchQuery, currentPage*10);
                pageNumber.setText(String.valueOf(currentPage+1));
            }
        }

        else if(pageSize != 0)
            Toast.makeText(HomepageActivity.this, R.string.waitApi, Toast.LENGTH_SHORT).show();

    }

    private void makeSearch(String query, int startIndex){
        avaliableForSearch = false;
        BookApiService serviceApi = RetrofitInstance.getService();
        Call<Books> call;
        switch (selectedSearchType){
            case("Title"):{
                query = query + "+intitle";
                call = serviceApi.getSearchRequest(query, startIndex);
            }break;

            case ("Author"):{
                query = query + "+inauthor";
                call = serviceApi.getSearchRequest(query, startIndex);
            }break;

            case ("ISBN"):{
                query = query + "+isbn";
                call = serviceApi.getSearchRequest(query, startIndex);
            }break;

            default:{  //default olarak özel bir alanda arama
                call = serviceApi.getSearchRequest(query, startIndex);
            }break;
        }

        call.enqueue(new Callback<Books>() {
            @Override
            public void onResponse(@NonNull Call<Books> call, @NonNull Response<Books> response) {
                Books books = response.body();
                if(books != null && books.getItems() != null){
                    itemList = books.getItems();
                    LinearLayoutManager llm = new LinearLayoutManager(HomepageActivity.this);
                    recyclerView.setLayoutManager(llm);
                    recyclerView.setHasFixedSize(true);
                    adapter = new CustomAdapter(HomepageActivity.this, itemList);
                    recyclerView.setAdapter(adapter);
                    recyclerView.addItemDecoration(new DividerItemDecoration(HomepageActivity.this, LinearLayoutManager.VERTICAL));
                    pageSize = books.getTotalItems()/10;   //sonuç sayısı / bir sayfada gösterilecek kitap sayısı
                    pageSize = books.getTotalItems()%10 > 0 ? pageSize + 1 : pageSize; //bölümde kalan var ise
                    currentPage = startIndex == 0 ? 0: currentPage; //searchView ile arama yapıldığında eğer sonuç var ise
                    pageNumber.setVisibility(View.VISIBLE);                             // sayfalama için şu anki sayfanın numarasını değişkene ata
                }  //sonuç var ise şu an ki sayfa sayısını göster
                                                    //varsayılan başlangıç değeri 1
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomepageActivity.this);
                    builder.setIcon(R.drawable.kutuphaneappicon);
                    builder.setTitle(R.string.searchResultEmptyTitle);
                    builder.setMessage(R.string.searchResultEmptyMessage);
                    builder.setNeutralButton(R.string.okButtonText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                }
                avaliableForSearch = true;   //arama bittikten sonra fonksiyonun diğer arama isteklerine cevap verebilmesi için
            }                                                                   // ilgili değişkeni true yap

            @Override
            public void onFailure(@NonNull Call<Books> call, @NonNull Throwable throwable) { //api'dan cevap alınamadığı durum
                Log.e("Api response failure!", throwable.getLocalizedMessage());
                avaliableForSearch = true;
            }
        });
    }

    private void gotoFavourites() {  //favoriler activity'e git
        Intent intent = new Intent(HomepageActivity.this, FavouritesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); //favorilerden sadece geriye (homepage'e) dönülüyor yani activity sonlandırılıyor
        startActivity(intent);                                          //bu yüzden intent flag'a gerek yok. kaldırılacak -> FLAG_ACTIVITY_REORDER_TO_FRONT
    }

    private void gotoSignIn(){  //giriş yapma activity'e git
        Intent intent = new Intent(HomepageActivity.this, UserSignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
