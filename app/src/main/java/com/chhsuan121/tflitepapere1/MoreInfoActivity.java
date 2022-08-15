package com.chhsuan121.tflitepapere1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import com.chhsuan121.tflitepapere1.databinding.ActivityMoreInfoBinding;

public class MoreInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.chhsuan121.tflitepapere1.databinding.ActivityMoreInfoBinding binding = ActivityMoreInfoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.button1.setOnClickListener(v -> {
            goToUrl (getString(R.string.web_link_1));
        });
        binding.button2.setOnClickListener(v -> {
            goToUrl (getString(R.string.web_link_2));
        });
        binding.button3.setOnClickListener(v -> {
            goToUrl (getString(R.string.web_link_3));
        });
        binding.button4.setOnClickListener(v -> {
            goToUrl (getString(R.string.web_link_4));
        });

        binding.buttonHome.setOnClickListener( v -> {
//            Intent mIntent = new Intent(getApplicationContext(),MainActivity.class);
//            startActivity(mIntent);
//            MoreInfoActivity.this.finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        });

    }

    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

    //    遮蔽返回鍵
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.ECLAIR) {
                event.startTracking();
            } else {
                onBackPressed(); // 是其他按鍵則再Call Back方法
            }
        }
        return false;
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }
}