package com.angelfish.videosettings.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.provider.Settings.Secure;

import com.angelfish.videosettings.BuildConfig;
import com.angelfish.videosettings.R;
import com.angelfish.videosettings.util.AddressUtils;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Calendar;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SettingsActivity extends AppCompatActivity {
    private Context mContext;
    private Button mButtonReset;
    private Button mButtonApply;
    private EditText mInputText;
    private static final String SettingsPref = "settings_pref";
    private static final String AddressKey = "AddressKey";
    private static final String ModeKey = "ModeKey";
    private static final String FileName = "/server.cfg";
    private SharedPreferences mSettingsSP;
    private String mSavedAddr = "";
    private String mSavedMode = "";
    private RadioGroup mRadioNetwork;
    private RadioGroup mSearchMode;
    private int mCheckingCount = 0;
    private String mLocalIPandPort = "";
    private boolean mIsSearching = false;
    private ProgressDialog mProgresssDialog;

    private static final String TAG = "MultiPlayer";

    private final static int DO_UPDATE_TEXT = 0;
    private final static int FINISH_NOT_FOUND = 1;
    private final static int SERVER_FOUND = 2;
    private final Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            final int what = msg.what;
            switch(what) {
                case DO_UPDATE_TEXT:
                    doUpdate();
                    break;
                case FINISH_NOT_FOUND:
                    finishSearching();
                    showNoServerFound();
                    break;
                case SERVER_FOUND:
                    finishSearching();
                    showServerFound();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getApplicationContext();
//        isWriteStoragePermissionGranted();
//        isReadStoragePermissionGranted();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.activity_settings);
        mSettingsSP = getSharedPreferences(SettingsPref, MODE_PRIVATE);

        mButtonReset =findViewById(R.id.btn_reset);
        mButtonApply = findViewById(R.id.btn_apply);
        mInputText = findViewById(R.id.textInput);
        mRadioNetwork = findViewById(R.id.radioNetwork);
        mSearchMode = findViewById(R.id.searchMode);



        mRadioNetwork.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...

                if(checkedId == R.id.radioLAN){
                    mSearchMode.setVisibility(View.VISIBLE);

                }else{
                    mSearchMode.setVisibility(View.GONE);
                }
            }

        });

        mSearchMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...

                if(checkedId == R.id.autoSearch){
                    mInputText.setEnabled(false);
                    startAutoSearch();
                }else{
                    mInputText.setEnabled(true);
                }
            }

        });

        String addrSettingsExtra;
        String modeSettingsExtra;

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            addrSettingsExtra= null;
        } else {
            addrSettingsExtra= extras.getString("AddressKey");
            if (addrSettingsExtra!=null){
               Log.e(TAG, "start settings activity with addr parameters "+addrSettingsExtra);
                mSettingsSP.edit().putString(AddressKey, addrSettingsExtra).apply();
            }
            modeSettingsExtra= extras.getString("ModeKey");
            if (modeSettingsExtra!=null){
                Log.e(TAG, "start settings activity with addr parameters "+modeSettingsExtra);
                mSettingsSP.edit().putString(ModeKey, modeSettingsExtra).apply();
            }
//            finish();
        }
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

    public void doUpdate(){
        if(mInputText!= null && mCheckingCount > 0){
            mInputText.setText(mLocalIPandPort);
        }
        if(mIsSearching == false){
            mProgresssDialog = new ProgressDialog(SettingsActivity.this);
            mProgresssDialog.setMessage(getString(R.string.str_searching));
            mProgresssDialog.setCanceledOnTouchOutside(false);
            mProgresssDialog.show();
            mIsSearching = true;
        }
    }

    public void finishSearching(){
        if(mInputText!= null && mCheckingCount > 0){
            mInputText.setEnabled(true);
            mCheckingCount = 0;
        }
        if(mProgresssDialog!=null && mIsSearching == true){
            mProgresssDialog.dismiss();
            mIsSearching = false;
        }
    }

    public void showNoServerFound(){
        AlertDialog alertDialog = new AlertDialog.Builder(SettingsActivity.this).create();
        alertDialog.setTitle(R.string.str_search_finished);
        alertDialog.setMessage(getString(R.string.str_no_server_found));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void showServerFound(){
        AlertDialog alertDialog = new AlertDialog.Builder(SettingsActivity.this).create();
        alertDialog.setTitle(R.string.str_search_finished);
        alertDialog.setMessage(getString(R.string.str_server_found));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public String getDeviceManufacturer() {
        String manufacturer = Build.MANUFACTURER;
        return capitalize(manufacturer);

    }

    public String getDeviceName() {
        String name = Build.MODEL;
        return capitalize(name);

    }

    public String getDeviceSerial() {
        String device_sn = Build.SERIAL;
        if (Build.VERSION.SDK_INT >= 26) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED) {
                try {
                    Class<?> c = Class.forName("android.os.SystemProperties");
                    Method get = c.getMethod("get", String.class);
                    Log.e(TAG, "before device_sn = "+device_sn);

                    device_sn = (String) get.invoke(c, "gsm.sn1");
                    if (device_sn.equals(""))
                        device_sn = (String) get.invoke(c, "ril.serialnumber");
                    if (device_sn.equals(""))
                        device_sn = (String) get.invoke(c, "ro.serialno");
                    if (device_sn.equals(""))
                        device_sn = (String) get.invoke(c, "sys.serialnumber");
                    if (device_sn.equals(""))
                        device_sn = Build.getSerial();
                    Log.e(TAG, "after device_sn = "+device_sn);
                } catch (Exception e) {
                    e.printStackTrace();
                    device_sn = "";
                }
            }
        }
        return capitalize(device_sn);

    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
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

    public void startAutoSearch(){
        String android_id = Secure.getString(mContext.getContentResolver(),
                Secure.ANDROID_ID);
        Log.i(TAG, "length of device id ="+android_id);
        if(android_id.length()==15){
            android_id = android_id+"0";
        }

        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        int rssi = 0;
        String IPAddress = AddressUtils.getIPAddress(true);
        String macAddress = AddressUtils.getMACAddress("wlan0");
        if(macAddress.equals("")){
            macAddress = AddressUtils.getMACAddress("eth0");

        }else {
            WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);

            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            rssi = wifiInfo.getRssi();
        }
        long timestamp = Calendar.getInstance().getTimeInMillis();
        String manufacturer = getDeviceManufacturer();
        String device_name = getDeviceName();
        String device_sn = getDeviceSerial();

        String partOfIPs = IPAddress.substring(0, IPAddress.lastIndexOf("."));
        mCheckingCount ++;
        mLocalIPandPort = "http://"+partOfIPs+"."+mCheckingCount+":9200/";
        myHandler.sendEmptyMessage(DO_UPDATE_TEXT);
        String checkingIPs = mLocalIPandPort +"/?act=api/device!activate&mac_addr="+macAddress+"&device_id="+android_id+"&version_code="+versionCode+"&version_name="+versionName+"&address="+IPAddress+"&timestamp="+timestamp+"&manufacturer="+manufacturer+"&device_name="+device_name+"&device_sn="+device_sn+"&rssi="+rssi;
        Log.e(TAG, "checkingIPs = "+checkingIPs);
        try{
            URL ativate_link = new URL(checkingIPs);
            new GetTokenTask().execute(ativate_link);


        }catch(MalformedURLException ex){
            ex.printStackTrace();
        }
    }

    private class GetTokenTask extends AsyncTask<URL, Integer, JSONObject> {
        protected JSONObject doInBackground(URL... urls) {
            int count = urls.length;

            try {
                for (int i = 0; i < count; i++) {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .retryOnConnectionFailure(false)
                            .build();
                    try {
                        Response response = client.newCall(new Request.Builder()
                                .url(urls[i])
                                .build()).execute();

                        String result = response.body().string();
                        Log.e(TAG, result);
                        return new JSONObject(result);
                    } catch (ConnectTimeoutException e) {
                        Log.e(TAG, "Timeout", e);
                        if(mCheckingCount<255) {
                            startAutoSearch();
                        }else{
                            Log.e(TAG, "Check finished, no server found!");
                        }
                    } catch (SocketTimeoutException e) {
                        if(mCheckingCount<255) {
                            startAutoSearch();
                        }else{
                            myHandler.sendEmptyMessage(FINISH_NOT_FOUND);
                            Log.e(TAG, "Check finished, no server found!");
                        }
                    }catch(ConnectException e){
                        if(mCheckingCount<255) {
                            startAutoSearch();
                        }else{
                            myHandler.sendEmptyMessage(FINISH_NOT_FOUND);
                            Log.e(TAG, "Check finished, no server found!");
                        }
                    }catch(UnknownHostException e){
                        if(mCheckingCount<255) {
                            startAutoSearch();
                        }else{
                            myHandler.sendEmptyMessage(FINISH_NOT_FOUND);
                            Log.e(TAG, "Check finished, no server found!");
                        }
                    }
                    finally {

                    }

//                    URLConnection conexion = urls[i].openConnection();
//                    conexion.setRequestProperty("Connection", "close");
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
//
//                    StringBuffer stringBuffer = new StringBuffer();
//                    String line;
//                    while ((line = bufferedReader.readLine()) != null)
//                    {
//                        stringBuffer.append(line);
//                    }

//                    return new JSONObject(stringBuffer.toString());
                }
            } catch (Exception ex) {
//                Toast.makeText(mContext, R.string.str_connection_error, Toast.LENGTH_LONG).show();
                ex.printStackTrace();
                return null;
            }
            return null;

        }

        protected void onProgressUpdate(Integer... progress) {
//            setProgressPercent(progress[0]);
        }

        protected void onPostExecute(JSONObject response) {
            if(response!=null){
                myHandler.sendEmptyMessage(SERVER_FOUND);
            }

        }
    }

        public boolean isWriteStoragePermissionGranted() {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Permission is granted2");
                    return true;
                } else {

                    Log.v(TAG, "Permission is revoked2");
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                    return false;
                }
            } else { //permission is automatically granted on sdk<23 upon installation
                Log.v(TAG, "Permission is granted2");
                return true;
            }
        }

        public void checkInputAndApply(String input) {
            if (input.equals("")) {
                Toast.makeText(mContext, R.string.str_please_input, Toast.LENGTH_LONG).show();
                return;
            } else if (!input.startsWith("http://")) {
                Toast.makeText(mContext, R.string.str_start_with_http, Toast.LENGTH_LONG).show();
                return;
            }
            mSavedAddr = input;
            mSettingsSP.edit().putString(AddressKey, input).apply();
            saveNewWebsiteToFile(input);
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

        public void saveNewWebsiteToFile(String website) {
            FileOutputStream FoutS = null;
            OutputStreamWriter outSW = null;

            try {
                FoutS = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + FileName));
                outSW = new OutputStreamWriter(FoutS);

                outSW.write(website);

                outSW.flush();
                // Rest of try block here
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {

                try {

                    outSW.close();

                    FoutS.close();

                } catch (IOException e) {

                    e.printStackTrace();

                }

            }


        }



}
