package com.xoftedge_dev.granitemarblemeasurementsheet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_PERMISSIONS = 1;
    private boolean granted = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

//        getSupportActionBar().hide();

        LogoLauncher logoLauncher = new LogoLauncher();
        logoLauncher.start();


    }

    private class LogoLauncher extends Thread {

        public void run(){
            try {
                sleep(2000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }

            if (checkAndRequestPermissions()) {
                Intent intent = new Intent(SplashActivity.this, Tutorial.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        }

    }

    private boolean checkAndRequestPermissions(){
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int phonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        List<String> listPermissions = new ArrayList<>();
        if (cameraPermission != PackageManager.PERMISSION_GRANTED){
            listPermissions.add(Manifest.permission.CAMERA);
        }
        if (phonePermission != PackageManager.PERMISSION_GRANTED){
            listPermissions.add(Manifest.permission.CALL_PHONE);
        }
        if (!listPermissions.isEmpty()){
            ActivityCompat.requestPermissions(this, listPermissions.toArray(new String[listPermissions.size()]), REQUEST_CODE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE_PERMISSIONS:
                Map<String, Integer> perms = new HashMap<>();
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);

                if (grantResults.length > 0){
                    for (int i=0; i<permissions.length; i++){
                        perms.put(permissions[i], grantResults[i]);
                    }
                    if (perms.get(Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED &&
                    perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
                        Intent intent = new Intent(SplashActivity.this, Tutorial.class);
                        startActivity(intent);
                    }else{
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this, Manifest.permission.CAMERA) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.CALL_PHONE)){
                            showDialogOK("Camera & Call Permission is required for this app.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which)
                                            {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    break;
                                            }
                                        }
                                    });
                        }else{
                            Toast.makeText(this, "Go to Settings & enable permissions", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener){
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }
}