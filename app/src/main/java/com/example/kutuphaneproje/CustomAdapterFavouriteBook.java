package com.example.kutuphaneproje;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CustomAdapterFavouriteBook extends RecyclerView.Adapter<CustomAdapterFavouriteBook.FavouriteBookViewHolder> {
    private List<FavouriteBook> favouriteBookList;
    private Context context;
    private Boolean itemRemoved;

    public CustomAdapterFavouriteBook(Context context, List<FavouriteBook> favouriteBookList) {
        this.context = context;
        this.favouriteBookList = favouriteBookList;
        itemRemoved = false;
    }
    public class FavouriteBookViewHolder extends RecyclerView.ViewHolder {

        private TextView bookNameTextView;
        private TextView authorNameTextView;
        private TextView isbnTextView;
        private ImageButton removeFavouritiesButton, shareButton;
        private KutuphaneDB appDB;

        public FavouriteBookViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            bookNameTextView = itemView.findViewById(R.id.bookNameTextView);
            authorNameTextView = itemView.findViewById(R.id.authorNameTextView);
            isbnTextView = itemView.findViewById(R.id.isbnTextView);
            removeFavouritiesButton = itemView.findViewById(R.id.removeFavouritiesButton);
            removeFavouritiesButton.setVisibility(View.VISIBLE);
            shareButton = itemView.findViewById(R.id.shareBookButton);
            shareButton.setVisibility(View.VISIBLE);
            appDB = KutuphaneDB.get_kutuphaneDB(context);

            removeFavouritiesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeFavouritiesButton.setClickable(false);  //üst üste tıklamadaki hatayı gidermek için
                        //kitabın isbn numarasından database'dekii book_id değerine ulaşıyoruz
                    int signedInUserID = appDB.getUserDAO().getSignedInUser().getUserID();
                    String removedBookId = favouriteBookList.get(getAbsoluteAdapterPosition()).getBook_id();  // sadece id değeri de olabilirdi
                    FavouriteBook removedBook = appDB.getFavouriteBookDAO().getFavouriteBook2(removedBookId, signedInUserID); //gereksiz!
                    appDB.getFavouriteBookDAO().deleteFavouriteBook(removedBook.getFavourite_id());  //book_id değeri ile kitabı database'den siliyoruz
                    String message = new StringBuilder().append(removedBook.getBookName()).append(" ")
                            .append(context.getResources().getString(R.string.bookRemovedFavouritiesText)).toString();
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    removeItem(getBindingAdapterPosition());  //ReceyclerView'dan kitabı kaldırıyoruz
                }
            });

            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //open a chooser and perform sharing bookname, author and isbn with plain/text format
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    String text = new StringBuilder().append("Book: ").append(bookNameTextView.getText().toString()).append("\n")
                            .append("Author: ").append(authorNameTextView.getText().toString()).append("\n")
                            .append("ISBN: ").append(isbnTextView.getText().toString()).toString();
                    intent.putExtra(Intent.EXTRA_TEXT, text);
                    intent.setType("text/plain");
                    context.startActivity(Intent.createChooser(intent, context.getString(R.string.chooserTitleText)));
                }
            });
        }
    }

    public void removeItem(int position){
        favouriteBookList.remove(position);  //listeden ilgili pozisyondaki elemnti kaldır
        notifyItemRemoved(position);   //elemntin kaldırıldığını adaptöre bildir
        notifyItemRangeChanged(position, favouriteBookList.size()); //listenin boyutu değişti yeniden
        // liteleme için bunu adaptöre bildiriyoruz
        itemRemoved = true;
    }

    @NonNull
    @Override
    public FavouriteBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_layout, parent, false);

        return new CustomAdapterFavouriteBook.FavouriteBookViewHolder(itemView ,this.context);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteBookViewHolder holder, int position) {
        FavouriteBook book = favouriteBookList.get(position);
        if(book != null){
            holder.bookNameTextView.setText(book.getBookName());
            holder.authorNameTextView.setText(book.getAuthorName());
            holder.isbnTextView.setText(book.getIsbn());
        }
    }

    @Override
    public int getItemCount() {
        return favouriteBookList.size();
    }

    public boolean isItemRemoved(){
        return itemRemoved;
    }
} // eof
