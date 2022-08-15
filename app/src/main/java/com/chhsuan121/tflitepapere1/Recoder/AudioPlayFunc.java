package com.chhsuan121.tflitepapere1.Recoder;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;


public class AudioPlayFunc extends Thread {
    private static final String TAG = "AudioPlayFunc";
    private static final String TMP_FOLDER_NAME = "AnWindEar";
    private static final int RECORD_AUDIO_BUFFER_TIMES = 1;
    private static final int PLAY_AUDIO_BUFFER_TIMES = 1;
    private static final int AUDIO_FREQUENCY = 44100;

    private static final int RECORD_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int PLAY_CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_MONO;
    private static final int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private volatile WindState state = WindState.IDLE; // 當前狀態
    private File tmpPCMFile = null;
    private File tmpWavFile = null;
    private OnState onStateListener;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private boolean stop_flag;

    /**
     * PCM緩存目錄
     */
    private static String cachePCMFolder;

    /**
     * wav緩存目錄
     */
    private static String wavFolderPath;
    private static AudioPlayFunc instance = new AudioPlayFunc();

    private AudioPlayFunc() {

    }

    public static AudioPlayFunc getInstance() {
        if (null == instance) {
            instance = new AudioPlayFunc();
        }
        return instance;
    }

    public void setOnStateListener(OnState onStateListener) {
        this.onStateListener = onStateListener;
    }

    /**
     * 播放錄製好的PCM文檔
     */
    public synchronized void startPlayPCM(String filename) {
        if (!isIdle() && !stop_flag) {
            return;
        }
        File file = new File(filename);
        new AudioTrackPlayThread(file).start();
    }

    /**
     * 播放錄製好的wav文檔
     */
    public synchronized void startPlayWav(String filename) {
        if (!isIdle() && !stop_flag) {
            return;
        }
        Log.v("AudioPlayFunc", "startPlay");
        File file = new File(filename);
        new AudioTrackPlayThread(file).start();
    }

    public synchronized void stopPlay() {
        if (!state.equals(WindState.PLAYING)) {
            return;
        }
        stop_flag = true;
        state = WindState.STOP_PLAY;
        while (stop_flag) {
            Log.v("AudioPlayFunc", "Wait stopPlay");
        }
        ;
    }

    public synchronized boolean isIdle() {
        return WindState.IDLE.equals(state);
    }

    /**
     * AudioTrack播放音頻線程
     * 使用FileInputStream讀取文檔
     */
    private class AudioTrackPlayThread extends Thread {
        AudioTrack track;
        int bufferSize = 10240;
        File audioFile = null;

        AudioTrackPlayThread(File aFile) {
            setPriority(Thread.MAX_PRIORITY);
            audioFile = aFile;
            int bufferSize = AudioTrack.getMinBufferSize(AUDIO_FREQUENCY,
                    PLAY_CHANNEL_CONFIG, AUDIO_ENCODING) * PLAY_AUDIO_BUFFER_TIMES;
            track = new AudioTrack(AudioManager.STREAM_MUSIC,
                    AUDIO_FREQUENCY,
                    PLAY_CHANNEL_CONFIG, AUDIO_ENCODING, bufferSize,
                    AudioTrack.MODE_STREAM);
        }

        @Override
        public void run() {
            super.run();
            state = WindState.PLAYING;
            notifyState(state);
            Log.v("AudioPlayFunc", "start playing");
            try {
                FileInputStream fis = new FileInputStream(audioFile);
                track.play();
                byte[] aByteBuffer = new byte[bufferSize];
                while (state.equals(WindState.PLAYING) &&
                        fis.read(aByteBuffer) >= 0 && !stop_flag) {
                    track.write(aByteBuffer, 0, aByteBuffer.length);
                }
                track.stop();
                track.release();
            } catch (Exception e) {
                Log.e(TAG, "AudioTrackPlayThread:", e);
                notifyState(WindState.ERROR);
            }
            state = WindState.STOP_PLAY;
            notifyState(state);
            state = WindState.IDLE;
            notifyState(state);
            stop_flag = false;
            Log.v("AudioPlayFunc", "stop playing");
        }
    }

    private void notifyState(final WindState currentState) {
        if (null != onStateListener) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    onStateListener.onStateChanged(currentState);
                }
            });
        }
    }

    public interface OnState {
        void onStateChanged(WindState currentState);
    }

    /**
     * 表示當前狀態
     */
    public enum WindState {
        ERROR,
        IDLE,
        RECORDING,
        STOP_RECORD,
        PLAYING,
        STOP_PLAY
    }
}