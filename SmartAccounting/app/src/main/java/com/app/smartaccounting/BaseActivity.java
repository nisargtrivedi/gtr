package com.app.smartaccounting;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.smartaccounting.util.AppPreferences;
import com.app.smartaccounting.util.ProgressD;

public class BaseActivity extends AppCompatActivity {

    public ProgressD progressD;
    public Toast toast;
    AppPreferences appPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
    public boolean isPermissionsAllowed() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        else
            return true;
    }
    public void showPermissionDeniedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Denied")
                .setMessage("Permission is denied, Please allow permissions from App Settings.")
                .setPositiveButton("App Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                }).setNegativeButton("Cancel",null).show();
    }



    public boolean askForPermissions() {
        if (!isPermissionsAllowed()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                showPermissionDeniedDialog();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{(Manifest.permission.WRITE_EXTERNAL_STORAGE),(Manifest.permission.READ_EXTERNAL_STORAGE),(Manifest.permission.CAMERA)},1000);
            }
            return false;
        }
        return true;
    }

    public void showLoading(Context context){
        //progressD=new ProgressD(context);
        progressD = ProgressD.show(context, "Loading", false, false);
    }
    public void hideLoading(){
        if (progressD != null && progressD.isShowing()) {
            progressD.cancel();
            progressD.dismiss();
            progressD = null;
        }
    }

    //Email Validation
    public Boolean isEmail(String s) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches();
    }
    //Phone Validation
    public Boolean isPhones(String s){
        return android.util.Patterns.PHONE.matcher(s).matches();
    }


    public void showToast(String msg){
        if(toast!=null){
            toast.cancel();
        }
        toast=Toast.makeText(this,msg,Toast.LENGTH_SHORT);
        toast.show();
    }
    public boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=null;
        if(connectivityManager!=null){
            networkInfo=connectivityManager.getActiveNetworkInfo();
        }
        return networkInfo!=null && networkInfo.isConnected();
    }
}
