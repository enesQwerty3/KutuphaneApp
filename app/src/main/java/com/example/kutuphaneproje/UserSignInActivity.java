package com.example.kutuphaneproje;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.splashscreen.SplashScreen;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Objects;
import java.util.regex.Pattern;

public class UserSignInActivity extends AppCompatActivity{
    private TextView kayitOlText;
    private TextInputLayout kullaniciAdi, sifre; // TextInput layout'lar
    private SharedPreferences preferences;
    private Button girisButton; // butonlar
    private SwitchCompat beniHatirlaSwitch;
    private Pattern validUsernamePattern;  //kullanıcı adının belirlenen regex patterni tutulacak
    private KutuphaneDB appDB; //database nesnesi

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        SplashScreen.installSplashScreen(this); //splash screen'i başlat
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_sing_in_layout);          //layout'u tanımla
        checkIsUserSignedIn();         //kullanıcı daha önceden giriş yapmış mı kontrol et
        initComponents();                     //bileşenleri başlat değişkenlere başlangıç değerlerini ata
        RegisterEventHandlers();                     //event handler'ları kayıt et
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void checkIsUserSignedIn(){ //preferences ile kullanıcı daha önce giriş yaptı mı kontrol etx
        SharedPreferences preferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
        if(preferences.contains("username")){
            String username = preferences.getString("username", null);
            gotoHomepageActivity(username);
        }
    }

    private void initComponents(){ //activity'de kullanılacak bileşenler değişkenlere tanımlanıyor
        kullaniciAdi = findViewById(R.id.kullaniciAdiTextInputLayout);
        sifre = findViewById(R.id.sifreTextInputLayout);
        girisButton = findViewById(R.id.girisButton);
        kayitOlText = findViewById(R.id.kayitOlTextView);
        beniHatirlaSwitch = findViewById(R.id.beniHatirlaSwitch);
        preferences = getSharedPreferences("userPrefs", MODE_PRIVATE); //private modda userPrefs adlı preferences'i oluştur
        validUsernamePattern = Pattern.compile("^[a-zA-Z]{1}(?=.*\\d)[a-zA-Z\\d]{4,7}$");  //a dan z ye büyük veya küçük harf ve sayı olabilir
        appDB = KutuphaneDB.get_kutuphaneDB(UserSignInActivity.this);                       //ilk karakter harf olmalı en az 1 sayı
                                   //database bağlantısı                                              //minimum 5 maksimum 8 karakter
    }

    private void RegisterEventHandlers(){
        girisButton.setOnClickListener(new View.OnClickListener() { //giriş butonu dinleyicisi
            @Override
            public void onClick(View v) {
                String _kullaniciAdi = Objects.requireNonNull(kullaniciAdi.getEditText()).getText().toString(); // edit text içerisindeki kullınıcı adını ata
                String _sifre = Objects.requireNonNull(sifre.getEditText()).getText().toString();  // edit text içerisindeki şifreyi ata
                AlertDialog.Builder builder = new AlertDialog.Builder(UserSignInActivity.this);
                AlertDialog alertDialog;

                boolean validUsername = validUsernamePattern.matcher(_kullaniciAdi).find();  //regex paterni ile kullanıcı adı uyuşuyor mu?
                boolean beniHatirlaChecked = beniHatirlaSwitch.isChecked();

                if(!_kullaniciAdi.isEmpty() && !_sifre.isEmpty() && validUsername){ //kullanıcı adi ve şifre alanı boş mu kontrol et
                    User user = appDB.getUserDAO().getUserByUsername(_kullaniciAdi);
                    if(user != null && user.getUsername().equals(_kullaniciAdi) && user.getPassword().equals(String.valueOf(_sifre.hashCode()))){ //kullanıcı kayıtlı ve girilen bilgiler-
                        if(beniHatirlaChecked){  //beni hatırla seçildi ise preferences'e kullanıcı adını kayıt et                 //doğru mu kontrol et
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("username", user.getUsername());
                            editor.apply();
                        }                                                //kullanıcı signed in değerini ilgili tabloda verilen userId yi true yap
                        appDB.getUserDAO().setSignedInUser(user.getUserID(), true);
                        gotoHomepageActivity(user.getUsername());  //ana sayfaya kullanıcı adını göndererek yönlendir
                    }
                                                                        //kullanıcı kayıtlı fakat kullanıcı adı veya şifre yanlış girilmiş mi kontrol et
                    else if(user != null && (!user.getUsername().equals(_kullaniciAdi) || !user.getPassword().equals(_sifre))){
                        builder.setTitle(R.string.usernameAndPasswordEmptyTitleText);
                        builder.setMessage(R.string.usernameOrPasswordIsWrong);
                        builder.setPositiveButton(R.string.okButtonText, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.setIcon(R.drawable.kutuphaneappicon);
                        alertDialog = builder.create();
                        alertDialog.show();
                    }

                    else //kullanıcı kayıtlı değil
                    {
                        builder.setTitle(R.string.notRegisteredUserTitleText);
                        builder.setMessage(R.string.notRegisteredUserMessageText);
                        builder.setPositiveButton(R.string.okButtonText, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.setNeutralButton(R.string.signUpText, new DialogInterface.OnClickListener() { //kullanıcıcı kayıt olma ekranına yönlerndirmek
                            @Override                                                                 //için buton
                            public void onClick(DialogInterface dialog, int which) {
                                gotoRegisterActivity();
                            }
                        });
                        builder.setIcon(R.drawable.kutuphaneappicon);
                        alertDialog = builder.create();
                        alertDialog.show();
                    }
                }

                else{ // kullanıcı veya şifre alanı boş veya geçersiz formatta
                    builder.setTitle(R.string.usernameAndPasswordEmptyTitleText);
                    builder.setPositiveButton(R.string.okButtonText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    if(!validUsername && !_kullaniciAdi.isEmpty() && !_sifre.isEmpty())  //kullanıcı adı geçersiz formatta mı?
                        builder.setMessage(R.string.invalidusernameText);       //kullanıcı adının boş olması validUsername'i false yapar
                                                                            //kullanıcı adının boş olup olmadığını anlamak için bu koşul var
                    else   //eğer kullanıcı adı ve şifre boş ise
                        builder.setMessage(R.string.usernameAndPasswordEmptyMessageText);

                    builder.setIcon(R.drawable.kutuphaneappicon);
                    alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });

        kayitOlText.setOnClickListener(new View.OnClickListener() { //kayıt ol yazısına basıldığında kayıt olma activity'e git
            @Override
            public void onClick(View v) {
                gotoRegisterActivity();
            }
        });
    }

    private void gotoHomepageActivity(String username){ //uygulamanaın ana sayfası olan activity'e kullanıcı adı ile git
        Intent intent = new Intent(UserSignInActivity.this, HomepageActivity.class);
        if(username != null)  //kullanıcı adının null olup olmadığını kontrol et
            intent.putExtra("username", username);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); //Giriş ekranına geri dönülememsi için
        startActivity(intent);                                                          //task'ı temizle ve activity'i bu task de aç
                                                                                        //amaç girişden sonra ana sayfanın root activity olmasıdır
    }

    private void gotoRegisterActivity(){ //kayıt olma activity'e git
        Intent intent = new Intent(UserSignInActivity.this, UserSignUpActivity.class);
        startActivity(intent);
    }
}
