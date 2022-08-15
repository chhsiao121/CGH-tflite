package com.chhsuan121.tflitepapere1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chhsuan121.tflitepapere1.Config.Setup;
import com.chhsuan121.tflitepapere1.Recoder.AudioPlayFunc;
import com.chhsuan121.tflitepapere1.Recoder.AudioRecordFunc;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;



public class WordcardActivity extends AppCompatActivity {

    public RecyclerView mRecyclerView;
    public MyAdapter myAdapter;
    public List<ProgressObject> progressObjects;
    public List<ButtonObject> btnPlayObject;
    public List<ButtonObject> btnRecordObject;
    public ArrayList<ListItem> wordcardsArray;
    public String[] wordCards;
    public String[] wordIds;
    public Button btn_Inf;
    public String wordcardsName;
    public ArrayList<String> stringWav = new ArrayList<>();
    public String AUDIO_WAV_DIRE;
    private Bundle bundle;
    private int age;
    private AudioRecordFunc recorder;
    private AudioPlayFunc player;
    public String root_path;
    private int Update_Count = 0;
    private int record_control_position = -1;
    private int play_control_position = -1;
    private boolean recordWords;
    File DenseNet121_model;
    //設定handler處理器
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case com.chhsuan121.tflitepapere1.Config.Setup.REQ_RECORD_UPDATE: {
                    int MAX_RECORD_COUNT;


                    MAX_RECORD_COUNT = Setup.TIME_RECORD_WORDS_SECOND * 1000 / Setup.TIME_RECORD_UPDATE;

                    if (Update_Count >= MAX_RECORD_COUNT) {
                        //Log.v("WordCardActivity","progressobject " + control_position + " = " + progressObjects.get(control_position).getProgress());
                        progressObjects.get(record_control_position).setProgress(0);
                        myAdapter.setProgress(Update_Count * 100 / MAX_RECORD_COUNT, record_control_position);
                        recorder.stopRecordAndFile();
                        //myAdapter.enableBtn();
                        Update_Count = 0;
                    } else {
                        //Log.v("WordCardActivity","progressobject " + control_position + " = " + progressObjects.get(control_position).getProgress());
                        Update_Count++;
                        progressObjects.get(record_control_position).setProgress(Update_Count * 100 / MAX_RECORD_COUNT);
                        myAdapter.setProgress(Update_Count * 100 / MAX_RECORD_COUNT, record_control_position);
                        handler.sendEmptyMessageDelayed(com.chhsuan121.tflitepapere1.Config.Setup.REQ_RECORD_UPDATE, com.chhsuan121.tflitepapere1.Config.Setup.TIME_RECORD_UPDATE);
                    }
                    break;
                }
                case com.chhsuan121.tflitepapere1.Config.Setup.REQ_PLAY_UPDATE: {
                    int MAX_PLAY_COUNT;

                    MAX_PLAY_COUNT = Setup.TIME_PLAY_WORDS_SECOND * 1000 / Setup.TIME_PLAY_UPDATE;


                    if (Update_Count >= MAX_PLAY_COUNT) {
                        //Log.v("WordCardActivity","progressobject " + control_position + " = " + progressObjects.get(control_position).getProgress());
                        progressObjects.get(play_control_position).setProgress(0);
                        myAdapter.setProgress(Update_Count * 100 / MAX_PLAY_COUNT, play_control_position);
                        player.stopPlay();
                        //myAdapter.enableBtn();
                        Update_Count = 0;
                    } else {
                        //Log.v("WordCardActivity","progressobject " + control_position + " = " + progressObjects.get(control_position).getProgress());
                        Update_Count++;
                        progressObjects.get(play_control_position).setProgress(Update_Count * 100 / MAX_PLAY_COUNT);
                        myAdapter.setProgress(Update_Count * 100 / MAX_PLAY_COUNT, play_control_position);
                        handler.sendEmptyMessageDelayed(com.chhsuan121.tflitepapere1.Config.Setup.REQ_PLAY_UPDATE, com.chhsuan121.tflitepapere1.Config.Setup.TIME_PLAY_UPDATE);
                    }
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordcard);
        this.btn_Inf = findViewById(R.id.inference_btn);
        recorder = com.chhsuan121.tflitepapere1.Recoder.AudioRecordFunc.getInstance();
        player = com.chhsuan121.tflitepapere1.Recoder.AudioPlayFunc.getInstance();
        progressObjects = new ArrayList<>();
        btnPlayObject = new ArrayList<>();
        btnRecordObject = new ArrayList<>();
        wordcardsArray = new ArrayList<>();
        bundle = getIntent().getExtras();
        root_path = getIntent().getStringExtra("root_path");
        DenseNet121_model = (File)getIntent().getExtras().get("DenseNet121_model");

        wordCards = Setup.WORD;
        wordIds = Setup.WORD_ID;


        Context context = this;
//        File appSpecificExternalDir = new File(context.getExternalFilesDir(), filename);


        for (int i = 1; i <= wordCards.length; i++) {
            progressObjects.add(new ProgressObject(i, "position " + i, 0));
            btnPlayObject.add(new ButtonObject(i, "position " + i, true));
            btnRecordObject.add(new ButtonObject(i, "position " + i, true));
            progressObjects.add(new ProgressObject(i, "position " + i, 0));
            btnPlayObject.add(new ButtonObject(i, "position " + i, true));
            btnRecordObject.add(new ButtonObject(i, "position " + i, true));
//            for (int j = 0; j <= wordCards[i - 1].length(); j++) { //以下為單字
//                progressObjects.add(new ProgressObject(i, "position " + i, 0));
//                btnPlayObject.add(new ButtonObject(i, "position " + i, true));
//                btnRecordObject.add(new ButtonObject(i, "position " + i, true));
//            }
        }

        for (int i = 1; i <= wordCards.length; i++) {
            HeaderModel Header_alphabet = new HeaderModel();
            Header_alphabet.setheader(wordCards[i - 1]);
            Header_alphabet.set_img(wordIds[i - 1]);
            wordcardsArray.add(Header_alphabet);
            ChildModel Child_item1 = new ChildModel();
            wordcardsName = wordIds[i - 1];
            Child_item1.setChild(wordcardsName);
            wordcardsArray.add(Child_item1);
//            for (int j = 0; j < wordCards[i - 1].length(); j++) { //以下為單字
//                ChildModel Child_item2 = new ChildModel();
//                wordcardsName = wordIds[i - 1] + "_" + (j + 1);
//                Child_item2.setChild(wordcardsName);
//                wordcardsArray.add(Child_item2);
//            }
        }



        myAdapter = new MyAdapter(progressObjects, btnPlayObject, btnRecordObject, wordcardsArray);
        mRecyclerView = findViewById(R.id.recycler_wordcard);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(myAdapter);


        //預測鍵功能
        btn_Inf.setOnClickListener(v -> {
            Log.e("FUCK", String.valueOf(stringWav));

            if (!stringWav.isEmpty()) {
                Intent intent = new Intent();
                intent.setClass(WordcardActivity.this, InferenceActivity.class);
                intent.putExtra("DenseNet121_model", DenseNet121_model);
                intent.putExtra("stringWav", stringWav);
                intent.putExtra("root_path", root_path);
                startActivity(intent);
                WordcardActivity.this.finish();
            } else {
                Toast.makeText(getApplicationContext(), "請先錄製聲音", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface ListItem {
        boolean isHeader();

        String getName();

        String getID();
    }

    public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int LAYOUT_HEADER = 0;
        private static final int LAYOUT_CHILD = 1;
        private List<ProgressObject> mProgressObjectList;
        private List<ButtonObject> mBtnRecordList;
        private List<ButtonObject> mBtnPlayList;
        private ArrayList<ListItem> mData;

        MyAdapter(List<ProgressObject> progressobjects, List<ButtonObject> btnplayobject, List<ButtonObject> btnrecordobject, ArrayList<ListItem> listItems) {
            mProgressObjectList = progressobjects;
            mBtnPlayList = btnplayobject;
            mBtnRecordList = btnrecordobject;
            mData = listItems;
        }

        //提供setprocess的funciotn給外部
        void setProgress(int progress, int position) {
            mProgressObjectList.get(position).setProgress(progress);
            notifyItemChanged(position, 1);
        }

        //提供設置btnplay狀態的function給外部
        public void setBtnPlay(boolean status, int position) {
            mBtnPlayList.get(position).setStatus(status);
            notifyItemChanged(position, 1);
        }

        //提供設置btnplay狀態的function給外部
        public void setBtnRecord(boolean status, int position) {
            mBtnRecordList.get(position).setStatus(status);
            notifyItemChanged(position, 1);
        }

        void initProgress() {
            for (int i = 0; i < mProgressObjectList.size(); i++) {
                mProgressObjectList.get(i).setProgress(0);
                notifyItemChanged(i, 1);
            }
        }

        public void disableBtn() {
            for (int i = 0; i < mBtnRecordList.size(); i++) {
                mBtnPlayList.get(i).setStatus(false);
                mBtnRecordList.get(i).setStatus(false);
                notifyItemChanged(i, 1);
            }
        }

        void enableBtn() {
            for (int i = 0; i < mBtnRecordList.size(); i++) {
                mBtnPlayList.get(i).setStatus(true);
                mBtnRecordList.get(i).setStatus(true);
                notifyItemChanged(i, 1);
            }
        }

        //更新特定頁面
        public void updateViewHolder(int position) {
            notifyItemChanged(position, 1);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            RecyclerView.ViewHolder vh;
            if (viewType == LAYOUT_HEADER) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.wordcardcell_header, parent, false);
                vh = new WordcardActivity.MyAdapter.HeaderViewHolder(v);
            } else {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.wordcardcell, parent, false);
                vh = new WordcardActivity.MyAdapter.ChildViewHolder(v);
            }

            return vh;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            final com.chhsuan121.tflitepapere1.File.FileManager fileManager = new com.chhsuan121.tflitepapere1.File.FileManager();
            final String unlabeldirectoryPath = getExternalFilesDir(root_path).getAbsolutePath();
            final Resources resource = getResources();


            if (holder.getItemViewType() == LAYOUT_HEADER) {
                HeaderViewHolder vaultItemHolder = (HeaderViewHolder) holder;
                vaultItemHolder.mTextView.setText(mData.get(position).getName());

                Log.e("HeaderViewHolder", "Header name" + mData.get(position).getID());
                int id = resource.getIdentifier(mData.get(position).getID(), "drawable", getPackageName());

                Drawable drawable = getResources().getDrawable(id);
                vaultItemHolder.image_wc.setImageDrawable(drawable);

            } else {
                ChildViewHolder vaultItemHolder = (ChildViewHolder) holder;


                //載入對應字卡以及描述
                Log.v("WordCardActivity", "image_name = " + mData.get(position).getName());
                String str = mData.get(position).getName();
                Log.v("WordCardActivity", "原本: " + str);
                if (str.chars().filter(num -> num == '_').count() == 1) {
                    recordWords = true; //完整詞彙
                    int id = resource.getIdentifier(str, "string", getPackageName());
                    String str_image = resource.getString(id);
                    vaultItemHolder.text_wc.setText(str_image);
                    //設定錄音與播放按鈕的顏色，如果此字卡已存在，按鈕將會改成綠色
                    File dir = new File(unlabeldirectoryPath);
                    File[] matchingFiles = dir.listFiles(new FilenameFilter() {
                        public boolean accept(File file, String name) {
                            return name.startsWith(mData.get(position).getName()+".wav");
                        }
                    });
                    if (matchingFiles != null && matchingFiles.length > 0){
                        vaultItemHolder.btn_record.setBackgroundColor(getResources().getColor(R.color.Tea_Green));
                        vaultItemHolder.btn_play.setBackgroundColor(getResources().getColor(R.color.Tea_Green));
                    }
                    else{
                        vaultItemHolder.btn_record.setBackgroundColor(getResources().getColor(R.color.transparent));
                        vaultItemHolder.btn_play.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }
                    //設定每個區塊背景為灰色
                    //設定每個區塊背景, 未標記為亮黑色, 標記為暗黑色
                    String[] files = fileManager.searchFileWithName(unlabeldirectoryPath, mData.get(position).getName().replace("wordcard", ""), 1);
                    if (files != null && files.length > 0) {
                        //Log.v("WordCardLabelActivity", position + " -> files size = " + files.length);
                        int color = getResources().getColor(R.color.colorDarkGray);
                        vaultItemHolder.mLinearLayout.setBackgroundColor(color);
                    } else {
                        //Log.v("WordCardLabelActivity", position + " -> files null");
                        int color = getResources().getColor(R.color.colorLightGray);
                        vaultItemHolder.mLinearLayout.setBackgroundColor(color);
                    }
                    //錄音鍵功能
                    vaultItemHolder.btn_record.setOnClickListener(v -> {
                        //Toast.makeText(WordCardActivity.this,position + "btn_record pressed",Toast.LENGTH_LONG).show();
                        initProgress();
                        //disableBtn();
                        Update_Count = 0;
                        record_control_position = position;
                        recorder.stopRecordAndFile();
                        player.stopPlay();
                        recorder = AudioRecordFunc.getInstance();
                        recorder.setFileBasePath(getExternalFilesDir(null).getAbsolutePath());
                        handler.removeMessages(Setup.REQ_PLAY_UPDATE);
                        handler.removeMessages(Setup.REQ_RECORD_UPDATE);

                        AudioRecordFunc.AUDIO_WAV_DIR = root_path;
                        AudioRecordFunc.AUDIO_RAW_FILENAME = mData.get(position).getName() + ".raw";
                        AudioRecordFunc.AUDIO_WAV_FILENAME = mData.get(position).getName() + ".wav";
                        int Result = recorder.startRecordAndFile();
                        handler.sendEmptyMessageDelayed(Setup.REQ_RECORD_UPDATE, Setup.TIME_RECORD_UPDATE);

//                        AUDIO_WAV_DIRE = Setup.DEF_STORAGE_PATH + AudioRecordFunc.AUDIO_WAV_DIR + AudioRecordFunc.AUDIO_WAV_FILENAME;
                        AUDIO_WAV_DIRE = getExternalFilesDir(null).getAbsolutePath()  +AudioRecordFunc.AUDIO_WAV_DIR + AudioRecordFunc.AUDIO_WAV_FILENAME;
                        if (stringWav.contains(AUDIO_WAV_DIRE)) {
                        } else {
                            stringWav.add(AUDIO_WAV_DIRE);
                        }
                        System.out.println("stringWav=" + stringWav);
                    });

                    //播放鍵功能
                    vaultItemHolder.btn_play.setOnClickListener(v -> {
                        initProgress();
                        //disableBtn();
                        Update_Count = 0;
                        play_control_position = position;
                        player.stopPlay();
                        recorder.stopRecordAndFile();
                        handler.removeMessages(Setup.REQ_PLAY_UPDATE);
                        handler.removeMessages(Setup.REQ_RECORD_UPDATE);
//                        String filename = Setup.DEF_STORAGE_PATH + root_path + mData.get(position).getName() + ".wav";
                        String filename = getExternalFilesDir(root_path).getAbsolutePath() +"/"+ mData.get(position).getName() + ".wav";
//                        Log.v("WordCardActivity", "filename = " + filename);
                        File audiofile = new File(filename);
                        if (audiofile.exists()) {
                            //player = AudioPlayFunc.getInstance();
                            player.startPlayWav(filename);
                            handler.sendEmptyMessageDelayed(Setup.REQ_PLAY_UPDATE, Setup.TIME_PLAY_UPDATE);
                        } else {
                            enableBtn();
                            Toast.makeText(WordcardActivity.this, "該字卡尚未進行錄音", Toast.LENGTH_SHORT).show();
                        }
                    });

                    vaultItemHolder.bind_processbar(mProgressObjectList.get(position));
                    vaultItemHolder.bind_btnplay(mBtnPlayList.get(position));
                    vaultItemHolder.bind_btnrecord(mBtnRecordList.get(position));
                }

            }
        }

        @Override
        public int getItemViewType(int position) {
            if (mData.get(position).isHeader()) {
                return LAYOUT_HEADER;
            } else {
                return LAYOUT_CHILD;
            }
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class ChildViewHolder extends RecyclerView.ViewHolder {
            RelativeLayout mLinearLayout;
            //            ImageView image_wc;
            TextView text_wc;
            ImageButton btn_record;
            ImageButton btn_play;

            ProgressBar progressbar;
            int mId;

            ChildViewHolder(View v) {
                super(v);
//                image_wc = v.findViewById(R.id.image_wordcard1);
                text_wc = v.findViewById(R.id.text_wordcard1);
                btn_record = v.findViewById(R.id.btn_wordcard1_recode);
                btn_play = v.findViewById(R.id.btn_wordcard1_play);

                progressbar = v.findViewById(R.id.progress_wordcard1);
                mLinearLayout = v.findViewById(R.id.wordcard_layout);
            }

            void bind_processbar(final ProgressObject progressObject) {
                mId = progressObject.getId();
                progressbar.setProgress(progressObject.getProgress());
            }

            void bind_btnplay(final ButtonObject buttonObject) {
                mId = buttonObject.getId();
                btn_play.setEnabled(buttonObject.getStatus());
            }

            void bind_btnrecord(final ButtonObject buttonObject) {
                mId = buttonObject.getId();
                btn_record.setEnabled(buttonObject.getStatus());
            }

            public int getId() {
                return mId;
            }
        }

        class HeaderViewHolder extends RecyclerView.ViewHolder {
            RelativeLayout mLinearLayout;
            TextView mTextView;
            ImageView image_wc;

            HeaderViewHolder(View v) {
                super(v);
                mTextView = v.findViewById(R.id.label_header_text);
                mLinearLayout = v.findViewById(R.id.label_header_layout);
                image_wc = v.findViewById(R.id.label_header_image);
            }
        }
    }

    //軟體的progress類型
    private class ProgressObject {
        private int mId;
        private String mTitle;
        private int mProgress;

        ProgressObject(int id, String title, int progress) {
            mId = id;
            mTitle = title;
            mProgress = progress;
        }

        int getId() {
            return mId;
        }

        void setId(int id) {
            mId = id;
        }

        String getTitle() {
            return mTitle;
        }

        void setTitle(String title) {
            mTitle = title;
        }

        int getProgress() {
            return mProgress;
        }

        void setProgress(int progress) {
            mProgress = progress;
        }
    }

    //軟體的button類型
    private class ButtonObject {
        private int mId;
        private String mTitle;
        private Boolean mStatus;

        ButtonObject(int id, String title, Boolean status) {
            mId = id;
            mTitle = title;
            mStatus = status;
        }

        int getId() {
            return mId;
        }

        void setId(int id) {
            mId = id;
        }

        String getTitle() {
            return mTitle;
        }

        void setTitle(String title) {
            mTitle = title;
        }

        Boolean getStatus() {
            return mStatus;
        }

        void setStatus(Boolean status) {
            mStatus = status;
        }
    }

    public class HeaderModel implements ListItem {

        String header;
        String header_img;


        void setheader(String header) {
            this.header = header;
        }

        void set_img(String header_img) {
            this.header_img = header_img;
        }

        @Override
        public boolean isHeader() {
            return true;
        }

        @Override
        public String getName() {
            return header;
        }

        @Override
        public String getID() {
            return header_img;
        }
    }

    public class ChildModel implements ListItem {

        String child;

        void setChild(String child) {
            this.child = child;
        }

        @Override
        public boolean isHeader() {
            return false;
        }

        @Override
        public String getName() {
            return child;
        }

        @Override
        public String getID() {
            return child;
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