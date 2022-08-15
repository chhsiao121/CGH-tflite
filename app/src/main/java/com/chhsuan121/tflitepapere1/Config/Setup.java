package com.chhsuan121.tflitepapere1.Config;
import android.os.Environment;
public class Setup {
    //google uploader 上傳網址
    public static final String DEF_USER_AGENT = "Mozilla/5.0 (X11; Linux86_64) AppleWebKit/534.24 (KHTML, like Gecko) Chrome/11.0.696.34 Safari/534.24";
    public static final String DEF_WEBAPP_URL = "https://driveuploader.com/upload/t6hj2e2uH3/";
    public static final String DEF_WEBAPP_NAME = "location";

    //設定系統儲存路徑
    public static final String DEF_STORAGE_PATH = Environment.getExternalStorageDirectory().toString();
    public static final String DEF_AUDIO_PATH = Environment.getExternalStorageDirectory().toString() + "/Medical_Project/Labeled/";
    public static final String DEF_AUDIO_PATH_UN = Environment.getExternalStorageDirectory().toString() + "/Medical_Project/Unlabeled/";
    public static final String AUDIO_RAW_FILENAME = "Temp.raw";
    public static final String AUDIO_WAV_FILENAME = "Temp.wav";

    //WordCardActivity 使用
    public static final int TIME_RECORD_UPDATE = 50;
    public static final int TIME_RECORD_WORDS_SECOND = 2; //錄詞

    public static final int TIME_PLAY_UPDATE = 50;
    public static final int TIME_PLAY_WORDS_SECOND = 2; //錄詞


    //WordCardActivity 使用
    public static final int REQ_RECORD_UPDATE = 1000;
    public static final int REQ_PLAY_UPDATE = 1001;

    //注音符號
    public static final String[] ALPHABETS = {
            "ㄅ", "ㄆ", "ㄇ", "ㄈ",
            "ㄉ", "ㄊ", "ㄋ", "ㄌ",
            "ㄍ", "ㄎ", "ㄏ", "ㄐ",
            "ㄑ", "ㄒ", "ㄓ", "ㄔ",
            "ㄕ", "ㄖ", "ㄗ", "ㄘ",
            "ㄙ", "ㄩ", "ㄩ", "ㄩ",
            "ㄩ", "ㄩ", "ㄩ", "ㄩ",
            "ㄩ", "ㄩ", "ㄩ", "ㄩ",
            "ㄩ", "ㄩ", "ㄩ", "ㄩ",
            "ㄩ", "ㄩ", "ㄩ", "ㄩ"
    };

    public static final String[] WORD_ID = {
            "wordcard01_1","wordcard01_3","wordcard02_1","wordcard02_3","wordcard03_1","wordcard03_3","wordcard04_1","wordcard04_3","wordcard05_1","wordcard05_3","wordcard06_1","wordcard06_2","wordcard07_1","wordcard07_3","wordcard08_2","wordcard08_3","wordcard09_2","wordcard09_3","wordcard10_1","wordcard10_2","wordcard11_1","wordcard11_3","wordcard12_2","wordcard12_3","wordcard13_2","wordcard13_3","wordcard14_1","wordcard14_3","wordcard15_2","wordcard15_3","wordcard16_1","wordcard16_3","wordcard17_1","wordcard17_2","wordcard18_1","wordcard18_2","wordcard19_1","wordcard19_3","wordcard20_1","wordcard20_3","wordcard21_1","wordcard21_2","wordcard21_3","wordcard24_1","wordcard24_2"
    };
    public static final String[] WORD = {"布丁","大白菜","螃蟹","蓮蓬頭","帽子","捉迷藏","鳳梨","吹風機","動物","看電視","太陽","枕頭","鈕扣","喝奶昔","恐龍","養樂多","烏龜","去公園","筷子","貝殼","漢堡","救護車","果醬","指甲刀","鋼琴","中秋節","信封","口香糖","蠟燭","擦桌子","抽屜","柳橙汁","閃電","牙刷","日歷","超人","走路","水族箱","草莓","上廁所","掃把","垃圾","去散步","杜鵑花","選擇"};


    //錯誤類別
    public static final String[] ERROR_TYPE = {"送氣化",
            "不送氣化",
            "唇音化",
            "舌根音化",
            "舌尖音化",
            "塞音化",
            "塞擦音化",
            "擦音化",
            "不卷舌化",
            "鼻音化",
            "邊音化",
            "子音省略",
            "附韻母省略",
            "聲隨韻母省略",
            "介音省略",
            "齒尖音",
            "歪曲音",
            "添加音",
            "不確定"

    };
}
