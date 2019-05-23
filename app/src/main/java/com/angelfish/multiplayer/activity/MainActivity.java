package com.angelfish.multiplayer.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.angelfish.multiplayer.R;
import com.angelfish.multiplayer.activity.api.ApiActivity;
import com.angelfish.multiplayer.activity.api.PlayerActivity;
import com.angelfish.multiplayer.activity.extend.ExtendActivity;
import com.angelfish.multiplayer.activity.list.ListActivity;
import com.angelfish.multiplayer.activity.pip.PIPDemoActivity;
import com.angelfish.multiplayer.util.PIPManager;
import com.angelfish.multiplayer.util.VideoCacheManager;
import com.yanzhenjie.permission.AndPermission;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private boolean isLive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.et);

        ((RadioGroup) findViewById(R.id.rg)).setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.vod:
                    isLive = false;
                    break;
                case R.id.live:
                    isLive = true;
                    break;
            }
        });
        AndPermission
                .with(this)
                .runtime()
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onDenied(data -> {

                })
                .onGranted(data -> {

                })
                .start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.close_float_window:
                PIPManager.getInstance().stopFloatWindow();
                PIPManager.getInstance().reset();
                break;
            case R.id.clear_cache:
                if (VideoCacheManager.clearAllCache(this)) {
                    Toast.makeText(this, "清除缓存成功", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void playOther(View view) {
        String url = editText.getText().toString();
        if (TextUtils.isEmpty(url)) return;
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("isLive", isLive);
        startActivity(intent);
    }

    public void clearUrl(View view) {
        editText.setText("");
    }

    public void api(View view) {
        startActivity(new Intent(this, ApiActivity.class));
    }

    public void extend(View view) {
        startActivity(new Intent(this, ExtendActivity.class));
    }

    public void list(View view) {
        startActivity(new Intent(this, ListActivity.class));
    }

    public void pip(View view) {
        startActivity(new Intent(this, PIPDemoActivity.class));
    }
}
