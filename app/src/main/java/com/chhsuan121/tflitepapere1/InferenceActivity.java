package com.chhsuan121.tflitepapere1;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.chhsuan121.tflitepapere1.TFlite.frontEnd;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.CompatibilityList;
import org.tensorflow.lite.gpu.GpuDelegate;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.CompatibilityList;
import org.tensorflow.lite.gpu.GpuDelegate;

public class InferenceActivity extends AppCompatActivity {
    public float[][] pythonInputData;
    public int PATH_number;
    File DenseNet121_model;
    private float[][] result;
    Interpreter.Options options = new Interpreter.Options();
    CompatibilityList compatList = new CompatibilityList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inference);

        // Initialize interpreter with GPU delegate
        if (compatList.isDelegateSupportedOnThisDevice()) {
            // if the device has a supported GPU, add the GPU delegate
            GpuDelegate.Options delegateOptions = compatList.getBestOptionsForThisDevice();
            GpuDelegate gpuDelegate = new GpuDelegate(delegateOptions);
            options.addDelegate(gpuDelegate);
        } else {
            // if the GPU is not supported, run on 5 threads
            options.setNumThreads(5);
        }
        int READ_EXTERNAL_STORAGE = 100;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE);
        new Thread(() -> {
            try {
                Thread.sleep(50);
                tflite_run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void tflite_run() {
        boolean bool_file = false;
        Log.e("AAA", "進入tflite_run");
        //建立result檔案
        String root_path = getIntent().getStringExtra("root_path");
        DenseNet121_model = (File) getIntent().getExtras().get("DenseNet121_model");



        //轉換音訊成特徵圖
        ArrayList<String> stringWav = getIntent().getStringArrayListExtra("stringWav");
        assert stringWav != null;
        PATH_number = stringWav.size();
        for (int i = 0; i < PATH_number; i++) {
            Log.d("AAA", "File PATH: " + stringWav.get(i));
            System.out.println("PATH: " + stringWav.get(i));
        }


        initPython();
        Log.e("AAA", "python啟動");
        pythonInputData = new frontEnd(stringWav, PATH_number).getPythonInputData();
        Log.e("AAA", "拿到pythoninputdata");
        result = new float[PATH_number][4];
        result = loadLocalModel(DenseNet121_model,"DenseNet121");//label : ['Velar(舌根音化)', 'Stopping(塞音)', 'Affricate'(塞擦音), 'Consonant vowel(聲隨韻母)']
        float[] result_average = average_result(result);
        Log.e("result_average", Arrays.toString(result_average));


        Intent mIntent = new Intent(this, ResultActivity.class);
        Bundle mBundle = new Bundle();

        Intent intent = new Intent();
        intent.setClass(this, ResultActivity.class);
        intent.putExtra("result_average", result_average);
        intent.putExtra("root_path", root_path);
        startActivity(intent);
        InferenceActivity.this.finish();
    }
    private float[][] loadLocalModel(File modelFile, String modelName) {
        float[][] modelResult = new float[PATH_number][4];
        Interpreter interpreter = new Interpreter(modelFile,options);
        for (int i = 0; i < PATH_number; i++) {
            float[][][][] modelInput = new float[1][128][128][3];
            float[][] modelOut = new float[][]{{0, 0, 0, 0}};

            for (int j = 0; j < 128; j++) {
                for (int k = 0; k < 128; k++) {
                    for(int l = 0; l < 3; l++){
                        modelInput[0][j][k][l] = pythonInputData[i][128*3*j+3*k+l];
                    }
                }
            }
            Log.e("model name:",modelName);
            Log.e("data name:",pythonInputData[i].toString());
            Log.e("fb_input", Arrays.deepToString(modelInput[0]));
            interpreter.run(modelInput, modelOut);
            Log.e("fb_output", Arrays.toString(modelOut[0]));
            modelResult[i] = new float[]{modelOut[0][0], modelOut[0][1],modelOut[0][2],modelOut[0][3]};
        }
        interpreter.close();
        return  modelResult;
    }



    public float[] average_result(float[][] result) {
//        float[] answer = new float[4]{0.0F,0.0F,0.0F,0.0F};
        float[] answer = new float[] { 0.0f, 0.0f, 0.0f ,0.0f};
        for (int i = 0; i < result.length; i++) {
            answer[0] = answer[0] + result[i][0];
            answer[1] = answer[1] + result[i][1];
            answer[2] = answer[2] + result[i][2];
            answer[3] = answer[3] + result[i][3];
        }
        answer[0] = answer[0] / result.length;
        answer[1] = answer[1] / result.length;
        answer[2] = answer[2] / result.length;
        answer[3] = answer[3] / result.length;
        return answer;
    }
    void initPython(){
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
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