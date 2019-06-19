package com.angelfish.multiplayer.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.angelfish.multiplayer.R;

public class SettingsActivity extends AppCompatActivity {
    private Context mContext;
    private Button mButtonReset;
    private Button mButtonApply;
    private EditText mInputText;
    private static final String SettingsPref = "settings_pref";
    private static final String AddressKey = "AddressKey";
    private SharedPreferences mSettingsSP;
    private String mSavedAddr = "";

    private static final String TAG = "MultiPlayer";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getApplicationContext();
        isWriteStoragePermissionGranted();
        isReadStoragePermissionGranted();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.activity_settings);
        mSettingsSP = getSharedPreferences(SettingsPref, MODE_PRIVATE);

        mButtonReset =findViewById(R.id.btn_reset);
        mButtonApply = findViewById(R.id.btn_apply);
        mInputText = findViewById(R.id.textInput);
        mSavedAddr = mSettingsSP.getString(AddressKey,"");
        if(!mSavedAddr.equals("")){
            mInputText.setText(mSavedAddr);
            mInputText.setSelection(mSavedAddr.length());
        }
        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputText.setText("");
                mInputText.setHint(R.string.str_default_website);
            }
        });

        mButtonApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String new_website = mInputText.getText().toString();
                checkInputAndApply(new_website);
            }
        });

    }

    public  boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted1");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted1");
            return true;
        }
    }

    public  boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted2");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted2");
            return true;
        }
    }

    public void checkInputAndApply(String input){
        if(input.equals("")){
            Toast.makeText(mContext, R.string.str_please_input, Toast.LENGTH_LONG).show();
            return;
        }else if (!input.startsWith("http://")){
            Toast.makeText(mContext, R.string.str_start_with_http, Toast.LENGTH_LONG).show();
            return;
        }
        mSavedAddr = input;
        mSettingsSP.edit().putString(AddressKey, input).apply();
        AlertDialog alertDialog = new AlertDialog.Builder(SettingsActivity.this).create();
        alertDialog.setTitle(R.string.str_setting_succeed);
        alertDialog.setMessage(getString(R.string.str_please_launch_main));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }
}
