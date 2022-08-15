package com.chhsuan121.tflitepapere1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.modeldownloader.CustomModel;
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions;
import com.google.firebase.ml.modeldownloader.DownloadType;
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader;

import org.tensorflow.lite.Interpreter;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private final int REQUESTCODE = 101;
    private final String[] moduleList = new String[]{"DenseNet121"};
    int progressNumber = 0;
    boolean modelComplete = false;
    File DenseNet121_model;
    Button btn_store;
    Button btn_download;
    ProgressBar progressBar;
    TextView textviewProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_store = findViewById(R.id.btn_store);
        btn_download = findViewById(R.id.btn_download);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        progressBar.setMax(100);
        textviewProgress = findViewById(R.id.textProgress);
        textviewProgress.setVisibility(View.GONE);
        request_permission();

        btn_store.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modelComplete) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, FormActivity.class);
                    intent.putExtra("DenseNet121_model", DenseNet121_model);
                    startActivity(intent);
                    MainActivity.this.finish();
                } else {
                    Toast.makeText(MainActivity.this, "請先下載模型", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_download.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (networkIsConnect()) {
                    Log.i("TAG", "Click btn_download");
                    Log.i("TAG", "btn_download: " + Thread.currentThread().getName());
                    progressBar.setVisibility(View.VISIBLE);
                    textviewProgress.setVisibility(View.VISIBLE);
                    btn_store.setEnabled(false);
                    btn_download.setEnabled(false);
                    threadRun();
                } else
                    Toast.makeText(getApplicationContext(), "請開啟網路，再下載模型", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean networkIsConnect() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            return networkInfo.isConnected();
        } else {
            return false;
        }
    }

    public void request_permission() {
        List<String> permissionList = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkSelfPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checkSelfPermission == PackageManager.PERMISSION_DENIED) {
                permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            checkSelfPermission = checkSelfPermission(Manifest.permission.RECORD_AUDIO);
            if (checkSelfPermission == PackageManager.PERMISSION_DENIED) {
                permissionList.add(Manifest.permission.RECORD_AUDIO);
            }
            if (!permissionList.isEmpty()) {
                requestPermissions(permissionList.toArray(new String[0]), REQUESTCODE);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUESTCODE) {
//            //询问用户权限
//            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[0]
//                    == PackageManager.PERMISSION_GRANTED) {
//            } else {
//            }
//        }
//        else if(requestCode == AUDIOREQUESTCODE){
//            if (permissions[0].equals(Manifest.permission.RECORD_AUDIO) && grantResults[0]
//                    == PackageManager.PERMISSION_GRANTED) {
//            } else {
//            }
//        }
        if (requestCode == REQUESTCODE) {
            if (grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; i++) {
                    // PERMISSION_DENIED 这个值代表是没有授权，我们可以把被拒绝授权的权限显示出来
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(MainActivity.this, permissions[i] + "權限被拒絕QQ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

    }

    private void threadRun() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //do something takes long time in the work-thread
                Log.i("TAG", "threadRun_run:" + Thread.currentThread().getName());
                for (String s : moduleList) {
                    downloadModel(s);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("TAG", "runOnUiThread_run:" + Thread.currentThread().getName());
                        btn_store.setEnabled(true);
//                        btn_download.setEnabled(true);
                    }
                });
            }
        }).start();
    }

    private synchronized void downloadModel(String moduleName) {
        Log.i("TAG", "downloadModel:" + Thread.currentThread().getName());
        CustomModelDownloadConditions conditions = new CustomModelDownloadConditions.Builder()
                .requireWifi()  // Also possible: .requireCharging() and .requireDeviceIdle()
                .build();
        FirebaseModelDownloader.getInstance()
                .getModel(moduleName, DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND, conditions)
                .addOnSuccessListener(model -> {
                    Log.i("TAG", "FirebaseModelDownloader.getInstance:" + Thread.currentThread().getName());
                    Log.e("TAG", "模型" + moduleName);
                    Log.e("TAG", "model download Success");
                    File modelFile = model.getFile();
                    Log.e("TAG", "Print model info:");
                    Log.e("TAG", model.getName());
                    Log.e("TAG", Objects.requireNonNull(model.getDownloadUrl()));
                    Log.e("TAG", Objects.requireNonNull(model.getLocalFilePath()));
                    Log.e("TAG", String.valueOf(model.getSize()));
                    Log.e("TAG", model.getModelHash());
                    DenseNet121_model = modelFile;
                    modelComplete = true;
                    btn_download.setEnabled(true);

                    if (progressNumber < 100) {
                        progressNumber = progressNumber + 100;
                        progressBar.setProgress(progressNumber);
                        textviewProgress.setText("下載進度" + progressNumber + " %");
                    }
                })
                .addOnFailureListener(model -> {
                    Toast.makeText(MainActivity.this, "下載模型失敗", Toast.LENGTH_SHORT).show();
                    btn_download.setEnabled(true);
                });
    }

    //    底下是按返回會結束程式
    private static Boolean isExit = false;
    private static Boolean hasTask = false;
    Timer timerExit = new Timer();
    TimerTask task = new TimerTask() {
        public void run() {
            isExit = false;
            hasTask = true;
        }
    };

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 判斷是否按下Back
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 是否要退出
            if (isExit == false) {
                isExit = true; //記錄下一次要退出
                Toast.makeText(this, "再按一次Back退出APP"
                        , Toast.LENGTH_SHORT).show();

                // 如果超過兩秒則恢復預設值
                if (!hasTask) {
                    timerExit.schedule(task, 2000);
                }
            } else {
                finish(); // 離開程式
                System.exit(0);
            }
        }
        return false;
    }
}