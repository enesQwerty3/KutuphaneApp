package com.example.kutuphaneproje;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kutuphaneproje.api.Books;
import com.example.kutuphaneproje.api.IndustryIdentifiers;
import com.example.kutuphaneproje.api.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {
    private List<Item> itemList;
    private Context context;
    private KutuphaneDB appDB;
    public class CustomViewHolder extends RecyclerView.ViewHolder{
        private TextView bookNameTextView;
        private TextView authorNameTextView;
        private TextView isbnTextView;
        private ImageButton addFavouritiesButton;

        @SuppressLint("UseCompatLoadingForDrawables")
        public CustomViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            bookNameTextView = itemView.findViewById(R.id.bookNameTextView);
            authorNameTextView = itemView.findViewById(R.id.authorNameTextView);
            isbnTextView = itemView.findViewById(R.id.isbnTextView);
            addFavouritiesButton = itemView.findViewById(R.id.addFavouritiesButton);
            addFavouritiesButton.setVisibility(View.VISIBLE);

            addFavouritiesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int userId = appDB.getUserDAO().getSignedInUser().getUserID();
                    String book_id = itemList.get(getAbsoluteAdapterPosition()).getId();
                    FavouriteBook favouriteBook = appDB.getFavouriteBookDAO().getFavouriteBook2(book_id, userId);

                    if(favouriteBook == null){  //check if book not in favourities
                        favouriteBook = new FavouriteBook();
                        favouriteBook.setBookName(bookNameTextView.getText().toString());
                        favouriteBook.setAuthorName(authorNameTextView.getText().toString());
                        favouriteBook.setIsbn(isbnTextView.getText().toString());
                        favouriteBook.setUserID(userId);
                        favouriteBook.setBook_id(itemList.get(getAbsoluteAdapterPosition()).getId());
                        appDB.getFavouriteBookDAO().insertBook(favouriteBook);
                        String message = new StringBuilder().append(favouriteBook.getBookName()).append(" ")
                                .append(context.getResources().getString(R.string.bookAddedFavouritiesText)).toString();
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        addFavouritiesButton.setImageDrawable(context.getDrawable(R.drawable.add_favourite_icon_disabled));
                        addFavouritiesButton.setEnabled(false);
                    }

                    else{   //if book is already in favourities
                        Toast.makeText(context, R.string.bookInFavouritiesText, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public CustomAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
        appDB = KutuphaneDB.get_kutuphaneDB(context);
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.book_layout, parent, false);

        return new CustomViewHolder(itemView ,this.context);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        if (itemList.get(position)!=null){
            Item item = itemList.get(position);
            try{ //try set book title
                if(item.getVolumeInfo().getTitle() != null)
                    holder.bookNameTextView.setText(item.getVolumeInfo().getTitle());
                else
                    holder.bookNameTextView.setText(R.string.UnknownText);

            }catch (Exception e){

                Log.i(CustomAdapter.class.getSimpleName(), "Book title couldn't set.", e);
                holder.bookNameTextView.setText("Title couldn't found");
            }

            try{  //try set Authors
                if(item.getVolumeInfo().getAuthors()!= null){
                    StringBuilder authors = new StringBuilder();
                    for(int i = 0; i < item.getVolumeInfo().getAuthors().size(); i++){
                        authors.append(item.getVolumeInfo().getAuthors().get(i));
                        if (item.getVolumeInfo().getAuthors().size() > 1 && i != item.getVolumeInfo().getAuthors().size() - 1)
                            authors.append(", ");    //birden fazla yazar varsa  ve son yazarda değil ise aralara virgül koy
                    }
                    holder.authorNameTextView.setText(authors.toString()); //string builder'ı string'e dönüştür ve text view'e ata
                }

                else
                    holder.authorNameTextView.setText(R.string.UnknownText);  //yazar adı yok

            }catch (Exception e){
                Log.e(CustomAdapter.class.getSimpleName(), "Book title couldn't set.");
                holder.authorNameTextView.setText("Book author couldn't found");   //yazar adı doğru bir şekilde yazdırılmadı text view boş kalmasın diye bulunamadı yazıyor
            }

            try{  //try set ISBN
                if(item.getVolumeInfo().getIndustryIdentifiers() != null){
                    for (IndustryIdentifiers identifier:item.getVolumeInfo().getIndustryIdentifiers()){
                        if(identifier.getType().equals("ISBN_10")){  //ISBN_10 ve ISBN_13 var gösterilmek istenilen ISBN_10
                            holder.isbnTextView.setText(identifier.getIdentifier());
                            break;
                        }
                        else //isbn türü OTHER, sadece 1 isbn numarsı var
                            holder.isbnTextView.setText(identifier.getIdentifier());  //isbn type OTHER
                    }
                }

                else
                    holder.isbnTextView.setText(R.string.UnknownText);

            }catch (Exception e){
                Log.e(CustomAdapter.class.getSimpleName(), "ISBN couldn't set.");
                holder.isbnTextView.setText("ISBN couldn't found");
            }

            int userID = appDB.getUserDAO().getSignedInUser().getUserID();
            String isbn = holder.isbnTextView.getText().toString();
            String book_id = itemList.get(holder.getAbsoluteAdapterPosition()).getId();
            FavouriteBook favouriteBook = appDB.getFavouriteBookDAO().getFavouriteBook2(book_id, userID);
            if(favouriteBook != null){
                holder.addFavouritiesButton.setImageDrawable(context.getDrawable(R.drawable.add_favourite_icon_disabled));
                holder.addFavouritiesButton.setEnabled(false); //...
            }
        }
        else
            Log.e(CustomAdapter.class.getSimpleName(), "OnBindViewHolder item is null.");
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
