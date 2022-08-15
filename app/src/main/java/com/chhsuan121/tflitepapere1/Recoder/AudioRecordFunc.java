package com.chhsuan121.tflitepapere1.Recoder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AudioRecordFunc {
    //音訊輸入-麥克風
    public final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;
    //採用頻率
    //44100是目前的標準，但是某些裝置仍然支援22050，16000，11025
    public final static int AUDIO_SAMPLE_RATE = 44100; //44.1KHz,普遍使用的頻率
    public final static int SUCCESS = 1000;
    public final static int E_NOSDCARD = 1001;
    public final static int E_STATE_RECODING = 1002;
    //錄音輸出檔案
    public static String AUDIO_WAV_DIR = "";
    public static String AUDIO_RAW_FILENAME = "Temp.raw";
    public static String AUDIO_WAV_FILENAME = "Temp.wav";
    private static AudioRecordFunc mInstance;
    // 緩衝區位元組大小
    private int bufferSizeInBytes = 0;
    //AudioName裸音訊資料檔案 ，麥克風
    private String AudioName = "";
    //NewAudioName可播放的音訊檔案
    private String NewAudioName = "";
    private String fileBasePath = "";
    private AudioRecord audioRecord;
    private boolean isRecord = false;// 設定正在錄製的狀態

    private AudioRecordFunc() {
    }

    public synchronized static AudioRecordFunc getInstance() {
        if (mInstance == null)
            mInstance = new AudioRecordFunc();
        return mInstance;
    }

    /**
     * 判斷是否有外部儲存裝置sdcard
     *
     * @return true | false
     */
    public static boolean isSdcardExit() {
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /**
     * 獲取檔案大小
     *
     * @param path,檔案的絕對路徑
     * @return
     */
    public static long getFileSize(String path) {
        File mFile = new File(path);
        if (!mFile.exists())
            return -1;
        return mFile.length();
    }

    public int startRecordAndFile() {
        //判斷是否有外部儲存裝置sdcard
        if (isSdcardExit()) {
            if (isRecord) {
                return E_STATE_RECODING;
            } else {
                if (audioRecord == null)
                    creatAudioRecord();
                audioRecord.startRecording();
                // 讓錄製狀態為true
                isRecord = true;
                // 開啟音訊檔案寫入執行緒
                new Thread(new AudioRecordThread()).start();
                return SUCCESS;
            }
        } else {
            return E_NOSDCARD;
        }
    }

    public void setFileBasePath(String fileBasePath) {
        this.fileBasePath = fileBasePath;
    }

    public void setAudioDir(String dir) {
        this.AUDIO_WAV_DIR = dir;
    }

    public void setAudioWavFilename(String filename) {
        this.AUDIO_WAV_FILENAME = filename;
    }

    public void stopRecordAndFile() {
        close();
    }

    public long getRecordFileSize() {
        return getFileSize(NewAudioName);
    }

    private void close() {
        if (audioRecord != null) {
            System.out.println("stopRecord");
            isRecord = false;//停止檔案寫入
            audioRecord.stop();
            audioRecord.release();//釋放資源
            audioRecord = null;
        }
    }

    private void creatAudioRecord() {
        // 獲取音訊檔案路徑
        String mAudioRawPath = "";
        if (isSdcardExit()) {
//            String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            mAudioRawPath = fileBasePath + AUDIO_WAV_DIR + AUDIO_RAW_FILENAME;
        }
        AudioName = mAudioRawPath;

        String mAudioWavPath = "";
        if (isSdcardExit()) {
//            String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            mAudioWavPath = fileBasePath + AUDIO_WAV_DIR + AUDIO_WAV_FILENAME;
        }
        NewAudioName = mAudioWavPath;

        // 獲得緩衝區位元組大小
        bufferSizeInBytes = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

        // 建立AudioRecord物件
        audioRecord = new AudioRecord(AUDIO_INPUT, AUDIO_SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSizeInBytes);
    }

    /**
     * 這裡將資料寫入檔案，但是並不能播放，因為AudioRecord獲得的音訊是原始的裸音訊，
     * 如果需要播放就必須加入一些格式或者編碼的頭資訊。但是這樣的好處就是你可以對音訊的 裸資料進行處理，比如你要做一個愛說話的TOM
     * 貓在這裡就進行音訊的處理，然後重新封裝 所以說這樣得到的音訊比較容易做一些音訊的處理。
     */
    private void writeDateTOFile() {
        // new一個byte陣列用來存一些位元組資料，大小為緩衝區大小
        byte[] audiodata = new byte[bufferSizeInBytes];
        FileOutputStream fos = null;
        int readsize = 0;
        try {
            File file = new File(AudioName);
            if (file.exists()) {
                file.delete();
            }
            fos = new FileOutputStream(file);// 建立一個可存取位元組的檔案
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (isRecord == true) {
            readsize = audioRecord.read(audiodata, 0, bufferSizeInBytes);
            if (AudioRecord.ERROR_INVALID_OPERATION != readsize && fos != null) {
                try {
                    fos.write(audiodata);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            if (fos != null)
                fos.close();// 關閉寫入流
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 這裡得到可播放的音訊檔案
    public void copyWaveFile(String inFilename, String outFilename) {
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = AUDIO_SAMPLE_RATE;
        int channels = 1;
        long byteRate = 16 * AUDIO_SAMPLE_RATE * channels / 8;
        byte[] data = new byte[bufferSizeInBytes];
        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;
            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);
            while (in.read(data) != -1) {
                out.write(data);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 這裡提供一個頭資訊。插入這些資訊就可以得到可以播放的檔案。
     * 為我為啥插入這44個位元組，這個還真沒深入研究，不過你隨便開啟一個wav
     * 音訊的檔案，可以發現前面的標頭檔案可以說基本一樣哦。每種格式的檔案都有
     * 自己特有的標頭檔案。
     */
    private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,
                                     long totalDataLen, long longSampleRate, int channels, long byteRate)
            throws IOException {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = 16; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }

    class AudioRecordThread implements Runnable {
        @Override
        public void run() {
            writeDateTOFile();//往檔案中寫入裸資料
            copyWaveFile(AudioName, NewAudioName);//給裸資料加上標頭檔案
            File rawFile = new File(fileBasePath + AUDIO_WAV_DIR + AUDIO_RAW_FILENAME);
//            new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Caramel");
//            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Caramel");
            boolean dstatus = rawFile.delete();
        }
    }
}
