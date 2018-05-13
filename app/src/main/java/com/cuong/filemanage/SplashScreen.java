package com.cuong.filemanage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        int version = Build.VERSION.SDK_INT;
        if (version > Build.VERSION_CODES.LOLLIPOP) {
            if (!checkIfHavePermisson()) {
                requestForPermission();
            } else {

              //  if (new SongController(getApplicationContext()).count() == 0) {
            //        new SongScanner(this).scan();
            //    }
                startMainActivity();
            }
        }

    }

    public void startMainActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                SplashScreen.this.startActivity(mainIntent);
                SplashScreen.this.finish();
            }
        }, 3000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1010) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "This application need READ_EXTERNAL_STORAGE permission to work correctly!", Toast.LENGTH_LONG).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 3000);

            } else {
              //  if (new SongController(getApplicationContext()).count() == 0) {
             //       new SongScanner(this).scan();

        //        }
                startMainActivity();
            }

        } else {


            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean checkIfHavePermisson() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestForPermission() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1010);
    }

}
