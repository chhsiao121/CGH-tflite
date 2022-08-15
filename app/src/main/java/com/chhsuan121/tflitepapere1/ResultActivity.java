package com.chhsuan121.tflitepapere1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.chhsuan121.tflitepapere1.databinding.ActivityResultBinding;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class ResultActivity extends AppCompatActivity {
    private StorageReference storageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.chhsuan121.tflitepapere1.databinding.ActivityResultBinding binding = ActivityResultBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        storageRef = FirebaseStorage.getInstance().getReference();
        String root_path = getIntent().getStringExtra("root_path");
        float [] result_average = (float[]) getIntent().getExtras().get("result_average");
        binding.textName1.setText("舌根音化: ");
        binding.textName2.setText("塞音化: ");
        binding.textName3.setText("塞擦音化: ");
        binding.textName4.setText("聲隨韻母: ");
        binding.textInfo1.setText(String.format("%.2f", result_average[0] * 100) + "%");
        binding.textInfo2.setText(String.format("%.2f", result_average[1] * 100) + "%");
        binding.textInfo3.setText(String.format("%.2f", result_average[2] * 100) + "%");
        binding.textInfo4.setText(String.format("%.2f", result_average[3] * 100) + "%");

        binding.saveButton.setOnClickListener(v -> {
            try {
                if (networkIsConnect()) ZipandUpload();
                else Toast.makeText(getApplicationContext(), "請開啟網路，再重新上傳數據", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();

            }
        });
        binding.exitButton.setOnClickListener(v -> {
            Intent mIntent = new Intent(getApplicationContext(),MoreInfoActivity.class);
            startActivity(mIntent);
            ResultActivity.this.finish();
        });



    }

    private boolean networkIsConnect(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null){
            return networkInfo.isConnected();
        }else {
            return false;
        }
    }
    public void ZipandUpload() throws IOException {
        String root_path = getIntent().getStringExtra("root_path");
        String file_path = getExternalFilesDir(root_path).getAbsolutePath();
        String zip_path = file_path + ".zip";
        if (new File(file_path).list() != null) {
            com.chhsuan121.tflitepapere1.Zip.ZipUtil.zip(new File(file_path), new File(zip_path));
            //Toast.makeText(MainActivity.this,"壓縮完成",Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), "所有檔案已壓縮成功 !!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "請先進行錄音程序 !!", Toast.LENGTH_SHORT).show();
        }
        File zipFile = new File(zip_path);
        Uri zipUri = Uri.fromFile(zipFile);
        final String fileName = zipUri.toString().split("/")[zipUri.toString().split("/").length - 1];
        StorageReference riversRef = storageRef.child(fileName);//會跳掉

        UploadTask uploadTask = riversRef.putFile(zipUri);
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
                builder.setMessage(fileName + "上傳失敗，請重新點選上傳按鈕進行上傳");
                builder.setTitle("上傳結果");
                builder.setPositiveButton("收到", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), fileName + "如果持續上傳失敗請聯絡開發者:m11002129@mail.ntust.edu.tw", Toast.LENGTH_LONG).show();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
                builder.setMessage(fileName + "已上傳成功");
                builder.setTitle("上傳結果");
                builder.setPositiveButton("收到", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), fileName + "上傳成功", Toast.LENGTH_LONG).show();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
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
