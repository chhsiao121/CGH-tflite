package com.chhsuan121.tflitepapere1.TFlite;

import android.util.Log;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

import java.util.ArrayList;

public class frontEnd {
    private int wavNumber;
    private float[][] pythonInputData;
    private ArrayList<String> wavPath;
    int GRAPH_X = 128;
    int GRAPH_y = 128;


    public frontEnd(ArrayList<String> inwavPath, int inwavNumber) {
        wavPath = inwavPath;
        wavNumber = inwavNumber;
        pythonInputData = new float[wavNumber][GRAPH_X*GRAPH_y];
    }

    public float[][] getPythonInputData() {
        for (int i = 0; i < wavNumber; i++) {
            System.out.println("特徵提取中: " + wavPath.get(i));
            Log.e("AAA", "特徵提取中"+ wavPath.get(i));
            float[] pythonData = new float[GRAPH_X*GRAPH_y];
            try {
                pythonData = callPythonCode(wavPath.get(i));
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("PATH ERROR ");
            }
            pythonInputData[i] = pythonData;

        }
        return pythonInputData;
    }


    private float[] callPythonCode(String path) {
        Python py = Python.getInstance();
        Log.e("AAA", "python getInstance ok!!");
        PyObject pyObject = py.getModule("mfcc").callAttr("mfcc", path);
        Log.e("AAA", "python mfcc ok!!");
        JavaBean javaBean = pyObject.toJava(JavaBean.class);
        return javaBean.getData();
    }
}
