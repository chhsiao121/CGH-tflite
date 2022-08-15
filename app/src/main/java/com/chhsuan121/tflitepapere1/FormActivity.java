package com.chhsuan121.tflitepapere1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.chhsuan121.tflitepapere1.databinding.ActivityFormBinding;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class FormActivity extends AppCompatActivity {
    private ActivityFormBinding binding;
    Date dNow = new Date( );
    SimpleDateFormat ft = new SimpleDateFormat ("yyyy.MM.dd.HH.mm.ss");
    //    private String root_path = "/CGH_Medical/Unlabeled/data_" + ft.format(dNow).toString().replace(":",".") + "/";
    private String root_path = "/Unlabeled/data_" + ft.format(dNow).toString().replace(":",".").replace(" ","_") + "/";
    public Calendar calendar;
    public SimpleDateFormat dateFormat;
    public String date;
    private int age;
    private String formattedDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        File DenseNet121_model = (File)getIntent().getExtras().get("DenseNet121_model");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        binding = ActivityFormBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
//        since android11 can't read internal memory data so commend
//        File folder = new File(Setup.DEF_STORAGE_PATH + root_path);
//        if (!folder.exists()) {
//            boolean isSuccess = folder.mkdirs();
//        }
        binding.chipGroup2.setSingleSelection(true);
        binding.chipGroup1.setSingleSelection(true);
        binding.chipGroup9.setSingleSelection(true);
        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("when you come");
        builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
        final MaterialDatePicker materialDatePicker = builder.build();

        binding.buttonDate.setOnClickListener(v -> {
            materialDatePicker.show(getSupportFragmentManager(),"DATA_PICKER");
        });

        materialDatePicker.addOnPositiveButtonClickListener(selection ->
                binding.buttonDate.setText(materialDatePicker.getHeaderText()));
        binding.buttonYes.setOnClickListener(v -> {

//                String root_path = getIntent().getStringExtra("root_path");
//                String file_path = Setup.DEF_STORAGE_PATH + root_path + "/" + "Questionnaire_result.txt";
//                Log.e("save",file_path);
//                File file = new File(file_path);
                int idTest =binding.chipGroup9.getCheckedChipId();
                if(idTest==R.id.chip_9_1){
                    root_path=root_path.substring(0,root_path.length()-1)+"_test/";
                }
                File file = new File(getExternalFilesDir(root_path),"Questionnaire_result.txt");
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
//                FileWriter write = new FileWriter(file_path, false);
                    FileWriter write = new FileWriter(file.getAbsolutePath(), false);
                    BufferedWriter buf = new BufferedWriter(write);
                    calendar = Calendar.getInstance();
                    dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                    date = dateFormat.format(calendar.getTime());
                    buf.write(date);
                    buf.write(String.valueOf(binding.textField.getEditText().getText()));
                    buf.newLine();
                    buf.write((String) binding.buttonDate.getText());
                    buf.newLine();
                    buf.write((String) binding.textViewQ1.getText());
                    buf.newLine();
                    for (Integer id:binding.chipGroup1.getCheckedChipIds()){
                        Chip chip = binding.chipGroup1.findViewById(id);
                        buf.write((String) chip.getText());
                        buf.newLine();
                    }
                    buf.write((String) binding.textViewQ2.getText());
                    buf.newLine();
                    for (Integer id:binding.chipGroup2.getCheckedChipIds()){
                        Chip chip = binding.chipGroup2.findViewById(id);
                        buf.write((String) chip.getText());
                        buf.newLine();
                    }
                    buf.write((String) binding.textViewQ3.getText());
                    buf.newLine();
                    for (Integer id:binding.chipGroup3.getCheckedChipIds()){
                        Chip chip = binding.chipGroup3.findViewById(id);
                        buf.write((String) chip.getText());
                        buf.newLine();
                    }
                    buf.write((String) binding.textViewQ4.getText());
                    buf.newLine();
                    for (Integer id:binding.chipGroup4.getCheckedChipIds()){
                        Chip chip = binding.chipGroup4.findViewById(id);
                        buf.write((String) chip.getText());
                        buf.newLine();
                    }
                    buf.write((String) binding.textViewQ5.getText());
                    buf.newLine();
                    for (Integer id:binding.chipGroup5.getCheckedChipIds()){
                        Chip chip = binding.chipGroup5.findViewById(id);
                        buf.write((String) chip.getText());
                        buf.newLine();
                    }
                    buf.write((String) binding.textViewQ6.getText());
                    buf.newLine();
                    for (Integer id:binding.chipGroup6.getCheckedChipIds()){
                        Chip chip = binding.chipGroup6.findViewById(id);
                        buf.write((String) chip.getText());
                        buf.newLine();
                    }
                    buf.write((String) binding.textViewQ7.getText());
                    buf.newLine();
                    for (Integer id:binding.chipGroup7.getCheckedChipIds()){
                        Chip chip = binding.chipGroup7.findViewById(id);
                        buf.write((String) chip.getText());
                        buf.newLine();
                    }
                    buf.write((String) binding.textViewQ8.getText());
                    buf.newLine();
                    for (Integer id:binding.chipGroup8.getCheckedChipIds()){
                        Chip chip = binding.chipGroup8.findViewById(id);
                        buf.write((String) chip.getText());
                        buf.newLine();
                    }
                    buf.write((String) binding.textViewQ9.getText());
                    buf.newLine();
                    for (Integer id:binding.chipGroup9.getCheckedChipIds()){
                        Chip chip = binding.chipGroup9.findViewById(id);
                        buf.write((String) chip.getText());
                        buf.newLine();
                    }
                    //寫入並關閉串流
                    buf.flush();
                    buf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();

                intent.putExtra("root_path", root_path);
                intent.putExtra("DenseNet121_model", DenseNet121_model);
                intent.setClass(FormActivity.this, WordcardActivity.class);
                startActivity(intent);
                FormActivity.this.finish();

        });
        binding.buttonNo.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(FormActivity.this, MainActivity.class);
            startActivity(intent);
            FormActivity.this.finish();
        });
    }
}