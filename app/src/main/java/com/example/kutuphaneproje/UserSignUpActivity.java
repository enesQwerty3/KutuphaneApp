package com.example.kutuphaneproje;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;
import java.util.regex.Pattern;

public class UserSignUpActivity extends AppCompatActivity {
    private EditText username, password;
    private Button signupButton;
    private ImageButton showPasswordButton, usernameInfoButton, passwordInfoButton;
    private boolean validUsername, validPassword;  // kullanıcı adı ve şifre doğru formda mı?
    private Pattern validUsernamePattern;
    private User user;
    private KutuphaneDB appDB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_sign_up_layout);
        initComponents();
        registerEventHandlers();
    }

    private void initComponents() { //acticity bileşenlerini tanımla
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //geri tuşunu aktif et manifest'de nereye geri döneceği tanımlı
        username = findViewById(R.id.kullaniciAdiEditText);
        password = findViewById(R.id.sifreEditText);
        signupButton = findViewById(R.id.kayitOlButton);
        showPasswordButton = findViewById(R.id.sifreGosterButton);
        usernameInfoButton = findViewById(R.id.kullaniciAdiuyariGosterButton);
        passwordInfoButton = findViewById(R.id.sifreUyariGosterButton);
        validUsernamePattern = Pattern.compile("^[a-zA-Z]{1}(?=.*\\d)[a-zA-Z\\d]{4,7}$"); //a dan z ye harf ve sayı olabilir en az bir sayı olmalı
        user = new User();  //kullanıcı nesnesini sadece acticity oluşturulduğunda oluştur
        appDB = KutuphaneDB.get_kutuphaneDB(UserSignUpActivity.this); //max 5 karakter olabilir en az 1 sayı
        validUsername = false;           //kullanıcı adı henüz kontrol edilmedi false ile başlat
        validPassword = false;          //şifre henüz kontrol edilmedi false ile başlat
        //database'i başlat
    }

    private void registerEventHandlers() {  //şifre göster butonuna basıldığında şifre içeriği gösteriliyor
        showPasswordButton.setOnClickListener(new View.OnClickListener() {  //show and hide password in EditText
            @Override
            public void onClick(View v) {
                if(password.getTransformationMethod().getClass().getSimpleName().equals("PasswordTransformationMethod"))
                    password.setTransformationMethod(new SingleLineTransformationMethod()); //gizli ise göster
                else
                    password.setTransformationMethod(new PasswordTransformationMethod()); //gizli değil ise gizle

                password.setSelection(password.getText().length());  //gösterme/gizleme işlemi sonrası imleçi en sona getir
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _kullaniciAdi = username.getText().toString(); // edit text içerisindeki kullınıcı adını ata
                String _sifre = password.getText().toString();  // edit text içerisindeki şifreyi ata
                AlertDialog.Builder builder = new AlertDialog.Builder(UserSignUpActivity.this);
                AlertDialog alertDialog;
                user.setUsername(_kullaniciAdi);
                user.setPassword(String.valueOf(_sifre.hashCode()));

                if(appDB.getUserDAO().getUserByUsername(user.getUsername()) == null) //belirtilen kullanıcı adında bir kullanıcı var mı kontrol et
                {
                    signupButton.setClickable(false);  //insert işlemi bitene kadar butonu tıklanamaz yap
                    appDB.getUserDAO().insertUser(user);   //kullanıcıyı database'e ekle
                    builder.setTitle(getResources().getText(R.string.signUpSuccessfulTitle));
                    builder.setMessage(getResources().getText(R.string.signUpSuccessfulMessage));
                    builder.setIcon(R.drawable.kutuphaneappicon);
                    builder.setPositiveButton(R.string.okButtonText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            username.setText(null);
                            password.setText(null);
                        }
                    });
                    alertDialog = builder.create();
                    alertDialog.show();          //kayıt başarılı alert dialog'u gösterdikten sonra butonu tekrar tıklanabilir yap
                    signupButton.setClickable(true);
                }

                else{
                    builder.setTitle(getResources().getText(R.string.signUpFailedTitle));
                    builder.setMessage(getResources().getText(R.string.signUpFailedMessage));
                    builder.setIcon(R.drawable.kutuphaneappicon);
                    builder.setPositiveButton(R.string.okButtonText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String usernameString = String.valueOf(s);
                if(usernameString.isEmpty())
                {
                    validUsername = false;
                    usernameInfoButton.setVisibility(View.VISIBLE);
                    setKayitOlButtonEnabled();
                }

                else{// check if regex pattern matches the usernameString
                    validUsername = validUsernamePattern.matcher(usernameString).find();
                    usernameInfoButton.setVisibility(validUsername ? View.GONE : View.VISIBLE);
                    setKayitOlButtonEnabled();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        usernameInfoButton.setOnClickListener(new View.OnClickListener() { //kullanıcı adı boş veya geçersiz formatta ise
            @Override                                                                     //tıklanarak açılabilir hata mesajı
            public void onClick(View v) {
                if(username.getError() == null){
                    if(!validUsername && username.getText().toString().isEmpty())
                        username.setError(getResources().getText(R.string.usernameEmptyText));
                    else
                        username.setError(getResources().getText(R.string.userNameFormatInfo));
                }
                else
                    username.setError(null);
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String passwordString = String.valueOf(s);
                if(passwordString.isEmpty())
                {
                    validPassword = false;
                    passwordInfoButton.setVisibility(View.VISIBLE);
                    setKayitOlButtonEnabled();
                }

                else
                {
                    validPassword = passwordString.length() >= 8;  //şifre en az 8 karakter mi?
                    passwordInfoButton.setVisibility(validPassword ? View.GONE : View.VISIBLE);
                    setKayitOlButtonEnabled();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        passwordInfoButton.setOnClickListener(new View.OnClickListener() { //tıklanıldığında kullanıcıya hata ile
            @Override                                                       //ilgili mesajı setError ile gösterir
            public void onClick(View v) {
                if(password.getError() == null){ //hata mesajını toggle yapmak için gerekli kontrol bloğu
                    if(!validPassword && password.getText().toString().isEmpty()) //şifre boş mu kontrol et
                        password.setError(getResources().getText(R.string.passwordEmptyText));
                    else //görülebilir olduğu yer ve çalışma mantığı gereği sadece şifre geçersiz formatta ise bu blok çalışır
                        password.setError(getResources().getText(R.string.passwordFormatInfo));
                }
                else
                    password.setError(null);
            }
        });

    }

    private void setKayitOlButtonEnabled(){ //geçerli bir kullanıcı adı ve şifre girildiğinde kayıt ol butonu
        signupButton.setEnabled(validUsername && validPassword);           //tıklanabilir hale gelir
    }
}