package com.chhsuan121.tflitepapere1.TFlite;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;

import com.chhsuan121.tflitepapere1.InferenceActivity;
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions;
import com.google.firebase.ml.modeldownloader.DownloadType;
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TFlite extends InferenceActivity {
    private int wavNumber;
    private Interpreter tflite = null;
    private float[][][] ndTfliteOutputData;
    int GRAPH_X = 256;
    int GRAPH_y = 128;
    int CLASSES = 2;



    public TFlite(int wavNumber, double[][][] pythonInputData) {
        this.wavNumber = wavNumber;
        ndTfliteOutputData = new float[this.wavNumber][1][CLASSES];

        for (int i = 0; i < wavNumber; i++) {
            float[][][][] tfliteInputData = reshape(pythonInputData[i]);
            List<Float> list = new ArrayList<>();
            for (int x = 0; i < tfliteInputData[1].length; x++) {
                for (int y = 0; i < tfliteInputData[2].length; y++) {
                    list.add(tfliteInputData[1][x][y][1]);
                }
            }
            float[] tmp = new float[list.size()];
            for (int j = 0; j < tmp.length; j++) {
                tmp[i] = list.get(i);
            }
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 256, 128, 1}, DataType.FLOAT32);
            inputFeature0.loadArray(tmp, new int[]{1, 256, 128, 1});

        }
    }

    public float[][][] getNdTfliteOutputData() {
        return ndTfliteOutputData;
    }

    private MappedByteBuffer loadModelFile(AssetFileDescriptor fileDescriptor) throws IOException {
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private void load_model(AssetManager assets, String modelFilename) throws IOException {
        AssetFileDescriptor fileDescriptor = assets.openFd(modelFilename);
        try {
            tflite = new Interpreter(loadModelFile(fileDescriptor));
            tflite.setNumThreads(4);


        } catch (IOException e) {
            System.out.println("load model error");
            e.printStackTrace();
        }
    }

    private float[][][][] reshape(double[][] inputData) {

        float[][] temData = new float[GRAPH_X][GRAPH_y];
        float[][][][] return_data = new float[1][GRAPH_X][GRAPH_y][1];

        for (int i = 0; i < GRAPH_X; i++) {
            for (int j = 0; j < GRAPH_y; j++) {
                temData[i][j] = (float) inputData[i][j];
            }
        }

        for (int i = 0; i < GRAPH_X; i++) {
            for (int j = 0; j < GRAPH_y; j++) {
                return_data[0][i][j][0] = temData[i][j];
            }
        }

        return return_data;
    }




}
